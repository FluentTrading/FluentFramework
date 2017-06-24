package com.fluent.framework.market.core;
/*@formatter:off */
import java.util.*;

import com.fluent.framework.core.*;
import com.fluent.framework.util.*;

import static com.fluent.framework.util.FluentTimeUtil.*;
import static com.fluent.framework.util.FluentToolkit.*;
import static com.fluent.framework.util.FluentUtil.*;


public final class ExchangeDetails{

    private final Exchange  exchange;
    private final String    open;
    private final long      openTimeMillis;
    private final String    close;
    private final long      closeTimeMillis;
    private final TimeZone  timeZone;
    private final int       speedLimit;
    
    public ExchangeDetails( String key, String open, String close, String timeZoneStr, String speedLimit ) throws FluentException{

        verify( key, open, close, timeZoneStr, speedLimit );

        this.open               = open;
        this.close              = close;
        this.exchange           = Exchange.fromCode( key );
        this.timeZone           = TimeZone.getTimeZone( timeZoneStr );
        this.speedLimit         = Integer.parseInt( speedLimit );
        this.openTimeMillis     = getAdjustedOpen( open, close, timeZone, System.currentTimeMillis( ) );
        this.closeTimeMillis    = getAdjustedClose( open, close, timeZone, System.currentTimeMillis( ) );

    }


    private final void verify( String key, String open, String close, String timeZone, String speedimit ) throws FluentException{

        String prefixMessage = "Exchange details is invalid as";

        if( Exchange.UNSUPPORTED == Exchange.fromCode( key ) ){
            throw new FluentException( prefixMessage + " Exchange [" + key + "] is invalid." );
        }

        if( isBlank(open) ){
            throw new FluentException( prefixMessage + " OpenTime [" + open + "] is invalid." );
        }

        if( isBlank(close) ){
            throw new FluentException( prefixMessage + " CloseTime [" + close + "] is invalid." );
        }

        FluentTimeUtil.parseTimeZone( timeZone );

        if( !isInteger( speedimit ) ){
            throw new FluentException( prefixMessage + " Speedimit [" + speedimit + "] is invalid." );
        }

    }

    
    public final Exchange getExchange( ){
        return exchange;
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

        builder.append( "[Exchange=" ).append( exchange );
        builder.append( ", Open=" ).append( openTimeMillis );
        builder.append( ", Close=" ).append( closeTimeMillis );
        builder.append( ", TimeZone=" ).append( timeZone.getID( ) );
        builder.append( ", SpeedLimit/sec=" ).append( speedLimit );
        builder.append( "]" );

        return builder.toString( );
    }


}
