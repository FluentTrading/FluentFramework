package com.fluent.framework.events;
/*@formatter:off */

public enum FluentEventType{

    APP_STATE_EVENT,
    EXCHANGE_CLOSING,
    METRONOME_EVENT,

    NEW_STRATEGY,
    MODIFY_STRATEGY,
    CANCEL_STRATEGY,
    PAUSE_STRATEGY,
    STOP_STRATEGY ,

    MD_SUBSCRIBE_EVENT,
    MARKET_DATA,
    MARKET_STATUS,
    MD_UNSUBSCRIBE_EVENT,
    
    REFERENCE_DATA,
    EXECUTION_REPORT,    
    ORDER_TO_MARKET,
    CLIENT_UPDATE;

    
}
