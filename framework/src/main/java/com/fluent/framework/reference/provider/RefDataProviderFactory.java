package com.fluent.framework.reference.provider;
/*@formatter:off */

import org.slf4j.*;

import java.util.*;

import com.typesafe.config.*;
import com.fluent.framework.core.*;
import com.fluent.framework.service.*;
import com.fluent.framework.collection.*;
import com.fluent.framework.reference.parser.*;
import com.fluent.framework.reference.provider.RefDataProvider.*;

import static com.fluent.framework.core.FluentConfiguration.*;


public final class RefDataProviderFactory{
    
    private final static String REFERENCE_CONFIG    = TOP_SECTION_KEY + "referenceData";
    private final static String NAME                = RefDataProviderFactory.class.getSimpleName( );
    private final static Logger LOGGER              = LoggerFactory.getLogger( NAME );
        
    
    public final static RefDataProvider create( FluentConfiguration config, FluentInDispatcher in ){
        
        try{
        
            Config refConfig    = config.getSectionConfig( REFERENCE_CONFIG );
            Source source       = parseSource( refConfig );
        
            switch( source ){
            
                case FILE:
                    return createFileProvider( refConfig, in );
                                
                case NETWORK:
                    return createNetworkProvider( refConfig, in );
                                                
                default:
                    throw new RuntimeException( "Unsupported Reference data provider: " + source );                    
            }
            
            
        }catch( Exception e ){
            LOGGER.error("FAILED to create reference data provider.", e );
            throw new RuntimeException( );
        }
        
    }
   

    protected static final Source parseSource( Config config ){
        String sourceStr        = config.getString( "source" );
        Source source           = Source.valueOf( sourceStr );
        
        return source;
    }
        
    
    protected final static RefDataProvider createFileProvider( Config refConfig, FluentInDispatcher in ){
        RefDataParser parser     = createParser( refConfig );
        RefDataProvider provider = new RefDataFileProvider( refConfig, parser, in );
        
        return provider;
    }
    
    
    protected final static RefDataProvider createNetworkProvider( Config refConfig, FluentInDispatcher in ){
        RefDataParser parser     = createParser( refConfig );
        RefDataProvider provider = new RefDataNetworkProvider( refConfig, parser, in );
        return provider;
    }
    
    
    
    protected final static RefDataParser createParser( Config refConfig ){
        
        List<RefDataField> list     = new ArrayList<>();
        String delimiter        = refConfig.getString( "delimiter" );
        List<String> columsn    = refConfig.getStringList( "columns" );
     
        for( String col : columsn ){
            RefDataField field = RefDataField.valueOf( col );
            list.add(  field );
        }
        
        RefDataParser parser    = new RefDataDefaultParser( delimiter, list );
        return parser;
    }

}