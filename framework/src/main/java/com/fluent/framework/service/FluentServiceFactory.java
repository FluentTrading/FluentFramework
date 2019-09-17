package com.fluent.framework.service;
//@formatter:off

import org.slf4j.*;

import java.util.*;

import com.fluent.framework.admin.*;
import com.fluent.framework.collection.*;
import com.fluent.framework.core.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.metronome.*;
import com.fluent.framework.order.*;
import com.fluent.framework.reference.core.*;
import com.fluent.framework.reference.provider.*;
import com.typesafe.config.*;

import static com.fluent.framework.service.FluentServiceType.*;


//1) Should this class hold all the services?
//2) Should we wrap all the services in a ServiceLocator class
//3) All the service claases should implements the FluentService interface
//4. How can we add more services to it?

public final class FluentServiceFactory{

    private final static String NAME        = FluentServiceFactory.class.getSimpleName( );
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );
    
    
    public final Collection<FluentService> create( FluentConfig config, Collection<FluentServiceType> types ) throws Exception{
    
        Map<Integer, FluentService> services = new TreeMap<>();
        
        for( FluentServiceType type : types ){
            
            switch( type ){
                    
                case IN_DISPATCH_SERVICE:
                    createInDispatcher( config, services );
                    break;
                    
                case OUT_DISPATCH_SERVICE:
                    createOutDispatcher( config, services );
                    break;
                    
                case METRONOME_SERVICE:
                    createMetroService( config, services );
                    break;
                                
                case REF_DATA_SERVICE:
                    createRefDataManager( config, services );
                    break;
         
                case MARKET_DATA_SERVICE:
                    createMarketDataManager( config, services );
                    break;
                    
                case ORDER_SERVICE:
                    createOrderManager( config, services );
                    break;
                                
                case STATE_PERSISTER:
                    createStatePersister( config, services );
                    break;
                    
                case ADMIN_SERVICE:
                    createAdminClient( config, services );
                    break;
                
                case METRICS_SERVICE:
                    createMetricsClient( config, services );
                    break;
                    
                default:
                    throw new RuntimeException( "Unknow service type " + type );
            }
        
        }
        
        return services.values( );
        
    }
    
    
    protected final FluentService createInDispatcher( FluentConfig config, Map<Integer, FluentService> serviceMap ){
        FluentService service   = new FluentInDispatcher( );
        store( service, serviceMap );
                
        return service; 
    }


    protected final FluentService createOutDispatcher( FluentConfig config, Map<Integer, FluentService> serviceMap){
        FluentService service   = new FluentOutDispatcher( );
        store( service, serviceMap );
        
        return service; 
    }


    protected final FluentService createRefDataManager( FluentConfig config, Map<Integer, FluentService> serviceMap ){
        Config refConfig        = ReferenceDataManager.parseReferenceConfig( config );
        int capacity            = ReferenceDataManager.parseInitialSize( refConfig );
                
        FluentInDispatcher in   = (FluentInDispatcher) serviceMap.get( IN_DISPATCH_SERVICE.getStartRank( ) );
        ReferenceDataProvider pr= ReferenceDataProviderFactory.createProvider( refConfig, in );
        FluentService service   = new ReferenceDataManager( capacity, pr, in );
        
        store( service, serviceMap );
        return service;
    }
    
    
    protected final FluentService createMetroService( FluentConfig config, Map<Integer, FluentService> serviceMap ){
        FluentInDispatcher in   = ( FluentInDispatcher) serviceMap.get( IN_DISPATCH_SERVICE.getStartRank( ) );
        FluentService metro     = new MetronomeService( config, in );
        
        store( metro, serviceMap );
        return metro;
    }


    protected final FluentService createMarketDataManager( FluentConfig config, Map<Integer, FluentService> serviceMap ) throws Exception{
        FluentInDispatcher in   = ( FluentInDispatcher) serviceMap.get( IN_DISPATCH_SERVICE.getStartRank( ) );
        FluentService mdManager = new MarketDataManager( config, in );
        
        store( mdManager, serviceMap );
        return mdManager;
    }

    
    protected final FluentService createOrderManager( FluentConfig config, Map<Integer, FluentService> serviceMap ){
        FluentInDispatcher in   = ( FluentInDispatcher) serviceMap.get(IN_DISPATCH_SERVICE.getStartRank( ));
        FluentService oManager  = new OrderManager( config, in );
        
        store( oManager, serviceMap );
        return oManager;
    }

    
    protected final FluentService createAdminClient( FluentConfig config, Map<Integer, FluentService> serviceMap ) throws Exception{
        Config adminConfig      = AdminClient.parseAdminConfig( config );
        int jmxPort             = AdminClient.parsePort( adminConfig );
        
        AdminService adminServer = new AdminService( jmxPort );
        
        store( adminServer, serviceMap );
        return adminServer;
        
    }
    
    
    protected final FluentService createStatePersister( FluentConfig config, Map<Integer, FluentService> serviceMap ){
        LOGGER.warn( "StatePersister is not yet implemented!");
        return null;
    }
    
    
    protected final FluentService createMetricsClient( FluentConfig config, Map<Integer, FluentService> serviceMap ){
        LOGGER.warn( "MetricsClient is not yet implemented!");
        return null;
    }
    
    
    protected final void store( FluentService service, Map<Integer, FluentService> serviceMap ){
        FluentServiceType type  = service.getType( );
        String className        = service.getClass( ).getSimpleName( );
        boolean duplicateFound  = serviceMap.containsKey( type.getStartRank( ) );
        if( duplicateFound ){
            throw new RuntimeException( "Duplicate service was configured for " + type );
        }
        
        serviceMap.put( type.getStartRank( ), service );
        LOGGER.info( "Created service [{}] with [{}] as implementing class.", type, className );
        
    }
    
    
}

