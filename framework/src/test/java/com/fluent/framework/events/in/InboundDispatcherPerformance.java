package com.fluent.framework.events.in;

import org.HdrHistogram.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.fluent.framework.events.core.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.market.event.*;

import static com.fluent.framework.util.FluentUtil.*;

// IMPROVEMENTS
// If you lower the OS parameter vm.dirty_expire_centisecs it reduces the peaks and make them more
// stable, but do not eliminate them all together
public final class InboundDispatcherPerformance{

    private final static int    TEST_BUCKET_CAPACITY = 64;
    private final static int    EXPECTED_AVG_LATENCY = 50 * 1000;
    // private final static int[] TEST_EVENT_COUNT = {50000, 75000, 1000000, 2000000, 45000 };
    private final static int[ ] TEST_EVENT_COUNT     = {50000, 2000000, 45000};
    private final static String CHRONCILE_LOCATION   = "target/";

    private final static String NAME                 = InboundDispatcherPerformance.class.getSimpleName( );
    private final static Logger LOGGER               = LoggerFactory.getLogger( NAME );


    public static double runLatencyTest( int run, int eventCount, FluentInEventDispatcher dispatcher ) throws Exception {

        MeasurableLatencyListener listener = new MeasurableLatencyListener( eventCount );
        dispatcher.register( listener );

        for( int i = 0; i < eventCount; i++ ){
            FluentInEvent event = new MarketDataEvent( Exchange.CME, InstrumentSubType.ED_FUTURES, "EDM6", 99.0, 100 + i, 100.0, 200 + i );
            dispatcher.enqueue( event );

            /*
             * if( System.nanoTime() - event.getCreationTime() < 5000 ){ Thread.yield(); }
             */
        }

        while( eventCount != listener.getEventsReceived( ) ){
            Thread.yield( );
        }

        double latency99Percentile = listener.get99PercentileLatency( false, LOGGER );

        // Thread.sleep( 7000 );
        dispatcher.deregister( listener );

        return latency99Percentile;
    }


    public static void main( String ... args ) throws Exception {

        int testIterations = TEST_EVENT_COUNT.length;

        LOGGER.info( "STARTING INBOUND DISPATCHER LATENCY TEST!!" );
        LOGGER.info( "Tests will run [{}] iterations, will feed events {}.", testIterations, Arrays.toString( TEST_EVENT_COUNT ) );
        LOGGER.info( "-----------------------------------------------------------------------------------------------------------{}", NEWLINE );

        double[ ] avgObservedLatencyArray = new double[ testIterations ];

        for( int run = 0; run < testIterations; run++ ){

            try{

                int eventThisIteration = TEST_EVENT_COUNT[ run ];
                String chronicleFileName = CHRONCILE_LOCATION + "InDispacterTest_" + run;

                // ChronicleTools.warmup();
                // PersisterService<InEvent> p = new InChroniclePersisterService(eventThisIteration,
                // chronicleFileName);
                FluentInEventDispatcher dispatch = new FluentInEventDispatcher( TEST_BUCKET_CAPACITY, eventThisIteration );

                System.out.println( "" );
                LOGGER.info( "Run: [{}], will feed [{}] events to Inbound dispatcher.", (run + 1), eventThisIteration );
                dispatch.start( );
                System.gc( );
                Thread.sleep( 2000 );

                avgObservedLatencyArray[ run ] = runLatencyTest( run, eventThisIteration, dispatch );

                sanityCheck( run, eventThisIteration, dispatch );
                dispatch.stop( );

                LOGGER.info( "-----------------------------------------------------------------------------------------------------------{}", NEWLINE );

            }catch( Exception e ){
                LOGGER.warn( "EXCPETION while running TEST#", run, e );

            }finally{
                File dir = new File( CHRONCILE_LOCATION );
                for( File file : dir.listFiles( ) ){
                    file.delete( );
                }
            }

        }

        double avgObserved_99Latency = arraySum( avgObservedLatencyArray ) / testIterations;
        LOGGER.info( "99th Percentile Observed Latencies: {}", Arrays.toString( avgObservedLatencyArray ) );
        LOGGER.info( "Avg 99th Percentile Latency :: Observed [{}], Expected:[{}].", avgObserved_99Latency, EXPECTED_AVG_LATENCY );
        if( avgObserved_99Latency > EXPECTED_AVG_LATENCY ){
            LOGGER.warn( "Test run [{}] FAILED!, Observed Latency > Expected Latency." );
        }

    }


    private static void sanityCheck( int run, int eventCount, FluentInEventDispatcher dispatch ) {

        int eventUnprocessed = dispatch.getQueueSize( );
        if( eventUnprocessed != 0 ){
            LOGGER.warn( "Test run [{}] FAILED!, Dispatcher has {} unprocessed events.", run, eventUnprocessed );
            throw new RuntimeException( );
        }

        // Inbound dispatcher compacts Market data events, there retrieved will never equal
        // eventCount.
        // int eventsRetrieved = p.retrieveAll().size();
        // LOGGER.warn("Run: [{}] Retrieved [{}] events from Chroncile.", run, eventsRetrieved );

    }


    private static double arraySum( double[ ] avgObservedLatencyArray ) {
        double total = 0;
        for( double num : avgObservedLatencyArray ){
            total += num;
        }

        return total;
    }



    public static class MeasurableLatencyListener implements FluentEventListener{

        private volatile int    eventsReceived;

        private final int       eventsToBeSent;
        private final Histogram histogram;

        public MeasurableLatencyListener( int eventsToBeSent ){
            this.eventsToBeSent = eventsToBeSent;
            this.histogram = new Histogram( TimeUnit.NANOSECONDS.convert( 5, TimeUnit.SECONDS ), 2 );
        }


        @Override
        public final String name( ) {
            return "MeasurableLatencyListener";
        }


        @Override
        public final boolean isSupported( FluentInType type ) {
            return true;
        }


        @Override
        public final boolean inUpdate( FluentInEvent event ) {
            histogram.recordValue( (System.nanoTime( ) - event.getCreationTime( )) );
            ++eventsReceived;

            return true;
        }


        public final int getEventsReceived( ) {
            return eventsReceived;
        }

        public final double get99PercentileLatency( boolean fullReport, Logger logger ) {

            double percentile = 99.0d;
            double granularity = 1.0; // 1 for Nanons, 1000 for micors
            String granularityName = (granularity == 1.0) ? "Nanos!" : "Micros!";

            LOGGER.info( "Latency Report:: Will report 99th percentile latency in {}.", granularityName );
            LOGGER.info( "Events sent [{}], Events Received [{}]", eventsToBeSent, eventsReceived );

            if( fullReport ){
                histogram.outputPercentileDistribution( System.out, granularity );
            }

            double percentile99 = histogram.getValueAtPercentile( percentile ) / granularity;
            LOGGER.info( "Latency at 99.99th percentile [{}] {}.", percentile99, granularityName );

            return percentile99;
        }

    }


}
