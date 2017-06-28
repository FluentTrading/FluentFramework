package com.fluent.framework.reference.provider;
/*@formatter:off */

import com.fluent.framework.collection.*;
import com.fluent.framework.core.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.reference.core.*;
import com.fluent.framework.reference.parser.*;


public abstract class RefDataProvider{

    public enum Source{
        FILE,
        NETWORK;
    }
    
    
    private final Source source;
    private final RefDataParser parser; 
    private final FluentInDispatcher dispatcher;
    
    public RefDataProvider( Source source, RefDataParser parser, FluentInDispatcher dispatcher ){
        this.source     = source;
        this.parser     = parser;
        this.dispatcher = dispatcher;
    }
    
    public abstract void start( );
    
    
    protected final RefDataParser getParser( ){
        return parser;
    }
    

    public final Source getSource(){
        return source;
    }
    

    
    protected final boolean enqueue( RefDataEvent event ){
        return dispatcher.enqueue( event );
    }
    
    
    public final String createKey( Exchange exchange, String ricSymbol ){
        return RefDataParser.createKey( exchange, ricSymbol );
    }
        

    protected final static RefDataParser parser( FluentConfiguration config ){
        throw new RuntimeException( "Unimplemented");
    }
    
}
