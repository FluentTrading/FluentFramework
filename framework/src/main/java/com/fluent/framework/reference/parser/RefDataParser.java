package com.fluent.framework.reference.parser;
/*@formatter:off */

import com.fluent.framework.market.core.*;
import com.fluent.framework.reference.core.*;

import static com.fluent.framework.util.FluentUtil.*;


public abstract class RefDataParser{
    
    public abstract RefDataEvent parse( int instIndex, String line );
        
    public final static String createKey( Exchange exchange, String ricSymbol ){
        return exchange + DOT + ricSymbol;        
    }
    

}