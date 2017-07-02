package fluent.framework.market.event;

import static fluent.framework.events.FluentEventSequencer.*;
import static fluent.framework.events.FluentEventType.*;

/* @formatter:Off */
import java.util.*;

import fluent.framework.events.*;
import fluent.framework.reference.core.*;


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
