package fluent.framework.collection;
/*
import org.HdrHistogram.*;
import org.agrona.concurrent.*;
import org.nustaq.serialization.simpleapi.*;

import java.io.*;
import java.util.concurrent.*;

import com.fluent.framework.collection.MktDataDispatcher.MktDataListener;


public final class TestDispatcherPerformance{


    private static volatile boolean warmingUp;

    protected static void printResult( Histogram histogram ) {
        System.out.println( "\nDetail Result (in micros)" );
        System.out.println( "------------------------------------------------------------------" );

        histogram.outputPercentileDistribution( System.out, 1000.0 );
        double valueAt99Percentile = histogram.getValueAtPercentile( 99.99d );

        System.out.println( "\nValue 99.99th percentile >> " + valueAt99Percentile / 1000.0 );
    }


    protected static MyFSTSerializer createFSTSerializer( boolean toStore, int eventCount, int memorySizeOf1Object )
            throws Exception {

        long expectedMemory = memorySizeOf1Object * eventCount;
        String fileLocation = "C:\\Temp";
        String journalName = "Test";
        MyFSTSerializer ser = new MyFSTSerializer( toStore, fileLocation, journalName, new DefaultCoder( ), expectedMemory, eventCount );

        return ser;
    }


    protected static void destroyFSTSerializer( MyFSTSerializer serializer ) {

        if( serializer != null ){
            serializer.stop( );
            boolean deleted = new File( serializer.getFilename( ) ).delete( );
            if( deleted ){
                System.out.println( "Deleted file from " + serializer.getFilename( ) );
            }else{
                throw new RuntimeException( "TEST FAILED as we failed to delete file " + serializer.getFilename( ) );
            }
        }

    }


    public static void testOffHeapPersistence( ) {

        MyFSTSerializer serializer = null;

        try{

            int eventCount = 50000;
            int memorySizeOf1Object = 1000;
            Histogram histogram = new Histogram( TimeUnit.NANOSECONDS.convert( 1, TimeUnit.SECONDS ), 2 );

            System.out.println( "Testing off heap persistence performance of FSTLongOffheapMap by storing " + eventCount + " events." );
            serializer = createFSTSerializer( true, eventCount, memorySizeOf1Object );
            serializer.start( );

            for( int i = 0; i < eventCount; i++ ){

                MktDataEvent event = new MktDataEvent( "EDM6", 99.0, (100 + i), 99.50, (200 + i) );
                serializer.storeEvent( event );
                histogram.recordValue( System.nanoTime( ) - event.getCreationTime( ) );

            }

            int retrievedEventSize = serializer.retrieveAllEvents( ).size( );
            if( eventCount != retrievedEventSize )
                throw new RuntimeException( "Store failed as we stored " + eventCount + " events but retrieved " + retrievedEventSize );


            printResult( histogram );

        }catch( Exception e ){
            throw new RuntimeException( "TEST FAILED as ", e );

        }finally{
            destroyFSTSerializer( serializer );
        }

    }



    public static void testDispatchAndPersistence( boolean toStore ) throws Exception {

        int eventCount = 50000;
        int memorySizeOf1Object = 1000;

        MyFSTSerializer serializer = createFSTSerializer( toStore, eventCount, memorySizeOf1Object );
        DummyListener listener1 = new DummyListener( );
        DummyListener2 listener2 = new DummyListener2( eventCount, serializer );

        MktDataDispatcher dispatcher = new MktDataDispatcher( eventCount, listener2, listener1 );

        if( toStore ){
            System.out.println( "Testing off heap persistence with dispathcer performance of FSTLongOffheapMap by storing " + eventCount + " events." );
        }else{
            System.out.println( "Testing off heap persistence with dispathcer performance of FSTLongOffheapMap WITHOUT storing " + eventCount + " events." );
        }

        dispatcher.start( );
        warmingUp = true;
        listener2.warmUp( new MktDataEvent( "EDM6", 99.0, 100, 99.50, 200 ) );
        dispatcher.warmUp( new MktDataEvent( "EDM6", 99.0, 100, 99.50, 200 ) );

        System.gc( );
        Thread.sleep( 3000 );
        warmingUp = false;


        for( int i = 0; i < eventCount; i++ ){

            MktDataEvent event = new MktDataEvent( "EDM6", 99.0, (100 + i), 99.50, (200 + i) );
            boolean done = dispatcher.enqueue( event );
            if( !done ){
                System.err.println( "Failed to enqueue " + event );
            }

            if( System.nanoTime( ) - event.getCreationTime( ) < 5000 ){
                Thread.yield( );
            }
        }

        // Let the listener get all the elements
        while( (dispatcher.getQueueSize( ) != 0) ){
            Thread.yield( );
        }

        Thread.sleep( 2000 );
        dispatcher.stop( );
        listener1.generateLatencyStats( );
        destroyFSTSerializer( serializer );

        // Thread.sleep( 120 * 60 * 1000 );
    }


    public static class DummyListener implements MktDataListener{

        private final Histogram histogram;

        public DummyListener( ){
            this.histogram = new Histogram( TimeUnit.NANOSECONDS.convert( 1, TimeUnit.SECONDS ), 2 );
        }


        @Override
        public final boolean update( MktDataEvent event ) {
            if( warmingUp )
                return false;

            histogram.recordValue( (System.nanoTime( ) - event.getCreationTime( )) );
            return true;
        }


        public final void generateLatencyStats( ) {
            histogram.outputPercentileDistribution( System.out, 1000.0 );
            double valueAt99Percentile = histogram.getValueAtPercentile( 99.99d );
            System.out.println( "\nValue at 99.99th percentile (micros) >> " + valueAt99Percentile / 1000.0 );
        }

    }



    public static class DummyListener2 implements MktDataListener{

        private final int                                        eventCount;
        private final MyFSTSerializer                            serializer;
        private final OneToOneConcurrentArrayQueue<MktDataEvent> serialierQueue;

        public DummyListener2( int eventCount, MyFSTSerializer serializer ){
            this.eventCount = eventCount;
            this.serializer = serializer;
            this.serialierQueue = new OneToOneConcurrentArrayQueue<>( eventCount );
        }


        public final void start( ) {
            serializer.start( );
        }


        public final void warmUp( MktDataEvent event ) {

            for( int i = 0; i < eventCount; i++ ){
                serialierQueue.offer( event );
                serialierQueue.poll( );
            }

            serialierQueue.clear( );

        }


        @Override
        public final boolean update( MktDataEvent event ) {
            if( warmingUp )
                return false;

            boolean added = serialierQueue.offer( event );
            if( !added ){
                System.err.print( "FAILED TO ADD to serializer queue" );
            }
            return true;
        }


        public final void generateLatencyStats( ) {
            System.out.println( "\nStored events >> " + serialierQueue.size( ) );
        }


        public final void stop( ) {
            serializer.stop( );
        }

    }



    public static void main( String ... args ) throws Exception {

        testOffHeapPersistence( );
        System.gc(); Thread.sleep( 2000 ); testDispatchAndPersistence( false );
        System.gc(); Thread.sleep( 2000 );
        testDispatchAndPersistence( true );

    }

}
*/