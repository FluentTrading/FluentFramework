package com.fluent.framework.collection;

import java.util.concurrent.atomic.*;

import static com.fluent.framework.util.FluentUtil.*;


/**
 * Improves upon plain AtomicLong by padding it so the storage of it falls in one cache-line
 * (prevents false sharing). Also, applies backoff when the CAS fails, helps when getNext() is
 * highly contended between multiple threads.
 */

public final class FluentAtomicLong{

    private final AtomicLong id;

    public FluentAtomicLong( ){
        this( ZERO );
    }

    public FluentAtomicLong( long startingId ){
        this.id = new FluentPaddedAtomicLong( startingId );
    }


    public final long get( ) {
        return id.get( );
    }


    public final long getAndIncrement( ) {
        for( ;; ){
            long current = id.get( );
            long next = current + ONE;
            if( compareAndSet( current, next ) )
                return next;
        }

    }


    public final long getAndSet( final long newValue ) {

        while( true ){
            long current = id.get( );
            if( compareAndSet( current, newValue ) )
                return current;
        }
    }



    protected final boolean compareAndSet( final long current, final long next ) {
        if( id.compareAndSet( current, next ) ){
            return true;
        }else{
            FluentBackoffStrategy.apply( ONE );
            return false;
        }

    }

}
