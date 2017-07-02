package fluent.framework.util;

import static fluent.framework.util.Constants.*;

import java.lang.management.*;
import java.net.*;
import java.util.*;


public final class Toolkit{

    protected Toolkit( ){}


    public static <T> T notNull( T reference, Object message ) {
        if( reference == null ){
            throw new NullPointerException( String.valueOf( message ) );
        }

        return reference;
    }


    public static String notBlank( String reference, Object message ) {
        if( reference == null || reference.trim( ).isEmpty( ) ){
            throw new IllegalStateException( String.valueOf( message ) );
        }

        return reference;
    }


    public static int notNegative( int value, Object message ) {
        if( value <= 0 ){
            throw new IllegalStateException( String.valueOf( message ) );
        }

        return value;
    }



    protected final static boolean isWindows( ) {
        String osType = System.getProperty( "os.name" ).toLowerCase( );
        return osType.indexOf( "win" ) >= ZERO;
    }


    protected final static boolean isLinux( ) {
        String osType = System.getProperty( "os.name" ).toLowerCase( );
        return osType.indexOf( "nix" ) >= ZERO || osType.indexOf( "nux" ) >= ZERO || osType.indexOf( "aix" ) > ZERO;
    }


    public final static boolean isInteger( String data ) {

        try{
            Integer.parseInt( data );
            return true;
        }catch( Exception e ){
        }

        return false;

    }


    public final static boolean isBlank( String data ) {
        return (data == null || data.isEmpty( )) ? true : false;
    }


    public final static String toUpper( String data ) {
        return isBlank( data ) ? EMPTY : data.trim( ).toUpperCase( );
    }


    public final static int nextPowerOfTwo( final int value ) {
        return 1 << (32 - Integer.numberOfLeadingZeros( value - 1 ));
    }


    public final static int parseInteger( String value ) {
        return isBlank( value ) ? ZERO : Integer.parseInt( value );
    }


    public final static double parseDouble( String value ) {
        return isBlank( value ) ? ZERO_DOUBLE : Double.parseDouble( value );
    }


    public final static String getFullProcessName( ) {
        return ManagementFactory.getRuntimeMXBean( ).getName( );
    }


    public final static double rndNumberBetween( final double upper, final double lower ) {
        return (Math.random( ) * (upper - lower)) + lower;
    }


    public static List<String> fastSplit( String text, char separator ) {

        final List<String> result = new ArrayList<String>( );

        if( text != null && text.length( ) > ZERO ){

            int index1 = ZERO;
            int index2 = text.indexOf( separator );

            while( index2 >= ZERO ){
                String token = text.substring( index1, index2 );
                result.add( token );
                index1 = index2 + ONE;
                index2 = text.indexOf( separator, index1 );
            }

            if( index1 < text.length( ) - ONE ){
                result.add( text.substring( index1 ) );
            }
        }// else: input unavailable

        return result;

    }


    public final static String getHostName( ) {
        String hostName = EMPTY;
        try{
            hostName = InetAddress.getLocalHost( ).getHostName( );
        }catch( Exception e ){
        }

        return hostName;

    }

    public final static String padLeft( String value, int spaces ){
        return String.format( "%1$" + spaces + "s", value );
    }
    
    public final static String padRight( String value, int spaces ){
        return String.format( "%1$-" + spaces + "s", value );
    }

}
