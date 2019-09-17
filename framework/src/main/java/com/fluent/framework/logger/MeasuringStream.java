package com.fluent.framework.logger;

import java.io.*;
import org.HdrHistogram.*;
import java.util.concurrent.*;


public final class MeasuringStream extends OutputStream{

    private final Histogram histogram;
    
    public MeasuringStream( ){
        this.histogram  = new Histogram( TimeUnit.MINUTES.toNanos(1), 2);
    }    
    
    @Override
    public void write( int b ) throws IOException{
        System.err.println( "write Called" );
    }
        
    
    public final void writeData( String fmtMessage, String entireMessage ){
        
        try{
            
            long sentTimeNanos      = Long.parseLong( fmtMessage );
            long timeTakenNanos     = System.nanoTime( ) - sentTimeNanos;
           
            histogram.recordValue( timeTakenNanos );
            //System.err.println( "Measure: timeTakenNanos >>" + timeTakenNanos );
        }catch( Exception e ){
            e.printStackTrace( );
        }
    }
    
    
    public final Histogram getHistogram(){
        return histogram;
    }
    
    
    @Override
    public void flush() throws IOException {
        //System.err.println( "Called flush()" );
    }
    
    
    @Override
    public void close() throws IOException{
       // System.err.println( "Called close()" );
       //getHistogram( ).outputPercentileDistribution(System.out, 1000.0);

    }


    
}
