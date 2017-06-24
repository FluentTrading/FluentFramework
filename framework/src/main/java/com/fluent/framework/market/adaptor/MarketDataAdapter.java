package com.fluent.framework.market.adaptor;

import org.slf4j.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import com.fluent.framework.core.*;
import com.fluent.framework.events.core.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.reference.core.*;



public abstract class MarketDataAdapter implements FluentEventListener, FluentLifecycle{

    private final List<Exchange>                     exchanges;
    private final MarketDataProvider                 provider;
    private final AtomicReference<MarketDataManager> managerRef;

    private final static String                      NAME   = MarketDataAdapter.class.getSimpleName( );
    private final static Logger                      LOGGER = LoggerFactory.getLogger( NAME );


    public MarketDataAdapter( MarketDataProvider provider, List<Exchange> exchanges ){
        this.provider = provider;
        this.exchanges = exchanges;
        this.managerRef = new AtomicReference<>( );

    }


    @Override
    public final String name( ) {
        return NAME;
    }


    public final void register( MarketDataManager manager ) {
        managerRef.set( manager );
        LOGGER.debug( "Registered [{}] as listener for MarketDataAdapter.", manager.name( ) );
    }


    public final void unregister( MarketDataManager manager ) {
        managerRef.set( null );
        LOGGER.debug( "UnRegistered [{}] as listener for MarketDataAdapter.", manager.name( ) );
    }


    public abstract boolean subscribe( RefDataEvent event );

    public abstract boolean unsubscribe( RefDataEvent event );



    public final List<Exchange> getExchanges( ) {
        return exchanges;
    }


    public final MarketDataProvider getProvider( ) {
        return provider;
    }


}
