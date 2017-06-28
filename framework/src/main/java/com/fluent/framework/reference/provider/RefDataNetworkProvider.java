package com.fluent.framework.reference.provider;
/*@formatter:off */

import org.slf4j.*;

import com.fluent.framework.core.*;
import com.fluent.framework.collection.*;
import com.fluent.framework.reference.parser.*;
import com.typesafe.config.*;


public final class RefDataNetworkProvider extends RefDataProvider{

    private final static String NAME        = RefDataNetworkProvider.class.getSimpleName( );
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );
  
    
    
    public RefDataNetworkProvider( Config refConfig, RefDataParser parser, FluentInDispatcher dispatcher ){
        super( Source.NETWORK, parser, dispatcher );
    }
    
    
    @Override
    public final void start(  ){
        connect( );
    }

    
    public final void connect( ){
     //Here we initiate the connection to the external system
     //As the data arraives we parse and feed to to RefeDataManager via getListener().update( event )
    }

    
    protected final static String connection( FluentConfiguration config ){
        throw new RuntimeException( "Unimplemented");
    }

    
    
}
