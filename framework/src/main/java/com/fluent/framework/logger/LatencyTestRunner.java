package com.fluent.framework.logger;

import org.HdrHistogram.*;

import ch.qos.logback.classic.*;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.*;


/*
    1. Ensure that additivity is controlled by the "additivity" flag. For testing it should be turned off.
    2. Ensure we can write data to a file just by passing null for OutStream and PatternLayoutEncoder.
       For testing we will pass MeasuringStream and MeasuringPatternLayoutEncoder.
*/


public class LatencyTestRunner{
    
    private final static boolean IMMEDIATE_FLUSH        = true;
    private final static boolean DEL_FILE_ON_EXIT       = true;
    private final static int ITERATION                  = 1_000;
    private final static int ASYNC_Q_SIZE               = 1_000;
    private final static int DISCARDING_THRESHOLD       = 0;
    private final static Level LEVEL                    = Level.INFO;
    private final static MeasuringStream OUT_STREAM     = new MeasuringStream( );
    private final static String PATTERN                 = "%date %level [%thread] %logger{10} %msg%n";

    
    private final static void configureFileAppender( ){
        LogbackConfiguration.configure( IMMEDIATE_FLUSH, PATTERN, new PatternLayoutEncoder(),
                                        DEL_FILE_ON_EXIT, null,
                                        ASYNC_Q_SIZE, DISCARDING_THRESHOLD );
    }
    
    
    private final static void configureMeasure( ){
        LogbackConfiguration.configure( IMMEDIATE_FLUSH, PATTERN, new MeasuringPatternLayoutEncoder(),
                                        DEL_FILE_ON_EXIT, OUT_STREAM,
                                        ASYNC_Q_SIZE, DISCARDING_THRESHOLD );
    }

    
    public static void main( String[ ] args ) throws Exception{

        configureFileAppender( );
        
        String testId       = "test_Id_1";
        Logger logger       = LogbackConfiguration.getLogger( testId, LEVEL, false );
        
        for( int i =0; i< ITERATION; i++ ){
            String time = String.valueOf( System.nanoTime() );
            logger.info("{}", time );
        }        
        
        Histogram histogram     = OUT_STREAM.getHistogram( );
        if( histogram.getTotalCount( ) != ITERATION ){
            //throw new RuntimeException( "Histogram didnt record all the msgs sent!");
        }
        
        histogram.outputPercentileDistribution(System.out, 1000.0);
        
        Thread.sleep( 50000 );
        LogbackConfiguration.stopAsyncAppender( );
        
    }

    
  }
