package com.fluent.framework.market.core;


public enum MarketDataProvider {

    REUTERS,
    ADMIN,
    UNKNOWN;

    public final static MarketDataProvider getProvider( String name ) {
        return MarketDataProvider.valueOf( name );
    }

}
