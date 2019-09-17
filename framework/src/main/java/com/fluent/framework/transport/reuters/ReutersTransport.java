package com.fluent.framework.transport.reuters;

import com.fluent.framework.transport.core.*;

public class ReutersTransport extends AbstractTransport implements Runnable{

    public ReutersTransport( TransportType type ){
        super( type );
    }


    @Override
    public final String name( ) {
        return null;
    }


    @Override
    public final boolean isConnected( ) {
        return false;
    }


    @Override
    public final void start( ) {}


    @Override
    public final void run( ) {}


    @Override
    public final void stop( ) {

    }

}
