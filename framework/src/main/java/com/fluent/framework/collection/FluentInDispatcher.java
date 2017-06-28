package com.fluent.framework.collection;
/*@formatter:off */

import org.slf4j.*;
import java.util.*;
import java.util.concurrent.*;
import org.agrona.concurrent.*;
import com.fluent.framework.core.*;
import com.fluent.framework.events.*;
import com.fluent.framework.service.*;

import static com.fluent.framework.util.FluentToolkit.*;
import static com.fluent.framework.util.FluentUtil.*;


//TODO: Should we use Disruptor or Agrona RingBuffer instead of ManyToOneConcurrentArrayQueue
public final class FluentInDispatcher implements FluentService, FluentLifecycle, Runnable{

    private volatile boolean                                   keepDispatching;

    private final int                                          bucketCapacity;
    private final int                                          queueCapacity;
    private final ExecutorService                              executor;

    private final List<FluentEventListener>                    eventListeners;
    private final ManyToOneConcurrentArrayQueue<FluentEvent>   eventQueue;

    private final static int                                   BUCKET_CAPACITY = THIRTY_TWO;
    private final static int                                   QUEUE_CAPACITY  = nextPowerOfTwo( MILLION );
    private final static String                                NAME            = FluentInDispatcher.class.getSimpleName( );
    private final static Logger                                LOGGER          = LoggerFactory.getLogger( NAME );


    // TODO: Use a better backoff mechanism

    public FluentInDispatcher( FluentConfiguration cfgManager ){
        this( BUCKET_CAPACITY, QUEUE_CAPACITY );
    }


    public FluentInDispatcher( int bucketCapacity, int queueCapacity ){

        this.bucketCapacity = notNegative( bucketCapacity, "Bucket Capacity must be positive." );
        this.queueCapacity  = notNegative( queueCapacity, "Queue Capacity must be positive." );

        this.eventListeners = new CopyOnWriteArrayList<FluentEventListener>( );
        this.eventQueue     = new ManyToOneConcurrentArrayQueue<FluentEvent>( queueCapacity );
        this.executor       = Executors.newSingleThreadExecutor( new FluentThreadFactory( "InDispatcher" ) );

    }


    @Override
    public final String name( ) {
        return NAME;
    }

    
    @Override
    public final FluentServiceType getType(){
        return FluentServiceType.IN_DISPATCH_SERVICE;
    }
    
    
    public final int getBucketCapacity( ) {
        return bucketCapacity;
    }


    public final int getQueueCapacity( ) {
        return queueCapacity;
    }


    protected final int getQueueSize( ) {
        return eventQueue.size( );
    }


    @Override
    public final void start( ) {

        if( keepDispatching ){
            LOGGER.warn( "Attempted to start {} while it is already running.", NAME );
            return;
        }

        keepDispatching = true;
        executor.submit( this );

        LOGGER.info( "Started inbound dispatcher with queue Size [{}], bucket size[{}].", queueCapacity, bucketCapacity );
    }


    public final boolean register( FluentEventListener listener ) {
        boolean added = eventListeners.add( listener );
        LOGGER.debug( "[#{} {}] ADDED as an Inbound event listener.", eventListeners.size( ), listener.name( ) );

        return added;
    }


    public final boolean deregister( FluentEventListener listener ) {
        boolean removed = eventListeners.remove( listener );
        LOGGER.debug( "[#{} {}] REMOVED as an Inbound event listener.", eventListeners.size( ), listener.name( ) );
        return removed;
    }



    public final boolean enqueue( final FluentEvent event ) {
        boolean result = eventQueue.offer( event );
        if( !result ){
            LOGGER.warn( "Failed to enqueue Size[{}], Event:[{}]", eventQueue.size( ), event );
        }

        return result;
    }



    @Override
    public final void run( ) {

        final ArrayDeque<FluentEvent> bucket = new ArrayDeque<>( bucketCapacity );

        while( keepDispatching ){
            process( bucket );

            bucket.clear( );
        }

    }


    protected final void process( ArrayDeque<FluentEvent> bucket ) {

        try{

            int itemsPolled = eventQueue.drainTo( bucket, bucketCapacity );
            if( itemsPolled == ZERO ){
                FluentBackoff.backoff( );
                return;
            }

            for( FluentEvent event : bucket ){
                FluentEventType type = event.getType( );

                for( FluentEventListener listener : eventListeners ){
                    if( listener.isSupported( type ) ){
                        listener.update( event );
                    }
                }

            }

        }catch( Exception e ){
            LOGGER.error( "FAILED to dispatch Inbound events.", e );
        }

    }


    @Override
    public final void stop( ) {
        keepDispatching = false;

        eventListeners.clear( );
        eventQueue.clear( );
        executor.shutdown( );
        LOGGER.info( "Successfully stopped Inbound dispatcher." );
    }


}