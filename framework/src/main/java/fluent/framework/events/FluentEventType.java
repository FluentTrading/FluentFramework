package fluent.framework.events;
/*@formatter:off */

import static fluent.framework.events.FluentEventCategory.*;

public enum FluentEventType{

    APP_STATE_EVENT         ( FROM_INTERNAL ),
    EXCHANGE_CLOSING        ( FROM_INTERNAL ),
    METRONOME_EVENT         ( FROM_INTERNAL ),

    NEW_STRATEGY            ( FROM_CLIENT ),
    MODIFY_STRATEGY         ( FROM_CLIENT ),
    CANCEL_STRATEGY         ( FROM_CLIENT ),
    PAUSE_STRATEGY          ( FROM_CLIENT ),
    STOP_STRATEGY           ( FROM_CLIENT ),

    MARKET_DATA             ( FROM_EXCHANGE ),
    MARKET_STATUS           ( FROM_EXCHANGE ),
    TRADE_DATA              ( FROM_EXCHANGE ),
    
    EXECUTION_REPORT        ( FROM_EXCHANGE ),

    REFERENCE_DATA          ( FROM_REFDATA_SERVICE),
    
    MD_SUBSCRIBE_EVENT      ( FROM_INTERNAL ),
    MD_UNSUBSCRIBE_EVENT    ( FROM_INTERNAL ),
    ORDER_TO_MARKET         ( TO_MARKET ),
    EVENT_TO_CLIENT         ( TO_CLIENT );


    private final FluentEventCategory category;
    
    private FluentEventType( FluentEventCategory category ){
        this.category = category;
    }

    
    public final FluentEventCategory getCategory( ){
        return category;
    }
    
}
