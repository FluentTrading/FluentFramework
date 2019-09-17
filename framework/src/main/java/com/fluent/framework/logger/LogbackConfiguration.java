package com.fluent.framework.logger;

import java.io.*;
import org.slf4j.*;

import ch.qos.logback.core.*;
import ch.qos.logback.core.util.*;
import ch.qos.logback.classic.*;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.*;
import ch.qos.logback.classic.spi.*;

/**
 * Logback flow (Assuming current = AsyncAppender -> FileAppender)
 * 
 * logger.info() 
 * 
 *      Logger appendLoopOnAppenders(E obj) -> AppenderAttachableImpl appendLoopOnAppenders(E e) (Assuming Logger is set to     AsynAppender)
 *      UnsynchronizedAppenderBase's doAppend(E obj) -> AsyncAppenderBase append(E obj) -> puts data in a A.B.Q
 *      
 *      Worker thread pulls out data
 *      AppenderAttachableImpl appendLoopOnAppenders(E obj) loops over attached appenders ( We only have LatencyRecordAppender)
 *      doAppend(E event) in UnsynchronizedAppenderBase gets called.
 *      Then append( E event ) is called in LatencyRecordAppender
 *      We call Encoder.doEncode(E event) in LatencyRecordAppender (TO simulate flow of a File or Console Appender)
 *      Encoder converts event to text and then calls its LatencyRecordAppender's outputstream.write
 */


public final class LogbackConfiguration{
    
    private final static LoggerContext context;
    private final static AsyncAppender asyncAppender;
    
    static{
       context          = (LoggerContext) LoggerFactory.getILoggerFactory();
       asyncAppender    = new AsyncAppender( );
    }
    
    public final static void configure( boolean immediateFlush, String encoderPattern, PatternLayoutEncoder encoder,
                                        boolean deleteFileOnExit, OutputStream outStream,
                                        int queueSize, int discardingThreshold ){
       
        //Layout
        encoder.setContext( context );
        encoder.setPattern( encoderPattern );
        encoder.setImmediateFlush( immediateFlush );
        encoder.start( );
       
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setContext(context);
        fileAppender.setName("FileAppender");
        fileAppender.setFile( "DummyLogbackPrefFile.txt");
        if( deleteFileOnExit ){
            new File( fileAppender.getFile( )).deleteOnExit( );;
        }
                    
        fileAppender.setEncoder( encoder );
        fileAppender.start();
        //FileAppender uses its own ResilientFileOutputStream
        //To use our own custom outputstream, we set it after the start()
        if( outStream != null ){
            fileAppender.setOutputStream( outStream );
        }
        //System.err.println( "Using " + fileAppender.getOutputStream( ).getClass( ).getName( ) );
        
        asyncAppender.setContext( context );
        asyncAppender.setName( "AsyncAppender" );
        asyncAppender.setQueueSize( queueSize );
        asyncAppender.setDiscardingThreshold( discardingThreshold );
        asyncAppender.addAppender( fileAppender );
        asyncAppender.start( );
        
        StatusPrinter.print(context);
               
    }
        
    
    public final static Logger getLogger( String name, Level level, boolean additivity ){
        Logger logger   = (Logger) LoggerFactory.getLogger( name );
        logger.setLevel( level );
        logger.setAdditive( additivity);
        logger.addAppender( asyncAppender );
        
        return logger;
    }
 
    
    public final static void stopLogger( Logger logger ){
        logger.detachAndStopAllAppenders( );
    }
    
    
    public final static void stopAsyncAppender( ){
        asyncAppender.stop( );
    }
   
}
