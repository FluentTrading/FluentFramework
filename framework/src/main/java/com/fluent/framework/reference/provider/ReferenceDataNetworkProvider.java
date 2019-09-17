package com.fluent.framework.reference.provider;
/*@formatter:off */

import org.slf4j.*;

import com.fluent.framework.collection.*;
import com.fluent.framework.core.*;
import com.fluent.framework.reference.parser.*;
import com.typesafe.config.*;

import static com.fluent.framework.reference.provider.ReferenceDataSource.*;


public final class ReferenceDataNetworkProvider extends ReferenceDataProvider{

    private final static String NAME        = ReferenceDataNetworkProvider.class.getSimpleName( );
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );
  
    
    public ReferenceDataNetworkProvider( Config refConfig, ReferenceDataParser parser, FluentInDispatcher dispatcher ){
        super( NETWORK_SOURCE, parser, dispatcher );
    }
    
    
    @Override
    public final void start(  ){
        connect( );
    }

    
    public final void connect( ){
     //Here we initiate the connection to the external system
     //As the data arraives we parse and feed to to RefeDataManager via getListener().update( event )
    }

    
    protected final static String connection( FluentConfig config ){
        throw new RuntimeException( "Unimplemented");
    }


    public final static Config parseNetworkConfig( Config refConfig ){
        return refConfig.getConfig( NETWORK_SOURCE.name( ) );
    }

    
    
}
