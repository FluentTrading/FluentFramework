package com.fluent.framework.admin.command;

import static com.fluent.framework.util.FluentUtil.*;


public enum AdminCommand {

    ADMIN_FEED_MARKET_DATA( "feedMarketData", "Inputs market data." ),
    ADMIN_STOP_STRATEGY( "stopStrategy", "Stops a running strategy" ),
    ADMIN_UNKNOWN_COMMAND( EMPTY, EMPTY );


    private final String methodName;
    private final String usageHelp;

    private AdminCommand( String methodName, String usageHelp ){
        this.methodName = methodName;
        this.usageHelp = usageHelp;
    }

    public final String getMethodName( ) {
        return methodName;
    }

    public final String getUsageHelp( ) {
        return usageHelp;
    }


}
