package fluent.framework.events;
/*@formatter:off */

import static fluent.framework.events.FluentEventDirection.*;


public enum FluentEventCategory{

    FROM_ADMIN          ( INCOMING ),
    FROM_INTERNAL       ( INCOMING ),
    FROM_CLIENT         ( INCOMING ),
    FROM_EXCHANGE       ( INCOMING ),
    FROM_REFDATA_SERVICE( INCOMING ), 
    
    TO_CLIENT           ( OUTGOING ),
    TO_MARKET           ( OUTGOING );
    
    
    private final FluentEventDirection direction;
    
    private FluentEventCategory( FluentEventDirection direction ){
        this.direction = direction;
    }
    
    
    public final FluentEventDirection getDirection( ){
        return direction;
    }
    
    
    public final boolean isInbound( ){
        return ( INCOMING == direction );
    }
    
    
    public final boolean isOutbound( ){
        return !isInbound( );
    }
    
    
}
