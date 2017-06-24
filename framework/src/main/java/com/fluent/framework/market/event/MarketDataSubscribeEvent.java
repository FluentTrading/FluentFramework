package com.fluent.framework.market.event;

/* @formatter:Off */
import java.util.*;

import com.fluent.framework.events.core.*;
import com.fluent.framework.reference.*;

import static com.fluent.framework.events.core.FluentEventSequencer.*;
import static com.fluent.framework.events.core.FluentEventType.*;


public final class MarketDataSubscribeEvent extends FluentEvent{

    private final ReferenceDataEvent[ ] refEvents;

    private static final long           serialVersionUID = 1L;


    public MarketDataSubscribeEvent( ReferenceDataEvent[ ] refEvents ){
        super( increment( ), MD_SUBSCRIBE_EVENT );

        this.refEvents = refEvents;

    }


    public final ReferenceDataEvent[ ] getReferenceEvents( ) {
        return refEvents;
    }


    @Override
    public final void toEventString( StringBuilder builder ) {
        builder.append( Arrays.deepToString( refEvents ) );
    }


}
