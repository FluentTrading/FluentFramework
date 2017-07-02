package fluent.framework.service;

import java.util.*;

import fluent.framework.core.*;

public enum FluentServiceType{

    IN_DISPATCH_SERVICE  ( 1 ),
    OUT_DISPATCH_SERVICE ( 2 ),
    
    METRONOME_SERVICE    ( 3 ),
    STATE_PERSISTER      ( 4 ),
    REF_DATA_SERVICE     ( 5 ),
    
    ORDER_SERVICE        ( 6 ),
    MARKET_DATA_SERVICE  ( 7 ),
    ADMIN_SERVICE        ( 8 ),
    UNKNOWN_SERVICE      ( -1 );
  
    
    private final int startRank;
    private final static String SERVICES_KEY = "fluent.services";
    
    private FluentServiceType( int startRank ){
        this.startRank = startRank ;                
    }
    
    
    public final int getStartRank( ){
        return startRank;
    }
           
    
    public final static Set<FluentServiceType> getServiceType( FluentConfig config ){
        
        Set<FluentServiceType> types   = new LinkedHashSet<>( );
        List<String> services          = config.getRawConfig( ).getStringList( SERVICES_KEY );
        
        for( String service : services ){
            FluentServiceType type  = getServiceType( service );
            boolean isInvalid       = ( UNKNOWN_SERVICE == type || type == null );
            if( isInvalid ) continue;
            
            types.add( type );
        }
            
        return types;
    }
    
    
    
    
    
    public final static FluentServiceType getServiceType( String name ){
        
        FluentServiceType type      = null;
        try{
            type  = FluentServiceType.valueOf( name );
        }catch( Exception e ){
            throw new RuntimeException( name + " is not a valid service type" );
        }
        
        return type;
    
    }


}
