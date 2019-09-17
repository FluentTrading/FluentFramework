package com.fluent.framework.market.adaptor;

import org.slf4j.*;

import java.util.*;

import com.fluent.framework.core.*;
import com.fluent.framework.market.core.*;
import com.typesafe.config.*;


public final class MarketDataAdaptorFactory{

    private final static String EXCHANGE    = "exchange";
    private final static String PROVIDER    = "provider";
    
    private final static String MARKET_KEY  = "fluent.mdAdaptors";
    private final static String NAME        = MarketDataAdaptorFactory.class.getSimpleName( );
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );


    public final static List< ? extends Config> getMDAdaptorConfigs( FluentConfig cfgManager ){
        return cfgManager.getRawConfig( ).getConfigList(  MARKET_KEY );
    }
    

    public final static Map<Exchange, MarketDataAdapter> createAdaptorMap( FluentConfig cfgManager )
            throws Exception {

        Map<Exchange, MarketDataAdapter> map = new HashMap<>( );

        for( Config adaptorConfig : getMDAdaptorConfigs(cfgManager) ){

            MarketDataAdapter adaptor   = null;
            List<Exchange> exchanges    = parseExchanges( adaptorConfig );
            String mdProviderName       = adaptorConfig.getString( PROVIDER );
            MarketDataProvider provider = MarketDataProvider.getProvider( mdProviderName );

            switch( provider ){

                case FILE:
                    adaptor = createFileMDAdaptor( exchanges, cfgManager );
                    break;
                    
                case REUTERS:
                    adaptor = createReutersMDAdaptor( exchanges, cfgManager );
                    break;

                default:
                    throw new Exception( "MD adapter unimplemented for " + provider );

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

    
    protected final static MarketDataAdapter createFileMDAdaptor( List<Exchange> exchanges, FluentConfig cfg ){
        return null;
    }
    

    protected final static MarketDataAdapter createReutersMDAdaptor( List<Exchange> exchanges, FluentConfig cfg ){
        return null;
    }


    private final static List<Exchange> parseExchanges( Config adaptorConfig ) throws Exception{

        List<Exchange> exchangeList = new ArrayList<>( );

        for( String exchangeStr : adaptorConfig.getStringList( EXCHANGE ) ){
            Exchange exchange = Exchange.fromCode( exchangeStr );

            if( Exchange.UNSUPPORTED == exchange ){
                throw new Exception( "Exchange: " + exchangeStr + " is invalid for MDAdaptor." );
            }
        }
        
        return exchangeList;
    
    }


}
