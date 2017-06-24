package com.fluent.framework.admin.command;

/* @formatter:Off */
import org.slf4j.*;
import javax.management.*;
import java.lang.management.*;
import com.j256.simplejmx.common.*;
import com.j256.simplejmx.server.*;
import com.fluent.framework.core.*;
import com.fluent.framework.events.core.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.market.event.*;
import com.fluent.framework.reference.core.*;

import static com.fluent.framework.util.FluentToolkit.*;
import static com.fluent.framework.util.FluentUtil.*;


public final class AdminCommandAdaptor implements FluentLifecycle{

    private final JmxServer      jmxServer;
    private final FluentServices services;
    private final RefDataManager refManager;

    private final static String  NAME   = AdminCommandAdaptor.class.getSimpleName( );
    private final static Logger  LOGGER = LoggerFactory.getLogger( NAME );


    public AdminCommandAdaptor( FluentServices services ){
        this.services   = services;
        this.refManager = services.getReferenceManager( );
        this.jmxServer  = getJMXServer( );
    }


    @Override
    public final String name( ) {
        return NAME;
    }



    @Override
    public final void start( ) throws FluentException {
        try{
            jmxServer.start( );
            jmxServer.register( this );

            LOGGER.info( "Successfully started ADMIN JMX server at at [{}]", jmxServer.getServerPort( ) );

        }catch( Exception e ){
            throw new FluentException( e );
        }
    }


    @JmxOperation( description = "Feed Market Data" )
    public final String feedMarketData( String data ) {

        String message              = EMPTY;
        String prefix               = "Admin feedMarketData()";

        try{
            
            boolean isInvalid       = isBlank( data );
            if( isInvalid ){
                message = prefix + " discarded as Invalid data supplied";
                LOGGER.warn( "{}", message );
                return message;
            }

            String[ ] dataArray     = data.split( PIPE );
            
            int index               = ZERO;
            Exchange exchange       = Exchange.fromCode( dataArray[index++] );
            String symbol           = dataArray[index++];
            double bid              = Double.parseDouble( dataArray[index++] );
            int bidSize             = Integer.parseInt( dataArray[index++] );
            double ask              = Double.parseDouble( dataArray[index++] );
            int askSize             = Integer.parseInt( dataArray[index++] );
            String key              = refManager.getKey( exchange, symbol );
            RefDataEvent ref        = refManager.get( key );
            if( ref == null ){
                message             = prefix + " discarded as there is no ref data for " + symbol;
                LOGGER.warn("{}", message );
                return message;
            }
            
            int instIndex           = ref.getIndex( );
            FluentEvent mdEvent     = new MarketDataEvent( instIndex, symbol, bid, bidSize, ask, askSize );
            services.getMdManager( ).update( mdEvent );

            message = prefix + " successfully updated.";

        }catch( Exception e ){
            message = prefix + " failed!";
            LOGGER.warn( "{}", message, e );
        }

        return message;

    }


    protected final JmxServer getJMXServer( ) {

        JmxServer jmxServer     = null;

        try{

            MBeanServer bServer = ManagementFactory.getPlatformMBeanServer( );
            if( bServer == null ){
                LOGGER.info( "FOUND an existing Managed Bean Server." );
                return null;
            }

            jmxServer           = new JmxServer( bServer );

        }catch( Exception e ){
            LOGGER.warn( "FAILED to create JMXserver.", e );
        }

        return jmxServer;

    }


    @Override
    public final void stop( ){
        jmxServer.stop( );
        LOGGER.info( "Successfully stopped JMX server." );
    }

}
