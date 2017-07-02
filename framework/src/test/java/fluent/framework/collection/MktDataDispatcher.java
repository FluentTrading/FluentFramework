package fluent.framework.collection;

import org.agrona.concurrent.*;
import java.util.concurrent.*;


public final class MktDataDispatcher implements Runnable{

    private volatile boolean                                   keepDispatching;

    private final int                                          queueSize;
    private final ThreadPoolExecutor                           service;
    private final MktDataListener[ ]                           listeners;
    private final ManyToManyConcurrentArrayQueue<MktDataEvent> eventQueue;


    public MktDataDispatcher( int queueSize, MktDataListener ... listeners ){

        this.queueSize = queueSize;
        this.listeners = listeners;
        this.eventQueue = new ManyToManyConcurrentArrayQueue<>( queueSize );
        this.service = new ThreadPoolExecutor( 1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>( 2 ) );

    }


    public final void start( ) {
        keepDispatching = true;
        service.prestartCoreThread( );
        service.execute( this );

    }


    protected final void warmUp( MktDataEvent event ) {

        for( int i = 0; i < (queueSize); i++ ){

            eventQueue.offer( event );

            while( !eventQueue.isEmpty( ) ){
                Thread.yield( );
            }

        }

        eventQueue.clear( );
        System.out.println( "Finished warming up MktDataDispatcher, QUEUSize:  " + eventQueue.size( ) );

    }


    public final boolean enqueue( final MktDataEvent event ) {
        return eventQueue.offer( event );
    }


    @Override
    public final void run( ) {

        while( keepDispatching ){

            try{

                MktDataEvent event = eventQueue.poll( );
                if( event == null ){
                    Thread.yield( );
                    continue;
                }

                for( MktDataListener listener : listeners ){
                    listener.update( event );
                }

            }catch( Exception e ){
                e.printStackTrace( );
            }
        }

    }


    protected final int getQueueSize( ) {
        return eventQueue.size( );
    }


    public final void stop( ) {
        keepDispatching = false;
        service.shutdown( );
    }



    public interface MktDataListener{
        public boolean update( MktDataEvent event );
    }

}
