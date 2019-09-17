package com.fluent.framework.events;
/*@formatter:off */


public enum FluentTags{

    // HEADER
    EVENT_ID            ( "EventId" ),
    EVENT_TYPE          ( "EventType" ),
    TIMESTAMP           ( "Timestamp" ),
    SEQUENCE            ( "Sequence" ),
    EVENT_CATEGORY      ( "EventCategory" ),

    // Admin
    ADMIN_ID            ( "AdminId" ),
    REASON              ( "Reason" ),
    USER                ( "User" ),

    REF_DATA_TAG        ( "RefData" ),

    // Trader Request Header
    STRATEGY_NAME       ( "StrategyName" ),
    STRATEGY_OWNER      ( "StrategyOwner" ),
    STRATEGY_SIDE       ( "StrategySide" ),
    STRATEGY_LEG_COUNT  ( "StrategyLegCount" ),
    MEQ                 ( "Meq" ),
    SPREAD              ( "Spread" ),
    SIDES               ( "Sides" ),
    QUANTITIES          ( "Quantities" ),
    INSTRUMENTS         ( "Instruments" ),
    MARKETS             ( "Markets" ),
    WORKING             ( "Working" ),
    SLICES              ( "Slice" ),
    INSTRUMENT_TYPE     ( "InstrumentType" ),

    // Market Data Header
    EXCHANGE            ( "Exchange" ),
    SYMBOL              ( "Symbol" ),
    BID                 ( "Bid" ),
    BIDSIZE             ( "BidSize" ),
    ASK                 ( "Ask" ),
    ASKSIZE             ( "AskSize" ),

    // Order Header
    ORDER_ID            ( "OrderId" ),
    ORDER_EXTERNAL_ID   ( "ExternalId" ),
    ORDER_TYPE          ( "OrderType" ),
    FILL_STATUS         ( "FillStatus" ),
    IS_REJECTED         ( "IsRejected" ),
    REJECTED_REASON     ( "RejectedReason" ),
    SIDE                ( "Side" ),
    PRICE               ( "Price" ),
    SEND_QUANTITY       ( "SendQuantity" ),
    SHOW_QUANTITY       ( "ShowQuantity" ),
    TRADER_ID           ( "TraderId" ),
    TRADER_NAME         ( "TraderName" ),
    PORTFOLIO           ( "Portfolio" ),

    STRATEGY_ID         ( "StrategyId" ),
    STRATEGY_TYPE       ( "StrategyType" ),

    // Execution Header
    EXECUTION_QUANTITY  ( "ExecutionQuantity" ),
    EXECUTION_PRICE     ( "ExecutionPrice" ),

    // LOOPBACK
    UPDATE_MESSAGE      ( "UpdateMessage" ),
    INPUT_EVENT_ID      ( "InputEventId" ),
    INPUT_EVENT_TYPE    ( "InputEventType" );


    private final String field;

    private FluentTags( String field ){
        this.field = field;
    }


    public final String field( ) {
        return field;
    }

}
