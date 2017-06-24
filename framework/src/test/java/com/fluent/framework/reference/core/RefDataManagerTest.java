package com.fluent.framework.reference.core;

import org.junit.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.reference.provider.*;

import static org.assertj.core.api.StrictAssertions.*;


public class RefDataManagerTest{
    
    private final static Exchange exchange      = Exchange.CME;
    private final static SpreadType sType       = SpreadType.OUTRIGHT;
    private final static InstrumentSubType iType= InstrumentSubType.ED_FUTURES;
        
    
    public RefDataManager create( int size ) throws Exception{
        RefDataProvider provider= new RefDataFileProvider( "", null );    
        RefDataManager manager  = new RefDataManager( size, provider );
        manager.start( );
        Thread.sleep( 1000 );
        
        return manager;
    }
   
    
    @Test
    public void testInitial( ) throws Exception{
        int initSize           = 2;
        RefDataManager manager = create( initSize );
        
        assertThat( manager.getCurrentIndex( ) ).isEqualTo( -1 );
        assertThat( manager.getCapacity( ) ).isEqualTo( initSize * 2 );        
    }
    
    
    @Test
    public void addOneRefDataEvent( ) throws Exception{
        
        int initSize           = 2;
        RefDataManager manager = create( initSize );
        
        int index               = 0;
        String ricSymbol        = "EDZ7";
        String key              = manager.getProvider( ).createKey( exchange, ricSymbol );
        RefDataEvent event      = new RefDataEvent( index, key, exchange, sType,
                                                iType, ricSymbol, ricSymbol,
                                                "12182017", 0.005, 1000000, 2500 );
        
        boolean updated     = manager.update( event );
        Thread.sleep( 100 );
        
        assertThat( updated ).isTrue( );
        assertThat( manager.isFull( ) ).isFalse( );
        assertThat( manager.getCurrentIndex( ) ).isEqualTo( index );
        assertThat( manager.get(index) ).isEqualTo( event );
        assertThat( manager.get(key) ).isEqualTo( event );
                
    }
    
    
    
    @Test
    public void addMultipleRefDataEvent( ) throws Exception{
        
        int initSize           = 4;
        RefDataManager manager = create( initSize );
                
        for( int index=0; index< initSize; index++ ){
        
            String ricSymbol    = index+"EDZ7";
            String key          = manager.getProvider( ).createKey( exchange, ricSymbol );
            RefDataEvent event  = new RefDataEvent( index, key, exchange, sType,
                                                    iType, ricSymbol, ricSymbol,
                                                    "12182017", 0.005, 1000000, 2500 );
        
            boolean updated     = manager.update( event );
            Thread.sleep( 100 );
            
            assertThat( updated ).isTrue( );
            assertThat( manager.isFull( ) ).isFalse( );
            assertThat( manager.get(index) ).isEqualTo( event );
            assertThat( manager.getCurrentIndex( ) ).isEqualTo( index );
        }

    }

    
    @Test
    public void addRefDataEventTillFull( ) throws Exception{

        int initSize           = 4;
        int maxNonFullSize     = (initSize*2) -1;
        RefDataManager manager = create( initSize );
        
        for( int index=0; index< maxNonFullSize; index++ ){
        
            String ricSymbol    = index+"EDZ7";
            String key          = manager.getProvider( ).createKey( exchange, ricSymbol );
            RefDataEvent event  = new RefDataEvent( index, key, exchange, sType,
                                                    iType, ricSymbol, ricSymbol,
                                                    "12182017", 0.005, 1000000, 2500 );
        
            boolean updated     = manager.update( event );
            Thread.sleep( 100 );
            
            assertThat( updated ).isTrue( );
            assertThat( manager.isFull( ) ).isFalse( );
            assertThat( manager.get(index) ).isEqualTo( event );
            assertThat( manager.getCurrentIndex( ) ).isEqualTo( index );
        }

        
        //NOW Reference Manager is full
        
        int newIndex        = manager.getCurrentIndex( ) + 1;
        String ricSymbol    = newIndex+"EDZ7";
        String key          = manager.getProvider( ).createKey( exchange, ricSymbol );
        RefDataEvent event  = new RefDataEvent( newIndex, key, exchange, sType,
                                            iType, ricSymbol, ricSymbol,
                                            "12182017", 0.005, 1000000, 2500 );
    
        boolean updated     = manager.update( event );
        assertThat( updated ).isFalse( );
        assertThat( manager.isFull( ) ).isTrue( );
        
    }
    
    
        
}
