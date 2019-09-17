package com.fluent.framework.market.event;
/*@formatter:off */

import com.fluent.framework.events.*;
import com.fluent.framework.market.core.*;

import static com.fluent.common.util.Constants.*;
import static com.fluent.framework.events.FluentEventType.*;


public final class MarketDataEvent extends FluentEvent{

    private final int       exchangeIndex;
    private final String    symbol;
    private final int       instIndex;
    private final double    bid;
    private final int       bidSize;
    private final double    ask;
    private final int       askSize;

    private final static long serialVersionUID = 1l;


    public MarketDataEvent( Exchange exchange, int instIndex, String symbol, double bid, int bidSize, double ask, int askSize ){

        super( FluentEventSequencer.increment( ), MARKET_DATA );

        this.exchangeIndex = exchange.getIndex( );
        this.instIndex  = instIndex;
        this.symbol     = symbol;

        this.bid        = bid;
        this.bidSize    = bidSize;
        this.ask        = ask;
        this.askSize    = askSize;

    }

    

    public final int getExchangeIndex( ){
        return exchangeIndex;
    }
    

    public final String getSymbol( ){
        return symbol;
    }


    public final int getInstrumentIndex( ){
        return instIndex;
    }


    public final double getBid( ){
        return bid;
    }


    public final int getBidSize( ){
        return bidSize;
    }


    public final double getAsk( ){
        return ask;
    }


    public final int getAskSize( ){
        return askSize;
    }

    
    @Override
    public final void toEventString( StringBuilder builder ) {
        builder.append( exchangeIndex ).append( COMMA );
        builder.append( symbol ).append( COMMA );
        builder.append( bidSize ).append( X_SPACE ).append( bid );
        builder.append( TAB );
        builder.append( askSize ).append( X_SPACE ).append( ask );
    }


}
