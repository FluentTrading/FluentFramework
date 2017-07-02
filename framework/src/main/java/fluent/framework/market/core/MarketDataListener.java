package fluent.framework.market.core;

import fluent.framework.market.event.*;

public interface MarketDataListener{

    public void mdUpdate( MarketDataEvent mdEvent );

}
