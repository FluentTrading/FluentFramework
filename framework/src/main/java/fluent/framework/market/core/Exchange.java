package fluent.framework.market.core;
import static fluent.framework.util.Constants.*;

/*@formatter:off */
import java.util.*;


public enum Exchange {

    CME                 ( "CME"         ),
    
    BTEC                ( "BTEC"        ),
    ESPEED              ( "ESPEED"      ),
    CASH_SMART_ROUTER   ( "UST_SMART",  BTEC, ESPEED ),

    ISWAP               ( "ISWAP"       ),
    DWEB                ( "DWEB"        ),
    SWAP_SMART_ROUTER   ( "SWAP_SMART", ISWAP, DWEB ),

    UNSUPPORTED         ( EMPTY         );

    private final String code;
    private final boolean isAggregate;
    private final Exchange[] underlying;

    private final static Map<String, Exchange> MAP;

    static{
        MAP = new HashMap<>( );
        for( Exchange exchange : Exchange.values( ) ){
            MAP.put( exchange.code, exchange );
            MAP.put( exchange.name( ), exchange );
        }
    }


    private Exchange( String code, Exchange ... underlying ){
        this.code           = code;
        this.underlying     = underlying;
        this.isAggregate    = ( underlying.length > ZERO );
    }


    public final String getCode( ) {
        return code;
    }


    public final boolean isAggregate( ) {
        return isAggregate;
    }


    public final Exchange[ ] getUnderlying( ) {
        return underlying;
    }


    public final static Exchange fromCode( String codeName ) {
        Exchange exchange = MAP.get( codeName );
        return (exchange == null) ? UNSUPPORTED : exchange;
    }


}
