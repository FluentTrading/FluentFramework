package com.fluent.framework.reference.core;
/*@formatter:off */

import org.slf4j.*;
import org.agrona.concurrent.*;
import org.cliffc.high_scale_lib.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import com.fluent.framework.core.*;
import com.fluent.framework.collection.*;
import com.fluent.framework.events.core.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.reference.provider.*;

import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.util.FluentToolkit.*;
import static com.fluent.framework.events.core.FluentEventType.*;


/**
 * ThreadSafe
 * RefDataManager stores RefDataEvents and provides index and key based query capability.
 * 
 * RefDataManager accepts an initial size and creates an array of twice that size.
 * It also creates and starts <link>RefDataProvider</link>.
 * Once RefDataEvent updates are sent to RefDataManager, it indexes them in an array and a Map.
 * Users of this class can retrieve them using an index or a Key (Exchange.RisSymbol).
 *  
 */

public final class RefDataManager implements FluentLifecycle{
   
    private volatile boolean keepProcessing;
    
    private final int arrayCapacity;
    private final AtomicInteger refIndex;
    private final RefDataProvider provider;
    private final RefDataProcessor processor;
    private final ExecutorService executor;
    private final ConcurrentMap<String, RefDataEvent> symbolMap;
    private final AtomicReferenceArray<RefDataEvent> symbolArray;
    
    private final static int DEFAULT_SIZE   = 2000;
    private final static String NAME        = RefDataManager.class.getSimpleName( );
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );
    
    
    //TODO: parse the config to get default refdata size
    public RefDataManager( FluentServices services ){
        this( DEFAULT_SIZE, RefDataProviderFactory.create(services) );                       
    }
       
    public RefDataManager( int initCapacity, RefDataProvider provider ){
        
        this.arrayCapacity  = notNegative(initCapacity, "Capacity can't be negative") * TWO;
        this.provider       = notNull( provider, "Provider can't be null");

        this.processor      = new RefDataProcessor( );
        this.refIndex       = new AtomicInteger( NEGATIVE_ONE );
        this.symbolArray    = new AtomicReferenceArray<>( arrayCapacity );
        this.symbolMap      = new NonBlockingHashMap<>( arrayCapacity );
        this.executor       = Executors.newSingleThreadExecutor( new FluentThreadFactory("RefDataProcessor") );
                
    }
    
        
    @Override
    public final String name( ){
        return NAME;
    }    
    

    @Override
    public final void start( ){
        
        keepProcessing = true;
        executor.submit( processor );
        
        provider.start( processor );
        LOGGER.info( "Successfully started [{}], listening for RefDataEvents.", NAME );
    }
    
    
    
    protected final int getCurrentIndex( ){
        return refIndex.get( );
    }
    
    
    protected final int getCapacity( ){
        return symbolArray.length( );
    }

    protected final RefDataProvider getProvider( ){
        return provider;        
    }
    
    protected final boolean isFull( ){
        return (getCapacity( ) - getCurrentIndex( )) <= ONE;
    }
    
    
    protected final boolean isIndexOutOfBounds( int index ){
        return (getCapacity( ) - index) < ONE;
    }
        
    
    public final boolean update( FluentEvent event ){
        return processor.update( event );        
    }
    
    
    //Check if the instIndex is out of Index before calling get
    public final RefDataEvent get( int index ){
        
        if( isIndexOutOfBounds(index) ){
            return null;
        }
        
        return symbolArray.get( index );
    }

    
    public final String getKey( Exchange exchange, String ricSymbol ){
        return provider.createKey( exchange, ricSymbol );
    }        
    
    public final RefDataEvent get( String key ){
        return symbolMap.get( key );
    }
        
     
    protected final boolean storeEvent( RefDataEvent event ){
        
        int arrayCapacity     = getCapacity( );
        boolean isArrayFull   = isFull( );
        if( isArrayFull ){
            LOGGER.warn( "Array is FULL to capcaity [{}]", arrayCapacity );
            LOGGER.warn( "Dropping event, please raise inital capacity estimate [{}]", event);
            return false;
        }
        
        int current           = refIndex.incrementAndGet( );
        String refKey         = event.getKey( );
        
        symbolArray.set( current, event );
        symbolMap.put( refKey, event );
        
        LOGGER.info( "Stored at Index:[{}] with Key:[{}] [{}]", current, refKey, event );
        
        return true;
        
    }
    
     
    
    @Override
    public final void stop( ) throws FluentException{
        keepProcessing  = false;
        executor.shutdown( );
        LOGGER.info( "Successfully stopped [{}].", NAME );
    }
    
    
    private final class RefDataProcessor implements Runnable, FluentEventListener{
        
        private final ManyToOneConcurrentArrayQueue<FluentEvent> queue;
        
        public RefDataProcessor( ){
            this.queue  = new ManyToOneConcurrentArrayQueue<>( THOUSAND );    
        }       
        
        @Override
        public final String name( ){
            return NAME;
        }

        @Override
        public final boolean isSupported( FluentEventType type ){
            return ( REFERENCE_DATA == type );
        }
        
        @Override
        public final boolean update( FluentEvent event ){
            return queue.offer( event );        
        }
        
        
        @Override
        public final void run( ){
            
            while( keepProcessing ){
                
                FluentEvent event   = queue.poll( );
                if( event == null ){
                    FluentBackoffStrategy.apply( THOUSAND );
                    continue;
                }
                
                boolean isRefData   = isSupported( event.getType( ) );
                if( !isRefData ){
                    LOGGER.warn( "Non RefDataEvent was sent to [{}]", NAME );
                    continue;
                }
                
                storeEvent( (RefDataEvent) event );
            }
            
        }
        
    }
    
    
}
