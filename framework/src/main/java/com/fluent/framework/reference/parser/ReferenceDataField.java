package com.fluent.framework.reference.parser;
/*@formatter:off */

import java.util.*;


public enum ReferenceDataField{
    
    SpreadTypeName, 
    InstrumentSubTypeName,
    RicSymbol,
    ExchangeSymbol,
    Expiry,
    TickSize,
    LotSize,
    PointValue;
    
    
    public final static List<ReferenceDataField> getList( List<String> columns ){
        
        List<ReferenceDataField> list = new ArrayList<>( );
        for( String col : columns ){
            ReferenceDataField field = ReferenceDataField.valueOf( col );
            list.add(  field );
        }
        
        return list;
    }
    
}