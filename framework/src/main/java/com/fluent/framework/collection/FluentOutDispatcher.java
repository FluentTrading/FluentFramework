package com.fluent.framework.collection;
/*@formatter:off */

import org.agrona.concurrent.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;

import com.fluent.common.collections.*;
import com.fluent.framework.core.*;
import com.fluent.framework.events.*;
import com.fluent.framework.service.*;

import static com.fluent.common.util.Constants.*;
import static com.fluent.common.util.Toolkit.*;


//TODO: Should we use Disruptor or Agrona RingBuffer instead of ManyToOneConcurrentArrayQueue
public final class FluentOutDispatcher implements FluentService, FluentLifecycle, Runnable{

    private volatile boolean                                    keepDispatching;

    private final int                                           queueCapacity;
    private final ExecutorService                               executor;
    private final IdleStrategy idleStrategy;
    private final List<FluentEventListener>                     eventListener;
    private final ManyToOneConcurrentArrayQueue<FluentEvent>    eventQueue;

    private final static int                                    QUEUE_CAPACITY = nextPowerOfTwo( MILLION );
    private final static String                                 NAME           = FluentOutDispatcher.class.getSimpleName( );
    private final static Logger                                 LOGGER         = LoggerFactory.getLogger( NAME );

    public FluentOutDispatcher( ){
        this( QUEUE_CAPACITY, new BusySpinIdleStrategy( ) );
    }

    public FluentOutDispatcher( int queueCapacity, IdleStrategy idleStrategy ){
        this.queueCapacity  = notNegative( queueCapacity, "Queue Capacity must be positive." );
        this.idleStrategy   = errorIfNull( idleStrategy, "IdleStrategy can't be null." );
        
        this.eventListener  = new CopyOnWriteArrayList<>( );
        this.eventQueue     = new ManyToOneConcurrentArrayQueue<>( queueCapacity );
        this.executor       = Executors.newSingleThreadExecutor( new FluentThreadFactory( "OutDispatcher" ) );

    }

    @Override
    public final String name( ) {
        return NAME;
    }
    
    
    @Override
    public final boolean isRunning( ){
        return keepDispatching;
    }
    
    
    @Override
    public final FluentServiceType getType(){
        return FluentServiceType.OUT_DISPATCH_SERVICE;
    }
       

    public final int getQueueSize( ) {
        return eventQueue.size( );
    }


    public final int getQueueCapacity( ) {
        return queueCapacity;
    }


    @Override
    public final void start( ) {

        if( keepDispatching ){
            LOGGER.warn( "Attempted to start {} while it is already running.", NAME );
            return;
        }

        keepDispatching = true;
        executor.submit( this );

        LOGGER.info( "Started Out-Dispatcher with queue size [{}].", queueCapacity );
    }


    public final boolean register( FluentEventListener listener ) {
        boolean added = eventListener.add( listener );
        LOGGER.debug( "[#{} {}] ADDED as an Outbound event listener.", eventListener.size( ), listener.name( ) );

        return added;
    }


    public final boolean deregister( FluentEventListener listener ) {
        boolean removed = eventListener.remove( listener );
        LOGGER.debug( "[#{} {}] REMOVED as an Outbound event listener.", eventListener.size( ), listener.name( ) );
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

        while( keepDispatching ){
            process( );
        }

    }


    protected final void process( ) {

        try{

            if( eventQueue.peek( ) == null ){
                idleStrategy.idle( );
                return;
            }

            FluentEvent event = eventQueue.poll( );
            for( FluentEventListener listener : eventListener ){
                if( listener.isSupported( event.getType( ) ) ){
                    listener.update( event );
                }
            }

        }catch( Exception e ){
            LOGGER.error( "FAILED to dispatch Outbound events.", e );
        }

    }


    @Override
    public final void stop( ) {
        keepDispatching = false;

        eventListener.clear( );
        eventQueue.clear( );
        executor.shutdown( );

        LOGGER.info( "Successfully stopped Outbound dispatcher." );
    }


}
