package fluent.framework.market.core;
import static fluent.framework.util.TimeUtils.*;
import static fluent.framework.util.Toolkit.*;
import static fluent.framework.util.Constants.*;

/*@formatter:off */
import java.util.*;

import fluent.framework.util.*;


public final class RouteDetails{

    private final String    route;
    private final String    open;
    private final long      openTimeMillis;
    private final String    close;
    private final long      closeTimeMillis;
    private final TimeZone  timeZone;
    private final int       speedLimit;
    
    public RouteDetails( String route, String open, String close, String timeZoneStr, String speedLimit ) throws Exception{

        verify( route, open, close, timeZoneStr, speedLimit );

        this.open               = open;
        this.close              = close;
        this.route              = route;
        this.timeZone           = TimeZone.getTimeZone( timeZoneStr );
        this.speedLimit         = Integer.parseInt( speedLimit );
        this.openTimeMillis     = getAdjustedOpen( open, close, timeZone, System.currentTimeMillis( ) );
        this.closeTimeMillis    = getAdjustedClose( open, close, timeZone, System.currentTimeMillis( ) );

    }


    private final void verify( String route, String open, String close, String timeZone, String speedimit ) throws Exception{

        String prefixMessage = "Route details is invalid as";

        if( isBlank(route)){
            throw new Exception( prefixMessage + " Route [" + route + "] is invalid." );
        }

        if( isBlank(open) ){
            throw new Exception( prefixMessage + " OpenTime [" + open + "] is invalid." );
        }

        if( isBlank(close) ){
            throw new Exception( prefixMessage + " CloseTime [" + close + "] is invalid." );
        }

        TimeUtils.parseTimeZone( timeZone );

        if( !isInteger( speedimit ) ){
            throw new Exception( prefixMessage + " Speedimit [" + speedimit + "] is invalid." );
        }

    }

    
    public final String getRoute( ){
        return route;
    }

    
    public final String getOpen( ){
        return open;
    }
    

    public final long getOpenMillis( ){
        return openTimeMillis;
    }

    
    public final String getClose( ){
        return close;
    }
    
    
    public final long getCloseMillis( ){
        return closeTimeMillis;
    }


    public final TimeZone getTimeZone( ){
        return timeZone;
    }


    public final int getSpeedLimit( ){
        return speedLimit;
    }

    
    public final boolean isExchangeOpen( ){
        return isExchangeOpen( System.currentTimeMillis( ) );
    }


    protected final boolean isExchangeOpen( long nowMillis ){
        return isOpen( nowMillis, openTimeMillis, closeTimeMillis );
    }


    @Override
    public String toString( ) {

        StringBuilder builder = new StringBuilder( TWO * SIXTY_FOUR );

        builder.append( "[ Route=" ).append( route );
        builder.append( ", Open=" ).append( openTimeMillis );
        builder.append( ", Close=" ).append( closeTimeMillis );
        builder.append( ", TimeZone=" ).append( timeZone.getID( ) );
        builder.append( ", SpeedLimit/sec=" ).append( speedLimit );
        builder.append( "]" );

        return builder.toString( );
    }


}
