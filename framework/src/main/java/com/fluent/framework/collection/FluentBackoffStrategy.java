package com.fluent.framework.collection;

import java.util.concurrent.locks.*;

import static com.fluent.framework.util.FluentUtil.*;


public final class FluentBackoffStrategy{


    public final static void apply( ) {
        apply( ONE );
    }


    public final static void apply( long time ) {
        if( IS_WINDOWS ){
            yield( );
        }else{
            park( time );
        }

    }


    protected final static void yield( ) {
        Thread.yield( );
    }


    protected final static void park( long time ) {
        LockSupport.parkNanos( time );
    }



}
