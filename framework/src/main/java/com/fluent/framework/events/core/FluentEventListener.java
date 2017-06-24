package com.fluent.framework.events.core;
/*@formatter:off */

public interface FluentEventListener{

    public String name( );
    public boolean isSupported( FluentEventType type );
    public boolean update( FluentEvent event );

}
