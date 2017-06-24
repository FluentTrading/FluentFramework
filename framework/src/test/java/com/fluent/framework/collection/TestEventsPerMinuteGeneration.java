package com.fluent.framework.collection;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;


public class TestEventsPerMinuteGeneration{



    public static void main( String[ ] args ) {

        int runTime = 10;
        TimeUnit runUnit = TimeUnit.MILLISECONDS;

        long runTimeNanos = TimeUnit.NANOSECONDS.convert( runTime, runUnit );
        long startTime = System.nanoTime( );

        AtomicLong counter = new AtomicLong( 0 );

        while( (System.nanoTime( ) - startTime) < runTimeNanos ){

            long value = counter.incrementAndGet( );
            MktDataEvent event = new MktDataEvent( "EDM6", 99.0, (100 + value), 99.50, 200 );

            if( System.nanoTime( ) - event.getCreationTime( ) < 3000 ){
                Thread.yield( );
            }

        }

        System.err.println( "Produced " + counter.get( ) + " events in " + runTime + " " + runUnit );

    }

}
