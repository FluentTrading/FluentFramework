package com.fluent.framework.market.instrument;


public enum SpreadType{

    OUTRIGHT,
    CALENDAR,
    BUTTERFLY,
    UNKNWON;

    
    public final static SpreadType fromName( String name ){
        for( SpreadType type : SpreadType.values( ) ){
            if( type.name( ).equalsIgnoreCase(name) ){
                return type;
            }
        }
        
        return UNKNWON;
    }
    
    
}
