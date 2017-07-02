package fluent.framework.core;

/* @formatter:Off */
public enum FluentState{

    INITIALIZING    ( "Initializing" ),
    RECOVERING      ( "Recovering" ),
    RUNNING         ( "Running" ),
    PAUSED          ( "Paused" ),
    STOPPING        ( "Stopping" ),
    STOPPED         ( "Stopped" );

    private final String description;

    private FluentState( String description ){
        this.description = description;
    }

    public final String getDescription( ) {
        return description;
    }

}
