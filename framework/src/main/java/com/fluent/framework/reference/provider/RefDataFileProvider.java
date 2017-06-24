package com.fluent.framework.reference.provider;
/*@formatter:off */
import org.slf4j.*;
import java.util.*;

import java.util.concurrent.atomic.*;
import com.fluent.framework.core.*;
import com.fluent.framework.events.core.*;
import com.fluent.framework.util.*;
import com.fluent.framework.reference.core.*;
import com.fluent.framework.reference.parser.*;


public final class RefDataFileProvider extends RefDataProvider{
    
    private final String fileName;
        
    private final static String NAME        = RefDataFileProvider.class.getSimpleName( );
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );
    
    
    public RefDataFileProvider( FluentServices services ){
        this( fileName(services), parser(services) );
        
    }
    
    public RefDataFileProvider( String fileName, RefDataParser parser ){
        super( Source.FILE, parser );
        
        this.fileName   = fileName;        
    }
    
    
    @Override
    public final void start( FluentEventListener listener ){
        if( listener == null ){
            throw new RuntimeException( "No FluentEventListener configured for RefDataEvent");
        }
        
        setListener( listener );
        LOGGER.info( "Registered [{}] as a listener for updates.", listener.name( ) );
        
        load( );
        
    }
       
    
    protected final void load( ){
                
        try{
            
            AtomicInteger counter     = new AtomicInteger( );
            List<String> dataLines    = FluentIOUtil.loadFile( fileName );
            
            for( String dataLine : dataLines ){
                int instrumentIndex   = counter.getAndIncrement( );
                RefDataEvent refData  = getParser( ).parse( instrumentIndex, dataLine );
                if( refData == null ) continue;
                
                getListener( ).update( refData );              
            }
            
        }catch( Exception e ){
            LOGGER.warn( "FAILED to parse reference data.", e );
        }
                
    }
    
    
    protected final static String fileName( FluentServices services ){
        throw new RuntimeException( "Unimplemented");
    }

    
    
    
    
}
