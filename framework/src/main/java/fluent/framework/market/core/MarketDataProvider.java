package fluent.framework.market.core;


public enum MarketDataProvider {

    REUTERS,
    FILE,
    UNKNOWN;

    public final static MarketDataProvider getProvider( String name ) {
        return MarketDataProvider.valueOf( name );
    }

}
