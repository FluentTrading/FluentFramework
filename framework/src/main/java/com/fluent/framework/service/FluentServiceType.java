package com.fluent.framework.service;

import java.util.*;

public enum FluentServiceType{

    IN_DISPATCH_SERVICE  ( 1 ),
    OUT_DISPATCH_SERVICE ( 5 ),
    
    METRONOME_SERVICE    ( 10 ),
    ADMIN_SERVICE        ( 15 ),
    STATE_PERSISTER      ( 20 ),
    METRICS_SERVICE      ( 25 ),
    
    REF_DATA_SERVICE     ( 30 ),
    MARKET_DATA_SERVICE  ( 35 ),
    ORDER_SERVICE        ( 40 ),
    
    UNKNOWN_SERVICE      ( -1 );
  
    
    private final int startRank;
        
    private FluentServiceType( int startRank ){
        this.startRank = startRank ;                
    }
    
    
    public final int getStartRank( ){
        return startRank;
    }
           
    
    public final static Set<FluentServiceType> getServiceType( List<String> serviceNames ){
        
        Set<FluentServiceType> types   = new LinkedHashSet<>( );
        
        for( String service : serviceNames ){
            types.add( getServiceType(service) );
        }
            
        return types;
    
    }
    
    
    public final static FluentServiceType getServiceType( String serviceName ){
        
        try{
            return FluentServiceType.valueOf( serviceName );
            
        }catch( Exception e ){
            throw new RuntimeException( serviceName + " is not a valid service type" );
        }
            
    }


}
