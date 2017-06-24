package com.fluent.framework.reference.core;
/*@formatter:off */
import com.fluent.framework.events.core.*;
import com.fluent.framework.market.core.*;

import static com.fluent.framework.events.core.FluentEventType.*;
import static com.fluent.framework.events.core.FluentEventSequencer.*;



public final class RefDataEvent extends FluentEvent{

    private final int index;
    private final String instKey;
    private final Exchange exchange;
    private final SpreadType spreadType;
    private final InstrumentSubType instSubType;
    private final String ricSymbol;
    private final String exchangeSymbol;
    private final String expiryDate;
    private final double tickSize;
    private final double lotSize;
    private final double pointValue;


    private static final long serialVersionUID = 1L;


    public RefDataEvent(  int index, String instKey, Exchange exchange, SpreadType spreadType,
                          InstrumentSubType instSubType,
                          String ricSymbol, String exchangeSymbol, String expiryDate,
                          double tickSize, double lotSize, double pointValue ){
        
        super( increment(), REFERENCE_DATA );
        
        this.index          = index;
        this.instKey        = instKey;
        this.exchange       = exchange;
        this.spreadType     = spreadType;
        this.instSubType    = instSubType;
        
        this.ricSymbol      = ricSymbol;
        this.exchangeSymbol = exchangeSymbol;
        this.expiryDate     = expiryDate;
        this.tickSize       = tickSize;
        this.lotSize        = lotSize;
        this.pointValue     = pointValue;
        
    }


    public final int getIndex( ){
        return index;
    }
    
    
    public final String getKey( ){
        return instKey;
    }
    
    
    public final Exchange getExchange( ){
        return exchange;
    }


    public final SpreadType getSpreadType( ){
        return spreadType;
    }


    public final InstrumentSubType getInstSubType( ){
        return instSubType;
    }

    

    public final String getRicSymbol( ){
        return ricSymbol;
    }


    public final String getExchangeSymbol( ){
        return exchangeSymbol;
    }


    public final String getExpiryDate( ){
        return expiryDate;
    }


    public final double getTickSize( ){
        return tickSize;
    }


    public final double getLotSize( ){
        return lotSize;
    }


    public final double getPointValue( ){
        return pointValue;
    }

    
    @Override
    public final void toEventString( StringBuilder builder ){
        
        builder.append( "Exchange=" ).append( exchange );
        builder.append( ", SpreadType=" ).append( spreadType );
        builder.append( ", InstrumentSubType=" ).append( instSubType );
        builder.append( ", RicSymbol=" ).append( ricSymbol );
        builder.append( ", ExchangeSymbol=" ).append( exchangeSymbol );
        builder.append( ", ExpiryDate=" ).append( expiryDate );
        builder.append( ", TickSize=" ).append( tickSize );
        builder.append( ", LotSize=" ).append( lotSize );
        builder.append( ", PointValue=" ).append( pointValue );
        builder.append( ", InstKey=" ).append( instKey );
        builder.append( ", InstIndex=" ).append( index );
        builder.append( "]" );
    
    }

}
