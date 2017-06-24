package com.fluent.framework.market.event;
/*@formatter:off */

import com.fluent.framework.events.core.*;

import static com.fluent.framework.events.core.FluentEventType.*;
import static com.fluent.framework.util.FluentUtil.*;


public final class MarketDataEvent extends FluentEvent{

    private final String            symbol;
    private final int               instIndex;
    private final double            bid;
    private final int               bidSize;
    private final double            ask;
    private final int               askSize;

    private final static long       serialVersionUID = 1l;


    public MarketDataEvent( int instIndex, String symbol, double bid, int bidSize, double ask, int askSize ){

        super( FluentEventSequencer.increment( ), MARKET_DATA );

        this.instIndex  = instIndex;
        this.symbol     = symbol;

        this.bid        = bid;
        this.bidSize    = bidSize;
        this.ask        = ask;
        this.askSize    = askSize;

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
        builder.append( symbol ).append( COMMA );
        builder.append( bidSize ).append( X_SPACE ).append( bid );
        builder.append( TAB );
        builder.append( askSize ).append( X_SPACE ).append( ask );
    }


}
