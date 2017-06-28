package com.fluent.framework.admin.events;
/* @formatter:Off */
import com.fluent.framework.core.*;
import com.fluent.framework.events.*;

import static com.fluent.framework.events.FluentEventSequencer.*;
import static com.fluent.framework.events.FluentEventType.*;
import static com.fluent.framework.util.FluentUtil.*;

public final class ApplicationStateEvent extends FluentEvent{

    private final FluentAppState state;
    private final String      message;

    private final static long serialVersionUID = 1L;


    public ApplicationStateEvent( FluentAppState state ){
        this( state, EMPTY );
    }

    public ApplicationStateEvent( FluentAppState state, String message ){
        super( increment( ), APP_STATE_EVENT );

        this.state = state;
        this.message = message;
    }

    
    public final FluentAppState getState( ){
        return state;
    }


    public final String getMessage( ){
        return message;
    }


    @Override
    public final void toEventString( StringBuilder builder ) {
        builder.append( state );
        builder.append( message );
    }


}
