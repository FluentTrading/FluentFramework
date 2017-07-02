package fluent.framework.reference.provider;
/*@formatter:off */

import java.util.*;
import com.typesafe.config.*;

import fluent.framework.collection.*;
import fluent.framework.reference.core.*;
import fluent.framework.reference.parser.*;



public abstract class ReferenceDataProvider{
   
    private final ReferenceDataSource source;
    private final ReferenceDataParser parser; 
    private final FluentInDispatcher dispatcher;
    
    protected final static String DELIMITER_KEY   = "delimiter";
    protected final static String COLUMNS_KEY     = "columns";
    
    
    public ReferenceDataProvider( ReferenceDataSource source, ReferenceDataParser parser, FluentInDispatcher dispatcher ){
        this.source     = source;
        this.parser     = parser;
        this.dispatcher = dispatcher;
    }
    
    public abstract void start( );
    
    
    protected final ReferenceDataParser getParser( ){
        return parser;
    }
    

    public final ReferenceDataSource getSource(){
        return source;
    }

    
    protected final boolean enqueue( ReferenceDataEvent event ){
        return dispatcher.enqueue( event );
    }
            
    
    public final static String parseDelimiter( Config fileConfig ){
        return fileConfig.getString( DELIMITER_KEY );
    }
    
    
    public final static List<String> parseColumns( Config fileConfig ){
        return fileConfig.getStringList( COLUMNS_KEY );
    }

    
}
