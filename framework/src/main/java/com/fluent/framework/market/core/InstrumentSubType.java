package com.fluent.framework.market.core;

import static com.fluent.framework.market.core.InstrumentType.*;


public enum InstrumentSubType{

    OTR_TRASURY ( TREASURY ),
    ED_FUTURES  ( FUTURES ),
    TY_FUTURES  ( FUTURES ),
    UNKNOWN_SUB ( UNKNOWN );

    private final InstrumentType type;

    private InstrumentSubType( InstrumentType type ){
        this.type = type;
    }


    public final InstrumentType getType( ){
        return type;
    }


    public final static InstrumentSubType fromName( String name ) {

        for( InstrumentSubType sType : InstrumentSubType.values( ) ){
            if( sType.name( ).equalsIgnoreCase( name ) ){
                return sType;
            }
        }

        return UNKNOWN_SUB;
    }

}
