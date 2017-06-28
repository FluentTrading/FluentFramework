package com.fluent.framework.market.event;
/*@formatter:off */
import java.util.*;

import com.fluent.framework.events.*;
import com.fluent.framework.reference.core.*;

import static com.fluent.framework.events.FluentEventSequencer.*;
import static com.fluent.framework.events.FluentEventType.*;


public final class MarketDataUnsubscribeEvent extends FluentEvent{

    private final RefDataEvent[ ] refEvents;

    private static final long           serialVersionUID = 1L;


    public MarketDataUnsubscribeEvent( RefDataEvent[ ] refEvents ){
        super( increment( ), MD_UNSUBSCRIBE_EVENT );

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
