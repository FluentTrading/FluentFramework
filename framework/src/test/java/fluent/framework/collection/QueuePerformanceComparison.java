package fluent.framework.collection;

import java.util.*;
import java.util.concurrent.*;
import org.agrona.concurrent.*;

public class QueuePerformanceComparison{

    // 15 == 32* 1024
    public static final int     QUEUE_CAPACITY = 1 << Integer.getInteger( "scale", 15 );
    public static final int     REPETITIONS    = 50 * 1000 * 1000;
    public static final Integer TEST_VALUE     = Integer.valueOf( 777 );


    private static Queue<Integer> createQueue( final int queueType ) {

        switch( queueType ){

            case 0:
                return new ArrayBlockingQueue<Integer>( QUEUE_CAPACITY );

            case 1:
                return new OneToOneConcurrentArrayQueue<Integer>( QUEUE_CAPACITY );

            case 2:
                return new ManyToManyConcurrentArrayQueue<Integer>( QUEUE_CAPACITY );

            default:
                throw new IllegalArgumentException( "Invalid option: " + queueType );

        }

    }


    private static void performanceRun( final int runNumber, final Queue<Integer> queue ) throws Exception {

        final long start = System.nanoTime( );
        final Thread thread = new Thread( new Producer( queue ) );
        thread.start( );

        Integer result;
        int i = REPETITIONS;
        do{
            while( null == (result = queue.poll( )) ){
                Thread.yield( );
            }

        }while( 0 != --i );

        thread.join( );


        final long duration = System.nanoTime( ) - start;
        final long operations = (REPETITIONS * 1000L * 1000L * 1000L) / duration;
        System.out.format( "%d - ops/sec=%,d - %s result=%d\n", Integer.valueOf( runNumber ), Long.valueOf( operations ), queue.getClass( ).getSimpleName( ), result );

    }


    public static class Producer implements Runnable{

        private final Queue<Integer> queue;

        public Producer( final Queue<Integer> queue ){
            this.queue = queue;
        }

        @Override
        public void run( ) {
            int i = REPETITIONS;

            do{
                while( !queue.offer( TEST_VALUE ) ){
                    Thread.yield( );
                }

            }while( 0 != --i );

        }
    }


    public static void main( final String[ ] args ) throws Exception {

        int queueType = 0;
        String queueName = (queueType == 0) ? "ArrayBlockingQueue" : (queueType == 1 ? "FluentSPSCQueue" : "FluentMPSCQueue");

        Queue<Integer> queue = createQueue( queueType );
        System.out.println( "QueueName: " + queueName + ", QueueType: " + queueType + ", Capacity: " + QUEUE_CAPACITY + ", Repetitions: " + REPETITIONS );

        for( int i = 0; i < 10; i++ ){
            System.gc( );
            performanceRun( i, queue );
        }

    }


}
