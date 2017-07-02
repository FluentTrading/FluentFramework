package fluent.framework.reference.provider;
/*@formatter:off */

import static fluent.framework.reference.provider.ReferenceDataSource.*;

import org.slf4j.*;

import com.typesafe.config.*;

import fluent.framework.collection.*;
import fluent.framework.core.*;
import fluent.framework.reference.parser.*;


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
