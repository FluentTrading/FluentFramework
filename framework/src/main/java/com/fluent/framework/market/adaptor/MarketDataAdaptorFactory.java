package com.fluent.framework.market.adaptor;

import org.slf4j.*;

import java.util.*;

import com.fluent.framework.core.*;
import com.fluent.framework.market.core.*;
import com.typesafe.config.*;


public final class MarketDataAdaptorFactory{

    private final static String EXCHANGE = "exchange";
    private final static String PROVIDER = "provider";

    private final static String NAME     = MarketDataAdaptorFactory.class.getSimpleName( );
    private final static Logger LOGGER   = LoggerFactory.getLogger( NAME );



    protected final static Map<Exchange, MarketDataAdapter> createAdaptorMap( FluentConfigManager cfgManager )
            throws FluentException {

        Map<Exchange, MarketDataAdapter> map = new HashMap<>( );

        for( Config adaptorConfig : cfgManager.getMDAdaptorConfigs( ) ){

            MarketDataAdapter adaptor = null;
            List<Exchange> exchanges = parseExchanges( adaptorConfig );
            String mdProviderName = adaptorConfig.getString( PROVIDER );
            MarketDataProvider provider = MarketDataProvider.getProvider( mdProviderName );

            switch( provider ){

                case REUTERS:
                    adaptor = createReutersMDAdaptor( exchanges, cfgManager );
                    break;

                case ADMIN:
                    adaptor = createAdminMDAdaptor( exchanges, cfgManager );
                    break;

                default:
                    throw new FluentException( "MarketDataAdapter unimplemented for " + provider );

            }

            if( adaptor == null )
                continue;

            for( Exchange exchange : exchanges ){

                boolean alreadyExists = map.containsKey( exchange );
                if( alreadyExists ){
                    LOGGER.error( "Market data adaptor for Exchange: " + exchange + " was already created." );
                    throw new RuntimeException( );
                }

                map.put( exchange, adaptor );
                LOGGER.info( "Market data adaptor created for Exchange: " + exchange );

            }

        }

        return map;

    }


    protected final static MarketDataAdapter createReutersMDAdaptor( List<Exchange> exchanges,
            FluentConfigManager cfgManager ) {
        return null;
    }


    protected final static MarketDataAdapter createAdminMDAdaptor( List<Exchange> exchanges,
            FluentConfigManager cfgManager ) {
        return null;
    }


    private final static List<Exchange> parseExchanges( Config adaptorConfig ) throws FluentException {

        List<Exchange> exchangeList = new ArrayList<>( );

        for( String exchangeStr : adaptorConfig.getStringList( EXCHANGE ) ){
            Exchange exchange = Exchange.fromCode( exchangeStr );

            if( Exchange.UNSUPPORTED == exchange ){
                throw new FluentException( "Exchange: " + exchangeStr + " is invalid for MDAdaptor." );
            }
        }
        return exchangeList;
    }


}
