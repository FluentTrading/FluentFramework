package com.fluent.framework.admin;
/*@formatter:off */

import static com.fluent.common.util.Toolkit.*;
import static com.fluent.common.util.Constants.*;


public enum AdminCommand {

    SHOW_INSTRUMENT     ( "showInstrument", "Prints instrument data." ),
    FEED_MARKET_DATA    ( "feedMD",         "Inputs market data." ),
    STOP_STRATEGY       ( "stopStrategy",   "Stops a running strategy" ),
    UNKNOWN_COMMAND     ( EMPTY,            EMPTY );

    private final String methodName;
    private final String usageHelp;


    private AdminCommand( String methodName, String usageHelp ){
        this.methodName = methodName;
        this.usageHelp  = usageHelp;
    }

    
    public final String getMethodName( ){
        return methodName;
    }
    

    public final String getUsageHelp( ){
        return usageHelp;
    }

    
    public final static void printCommands( ){
        
        for( AdminCommand cmd : AdminCommand.values( ) ){
            String showCmd  = padRight( cmd.methodName + " ", 25) + padRight( cmd.usageHelp, 50 );
            System.out.println(  showCmd );
        }
        
    }

    
    public final static AdminCommand parseCommands( String method ){
        
        for( AdminCommand cmd : AdminCommand.values( ) ){
            if( cmd.methodName.equalsIgnoreCase(method) ){
                return cmd;
            }            
        }
        
        return AdminCommand.UNKNOWN_COMMAND;
    }

}
