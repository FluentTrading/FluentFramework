package com.fluent.framework.market.core;

import com.fluent.framework.market.event.*;

public interface MarketDataListener{

    public void mdUpdate( MarketDataEvent mdEvent );

}
