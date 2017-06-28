package com.fluent.framework.admin.events;

import com.fluent.framework.events.*;

import static com.fluent.framework.events.FluentEventSequencer.*;
import static com.fluent.framework.events.FluentEventType.*;

public final class MetronomeEvent extends FluentEvent{

    private final static long serialVersionUID = 1L;


    public MetronomeEvent( ){
        super( increment( ), METRONOME_EVENT );
    }

    @Override
    public final void toEventString( StringBuilder builder ) {}


}
