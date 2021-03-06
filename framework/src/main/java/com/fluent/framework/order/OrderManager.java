package com.fluent.framework.order;
import org.slf4j.*;

import com.fluent.framework.collection.*;
import com.fluent.framework.core.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.service.*;

public final class OrderManager implements FluentService{

    private volatile boolean isRunning;
    
    private final static String NAME   = MarketDataManager.class.getSimpleName( );
    private final static Logger LOGGER = LoggerFactory.getLogger( NAME );

    
    public OrderManager( FluentConfig config, FluentInDispatcher inDispatcher ){
        
    }


    @Override
    public final String name( ){
        return NAME;
    }
    
    
    @Override
    public final boolean isRunning( ){
        return isRunning;
    }
    

    @Override
    public final FluentServiceType getType( ){
        return FluentServiceType.ORDER_SERVICE;
    }

    
    @Override
    public final void start( ) throws Exception{
        this.isRunning  = true;
    }


    @Override
    public final void stop( ) throws Exception{
        this.isRunning  = false;
    }
    
    
    
}
