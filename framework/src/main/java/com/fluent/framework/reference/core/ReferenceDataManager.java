package com.fluent.framework.reference.core;
/*@formatter:off */

import org.agrona.concurrent.*;
import org.cliffc.high_scale_lib.*;
import org.slf4j.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import com.fluent.common.util.*;
import com.fluent.framework.collection.*;
import com.fluent.framework.core.*;
import com.fluent.framework.events.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.reference.parser.*;
import com.fluent.framework.reference.provider.*;
import com.fluent.framework.service.*;
import com.typesafe.config.*;

import static com.fluent.common.util.Constants.*;
import static com.fluent.common.util.Toolkit.*;
import static com.fluent.framework.core.FluentConfig.*;
import static com.fluent.framework.events.FluentEventType.*;
import static com.fluent.framework.service.FluentServiceType.*;

/**
 * ThreadSafe
 * RefDataManager receives RefDataEvents and provides index and key based lookup capability.
 * 
 * RefDataManager takes the initial size and creates an array of twice that size.
 * This is to support cases when RefDataEvents are added while the app is running.
 * 
 * It also creates and starts <link>RefDataProvider</link> which feeds RefDataEvent via the Dispatcher.
 * Once RefDataEvent updates are sent to RefDataManager, it indexes them in an array and a Map.
 * Users of this class can retrieve them using an index or a Key (Exchange.RicSymbol).
 */

public final class ReferenceDataManager implements FluentEventListener, FluentService{
   
    private volatile boolean isRunning;
    
    private final int arrayCapacity;
    private final AtomicInteger refIndex;
    private final IdleStrategy idleStrategy;
    private final ReferenceDataProvider provider;
    private final FluentInDispatcher inDispatcher;
    private final ConcurrentMap<String, ReferenceDataEvent> symbolMap;
    private final AtomicReferenceArray<ReferenceDataEvent> symbolArray;
    
    private final static String INITIAL_SIZE    = "initialSize";
    private final static String REFERENCE_CFG   = TOP_SECTION_KEY + "referenceData";
    private final static String NAME            = ReferenceDataManager.class.getSimpleName( );
    private final static Logger LOGGER          = LoggerFactory.getLogger( NAME );
    
    
    public ReferenceDataManager( int initCapacity, ReferenceDataProvider provider, FluentInDispatcher inDispatcher ){
        this( initCapacity, provider, inDispatcher, new BusySpinIdleStrategy( ) );
    }
            
    
    public ReferenceDataManager( int initCapacity, ReferenceDataProvider provider, FluentInDispatcher inDispatcher, IdleStrategy idleStrategy ){
        
        this.arrayCapacity  = notNegative(initCapacity, "Capacity can't be negative") * TWO;
        this.provider       = Toolkit.errorIfNull( provider, "Provider can't be null");
        this.inDispatcher   = Toolkit.errorIfNull( inDispatcher, "InDispatcher can't be null");
        this.idleStrategy   = Toolkit.errorIfNull( idleStrategy, "IdleStrategy can't be null");
        
        this.refIndex       = new AtomicInteger( NEGATIVE_ONE );
        this.symbolArray    = new AtomicReferenceArray<>( arrayCapacity );
        this.symbolMap      = new NonBlockingHashMap<>( arrayCapacity );
                        
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
        idleStrategy.idle( );
        this.isRunning  = true;
        
        LOGGER.info( "Successfully started {} with array capacity [{}].", NAME, arrayCapacity );
    
    }
        
    
    protected final int getCurrentIndex( ){
        return refIndex.get( );
    }
    
    
    protected final int getCapacity( ){
        return symbolArray.length( );
    }

    
    protected final ReferenceDataProvider getProvider( ){
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
         
            ReferenceDataEvent newEvent   = (ReferenceDataEvent) event;
            String refKey           = newEvent.getKey( );
            ReferenceDataEvent oldEvent   = symbolMap.get( refKey );
            boolean isNew           = (oldEvent == null);
            storedCorrectly         = isNew ? handleNew(newEvent) : handleUpdate(oldEvent, newEvent);
            
        }catch( Exception e ){
            LOGGER.warn( "FAILED to store {}", event, e );
        }
        
        return storedCorrectly;
        
    }
    
    
    //RefDataEvent coming from provider doesn't have index set.
    protected final boolean handleNew( ReferenceDataEvent noIndex ){
        
        int arrayCapacity   = getCapacity( );
        boolean isArrayFull = isFull( );
        if( isArrayFull ){
            LOGGER.warn( "Array is FULL to capcaity [{}]", arrayCapacity );
            LOGGER.warn( "Dropping event, please raise inital capacity estimate [{}]", noIndex );
            return false;
        }
    
        String refKey       = noIndex.getKey( );
        int current         = refIndex.incrementAndGet( );
        ReferenceDataEvent event  = ReferenceDataEvent.copy( current, noIndex );
        
        boolean updateMap   = (symbolMap.putIfAbsent(refKey, event) == null) ;
        boolean updateArray = symbolArray.compareAndSet( current, null, event );
        boolean updated     = updateMap && updateArray;
    
        if( updated ){
            LOGGER.info( "Stored at Index:[{}] [{}]", current, event );
            return true;
        }
        
        LOGGER.warn( "FAILED to store at Index:[{}] [{}]", current, event );
        return false;
        
    }
    
    
    protected final boolean handleUpdate( ReferenceDataEvent older, ReferenceDataEvent event ){

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
        return ReferenceDataParser.createKey( exchange, ricSymbol );
    }        
    
    
    public final ReferenceDataEvent get( String key ){
        return symbolMap.get( key );
    }
       
    
    //Check if the instIndex is out of Index before calling get
    public final ReferenceDataEvent get( int index ){
        
        if( isIndexOutOfBounds(index) ){
            return null;
        }
        
        return symbolArray.get( index );
    }

    
    public final static Config parseReferenceConfig( FluentConfig config ){
        return config.getSectionConfig( REFERENCE_CFG );
    }
    
    public final static int parseInitialSize( Config refConfig ){
        return refConfig.getInt( INITIAL_SIZE );
    }
        
     
    @Override
    public final void stop( ) throws Exception{
        this.isRunning  = false;
        LOGGER.info( "Successfully stopped [{}].", NAME );
    }
       
    
}
