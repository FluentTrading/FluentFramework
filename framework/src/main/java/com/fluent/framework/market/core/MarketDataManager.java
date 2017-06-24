package com.fluent.framework.market.core;
/*@formatter:off */

import org.slf4j.*;
import java.util.*;

import org.cliffc.high_scale_lib.*;

import com.fluent.framework.core.*;
import com.fluent.framework.events.core.*;
import com.fluent.framework.events.in.*;
import com.fluent.framework.market.adaptor.*;
import com.fluent.framework.market.event.*;
import com.fluent.framework.reference.core.*;

import static com.fluent.framework.events.core.FluentEventType.*;
import static com.fluent.framework.util.FluentUtil.*;


public final class MarketDataManager implements FluentLifecycle, FluentEventListener, MarketDataListener{

    private final MarketDataFilter                 mdFilter;
    private final FluentInEventDispatcher          inDispatcher;

    private final Map<String, Integer>             subsCounter;
    private final Map<String, MarketDataEvent>     mdCache;
    private final Map<Exchange, MarketDataAdapter> adaptorMap;

    private final static String                    NAME   = MarketDataManager.class.getSimpleName( );
    private final static Logger                    LOGGER = LoggerFactory.getLogger( NAME );


    public MarketDataManager( FluentConfiguration config, FluentInEventDispatcher inDispatcher ) throws FluentException{

        this.inDispatcher   = inDispatcher;
        this.mdCache        = new NonBlockingHashMap<>( );
        this.subsCounter    = new NonBlockingHashMap<>( );
        this.mdFilter       = new MarketDataFilter( config );
        this.adaptorMap     = MarketDataAdaptorFactory.createAdaptorMap( config );

    }


    @Override
    public final String name( ) {
        return NAME;
    }


    @Override
    public final boolean isSupported( FluentEventType type ) {
        return (MD_SUBSCRIBE_EVENT == type || MD_UNSUBSCRIBE_EVENT == type);
    }


    @Override
    public final void start( ) throws FluentException {

        for( MarketDataAdapter adaptor : adaptorMap.values( ) ){
            adaptor.register( this );
            adaptor.start( );
        }
        
        LOGGER.info( "Started MarketDataManager for {}.{}", adaptorMap.keySet( ), NEWLINE );
    }


    @Override
    public final void mdUpdate( MarketDataEvent event ) {

        FluentEventType type = event.getType( );

        switch( type ){

            case MARKET_DATA:
                processMarketData( event );
                break;

            case MARKET_STATUS:
                processMarketStatus( event );
                break;

            default:
                LOGGER.warn( "Event type:{} is unsupported.", type );
        }

    }


    protected final void processMarketData( FluentEvent event ) {

        MarketDataEvent mdEvent = (MarketDataEvent) event;
        String mdSymbol         = mdEvent.getSymbol( );

        MarketDataEvent prevMd  = mdCache.get( mdSymbol );
        boolean toFilterOut     = mdFilter.toFilter( prevMd, mdEvent );
        if( toFilterOut ) return;

        inDispatcher.enqueue( mdEvent );
        mdCache.put( mdSymbol, mdEvent );

    }


    protected final void processMarketStatus( FluentEvent statusEvent ){
        inDispatcher.enqueue( statusEvent );
    }



    @Override
    public final boolean update( FluentEvent event ) {

        FluentEventType type    = event.getType( );

        switch( type ){

            case MD_SUBSCRIBE_EVENT:
                handleSubscribe( event );
                break;

            case MD_UNSUBSCRIBE_EVENT:
                handleUnsubscribe( event );
                break;

            default:
                LOGGER.warn( "Event OutType:{} is unsupported.", type );

        }

        return true;

    }


