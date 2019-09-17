package com.fluent.framework.logger;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class EncodingTests {
    
    private static final TestRecord ENGLISH = new TestRecord( "Develop with pleasure", Charset.forName("US-ASCII") );
    private static final TestRecord[] m_data = { ENGLISH };
    private static final Charset UTF8 = Charset.forName( "UTF-8" );

    private static final Charset[] ASCII_ENCODINGS = { ENGLISH.charset, UTF8 };

    private static final int DATA_SIZE = 100 * 1024 * 1024;
    private static final int REPEATS = 10;

    public static void main(String[] args) {
        
        testBackForthConversion();
        if ( !ENGLISH.str.equals( asciiBytesToString(toAsciiBytes(ENGLISH.str)) ) )
            System.out.println( "Direct conversion failed" );

        compareToDirectProcessing( 200 * 1024 );
        compareToDirectProcessing( DATA_SIZE );
        compareToDirectProcessing( DATA_SIZE );

        testToByteConversionSpeed( 200 * 1000, 1000 ); //warmup
        testToByteConversionSpeed( DATA_SIZE, 1000 );
        testToByteConversionSpeed( DATA_SIZE, DATA_SIZE );

        testFromByteConversionSpeed(200 * 1000, 1000); //warmup
        testFromByteConversionSpeed(DATA_SIZE, 1000);
        testFromByteConversionSpeed(DATA_SIZE, DATA_SIZE);
    
    }

    private static String build( final String str, final int dataSize )
    {
        final StringBuilder sb = new StringBuilder( dataSize + 1000 );
        while ( sb.length() < dataSize )
        {
            sb.append( str );
            sb.append('\n');
        }
        return sb.toString();
    }

    private static void testToByteConversionSpeed( final int dataSize, final int chunkSize )
    {
        for ( final TestRecord test : m_data )
            testEncodingToBytes( dataSize, test.str, chunkSize, test.charset );
        for ( final TestRecord test : m_data )
            testEncodingToBytes( dataSize, test.str, chunkSize, UTF8 );
    }

    private static void testFromByteConversionSpeed( final int dataSize, final int chunkSize )
    {
        for ( final TestRecord test : m_data )
            testEncodingToString( dataSize, test.str, chunkSize, test.charset );
        for ( final TestRecord test : m_data )
            testEncodingToString( dataSize, test.str, chunkSize, UTF8 );
    }

    private static void compareToDirectProcessing( final int dataSize )
    {
        final String data = build( ENGLISH.str, dataSize );
        testDirectToBytes(dataSize, data);
        testDirectToString(dataSize, data, toAsciiBytes( data ));

        for ( final Charset charset : ASCII_ENCODINGS )
        {
            testEncodingToBytes(dataSize, ENGLISH.str, dataSize, charset);
            testEncodingToString(dataSize, ENGLISH.str, dataSize, charset );
        }

    }

    private static void testEncodingToBytes(int dataSize, String originalString, int chunkSize, Charset charset) {
        final String data = build( originalString, chunkSize );
        final int cnt = dataSize / data.length() == 0 ? 1 : dataSize / data.length();
        final List<byte[]> res = new ArrayList<byte[]>( cnt );
        final long start = System.currentTimeMillis();
        for ( int j = 0; j < REPEATS; ++j ) {
            for (int i = 0; i < cnt; i++) {
                res.add(data.getBytes(charset));
            }
            res.clear();
        }
        final long time = System.currentTimeMillis() - start;
        if ( dataSize >= 1000 * 1000 )
            System.out.println( "toBytesEncoding: encoding = " + charset.displayName() + "; chunkSize = " + ( chunkSize / 1024.0 ) + "K; dataSize = " +
                    (data.length() / 1024.0 / 1024.0) + "M; time = " + time / 1000.0 + " sec" );
        if ( res.size() != 0 ) System.out.println( res.size() );
        System.gc();
    }

    private static void testEncodingToString(int dataSize, String originalString, int chunkSize, Charset charset ) {
        final String data = build( originalString, chunkSize );
        final byte[] bytes = data.getBytes( charset );
        final int cnt = dataSize / data.length() == 0 ? 1 : dataSize / data.length();
        final List<String> res = new ArrayList<String>( cnt );
        final long start = System.currentTimeMillis();
        for ( int j = 0; j < REPEATS; ++j ) {
            for (int i = 0; i < cnt; i++) {
                res.add( new String( bytes, charset ) );
            }
            res.clear();
        }
        final long time = System.currentTimeMillis() - start;
        if ( dataSize > 1000000 )
            System.out.println( "toString: " + charset.displayName() + "; chunkSize = " + data.length() + "; totalSize = " + ( cnt * data.length() * REPEATS ) / 1024.0 / 1024.0 + " Mb; time = " +
                            time / 1000.0 + " sec"
            );
        if ( res.size() != 0 ) System.out.println( res.size() );
        System.gc();
    }

    private static void testDirectToBytes(int dataSize, String data) {
        final List<byte[]> res = new ArrayList<byte[]>( REPEATS );
        final long start = System.currentTimeMillis();
        for (int j = 0; j < REPEATS ; j++) {
            res.add(toAsciiBytes(data));
            res.remove( 0 );
        }
        final long time = System.currentTimeMillis() - start;
        if ( dataSize >= 1000 * 1000 )
            System.out.println( "toBytesDirect: dataSize = " + data.length() + "; time = " + time / 1000.0 + " sec" );
        if ( res.size() != 0 ) System.out.println( res.size() );
        System.gc();
    }

    private static void testDirectToString(int dataSize, String data, byte[] bytes) {
        final List<String> res = new ArrayList<String>( REPEATS );
        final long start = System.currentTimeMillis();
        for (int j = 0; j < REPEATS ; j++) {
            res.add( asciiBytesToString( bytes ) );
            res.remove( 0 );
        }
        final long time = System.currentTimeMillis() - start;
        if ( dataSize >= 1000 * 1000 )
            System.out.println( "toStringDirect: dataSize = " + data.length() + "; time = " + time / 1000.0 + " sec" );
        if ( res.size() != 0 ) System.out.println( res.size() );
        System.gc();
    }


    private static byte[] toAsciiBytes( final String str )
    {
        final byte[] res = new byte[ str.length() ];
        for (int i = 0; i < str.length() ; i++)
            res[ i ] = (byte) str.charAt( i );
        return res;
    }

    private static String asciiBytesToString( final byte[] ascii )
    {
        //deprecated constructor allowing data to be copied directly into String char[]. So convenient...
        return new String( ascii, 0 );
    }

    private static void testBackForthConversion() {
        for ( final TestRecord rec : m_data )
            convertBackAndForth( rec );
    }

    private static void convertBackAndForth( final TestRecord rec )
    {
        final byte[] bytes = rec.str.getBytes( rec.charset );
        final String back = new String( bytes, rec.charset );
        if (!rec.str.equals(back)) {
            System.out.println( "Failed for " + rec.str + " in " + rec.charset.displayName() );
        }
    }

    private static class TestRecord{
        public final String str;
        public final Charset charset;

        private TestRecord(String str, Charset charset) {
            this.str = str;
            this.charset = charset;
        }
    }
}
