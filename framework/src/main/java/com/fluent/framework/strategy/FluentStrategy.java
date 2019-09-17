package com.fluent.framework.strategy;

import com.fluent.framework.core.*;
import com.fluent.framework.events.*;


public abstract class FluentStrategy implements FluentLifecycle{

    private final String       strategyId;

    public FluentStrategy( String strategyId ){
        this.strategyId = strategyId;
    }


    public abstract void update( FluentEvent event );


    public final String getStrategyId( ) {
        return strategyId;
    }


    protected final boolean marketUpdateRequired( String instrument, String[ ] legInstruments ) {
        for( String legInstrument : legInstruments ){
            if( legInstrument.equalsIgnoreCase( instrument ) ){
                return true;
            }
        }

        return false;
    }


}
