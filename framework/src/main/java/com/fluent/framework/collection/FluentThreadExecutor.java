package com.fluent.framework.collection;
/* @formatter:Off */

import org.slf4j.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import com.fluent.framework.core.*;

import static com.fluent.framework.util.FluentUtil.*;

import org.agrona.concurrent.*;


public final class FluentThreadExecutor<T> implements FluentLifecycle, ExecutorService{

    private volatile boolean      running;
    private volatile boolean      stopped;

    private final int             threadCount;
    private final Thread[ ]       threadArray;
    private final Queue<Runnable> threadQueue;

    private final static String   NAME   = FluentThreadExecutor.class.getSimpleName( );
    private final static Logger   LOGGER = LoggerFactory.getLogger( NAME );


    public FluentThreadExecutor( int threadCount ){

        this.threadCount = threadCount;
        this.threadArray = new Thread[ threadCount ];
        this.threadQueue = new OneToOneConcurrentArrayQueue<Runnable>( SIXTY_FOUR * SIXTY_FOUR );
    }


    @Override
    public final String name( ) {
        return NAME;
    }


    @Override
    public final void start( ) {

        running = true;
        stopped = false;

        for( int i = ZERO; i < threadCount; i++ ){
            threadArray[ i ] = new Thread( new Worker( ) );
            threadArray[ i ].start( );
        }

    }


    @Override
    public final void execute( Runnable runnable ) {

        if( stopped )
            return;
        if( runnable == null )
            return;

        boolean inserted = threadQueue.offer( runnable );

        while( !inserted ){
            LockSupport.parkNanos( 1 );
        }

    }


    @Override
    public final void shutdown( ) {

        running = false;
        for( int i = 0; i < threadCount; i++ ){
            threadArray[ i ].interrupt( );
            threadArray[ i ] = null;
        }

        stopped = true;

    }


    @Override
    public final boolean isShutdown( ) {
        return running;
    }


    @Override
    public final boolean isTerminated( ) {
        return stopped;
    }


    @Override
    public final boolean awaitTermination( long timeout, TimeUnit unit ) {
        throw new UnsupportedOperationException( );
    }


    @Override
    @SuppressWarnings( "hiding" )
    public final <T> List<Future<T>> invokeAll( Collection<? extends Callable<T>> tasks ) {
        throw new UnsupportedOperationException( );
    }


    @Override
    @SuppressWarnings( "hiding" )
    public final <T> List<Future<T>> invokeAll( Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit ) {
        throw new UnsupportedOperationException( );
    }


    @Override
    @SuppressWarnings( "hiding" )
    public final <T> T invokeAny( Collection<? extends Callable<T>> tasks ) {
        throw new UnsupportedOperationException( );
    }


    @Override
    @SuppressWarnings( "hiding" )
    public final <T> T invokeAny( Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit ) {
        throw new UnsupportedOperationException( );
    }


    @Override
    public final List<Runnable> shutdownNow( ) {
        throw new UnsupportedOperationException( );
    }


    @Override
    @SuppressWarnings( "hiding" )
    public final <T> Future<T> submit( Callable<T> task ) {
        throw new UnsupportedOperationException( );
    }


    @Override
    public final Future<?> submit( Runnable task ) {
        throw new UnsupportedOperationException( );
    }


    @Override
    @SuppressWarnings( "hiding" )
    public final <T> Future<T> submit( Runnable task, T result ) {
        throw new UnsupportedOperationException( );
    }


    @Override
    public final String toString( ) {
        throw new UnsupportedOperationException( );
    }


    @Override
    public final void stop( ) {
        shutdown( );
    }



    private final class Worker implements Runnable{


        @Override
        public void run( ) {

            while( running ){

                Runnable runnable = null;

                // Can we simplify this logic to use only one loop??

                while( true ){

                    if( Thread.interrupted( ) )
                        return;

                    runnable = threadQueue.poll( );
                    if( runnable != null )
                        break;
                    LockSupport.parkNanos( 1 );
                }

                try{

                    if( runnable != null ){
                        runnable.run( );
                    }

                }catch( Exception e ){
                    LOGGER.warn( "Exception while running thread.", e );
                }

            }

        }


    }


}
