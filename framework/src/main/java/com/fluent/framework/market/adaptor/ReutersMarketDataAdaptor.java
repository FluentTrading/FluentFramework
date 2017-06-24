package com.fluent.framework.market.adaptor;

import java.util.*;

import com.fluent.framework.market.core.*;
import com.fluent.framework.reference.*;


public final class ReutersMarketDataAdaptor extends MarketDataAdapter{



    public ReutersMarketDataAdaptor( MarketDataProvider provider, List<Exchange> exchanges ){
        super( provider, exchanges );
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onMessage( String message ) {
        // TODO Auto-generated method stub

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
    public boolean subscribe( ReferenceDataEvent event ) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean unsubscribe( ReferenceDataEvent event ) {
        // TODO Auto-generated method stub
        return false;
    }


}
