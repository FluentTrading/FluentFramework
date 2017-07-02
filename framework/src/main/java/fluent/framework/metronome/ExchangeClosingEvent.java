package fluent.framework.metronome;
/*@formatter:off */

import fluent.framework.events.*;
import fluent.framework.market.core.*;

import static fluent.framework.events.FluentEventSequencer.*;
import static fluent.framework.events.FluentEventType.*;


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
