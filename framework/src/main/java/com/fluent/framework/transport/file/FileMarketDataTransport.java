package com.fluent.framework.transport.file;
/*@formatter:off */
import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.fluent.common.collections.*;
import com.fluent.framework.events.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.market.event.*;
import com.fluent.framework.transport.core.*;

import static com.fluent.common.util.TimeUtils.*;
import static com.fluent.common.util.Constants.*;
import static com.fluent.common.util.Toolkit.*;




public final class FileMarketDataTransport extends AbstractTransport implements Runnable{

    private volatile int                   index;

    private final String                   fileLocation;
    private final int                      frequency;
    private final TimeUnit                 timeUnit;
    private final List<String>             dataList;
    private final ScheduledExecutorService executor;

    private final static int               DEFAULT_FREQUENCY = ONE;
    private final static TimeUnit          DEFAULT_TIMEUNIT  = TimeUnit.SECONDS;
    private final static String            NAME              = FileMarketDataTransport.class.getSimpleName( );
    private final static Logger            LOGGER            = LoggerFactory.getLogger( NAME );


    public FileMarketDataTransport( String fileLocation ){
        this( fileLocation, DEFAULT_FREQUENCY, DEFAULT_TIMEUNIT );
    }


    public FileMarketDataTransport( String fileLocation, int frequency, TimeUnit timeUnit ){
        super( TransportType.FILE );

        this.fileLocation   = notBlank( fileLocation, "File location must be valid." );
        this.frequency      = notNegative( frequency, "Frequency must be positive." );
        this.timeUnit       = timeUnit;
        this.dataList       = loadData( );
        this.executor       = Executors.newSingleThreadScheduledExecutor( new FluentThreadFactory( NAME ) );

    }


    @Override
    public final String name( ) {
        return NAME;
    }


    @Override
    public final boolean isConnected( ) {
        return true;
    }


    @Override
    public final void start( ) {
        executor.scheduleAtFixedRate( this, frequency, 5 * frequency, timeUnit );
        LOGGER.info( "Publisher type [{}] started, will publish prices every {} {}.", getType( ), frequency, timeUnit );
    }



    @Override
    public final void run( ) {

        String message      = dataList.get( index );
        //int instIndex, String symbol, double bid, int bidSize, double ask, int askSize
        FluentEvent event   = new MarketDataEvent( Exchange.CME, 1, "EDZ19", 99.90, 900, 100.0, 1000 ); 
        distribute( event );
        ++index;

    }



    protected final List<String> loadData( ) {

        FileReader reader       = null;
        BufferedReader buff     = null;
        List<String> dataList   = new LinkedList<>( );

        try{

            reader = new FileReader( fileLocation );
            buff = new BufferedReader( reader );

            String str;
            while( (str = buff.readLine( )) != null ){
                dataList.add( str );
            }

        }catch( Exception e ){
            LOGGER.warn( "FAILED to load data from [{}].", fileLocation, e );

        }finally{
            if( buff != null ){
                try{
                    buff.close( );
                }catch( IOException e ){
                }
            }

        }

        LOGGER.info( "Loaded [{}] counts of data from [{}].", dataList.size( ), fileLocation );

        return dataList;

    }


    @Override
    public final void stop( ) {
        executor.shutdown( );
    }


}
