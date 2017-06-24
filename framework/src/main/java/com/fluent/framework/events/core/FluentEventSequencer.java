package com.fluent.framework.events.core;
import com.fluent.framework.collection.*;

public final class FluentEventSequencer{
    
    private final static FluentAtomicLong SEQUENCE = new FluentAtomicLong( );
    
    public final static long increment( ){
        return SEQUENCE.getAndIncrement( );
    }
    
//    
//    public final static long current( ){
//        return SEQUENCE.get( );
//    }
    

}
