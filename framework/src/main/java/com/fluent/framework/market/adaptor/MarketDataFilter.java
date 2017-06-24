package com.fluent.framework.market.adaptor;

import com.fluent.framework.core.*;
import com.fluent.framework.market.event.*;


public final class MarketDataFilter{

    private final FluentConfigManager config;

    public MarketDataFilter( FluentConfigManager config ){
        this.config = config;
    }


    public final boolean toFilter( MarketDataEvent prevMDEvent, MarketDataEvent currMDEvent ) {
        return false;
    }


}
