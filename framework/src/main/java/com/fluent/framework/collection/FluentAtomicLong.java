package com.fluent.framework.collection;

import org.agrona.concurrent.*;

import java.util.concurrent.atomic.*;

import com.fluent.common.collections.*;

import static com.fluent.common.util.Constants.*;


/**
 * Improves upon plain AtomicLong by padding it so the storage of it falls in one cache-line
 * (prevents false sharing). Also, applies backoff when the CAS fails, helps when getNext() is
 * highly contended between multiple threads.
 */

public final class FluentAtomicLong{

    private final AtomicLong startingId;
    private final IdleStrategy idleStrategy;
    
    public FluentAtomicLong( ){
        this( ZERO, new BusySpinIdleStrategy( ));
    }

    public FluentAtomicLong( long startingId, IdleStrategy idelStrategy ){
        this.idleStrategy   = idelStrategy;
        this.startingId     = new FluentPaddedAtomicLong( startingId );
    }


    public final long get( ) {
        return startingId.get( );
    }


    public final long getAndIncrement( ) {
        for( ;; ){
            long current = startingId.get( );
            long next = current + ONE;
            if( compareAndSet( current, next ) )
                return next;
        }

    }


    public final long getAndSet( final long newValue ) {

        while( true ){
            long current = startingId.get( );
            if( compareAndSet( current, newValue ) )
                return current;
        }
    }



    protected final boolean compareAndSet( final long current, final long next ) {
        if( startingId.compareAndSet( current, next ) ){
            return true;
        }else{
            idleStrategy.idle( );
            return false;
        }

    }

}
