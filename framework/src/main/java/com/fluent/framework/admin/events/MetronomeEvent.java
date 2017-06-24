package com.fluent.framework.admin.events;

/* @formatter:Off */
import com.fluent.framework.events.core.*;

import static com.fluent.framework.events.core.FluentEventType.*;
import static com.fluent.framework.events.core.FluentEventSequencer.*;

public final class MetronomeEvent extends FluentEvent{

    private final static long serialVersionUID = 1L;


    public MetronomeEvent( ){
        super( increment( ), METRONOME_EVENT );
    }

    @Override
    public final void toEventString( StringBuilder builder ) {}


}
