package com.fluent.framework.core;
/*@formatter:off */

public interface FluentLifecycle{

    public String name( );
    public void start( ) throws FluentException;
    public void stop( ) throws FluentException;

}
