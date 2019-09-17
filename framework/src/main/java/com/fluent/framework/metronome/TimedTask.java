package com.fluent.framework.metronome;
/* @formatter:Off */

import static com.fluent.common.util.Constants.*;

public enum TimedTask {

    TWO_MINS_TO_CLOSE   ( true, 2 * 60 * 1000   ),
    ONE_MIN_TO_CLOSE    ( true, 1 * 60 * 1000   ),
    TEN_SECS_TO_CLOSE   ( true, 1 * 10 * 1000   ),
    CLOSING_TIME        ( true, 1 * 01 * 1000   ),
    WORKING_HOURS       ( false, ZERO           ),
    UNAPPLICABLE        ( false, NEGATIVE_ONE   );

    private final boolean     isClosing;
    private final long        offsetClose;

    private final static long CLOSING_TIME_LEEWAY = 5000;

    private TimedTask( boolean isClosing, long offsetClose ){
        this.isClosing = isClosing;
        this.offsetClose = offsetClose;
    }


    public final boolean isClosing( ) {
        return isClosing;
    }


    public final long getOffsetClose( ) {
        return offsetClose;
    }


    public final static TimedTask getTask( long nowMillis, long openMillis, long closeMillis ) {

        // Check for garbage values
        if( nowMillis <= ZERO || openMillis <= ZERO || closeMillis <= ZERO ){
            return UNAPPLICABLE;
        }

        // Opens later
        if( openMillis > nowMillis ){
            return UNAPPLICABLE;
        }


        // Opens later
        boolean closedInPast = (closeMillis < nowMillis);
        if( closedInPast ){
            if( nowMillis - closeMillis <= CLOSING_TIME_LEEWAY ){
                return CLOSING_TIME;
            }else{
                return UNAPPLICABLE;
            }
        }


        long timeLeftMillis = (closeMillis - nowMillis);

        if( timeLeftMillis <= CLOSING_TIME.offsetClose ){
            return CLOSING_TIME;
        }

        if( timeLeftMillis > CLOSING_TIME.offsetClose && timeLeftMillis < TEN_SECS_TO_CLOSE.offsetClose ){
            return TEN_SECS_TO_CLOSE;
        }

        if( timeLeftMillis > TEN_SECS_TO_CLOSE.offsetClose && timeLeftMillis < ONE_MIN_TO_CLOSE.offsetClose ){
            return ONE_MIN_TO_CLOSE;
        }

        if( timeLeftMillis > ONE_MIN_TO_CLOSE.offsetClose && timeLeftMillis < TWO_MINS_TO_CLOSE.offsetClose ){
            return TWO_MINS_TO_CLOSE;
        }

        return WORKING_HOURS;

    }


}
