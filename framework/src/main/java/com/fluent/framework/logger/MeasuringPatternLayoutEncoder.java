package com.fluent.framework.logger;

import java.io.*;
import ch.qos.logback.classic.encoder.*;
import ch.qos.logback.classic.spi.*;

public class MeasuringPatternLayoutEncoder extends PatternLayoutEncoder{
    
    
    public MeasuringPatternLayoutEncoder( ){
        
    }
    
    private final byte[] convertToBytes(String s) {
        if( getCharset( ) == null ){
          return s.getBytes();
          
        }else{
          try{
            return s.getBytes(getCharset( ).name());
          
          } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(
                    "An existing charset cannot possibly be unsupported.");
          }
        }
      }
    
    @Override
    public final void doEncode(ILoggingEvent event) throws IOException{
        
        String txt = layout.doLayout(event);
        outputStream.write( convertToBytes(txt) );
        if( isImmediateFlush( ) ){
          outputStream.flush();
        }
        
    }
    
    /*
    @Override
    public void doEncode( ILoggingEvent event ) throws IOException {
        
        String entireMessage    = layout.doLayout(event);
        String fmtMessage       = event.getFormattedMessage( );
        
        MeasuringStream mStream = (MeasuringStream) outputStream;
        mStream.writeData( fmtMessage, entireMessage );
        
        if( isImmediateFlush( ) ){
            mStream.flush();
        }
        
    }
    */
    
}
