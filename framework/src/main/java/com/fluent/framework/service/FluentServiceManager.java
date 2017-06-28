package com.fluent.framework.service;

//@formatter:off
import org.slf4j.*;
import java.util.*;

import com.fluent.framework.core.*;
import com.fluent.framework.events.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.order.*;
import com.fluent.framework.reference.core.*;
import com.fluent.framework.reference.provider.*;

import com.fluent.framework.admin.core.*;
import com.fluent.framework.admin.events.*;
import com.fluent.framework.collection.*;

import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.util.FluentTimeUtil.*;
import static com.fluent.framework.core.FluentAppState.*;
import static com.fluent.framework.events.FluentEventType.*;
import static com.fluent.framework.service.FluentServiceType.*;


//1) Should this class hold all the services?
//2) Should we wrap all the services in a ServiceLocator class
//3) All the service claases should implements the FluentService interface
//4. How can we add more services to it?

public final class FluentServiceManager implements FluentEventListener, FluentLifecycle{

    private volatile FluentAppState appState;
    
    private final FluentConfiguration config;
    private final Map<FluentServiceType, FluentService> serviceMap;
    
    private final static String NAME   = FluentServiceManager.class.getSimpleName( );
    private final static Logger LOGGER = LoggerFactory.getLogger( NAME );


    public FluentServiceManager( String fileName ) throws Exception{
        this.appState   = INITIALIZING;
        this.serviceMap = new LinkedHashMap<> ();
        this.config     = new FluentConfiguration( fileName );        
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


    public final FluentAppState getState( ){
        return appState;
    }
    
    
    protected final void setState( FluentAppState newState ){
        FluentAppState old  = appState;
        this.appState       = newState;
        LOGGER.info( "Fluent application state changed from [{}] to [{}]", old, appState );
    }


    @Override
    public final boolean update( FluentEvent event ){
        
        ApplicationStateEvent sEvent    = (ApplicationStateEvent) event;
        FluentAppState state            = sEvent.getState( );
        
        switch( state ){
            
            case RUNNING:
                setState( RUNNING );
                break;
                
            case STOPPING:
                setState( STOPPING );
                stop( );
                break;
                
            default:
                LOGGER.info( "Received State event to STOP [{}]{}", sEvent, NEWLINE );
        }
        
        return true;
    }


    @Override
    public final void start( ){

        try{

            long startTime  = currentMillis( );
            
            createServices( );
            startServices( );
            register( );

            long timeTaken  = currentMillis( ) - startTime;
            
            LOGGER.info( "Successfully STARTED {} in [{}] ms.", name(), timeTaken );
            LOGGER.info( "************************************************************** {}", NEWLINE );

        }catch( Exception e ){

            appState        = STOPPED;
            
            LOGGER.error( "Fatal error while starting {}.", name() );
            LOGGER.error( "Exception: ", e );
            LOGGER.info( "************************************************************** {}", NEWLINE );

            System.exit( ZERO );
        }

    }

    
    protected final void createServices( ) throws Exception{

        Collection<FluentServiceType> types  = getServiceType( config );
        LOGGER.info( "Creating services for {}", types );
        
        for( FluentServiceType type : types ){
            
            switch( type ){
                    
                case IN_DISPATCH_SERVICE:
                    createInDispatcher( config);
                    break;
                    
                case OUT_DISPATCH_SERVICE:
                    createOutDispatcher( config );
                    break;
                    
                case METRONOME_SERVICE:
                    createMetroService( config );
                    break;
                                
                case REF_DATA_SERVICE:
                    createRefDataManager( config );
                    break;
         
                case MARKET_DATA_SERVICE:
                    createMarketDataManager( config );
                    break;
                    
                case ORDER_SERVICE:
                    createOrderManager( config );
                    break;
                                
                case STATE_PERSISTER:
                    createStatePersister( config );
                    break;
                    
                case ADMIN_CLIENT:
                    createAdminClient( config );
                    break;
                
                
                default:
                    throw new RuntimeException( "Unknow service type " + type );
            }
        
        }
        
        LOGGER.info( "{}", NEWLINE );
            
    }
    

    protected final void startServices( ) throws Exception {
        
        LOGGER.info( "Attempting to start all configured services.");
        
        for( FluentService service : serviceMap.values( ) ){
            service.start( );
        }
                
    }


    protected final void register( ) throws Exception{
        FluentService service           = serviceMap.get( IN_DISPATCH_SERVICE );
        FluentInDispatcher dispatcher   = (FluentInDispatcher)service;
        dispatcher.register( this );
        
    }
    

    protected final FluentService get( FluentServiceType type ){
        return serviceMap.get( type );
    }
    
 
    protected final void store( FluentService service ){
        FluentServiceType type  = service.getType( );
        String className        = service.getClass( ).getSimpleName( );
        boolean duplicateFound  = serviceMap.containsKey( type );
        if( duplicateFound ){
            throw new RuntimeException( "Duplicate service was configured for " + type );
        }
        
        serviceMap.put( type, service );
        LOGGER.info( "Created service for [{}], implemnting class [{}]", type, className );
        
    }
    
    
    protected final FluentService createInDispatcher( FluentConfiguration config ){
        FluentService service   = new FluentInDispatcher( config );
        store( service );
                
        return service; 
    }


    protected final FluentService createOutDispatcher( FluentConfiguration config){
        FluentService service   = new FluentOutDispatcher( config );
        store( service );
        
        return service; 
    }


    protected final FluentService createRefDataManager( FluentConfiguration config ){
      
        int intitialSize        = 1000; //config.parse
        FluentInDispatcher in   = ( FluentInDispatcher) serviceMap.get( IN_DISPATCH_SERVICE );
        RefDataProvider provider= RefDataProviderFactory.create( config, in );
        FluentService service   = new RefDataManager( intitialSize, provider, in );
        
        store( service );
        return service;
    }
    
    
    protected final FluentService createMetroService( FluentConfiguration config ){
        FluentInDispatcher in   = ( FluentInDispatcher) serviceMap.get( IN_DISPATCH_SERVICE );
        FluentService metro     = new MetronomeService( config, in );
        
        store( metro );
        return metro;
    }


    protected final FluentService createMarketDataManager( FluentConfiguration config ) throws Exception{
        FluentInDispatcher in   = ( FluentInDispatcher) serviceMap.get( IN_DISPATCH_SERVICE );
        FluentService mdManager = new MarketDataManager( config, in );
        
        store( mdManager );
        return mdManager;
    }

    
    protected final FluentService createOrderManager( FluentConfiguration config ){
        FluentInDispatcher in   = ( FluentInDispatcher) serviceMap.get( IN_DISPATCH_SERVICE );
        FluentService oManager  = new OrderManager( config, in );
        
        store( oManager );
        return oManager;
    }

    
    protected final FluentService createStatePersister( FluentConfiguration config ){
        
        return null;
    }
 
    
    protected final void createAdminClient( FluentConfiguration config ){
        
    }
    
    
    @Override
    public final String toString( ){
        return config.getConfigInfo( );
    }
    
    
    protected final void stopServices( ) throws Exception {

        LOGGER.info( "Attemtping to stop all configured services");
        
        for( FluentService service : serviceMap.values( ) ){
            service.stop( );
        }
        
    }


    @Override
    public void stop( ) {

        try{

            stopServices( );
            setState( STOPPING );
            LOGGER.debug( "Successfully stopped {}.", NAME );

        }catch( Exception e ){
            LOGGER.warn( "Exception while stopping {}.", NAME );
            LOGGER.warn( "Exception", e );
        }

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

