package fluent.framework.metronome;
/*@formatter:off */
import fluent.framework.events.*;

import static fluent.framework.events.FluentEventType.*;
import static fluent.framework.events.FluentEventSequencer.*;


public final class MetronomeEvent extends FluentEvent{

    private final static long serialVersionUID = 1L;


    public MetronomeEvent( ){
        super( increment( ), METRONOME_EVENT );
    }

    @Override
    public final void toEventString( StringBuilder builder ) {}


}
