package com.fluent.framework.collection;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static com.fluent.framework.util.FluentUtil.*;


public final class FluentThreadFactory implements ThreadFactory{

    private final String            threadName;

    private final static AtomicLong COUNTER = new AtomicLong( ZERO );


    public FluentThreadFactory( String threadName ){
        this.threadName = threadName;
    }


    @Override
    public final Thread newThread( final Runnable runnable ) {

        long threadCount = COUNTER.incrementAndGet( );
        String fullName = threadCount + DASH + threadName;

        return new Thread( runnable, fullName );
    }

}
