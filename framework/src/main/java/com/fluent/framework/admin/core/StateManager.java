package com.fluent.framework.admin.core;
/* @formatter:Off */
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import com.fluent.framework.admin.events.*;
import com.fluent.framework.collection.*;
import com.fluent.framework.core.*;
import com.fluent.framework.events.core.*;
import com.fluent.framework.events.in.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.util.*;

import static java.util.concurrent.TimeUnit.*;
import static com.fluent.framework.admin.core.FluentState.*;
import static com.fluent.framework.util.FluentUtil.*;


public final class StateManager implements Runnable, FluentLifecycle{

    private volatile boolean                          keepRunning;

    private final int                                 delay;
    private final TimeUnit                            unit;
    private final FluentConfiguration                 config;

    private final FluentInEventDispatcher             inDispatcher;
    private final ScheduledExecutorService            service;
    private final Map<Exchange, Set<TimedTask>>       eTaskStatus;

    private final static AtomicReference<FluentState> APP_STATE;

    private final static int                          DEFAULT_DELAY = 5;
    private final static TimeUnit                     DEFAULT_UNIT  = SECONDS;
    private final static String                       NAME          = StateManager.class.getSimpleName( );
    private final static Logger                       LOGGER        = LoggerFactory.getLogger( NAME );


    static{
        APP_STATE = new AtomicReference<>( INITIALIZING );
    }


    public StateManager( FluentConfiguration config, FluentInEventDispatcher inDispatcher ){
        this( DEFAULT_DELAY, DEFAULT_UNIT, config, inDispatcher );
    }


    public StateManager( int delay, TimeUnit unit, FluentConfiguration config, FluentInEventDispatcher inDispatcher ){

        this.delay          = delay;
        this.unit           = unit;
        this.config         = config;
        this.inDispatcher   = inDispatcher;
        this.eTaskStatus    = new HashMap<>( );
        this.service        = Executors.newSingleThreadScheduledExecutor( new FluentThreadFactory( NAME ) );

    }


    @Override
    public final String name( ) {
        return NAME;
    }


    public static final boolean isRunning( ) {
        return (FluentState.RUNNING == getState( ));
    }


    public static final FluentState getState( ) {
        return APP_STATE.get( );
    }


    public static final FluentState setState( FluentState newState ) {
        return APP_STATE.getAndSet( newState );
    }
    

    @Override
    public final void start( ) {

        for( ExchangeDetails details : config.getExchangeDetailsMap( ).values( ) ){
            eTaskStatus.put( details.getExchange( ), new HashSet<TimedTask>( ) );
        }

        keepRunning = true;
        service.scheduleAtFixedRate( this, delay, delay, unit );
        LOGGER.info( "Monitoring Exchanges {}.", config.getExchangeDetailsMap( ).values( ) );
        LOGGER.info( "Started will publish Metronome events every {} {}.{}", delay, unit, NEWLINE );

    }


    @Override
    public final void run( ) {

        if( keepRunning ){
            sendMetronomeEvent( );
            checkExchangeClosingTasks( );
        }

    }


    protected final void sendMetronomeEvent( ) {

        long nowMillis      = FluentTimeUtil.currentMillis( );
        long openMillis     = config.getAppOpenTime( );
        long closeMillis    = config.getAppCloseTime( );
        TimedTask timeTask  = TimedTask.getTask( nowMillis, openMillis, closeMillis );
        boolean isAppClose  = (TimedTask.CLOSING_TIME == timeTask);

        FluentEvent inEvent = null;

        if( !isAppClose ){
            inEvent = new MetronomeEvent( );
        }else{
            // inEvent = new ExchangeClosingEvent(Exchange.ALL, TimedTask.CLOSING_TIME);
        }

        inDispatcher.enqueue( inEvent );


    }


    protected final void checkExchangeClosingTasks( ) {

        for( ExchangeDetails details : config.getExchangeDetailsMap( ).values( ) ){

            // Check if Exchange is closing
            Exchange exchange = details.getExchange( );
            Set<TimedTask> tSet = eTaskStatus.get( exchange );
            if( tSet == null ){
                LOGGER.warn( "Closing time isn't configured for Exchange [{}].", exchange );
                continue;
            }

            long nowMillis = FluentTimeUtil.currentMillis( );
            TimedTask timeTask = TimedTask.getTask( nowMillis, details.getOpenMillis( ), details.getCloseMillis( ) );
            boolean isClosing = timeTask.isClosing( );
            if( !isClosing )
                continue;

            boolean alreadySent = tSet.contains( timeTask );
            if( alreadySent )
                continue;

            ExchangeClosingEvent event = new ExchangeClosingEvent( exchange, timeTask );
            tSet.add( timeTask );
            inDispatcher.enqueue( event );

            LOGGER.info( "Sent closing time event [{}].", event );

        }

    }



    @Override
    public final void stop( ) {
        keepRunning = false;
        service.shutdown( );
    }


}
