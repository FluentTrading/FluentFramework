package fluent.framework.util;

import static fluent.framework.util.Constants.*;

import java.text.*;
import java.util.*;


public final class TimeUtils{

    protected TimeUtils( ){}

    public final static String TODAY = new SimpleDateFormat( "yyyy.MM.dd" ).format( new Date( ) );


    public final static long currentNanos( ){
        return System.nanoTime( );
    }


    public final static long currentMillis( ){
        return System.currentTimeMillis( );
    }


    public final static TimeZone parseTimeZone( String timeZoneStr ) throws Exception {

        TimeZone timeZone = TimeZone.getTimeZone( timeZoneStr );
        if( !timeZone.getID( ).equals( timeZoneStr ) ){
            throw new Exception( "TimeZone [" + timeZoneStr + "] is invalid." );
        }

        return timeZone;
    }


    public final static boolean isOpen( long nowMillis, long openMillis, long closeMillis ) {
        return (nowMillis > openMillis && nowMillis < closeMillis);
    }


    public final static long getAdjustedOpen( String open, String close, TimeZone tZone, long nowMillis ) {

        long openTimeInMillis = getTimeInMillis( open, tZone );
        long closeTimeInMillis = getTimeInMillis( close, tZone );
        boolean openLessThanClose = openTimeInMillis <= closeTimeInMillis;

        // Regular Case: Open is less than close (Open close happens on the same day)
        // Open: 09:00:00 - Close: 17:00:00
        if( openLessThanClose )
            return openTimeInMillis;

        // Irregular Case: Open is greater than close
        // Is time right now before close?
        // If yes, that means Open was yesterday
        // Open: 09:00:00 - Close: 17:00:00

        boolean nowBeforeClose = (nowMillis < closeTimeInMillis);
        if( nowBeforeClose ){
            return openTimeInMillis - _24_HOURS_IN_MILLIS;
        }

        return openTimeInMillis;
    }


    public final static long getAdjustedClose( String open, String close, TimeZone tZone, long nowMillis ) {

        long openTimeInMillis = getTimeInMillis( open, tZone );
        long closeTimeInMillis = getTimeInMillis( close, tZone );
        boolean openLessThanClose = openTimeInMillis <= closeTimeInMillis;

        // Regular Case: Open is less than close (Open close happens on the same day)
        // Open: 09:00:00 - Close: 17:00:00
        if( openLessThanClose )
            return closeTimeInMillis;

        // Irregular Case: Open is greater than close (close time happens the next day)
        // Is time right now after close?
        // If yes, that means close is tomorrow
        // Open: 18:00:00 - Close: 16:00:00, Now: 17:00:00

        boolean nowAfterClose = (nowMillis > closeTimeInMillis);
        if( nowAfterClose ){
            return closeTimeInMillis + _24_HOURS_IN_MILLIS;
        }

        return closeTimeInMillis;
    }



    public final static long getTimeInMillis( String timeStr, TimeZone tZone ) {

        long timeInMilliseconds = ZERO;

        try{

            String[ ] splitTokens = timeStr.trim( ).split( COLON );
            if( splitTokens.length != THREE ){
                throw new RuntimeException( "Invalid time format! Time " + timeStr + " must be in HH:MM:SS format" );
            }

            Calendar calendar = Calendar.getInstance( tZone );
            calendar.set( Calendar.HOUR_OF_DAY, Integer.parseInt( splitTokens[ ZERO ] ) );
            calendar.set( Calendar.MINUTE, Integer.parseInt( splitTokens[ ONE ] ) );
            calendar.set( Calendar.SECOND, Integer.parseInt( splitTokens[ TWO ] ) );
            calendar.set( Calendar.MILLISECOND, ZERO );

            timeInMilliseconds = calendar.getTimeInMillis( );

        }catch( Exception e ){
            throw new RuntimeException( "Failed to parse time " + timeStr, e );
        }

        return timeInMilliseconds;
    }



}
