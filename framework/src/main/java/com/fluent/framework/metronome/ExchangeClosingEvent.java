package com.fluent.framework.metronome;
/*@formatter:off */

import com.fluent.framework.events.*;
import com.fluent.framework.market.core.*;

import static com.fluent.framework.events.FluentEventSequencer.*;
import static com.fluent.framework.events.FluentEventType.*;


public final class ExchangeClosingEvent extends FluentEvent{

    private final Exchange    exchange;
    private final TimedTask   timeTask;

    private final static long serialVersionUID = 1L;


    public ExchangeClosingEvent( Exchange exchange, TimedTask timeTask ){
        super( increment( ), EXCHANGE_CLOSING );

        this.exchange = exchange;
        this.timeTask = timeTask;
    }


    public final Exchange getExchange( ) {
        return exchange;
    }


    public final TimedTask getTimedTask( ) {
        return timeTask;
    }


    public final boolean isClosing( ) {
        return timeTask.isClosing( );
    }


    @Override
    public final void toEventString( StringBuilder builder ) {
        builder.append( "Exchange:" ).append( exchange );
        builder.append( ", TimedTask:" ).append( timeTask );

    }


}
