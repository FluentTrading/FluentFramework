package com.fluent.framework.market.core;

import static com.fluent.common.util.Constants.*;

/*@formatter:off */
import java.util.*;


public enum Exchange {

    CME                 ( 1, "CME"         ),
    CBOT                ( 2, "CBOT"        ),
    BTEC                ( 3, "BTEC"        ),
    ESPEED              ( 4, "ESPEED"      ),
    UNSUPPORTED         ( 0, EMPTY         );

    private final int index;
    private final String code;

    private final static Map<String, Exchange> MAP;

    static{
        MAP = new HashMap<>( );
        for( Exchange exchange : Exchange.values( ) ){
            MAP.put( exchange.code, exchange );
            MAP.put( exchange.name( ), exchange );
        }
    }


    private Exchange( int index, String code ){
        this.index  = index;
        this.code   = code;        
    }


    public final String getCode( ) {
        return code;
    }


    public final int getIndex( ) {
        return index;
    }


    public final static Exchange fromCode( String codeName ) {
        Exchange exchange = MAP.get( codeName );
        return (exchange == null) ? UNSUPPORTED : exchange;
    }


}
