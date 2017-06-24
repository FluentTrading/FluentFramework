package com.fluent.framework.reference.provider;
/*@formatter:off */

import com.fluent.framework.core.*;
import com.fluent.framework.events.core.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.reference.parser.*;

public abstract class RefDataProvider{

    public enum Source{
        FILE,
        NETWORK;
    }
    
    private volatile FluentEventListener listener;
    
    private final Source source;
    private final RefDataParser parser; 
    
    public RefDataProvider( Source source, RefDataParser parser ){
        this.source = source;
        this.parser = parser;        
    }
    
    public abstract void start( FluentEventListener listener );
    
    
    protected final RefDataParser getParser( ){
        return parser;
    }
    

    public final Source getSource(){
        return source;
    }
       
    
    public final String createKey( Exchange exchange, String ricSymbol ){
        return RefDataParser.createKey( exchange, ricSymbol );
    }
    
    protected final void setListener( FluentEventListener listener ){
        this.listener = listener;
    }
    
    protected final FluentEventListener getListener( ){
        return listener;
    }
    

    protected final static RefDataParser parser( FluentServices services ){
        throw new RuntimeException( "Unimplemented");
    }
    
}
