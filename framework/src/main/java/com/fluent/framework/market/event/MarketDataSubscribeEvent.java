package com.fluent.framework.market.event;

/* @formatter:Off */
import java.util.*;

import com.fluent.framework.events.core.*;
import com.fluent.framework.reference.core.*;

import static com.fluent.framework.events.core.FluentEventSequencer.*;
import static com.fluent.framework.events.core.FluentEventType.*;


public final class MarketDataSubscribeEvent extends FluentEvent{

    private final RefDataEvent[ ] refEvents;

    private static final long           serialVersionUID = 1L;


    public MarketDataSubscribeEvent( RefDataEvent[ ] refEvents ){
        super( increment( ), MD_SUBSCRIBE_EVENT );

        this.refEvents = refEvents;

    }


    public final RefDataEvent[ ] getReferenceEvents( ) {
        return refEvents;
    }


    @Override
    public final void toEventString( StringBuilder builder ) {
        builder.append( Arrays.deepToString( refEvents ) );
    }


}
