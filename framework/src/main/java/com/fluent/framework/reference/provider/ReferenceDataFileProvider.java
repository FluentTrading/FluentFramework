package com.fluent.framework.reference.provider;
/*@formatter:off */
import org.slf4j.*;

import java.util.*;

import com.fluent.common.util.*;
import com.fluent.framework.collection.*;
import com.fluent.framework.reference.core.*;
import com.fluent.framework.reference.parser.*;
import com.typesafe.config.*;

import static com.fluent.framework.reference.provider.ReferenceDataSource.*;


public final class ReferenceDataFileProvider extends ReferenceDataProvider{
    
    private final String fileName;
        
    private final static String FILENAME_KEY    = "fileName";
    private final static String NAME            = ReferenceDataFileProvider.class.getSimpleName( );
    private final static Logger LOGGER          = LoggerFactory.getLogger( NAME );
    
        
    public ReferenceDataFileProvider( String fileName, ReferenceDataParser parser, FluentInDispatcher dispatcher ){
        super( FILE_SOURCE, parser, dispatcher );
        
        this.fileName   = fileName;
                
    }
    
    
    @Override
    public final void start( ){
        load( );
    }
       
    
    protected final void load( ){
                
        try{
            
            List<String> dataLines    = IOUtils.loadFile( fileName );
            
            for( String dataLine : dataLines ){
                ReferenceDataEvent refData  = getParser( ).parse( dataLine );
                if( refData == null ) continue;
                
                enqueue( refData );              
            }
            
        }catch( Exception e ){
            LOGGER.warn( "FAILED to parse reference data.", e );
        }
                
    }
    
    
    public final static String parseFileName( Config fileConfig ){
        return fileConfig.getString( FILENAME_KEY );
    }
    

    public final static Config parseFileConfig( Config refConfig ){
        return refConfig.getConfig( FILE_SOURCE.name( ) );
    }
        
    
    
}
