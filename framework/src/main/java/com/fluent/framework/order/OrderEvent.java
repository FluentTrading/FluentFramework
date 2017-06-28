package com.fluent.framework.order;

import com.fluent.framework.events.*;
import com.fluent.framework.market.core.Exchange;

import static com.fluent.framework.events.FluentTags.*;
import static com.fluent.framework.events.FluentEventSequencer.*;
import static com.fluent.framework.events.FluentEventType.*;


public final class OrderEvent extends FluentEvent{

	private final String eventId;
    private final String strategyId;
    private final String orderId;
    private final String symbol;
    private final OrderType orderType;
    private final Exchange exchange;

    private final Side side;
    private final double price;
    private final int sendQuantity;
    private final int showQuantity;
    private final String traderId;
    private final String traderName;
    private final String portfolio;

    private final static String PREFIX = "ORDER_";
    private final static long serialVersionUID = 1l;
    


    public OrderEvent( String strategyId, String orderId, Exchange exchange, OrderType orderType,
                       Side side, String symbol, double price, int sendQuantity, int showQuantity, String traderId,
                       String traderName, String portfolio ){

        super( increment(), ORDER_TO_MARKET );

        this.eventId     	= PREFIX + getSequenceId();
        this.strategyId     = strategyId;
        this.orderId     	= orderId;
        this.exchange	    = exchange;
        this.orderType      = orderType;
        this.side           = side;
        this.symbol   		= symbol;
        this.price          = price;
        this.sendQuantity   = sendQuantity;
        this.showQuantity   = showQuantity;
        this.traderId       = traderId;
        this.traderName     = traderName;
        this.portfolio      = portfolio;

    }

    
    public final String getStrategyId( ){
        return strategyId;
    }

    
    public final String getOrderId( ){
        return orderId;
    }

    
    public final String getSymbol( ){
        return symbol;
    }
    

    public final OrderType getOrderType( ){
        return orderType;
    }
    

    public final Exchange getExchange( ){
        return exchange;
    }
    

    public final Side getSide(){
        return side;
    }
    

    public final double getPrice( ){
        return price;
    }

    
    public final int getSendQuantity( ){
        return sendQuantity;
    }

    public final String getTraderId( ){
        return traderId;
    }
    

    public final int getShowQuantity(){
        return showQuantity;
    }


    public final String getTraderName(){
        return traderName;
    }


    public final String getPortfolio(){
        return portfolio;
    }

    
    @Override
    public final void toEventString( StringBuilder object ){

        object.append( STRATEGY_ID.field()).append( getStrategyId() );
        object.append( ORDER_ID.field()).append(getOrderId() );
        object.append( EXCHANGE.field()).append( getExchange().name() );
        object.append( ORDER_TYPE.field()).append( getOrderType().name() );
        object.append( SIDE.field()).append(getSide().name() );
        object.append( SYMBOL.field()).append(getSymbol() );
        object.append( PRICE.field()).append( getPrice() );
        object.append( SEND_QUANTITY.field()).append(getSendQuantity() );
        object.append( SHOW_QUANTITY.field()).append(getShowQuantity() );
        object.append( TRADER_ID.field()).append(getTraderId() );
        object.append( TRADER_NAME.field()).append(getTraderName() );
        object.append( PORTFOLIO.field()).append(getPortfolio());

    }

    
}
