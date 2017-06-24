package com.fluent.framework.reference.provider;
/*@formatter:off */
import org.slf4j.*;
import com.fluent.framework.core.*;
import com.fluent.framework.events.core.*;
import com.fluent.framework.reference.parser.*;


public final class RefDataNetworkProvider extends RefDataProvider{

    private final static String NAME        = RefDataNetworkProvider.class.getSimpleName( );
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );
  
    
    public RefDataNetworkProvider( FluentServices services ){
        this( connection(services), parser(services) );
        
    }
    
    public RefDataNetworkProvider( String connection, RefDataParser parser ){
        super( Source.NETWORK, parser );
    }
    
    
    @Override
    public final void start( FluentEventListener listener ){
        if( listener == null ){
            throw new RuntimeException( "No FluentEventListener configured for RefDataEvent");
        }
        
        setListener( listener );
        LOGGER.info( "Registered [{}] as a listener for RefDataEvent updates.", listener.name( ) );
        
        connect( );
        
    }

    
    public final void connect( ){
     //Here we initiate the connection to the external system
     //As the data arraives we parse and feed to to RefeDataManager via getListener().update( event )
    }

    
    protected final static String connection( FluentServices services ){
        throw new RuntimeException( "Unimplemented");
    }

    
    
}
