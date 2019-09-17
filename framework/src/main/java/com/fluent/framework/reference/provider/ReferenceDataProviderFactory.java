package com.fluent.framework.reference.provider;
/*@formatter:off */

import org.slf4j.*;
import java.util.*;

import com.fluent.framework.collection.*;
import com.fluent.framework.reference.parser.*;
import com.typesafe.config.*;


public final class ReferenceDataProviderFactory{
    
    
    private final static String NAME    = ReferenceDataProviderFactory.class.getSimpleName( );
    private final static Logger LOGGER  = LoggerFactory.getLogger( NAME );
        
    
    public final static ReferenceDataProvider createProvider( Config refConfig, FluentInDispatcher in ){
        
        try{
        
            ReferenceDataSource source    = ReferenceDataSource.parseSource( refConfig );
        
            switch( source ){
            
                case FILE_SOURCE:
                    return createFileProvider( refConfig, in );
                                
                case NETWORK_SOURCE:
                    return createNetworkProvider( refConfig, in );
                                                
                default:
                    throw new RuntimeException( "Unsupported Reference data provider: " + source );                    
            }
            
            
        }catch( Exception e ){
            LOGGER.error("FAILED to create reference data provider.", e );
            throw new RuntimeException( );
        }
        
    }
           
    
    protected final static ReferenceDataProvider createFileProvider( Config refConfig, FluentInDispatcher in ){
        
        Config fileConfig        = ReferenceDataFileProvider.parseFileConfig( refConfig );
        String fileName          = ReferenceDataFileProvider.parseFileName( fileConfig );
        String delimiter         = ReferenceDataFileProvider.parseDelimiter( fileConfig );
        List<String> columns     = ReferenceDataFileProvider.parseColumns( fileConfig );
        
        ReferenceDataParser parser     = createParser( delimiter, columns );
        ReferenceDataProvider provider = new ReferenceDataFileProvider( fileName, parser, in );
        
        return provider;
    }
    
    
    protected final static ReferenceDataProvider createNetworkProvider( Config refConfig, FluentInDispatcher in ){
        Config networkConfig     = ReferenceDataNetworkProvider.parseNetworkConfig( refConfig );
        ReferenceDataParser parser     = null;
        ReferenceDataProvider provider = new ReferenceDataNetworkProvider( refConfig, parser, in );
        return provider;
    
    }
        
    
    protected final static ReferenceDataParser createParser( String delimiter, List<String> columns ){
        
        List<ReferenceDataField> list = ReferenceDataField.getList( columns );
        ReferenceDataParser parser    = new ReferenceDataParser( delimiter, list );
        
        return parser;
    }

}