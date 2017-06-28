package com.fluent.framework.admin.core;
/* @formatter:Off */
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;

import com.fluent.framework.admin.events.*;
import com.fluent.framework.collection.*;
import com.fluent.framework.core.*;
import com.fluent.framework.events.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.service.*;
import com.fluent.framework.util.*;
import com.typesafe.config.*;

import static java.util.concurrent.TimeUnit.*;
import static com.fluent.framework.core.FluentAppState.*;
import static com.fluent.framework.service.FluentServiceType.*;

//TODO:
//Check if time gap between exchanges in handled correctly
//Doesn't monitor Routes

public final class MetronomeService implements Runnable, FluentService{

    private volatile boolean keepRunning;
    
    private final int                   delay;
    private final TimeUnit              unit;
    private final FluentConfiguration   config;
    private final FluentInDispatcher    dispatcher;
    private final ScheduledExecutorService service;

    private final Map<Exchange, ExchangeDetails> exchangeMap;
    private final Map<Exchange, Set<TimedTask>> eTaskStatus;

    private final static int        DEFAULT_DELAY = 5;
    private final static TimeUnit   DEFAULT_UNIT  = SECONDS;
    private final static String     EXCHANGE_KEY  = "fluent.exchanges";
    private final static String     NAME          = MetronomeService.class.getSimpleName( );
    private final static Logger     LOGGER        = LoggerFactory.getLogger( NAME );


    public MetronomeService( FluentConfiguration config, FluentInDispatcher inDispatcher ){
        this( DEFAULT_DELAY, DEFAULT_UNIT, config, inDispatcher );
    }


    public MetronomeService( int delay, TimeUnit unit, FluentConfiguration config, FluentInDispatcher dispatcher ){

        this.delay          = delay;
        this.unit           = unit;
        this.config         = config;
        this.dispatcher     = dispatcher;
        this.eTaskStatus    = new HashMap<>( );
        this.exchangeMap    = parseDetails( EXCHANGE_KEY );
        this.service        = Executors.newSingleThreadScheduledExecutor( new FluentThreadFactory( NAME ) );

    }


    @Override
    public final String name( ){
        return NAME;
    }

    
    @Override
    public final FluentServiceType getType( ){
        return METRONOME_SERVICE;
    }
    

    @Override
    public final void start( ) {

       for( ExchangeDetails details : exchangeMap.values( ) ){
           eTaskStatus.put( details.getExchange( ), new HashSet<TimedTask>( ) );
       }
            
       keepRunning = true;
       service.scheduleAtFixedRate( this, delay, delay, unit );
       LOGGER.info( "Monitoring configured exchanges for closing {}", exchangeMap.values( ) );
       LOGGER.info( "Started {} will publish metronome every {} {}", NAME, delay, unit );
       
       sendApplicationEvent( RUNNING );
       
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

        if( isAppClose ){
            sendApplicationEvent( STOPPING );
            return;
        }
         
        dispatcher.enqueue( new MetronomeEvent( ) );
        
    }

    
    protected final void sendApplicationEvent( FluentAppState state ){
        FluentEvent inEvent = new ApplicationStateEvent( state );
        dispatcher.enqueue( inEvent );
    }


    protected final void checkExchangeClosingTasks( ) {

        for( ExchangeDetails details : exchangeMap.values( ) ){

            // Check if Exchange is closing
            Exchange exchange   = details.getExchange( );
            Set<TimedTask> tSet = eTaskStatus.get( exchange );
            if( tSet == null ){
                LOGGER.warn( "Closing time isn't configured for Exchange [{}].", exchange );
                continue;
            }

            long nowMillis      = FluentTimeUtil.currentMillis( );
            TimedTask timeTask  = TimedTask.getTask( nowMillis, details.getOpenMillis( ), details.getCloseMillis( ) );
            boolean isClosing   = timeTask.isClosing( );
            if( !isClosing ) continue;

            boolean alreadySent = tSet.contains( timeTask );
            if( alreadySent ) continue;

            FluentEvent event   = new ExchangeClosingEvent( exchange, timeTask );
            tSet.add( timeTask );
            dispatcher.enqueue( event );

            LOGGER.info( "Sent closing time event [{}].", event );

        }

    }
    
    
    
    protected final Map<Exchange, ExchangeDetails> parseDetails( String key ){

        Map<Exchange, ExchangeDetails> eMAP = new HashMap<>( );
        List<? extends Config> eConfigList  = config.getRawConfig( ).getConfigList( key );

        for( Config eConfig : eConfigList ){
            
            try{

                String exchangeKey      = eConfig.getString( "name" );
                String openTime         = eConfig.getString( "openTime" );
                String closeTime        = eConfig.getString( "closeTime" );
                String timeZone         = eConfig.getString( "timeZone" );
                String speedLimit       = eConfig.getString( "speedLimit" );

                ExchangeDetails details = new ExchangeDetails( exchangeKey, openTime, closeTime, timeZone, speedLimit );
                eMAP.put( details.getExchange( ), details );
            
            }catch( Exception e ){
                LOGGER.error( "FAILED to parse config {}", eConfig );
                throw new RuntimeException( );
            }
            
        }

        return eMAP;

    }


    @Override
    public final void stop( ) {
        keepRunning = false;
        service.shutdown( );
    }


}
