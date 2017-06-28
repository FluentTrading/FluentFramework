package com.fluent.framework.reference.core;
/*@formatter:off */

import org.slf4j.*;
import org.cliffc.high_scale_lib.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import com.fluent.framework.events.*;
import com.fluent.framework.collection.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.reference.provider.*;
import com.fluent.framework.service.*;

import static com.fluent.framework.events.FluentEventType.*;
import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.util.FluentToolkit.*;
import static com.fluent.framework.service.FluentServiceType.*;

/**
 * ThreadSafe
 * RefDataManager stores RefDataEvents and provides index and key based lookup capability.
 * 
 * RefDataManager accepts an initial size and creates an array of twice that size.
 * It also creates and starts <link>RefDataProvider</link> which feeds RefDataEvent via the Dispatcher.
 * Once RefDataEvent updates are sent to RefDataManager, it indexes them in an array and a Map.
 * Users of this class can retrieve them using an index or a Key (Exchange.RicSymbol).
 *  
 */

public final class RefDataManager implements FluentEventListener, FluentService{
   
    private final int arrayCapacity;
    private final AtomicInteger refIndex;
    private final RefDataProvider provider;
    private final FluentInDispatcher inDispatcher;
    private final ConcurrentMap<String, RefDataEvent> symbolMap;
    private final AtomicReferenceArray<RefDataEvent> symbolArray;
    
    private final static String NAME        = RefDataManager.class.getSimpleName( );
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );
    
    
    //TODO: parse the config to get default refdata size
    public RefDataManager( int initCapacity, RefDataProvider provider, FluentInDispatcher inDispatcher ){
        
        this.arrayCapacity  = notNegative(initCapacity, "Capacity can't be negative") * TWO;
        this.provider       = notNull( provider, "Provider can't be null");
        this.inDispatcher   = inDispatcher;
        
        this.refIndex       = new AtomicInteger( NEGATIVE_ONE );
        this.symbolArray    = new AtomicReferenceArray<>( arrayCapacity );
        this.symbolMap      = new NonBlockingHashMap<>( arrayCapacity );
                        
    }
    
        
    @Override
    public final String name( ){
        return NAME;
    }    
    
    
    @Override
    public final FluentServiceType getType(){
        return REF_DATA_SERVICE;
    }
    
    
    @Override
    public final boolean isSupported( FluentEventType type ){
        return ( REFERENCE_DATA == type );
    }
    
    
    @Override
    public final void start( ){
        
        inDispatcher.register( this );
        
        provider.start( );
        FluentBackoff.backoff( ONE, TimeUnit.SECONDS );
        
        LOGGER.info( "Successfully started {}, listening for events.", NAME );
    
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
        
    
    @Override
    public final boolean update( FluentEvent event ){
        return storeEvent( event );        
    }
    
    
    protected final boolean storeEvent( FluentEvent event ){
        
        boolean storedCorrectly     = false;
        
        try{
         
            RefDataEvent newer      = (RefDataEvent) event;
            String refKey           = newer.getKey( );
            RefDataEvent older      = symbolMap.get(refKey);
            boolean isNew           = (older == null);
            storedCorrectly         = isNew ? handleNew(newer) : handleUpdate(older, newer);
            
        }catch( Exception e ){
            LOGGER.warn( "FAILED to store {}", event, e );
        }
        
        return storedCorrectly;
        
    }
    
    
    //RefDataEvent coming from provider doesn't have index set.
    protected final boolean handleNew( RefDataEvent noIndex ){
        
        int arrayCapacity   = getCapacity( );
        boolean isArrayFull = isFull( );
        if( isArrayFull ){
            LOGGER.warn( "Array is FULL to capcaity [{}]", arrayCapacity );
            LOGGER.warn( "Dropping event, please raise inital capacity estimate [{}]", noIndex );
            return false;
        }
    
        String refKey       = noIndex.getKey( );
        int current         = refIndex.incrementAndGet( );
        RefDataEvent event  = RefDataEvent.copy( current, noIndex );
        
        boolean updateMap   = ( symbolMap.putIfAbsent(refKey, event) == null) ;
        boolean updateArray = symbolArray.compareAndSet( current, null, event );
        boolean updated     = updateMap && updateArray;
    
        if( updated ){
            LOGGER.info( "Stored at Index:[{}] [{}]", current, event );
            return true;
        }
        
        LOGGER.warn( "FAILED to store at Index:[{}] [{}]", current, event );
        return false;
        
    }
    
    
    protected final boolean handleUpdate( RefDataEvent older, RefDataEvent event ){

        int index           = event.getIndex( );
        String refKey       = event.getKey( );
        
        boolean updateMap   = symbolMap.replace( refKey, older, event );
        boolean updateArray = symbolArray.compareAndSet( index,  older, event );
        boolean updated     = updateMap && updateArray;
        
        if( updated ){
            LOGGER.info( "Updated at Index:[{}] [{}] -> [{}]", index, older, event );
            return true;
        }
        
        LOGGER.warn( "FAILED to update at Index:[{}] [{}]", index, event );
        return false;
        
    }
    
    
    public final String getKey( Exchange exchange, String ricSymbol ){
        return provider.createKey( exchange, ricSymbol );
    }        
    
    public final RefDataEvent get( String key ){
        return symbolMap.get( key );
    }
       
    
    //Check if the instIndex is out of Index before calling get
    public final RefDataEvent get( int index ){
        
        if( isIndexOutOfBounds(index) ){
            return null;
        }
        
        return symbolArray.get( index );
    }

 
    @Override
    public final void stop( ) throws Exception{
        LOGGER.info( "Successfully stopped [{}].", NAME );
    }
    
   
    
}
