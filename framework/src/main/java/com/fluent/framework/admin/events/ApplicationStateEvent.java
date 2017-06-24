package com.fluent.framework.admin.events;
/* @formatter:Off */
import com.fluent.framework.admin.core.*;
import com.fluent.framework.events.core.*;

import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.events.core.FluentEventType.*;
import static com.fluent.framework.events.core.FluentEventSequencer.*;

public final class ApplicationStateEvent extends FluentEvent{

    private final FluentState state;
    private final String      message;

    private final static long serialVersionUID = 1L;


    public ApplicationStateEvent( FluentState state ){
        this( state, EMPTY );
    }

    public ApplicationStateEvent( FluentState state, String message ){
        super( increment( ), APP_STATE_EVENT );

        this.state = state;
        this.message = message;
    }


    public final boolean isStopping( ) {
        return (FluentState.STOPPING == state);
    }


    public final FluentState getState( ) {
        return state;
    }


    public final String getMessage( ) {
        return message;
    }


    @Override
    public final void toEventString( StringBuilder builder ) {
        builder.append( state );
        builder.append( message );
    }


}
