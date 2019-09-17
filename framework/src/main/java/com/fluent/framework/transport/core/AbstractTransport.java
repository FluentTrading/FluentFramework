package com.fluent.framework.transport.core;

import java.util.*;
import java.util.concurrent.*;

import com.fluent.framework.events.*;



public abstract class AbstractTransport implements Transport<FluentEventListener>{

    private final TransportType                   type;
    private final AbstractSet<FluentEventListener> listeners;


    public AbstractTransport( TransportType type ){
        this.type = type;
        this.listeners = new CopyOnWriteArraySet<>( );
    }


    @Override
    public final TransportType getType( ){
        return type;
    }


    @Override
    public final void register( FluentEventListener listener ){
        listeners.add( listener );
    }


    @Override
    public final void deregister( FluentEventListener listener ){
        listeners.remove( listener );
    }


    protected final void distribute( FluentEvent event ){

        for( FluentEventListener listener : listeners ){
            listener.update( event );
        }
    }



}
