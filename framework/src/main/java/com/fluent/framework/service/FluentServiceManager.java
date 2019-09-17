package com.fluent.framework.service;
//@formatter:off

import org.slf4j.*;

import java.util.*;

import com.fluent.framework.collection.*;
import com.fluent.framework.core.*;
import com.fluent.framework.events.*;
import com.fluent.framework.metronome.*;

import static com.fluent.common.util.Constants.*;
import static com.fluent.common.util.TimeUtils.*;
import static com.fluent.framework.core.FluentState.*;
import static com.fluent.framework.events.FluentEventType.*;


//1) Should this class hold all the services?
//2) Should we wrap all the services in a ServiceLocator class
//3) All the service claases should implements the FluentService interface
//4. How can we add more services to it?

public final class FluentServiceManager implements FluentEventListener, FluentLifecycle{

    private volatile FluentState appState;
    
    private final FluentConfig config;
    private final Collection<FluentService> services;
    
    private final static String SERVICES_KEY= "fluent.services";
    private final static String NAME        = FluentServiceManager.class.getSimpleName( );
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );


    public FluentServiceManager( FluentConfig config ) throws Exception{
        this.appState   = INITIALIZING;
        this.config     = config;
        this.services   = createServices( config );
    }

    
    @Override
    public final String name( ){
        return NAME;
    }


    @Override
    public final boolean isSupported( FluentEventType type ){
        return (APP_STATE_EVENT == type);
    }
        
    
    public final boolean isRunning( ){
        return (RUNNING == appState );
    }


    public final FluentState getState( ){
        return appState;
    }
    
    
    protected final void setState( FluentState newState ){
        FluentState old  = appState;
        this.appState    = newState;
        LOGGER.info( "Fluent application state changed from [{}] to [{}]", old, appState );
    }


    @Override
    public final boolean update( FluentEvent event ){
        
        ApplicationStateEvent sEvent = (ApplicationStateEvent) event;
        FluentState state            = sEvent.getState( );
        
        switch( state ){
            
            case RUNNING:
                setState( RUNNING );
                break;
                
            case STOPPING:
                setState( STOPPING );
                stop( );
                break;
                
            default:
                LOGGER.warn( "Unhandled state event [{}]{}", sEvent, NEWLINE );
        }
        
        return true;
    }


    @Override
    public final void start( ){

        try{

            long startTime  = currentMillis( );
            registerToInDispatcher( );
            startServices( );
            long timeTaken  = currentMillis( ) - startTime;
            
            LOGGER.info( "Successfully STARTED {} in [{}] ms.", name(), timeTaken );
            LOGGER.info( "************************************************************** {}", NEWLINE );

        }catch( Exception e ){

            appState        = STOPPED;
            
            LOGGER.error( "Fatal error while starting {}.", name() );
            LOGGER.error( "Exception: ", e );
            LOGGER.error( "************************************************************** {}", NEWLINE );

            System.exit( ZERO );
        }

    }

    
    protected final void registerToInDispatcher( ) throws Exception{
        for( FluentService service : services ) {
            if( FluentServiceType.IN_DISPATCH_SERVICE == service.getType( ) ) {
                FluentInDispatcher dispatcher   = (FluentInDispatcher)service;
                dispatcher.register( this );
            }
        }               
    }
    
    
    protected final void startServices( ) throws Exception {
        for( FluentService service : services ) {
            LOGGER.info( "Starting [{}] with starting rank [{}]", service.name( ), service.getType( ).getStartRank( ) );
            service.start( );
        }
    }    
    
        
    protected final Collection<FluentService> createServices( FluentConfig config ) throws Exception {
        
        FluentServiceFactory factory    = new FluentServiceFactory();
        List<String> serviceNames       = config.getRawConfig( ).getStringList( SERVICES_KEY );
        Set<FluentServiceType> types    = FluentServiceType.getServiceType(serviceNames);
        
        LOGGER.info( "Creating services for {}", types );
        Collection<FluentService> services= factory.create( config, types );
    
        return services;
    }
    
    
    @Override
    public final void stop( ) {

        try{

            for( FluentService service : services ) {
                service.stop( );
            }
            
            setState( STOPPING );
            LOGGER.info( "Successfully stopped {}.", NAME );

        }catch( Exception e ){
            LOGGER.error( "Exception while stopping {}.", NAME, e);
        }

    }
    
    @Override
    public final String toString( ){
        return config.getApplicationInfo( );
    }
    

    /*
    protected final void sendNewTESTStrategy( ){
    
        try{
            Thread.sleep( 2000 );
        }catch( InterruptedException e ){
            e.printStackTrace();
        }
        
        LOGGER.info(" =================================================");
        LOGGER.info(" Sending TEST Strategy!");
        LOGGER.info(" ================================================={}", NEWLINE);
        
        String strategyId           = "10.1";
        String strategyName         = "EDSpread";
        String strategyTrader       = "visingh";
        Side strategySide           = Side.BUY;
        int strategyLegCount        = 2;
        Exchange strategyExchange   = Exchange.CME;
        double strategySpread       = 0.15;
        
        int[] legQtys               = {100, 200};
        Side[] legSides             = {Side.BUY, Side.SELL};
        String[] legInstruments     = {"EDH6", "EDM6"};
        boolean[] legWorking        = {true, false};
        int[] legSlices             = {10, 20};
        InstrumentType[] legTypes   = {InstrumentType.FUTURES, InstrumentType.FUTURES};
                
        
        InEvent newStratgey         = new NewStrategyEvent( strategyId, strategyName, strategyTrader, strategySide, strategyLegCount, strategyExchange, strategySpread,
                                                            legQtys, legSides, legInstruments, legWorking, legSlices, legTypes );
        inDispatcher.enqueue( newStratgey );
    
    }
    */
    
}

