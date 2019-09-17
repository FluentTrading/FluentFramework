package com.fluent.framework.events;
/*@formatter:off */

import static com.fluent.common.util.TimeUtils.*;
import static com.fluent.framework.events.FluentTags.*;
import static com.fluent.common.util.Constants.*;

import java.io.*;


public abstract class FluentEvent implements Serializable{

    private final long             sequenceId;
    private final long             creationTime;
    private final FluentEventType  eventType;
    
    private final static long      serialVersionUID = 1l;


    protected FluentEvent( long sequenceId, FluentEventType eventType ){
        this.sequenceId     = sequenceId;
        this.eventType      = eventType;
        this.creationTime   = currentNanos( );
    }


    public abstract void toEventString( StringBuilder builder );


    public final long getSequenceId( ){
        return sequenceId;
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
        builder.append( sequenceId ).append( COMMASP );
        toEventString( builder );

        // Footer
        builder.append( COMMASP );
        builder.append( EVENT_TYPE.field( ) ).append( COLON ).append( eventType.name( ) );

        return builder.toString( );

    }


}
