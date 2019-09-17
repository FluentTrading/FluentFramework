package com.fluent.framework.logger;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;

import org.agrona.*;

public class ElasticBufferTest{
   
    public static byte[] stringToBytesASCII( String str ){
    
        byte[] b = new byte[str.length()];
        for( int i = 0; i < b.length; i++ ){
            b[i] = (byte) str.charAt(i);
        }
        
        return b;
       
    }
    

    public static String bytesToASCII( byte[] bytes ){
        
        char[] b = new char[bytes.length];
        for( int i = 0; i < b.length; i++ ){
            b[i] = (char) bytes[i];
        }
        
        //byte bytes[], int offset, int length, Charset charset
        String data = new String( bytes, 0, bytes.length, StandardCharsets.UTF_8 );
        return data;
       
    }
    
   
    public static void decode1( ){
        
        ExpandableArrayBuffer buffer = new ExpandableArrayBuffer( );
        
        buffer.putByte( 0, (byte) 12 );
        buffer.putChar( 1, ':', ByteOrder.nativeOrder( ) );
        buffer.putByte( 3, (byte) 10 );
        buffer.putChar( 4, ':', ByteOrder.nativeOrder( ) );
        buffer.putByte( 6, (byte) 50 );
        buffer.putChar( 7, ' ', ByteOrder.nativeOrder( ) );
        buffer.putChar( 9, '-', ByteOrder.nativeOrder( ) );
        buffer.putChar( 11, ' ', ByteOrder.nativeOrder( ) );
        
        String data = "This is a test!";
        //buffer.putShort( 13, (short) data.length( ) );
        //buffer.putBytes( 15, stringToBytesASCII(data), 0, data.length( ) );
        buffer.putStringUtf8( 13, data, ByteOrder.nativeOrder( ), data.length( ) );
        
        System.err.print( bytesToASCII(buffer.byteArray()) );
        System.err.println("===================================================");
                
        System.err.print( buffer.getByte(0) );
        System.err.print( buffer.getChar(1) );
        System.err.print( buffer.getByte(3) );
        System.err.print( buffer.getChar(4) );
        System.err.print( buffer.getByte(6) );
        System.err.print( buffer.getChar(7) );
        System.err.print( buffer.getChar(9) );
        System.err.print( buffer.getChar(11) );
        
        System.err.print( buffer.getStringUtf8( 13, ByteOrder.nativeOrder( )) );
        
        
        
        //RandomAccessFile file = new RandomAccessFile( "C:\\temp\\test.log", "rw");
        //file.writeC
    }
    

    
    
    public static void decode2( ) throws Exception{
        
        ExpandableArrayBuffer buffer = new ExpandableArrayBuffer( );
        
        buffer.putByte( 0, (byte) 12 );
        buffer.putChar( 1, ':', ByteOrder.nativeOrder( ) );
        buffer.putByte( 3, (byte) 10 );
        buffer.putChar( 4, ':', ByteOrder.nativeOrder( ) );
        buffer.putByte( 6, (byte) 50 );
        buffer.putChar( 7, ' ', ByteOrder.nativeOrder( ) );
        buffer.putChar( 9, '-', ByteOrder.nativeOrder( ) );
        buffer.putChar( 11, ' ', ByteOrder.nativeOrder( ) );
        
        String data = "This is a test!";
        buffer.putStringUtf8( 13, data, ByteOrder.nativeOrder( ), data.length( ) );
        //buffer.putBytes( 13, data.getBytes( ), 0, data.length( ) );
        
        //Maybe at 0, we can put the last position of the array in  ExpandableArrayBuffer
        
        RandomAccessFile file = new RandomAccessFile( "C:\\temp\\test.log", "rw");
        
        for (int i = 0; i < 50; i++){
            file.writeChar( (char) buffer.byteArray( )[i]);
        }
        
        /*
        file.writeByte( buffer.getByte( 0 ) );
        file.writeChar( buffer.getChar( 1 ) );
        file.writeByte( buffer.getByte( 3 ) );
        file.writeChar( buffer.getChar( 4 ) );
        file.writeByte( buffer.getByte( 6 ) );
        file.writeChar( buffer.getChar( 7 ) );
        file.writeChar( buffer.getChar( 9 ) );
        file.writeChar( buffer.getChar( 11 ) );
        
        file.writeUTF( buffer.getStringUtf8( 13 ) );
        */
        
        file.close( );
    }

    
    public static void main( String[ ] args ) throws Exception{
        ElasticBufferTest.decode1( );        
    }


}
