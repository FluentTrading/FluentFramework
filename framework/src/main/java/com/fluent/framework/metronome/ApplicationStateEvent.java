package com.fluent.framework.metronome;
/*@formatter:off */

import com.fluent.framework.core.*;
import com.fluent.framework.events.*;

import static com.fluent.common.util.Constants.*;
import static com.fluent.framework.events.FluentEventSequencer.*;
import static com.fluent.framework.events.FluentEventType.*;



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

    
    public final FluentState getState( ){
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
