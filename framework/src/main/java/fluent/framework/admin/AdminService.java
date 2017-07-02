package fluent.framework.admin;
/*@formatter:off */

import org.slf4j.*;
import javax.management.*;
import java.lang.management.*;
import com.typesafe.config.*;
import com.j256.simplejmx.common.*;
import com.j256.simplejmx.server.*;

import fluent.framework.core.*;
import fluent.framework.events.*;
import fluent.framework.market.core.*;
import fluent.framework.market.event.*;
import fluent.framework.reference.core.*;
import fluent.framework.service.*;

import static fluent.framework.core.FluentConfig.*;
import static fluent.framework.util.Constants.*;
import static fluent.framework.util.Toolkit.*;


@JmxResource( description="Fluent Admin", domainName="fluent.framework.admin", beanName="AdminService")
public final class AdminService implements FluentService{
    
    private volatile boolean isRunning;
    private final JmxServer jmxServer;
    
    private final static String ADMIN_CFG   = TOP_SECTION_KEY + "adminClient";
    private final static String JMX_PORT_CFG= "jmxPort";
    
    private final static String NAME        = AdminService.class.getSimpleName( );
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );

    
    public AdminService( int port ) throws Exception{
        this.jmxServer  = createServer( port );
    }

    
    @Override
    public final String name( ){
        return NAME;
    }


    @Override
    public final FluentServiceType getType( ){
        return FluentServiceType.ADMIN_SERVICE;
    }

    
    @Override
    public final boolean isRunning( ){
        return isRunning;
    }
    
    @Override
    public final void start( ) throws Exception{

        try{
            
            if( jmxServer == null ) return;
            
            jmxServer.start( );
            jmxServer.register( this );
        
            this.isRunning  = true;
            LOGGER.info( "Successfully started jmx server{}", NEWLINE );
            
        }catch( Exception e ){
            LOGGER.warn( "FAILED to start jmx server.", e );
        }
                    
    }


    
    protected final JmxServer createServer( int port ){
        
        JmxServer jmxServer     = null;
        boolean isWindows       = IS_WINDOWS;
        
        try{
        
            if( isWindows ){
                jmxServer       = new JmxServer(port);
                LOGGER.info("Created new jmx server at port [{}]", port );
                return jmxServer;
            }
            
            //On Linux box, we expect the start-up script to set-up the MBeanServer
            MBeanServer bServer = ManagementFactory.getPlatformMBeanServer( );
            if( bServer != null ){
                jmxServer       = new JmxServer( bServer );
                LOGGER.info("Located jmx server using an exisiting MBeanServer at [{}]", jmxServer );
                return jmxServer;
            }
            
            LOGGER.warn("FAILED to locate an exisitng MBean server!");
            LOGGER.warn("[{}] will be unavailable for this session!", getType( ) );
            
        }catch( Exception e ){
            LOGGER.warn("FAILED to create jmx server at port [{}]", port, e );            
        }
        
        return jmxServer;
    }
    
/*
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
            ReferenceDataEvent ref  = refManager.get( key );
            if( ref == null ){
                message             = prefix + " discarded as there is no ref data for " + symbol;
                LOGGER.warn("{}", message );
                return message;
            }
            
            int instIndex           = ref.getIndex( );
            FluentEvent mdEvent     = new MarketDataEvent( instIndex, symbol, bid, bidSize, ask, askSize );
            mdManager.update( mdEvent );

            message = prefix + " successfully updated.";

        }catch( Exception e ){
            message = prefix + " failed!";
            LOGGER.warn( "{}", message, e );
        }

        return message;

    }
    */
    
    


    public final static Config parseAdminConfig( FluentConfig config ){
        return config.getSectionConfig( ADMIN_CFG );
        
    }


    public final static int parsePort( Config adminConfig ){
        return adminConfig.getInt( JMX_PORT_CFG );
    }

    


    @Override
    public final void stop( ) throws Exception{
        
        try{
            
            if( jmxServer != null ){
                jmxServer.stop( );
                this.isRunning  = false;
            }
            
            LOGGER.info( "Successfully stopped jmx server{}", NEWLINE );
            
        }catch( Exception e ){
            LOGGER.warn( "FAILED to stop jmx server.", e );
        }
    }

}
