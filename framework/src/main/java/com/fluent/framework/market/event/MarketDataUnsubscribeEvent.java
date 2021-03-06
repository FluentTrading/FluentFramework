package com.fluent.framework.market.event;
/*@formatter:off */
import java.util.*;

import com.fluent.framework.events.*;
import com.fluent.framework.reference.core.*;

import static com.fluent.framework.events.FluentEventSequencer.*;
import static com.fluent.framework.events.FluentEventType.*;


public final class MarketDataUnsubscribeEvent extends FluentEvent{

    private final ReferenceDataEvent[ ] refEvents;

    private static final long           serialVersionUID = 1L;


    public MarketDataUnsubscribeEvent( ReferenceDataEvent[ ] refEvents ){
        super( increment( ), MD_UNSUBSCRIBE_EVENT );

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
