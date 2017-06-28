package com.fluent.framework.core;

/* @formatter:Off */
public enum FluentAppState{

    INITIALIZING    ( "Initializing" ),
    RECOVERING      ( "Recovering" ),
    RUNNING         ( "Running" ),
    PAUSED          ( "Paused" ),
    STOPPING        ( "Stopping" ),
    STOPPED         ( "Stopped" );

    private final String description;

    private FluentAppState( String description ){
        this.description = description;
    }

    public final String getDescription( ) {
        return description;
    }

}
