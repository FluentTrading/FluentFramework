package com.fluent.framework.transport.core;

import com.fluent.framework.core.*;


public interface Transport<T> extends FluentLifecycle{

    public boolean isConnected( );
    public TransportType getType( );
    public void register( T listener );
    public void deregister( T listener );

}
