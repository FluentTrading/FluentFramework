package com.fluent.framework.reference.provider;
/*@formatter:off */
import org.slf4j.*;
import java.util.*;

import com.fluent.framework.collection.*;
import com.fluent.framework.util.*;
import com.typesafe.config.*;
import com.fluent.framework.reference.core.*;
import com.fluent.framework.reference.parser.*;


public final class RefDataFileProvider extends RefDataProvider{
    
    private final String fileName;
        
    private final static String NAME        = RefDataFileProvider.class.getSimpleName( );
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );
    
    
    public RefDataFileProvider( Config refConfig, RefDataParser parser, FluentInDispatcher dispatcher ){
        this( refConfig.getString("fileName"), parser, dispatcher );
    }
    
    public RefDataFileProvider( String fileName, RefDataParser parser, FluentInDispatcher dispatcher ){
        super( Source.FILE, parser, dispatcher );
        
        this.fileName   = fileName;
                
    }
    
    
    @Override
    public final void start( ){
        load( );
    }
       
    
    protected final void load( ){
                
        try{
            
            List<String> dataLines    = FluentIOUtil.loadFile( fileName );
            
            for( String dataLine : dataLines ){
                RefDataEvent refData  = getParser( ).parse( dataLine );
                if( refData == null ) continue;
                
                enqueue( refData );              
            }
            
        }catch( Exception e ){
            LOGGER.warn( "FAILED to parse reference data.", e );
        }
                
    }
    
    

    
    
    
}
