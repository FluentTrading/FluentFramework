package fluent.framework.events;
/*@formatter:off */

import static fluent.framework.events.FluentTags.*;
import static fluent.framework.util.TimeUtils.*;
import static fluent.framework.util.Constants.*;

import java.io.*;


public abstract class FluentEvent implements Serializable{

    private final String                  eventId;
    private final long                    sequenceId;
    private final long                    creationTime;
    private final FluentEventType         eventType;
    
    private final static long             serialVersionUID = 1l;


    protected FluentEvent( long sequenceId, FluentEventType eventType ){
        this.sequenceId     = sequenceId;
        this.eventType      = eventType;
        this.creationTime   = currentNanos( );
        this.eventId        = getType( ) + UNDERSCORE + sequenceId;
    }


    public abstract void toEventString( StringBuilder builder );


    public final long getSequenceId( ){
        return sequenceId;
    }


    public final String getEventId( ){
        return eventId;
    }


    public final long getCreationTime( ){
        return creationTime;
    }


    public final FluentEventType getType( ){
        return eventType;
    }


    @Override
    public final String toString( ) {

        StringBuilder builder = new StringBuilder( EIGHT * SIXTY_FOUR );

        // Header
        builder.append( eventId ).append( COMMASP );
        toEventString( builder );

        // Footer
        builder.append( COMMASP );
        builder.append( EVENT_TYPE.field( ) ).append( COLON ).append( getType( ) );

        return builder.toString( );

    }


}