    protected final void handleSubscribe( FluentEvent event ) {

        try{

            MarketDataSubscribeEvent sEvent = (MarketDataSubscribeEvent) event;
            RefDataEvent[ ] subEvents = sEvent.getReferenceEvents( );

            for( RefDataEvent subEvent : subEvents ){

                LOGGER.debug( "Attempting to subscribe for [{}]", sEvent );

                Exchange exchange = subEvent.getExchange( );
                MarketDataAdapter adaptor = adaptorMap.get( exchange );

                if( adaptor == null ){
                    LOGGER.warn( "FAILED to subscribe for [{}] as Adaptor is unconfigured.", exchange );
                    continue;
                }

                String mdSymbol = subEvent.getRicSymbol( );
                int subscriptionCounter = subsCounter.get( mdSymbol );
                if( subscriptionCounter > ZERO ){
                    ++subscriptionCounter;
                    sendLocalSnapshot( mdSymbol );

                    LOGGER.debug( "Subscription already exists, count incremented [{}].", subscriptionCounter );
                    subsCounter.put( mdSymbol, subscriptionCounter );
                    continue;
                }

                // Subscription doesn't exist, really subscribe
                boolean subscribed = adaptor.subscribe( subEvent );
                if( subscribed ){
                    subscriptionCounter = ONE;
                    subsCounter.put( mdSymbol, subscriptionCounter );
                    LOGGER.debug( "NEW Subscription created, count [{}].", subscriptionCounter );
                }

            }

        }catch( Exception e ){
            LOGGER.warn( "FAILED to subscribe for [{}]", event, e );
        }

    }


    protected final void sendLocalSnapshot( String mdSymbol ) {

        MarketDataEvent mdEvent = mdCache.get( mdSymbol );
        if( mdEvent == null ){
            LOGGER.warn( "Can't send local snapshot for [{}] as MS isn't  in cache.", mdSymbol );
            return;
        }

        inDispatcher.enqueue( mdEvent );
        LOGGER.debug( "Sent MD update from local cache.", mdEvent );

    }


    protected final void handleUnsubscribe( FluentEvent event ) {

        try{

            MarketDataUnsubscribeEvent sEvent = (MarketDataUnsubscribeEvent) event;
            RefDataEvent[ ] subEvents = sEvent.getReferenceEvents( );

            for( RefDataEvent subEvent : subEvents ){

                LOGGER.debug( "Attempting to unsubscribe for [{}]", sEvent );

                Exchange exchange = subEvent.getExchange( );
                MarketDataAdapter adaptor = adaptorMap.get( exchange );

                if( adaptor == null ){
                    LOGGER.warn( "FAILED to unsubscribe for [{}] as Adaptor is unconfigured.", exchange );
                    continue;
                }

                String mdSymbol = subEvent.getRicSymbol( );
                int unsubscriptionCount = subsCounter.get( mdSymbol );
                if( unsubscriptionCount == ZERO ){
                    LOGGER.warn( "FAILED to unsubscribe as we werent subscribed for [{}] on [{}].", mdSymbol, exchange );
                    continue;
                }

                // Subscription Count more than 1, reduce the count
                if( unsubscriptionCount > ZERO ){
                    --unsubscriptionCount;
                    subsCounter.put( mdSymbol, unsubscriptionCount );
                    LOGGER.debug( "Other subscription exists, count reduced [{}].", unsubscriptionCount );
                    continue;
                }

                // Subscription Count exactly 1, unsubscribe
                boolean unsubscribed = adaptor.unsubscribe( subEvent );
                if( unsubscribed ){
                    unsubscriptionCount = ZERO;
                    subsCounter.put( mdSymbol, unsubscriptionCount );
                    LOGGER.debug( "Successfully unsubscribed for [{}] on [{}].", mdSymbol, exchange );
                }

            }

        }catch( Exception e ){
            LOGGER.warn( "FAILED to unsubscribe for [{}]", event, e );
        }

    }


    @Override
    public final void stop( ) throws FluentException {
        for( MarketDataAdapter adaptor : adaptorMap.values( ) ){
            adaptor.stop( );
        }
    }



}
