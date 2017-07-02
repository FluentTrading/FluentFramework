package fluent.framework.market.event;
import static fluent.framework.events.FluentEventSequencer.*;
import static fluent.framework.events.FluentEventType.*;

/*@formatter:off */
import java.util.*;

import fluent.framework.events.*;
import fluent.framework.reference.core.*;


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
