package com.fluent.framework.market.adaptor;

import java.util.*;

import com.fluent.framework.events.core.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.reference.core.*;


public final class ReutersMarketDataAdaptor extends MarketDataAdapter{



    public ReutersMarketDataAdaptor( MarketDataProvider provider, List<Exchange> exchanges ){
        super( provider, exchanges );
        // TODO Auto-generated constructor stub
    }

    
    @Override
    public boolean update( FluentEvent event ){
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void start( ) {
        // TODO Auto-generated method stub

    }

    @Override
    public void stop( ) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean subscribe( RefDataEvent event ) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean unsubscribe( RefDataEvent event ) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSupported( FluentEventType type ) {
        // TODO Auto-generated method stub
        return false;
    }


}
