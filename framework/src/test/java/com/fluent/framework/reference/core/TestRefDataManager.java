package com.fluent.framework.reference.core;

import static org.assertj.core.api.Assertions.*;

import org.junit.*;

import com.fluent.framework.market.core.*;
import com.fluent.framework.market.instrument.*;
import com.fluent.framework.reference.parser.*;
import com.fluent.framework.reference.provider.*;
import com.fluent.framework.util.*;


public class TestRefDataManager{
    
    private final static Exchange exchange      = Exchange.CME;
    private final static SpreadType sType       = SpreadType.OUTRIGHT;
    private final static InstrumentSubType iType= InstrumentSubType.ED_FUTURES;
        
    
    public ReferenceDataManager create( int size ) throws Exception{
        ReferenceDataProvider provider= new JUnitUtil.DummyRefDataFileProvider( );    
        ReferenceDataManager manager  = new ReferenceDataManager( size, provider, JUnitUtil.inDispatcher( ) );
        manager.start( );
        Thread.sleep( 1000 );
        
        return manager;
    }
   
    
    //@Test
    public void testInitial( ) throws Exception{
        int initSize           = 2;
        ReferenceDataManager manager = create( initSize );
        
        assertThat( manager.getCurrentIndex( ) ).isEqualTo( -1 );
        assertThat( manager.getCapacity( ) ).isEqualTo( initSize * 2 );        
    }
    
    
    @Test
    public void addOneRefDataEvent( ) throws Exception{
        
        int initSize           = 2;
        ReferenceDataManager manager = create( initSize );
        
        int index               = 0;
        String ricSymbol        = "EDZ7";
        String key              = ReferenceDataParser.createKey( exchange, ricSymbol );
        ReferenceDataEvent event= new ReferenceDataEvent( index, key, exchange, sType,
                                                iType, ricSymbol, ricSymbol,
                                                "12182017", 0.005, 1000000, 2500 );
        
        boolean updated         = manager.update( event );
                
        assertThat( updated ).isTrue( );
        assertThat( manager.isFull( ) ).isFalse( );
        assertThat( manager.getCurrentIndex( ) ).isEqualTo( index );
        System.out.println( event );
        System.out.println( manager.get(index) );
        System.out.println( manager.get(key) );
                
    }
    
    
    
    //@Test
    public void addMultipleRefDataEvent( ) throws Exception{
        
        int initSize           = 4;
        ReferenceDataManager manager = create( initSize );
                
        for( int index=0; index< initSize; index++ ){
        
            String ricSymbol    = index+"EDZ7";
            String key          = ReferenceDataParser.createKey( exchange, ricSymbol );
            ReferenceDataEvent event  = new ReferenceDataEvent( index, key, exchange, sType,
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

    
    //@Test
    public void addRefDataEventTillFull( ) throws Exception{

        int initSize           = 4;
        int maxNonFullSize     = (initSize*2) -1;
        ReferenceDataManager manager = create( initSize );
        
        for( int index=0; index< maxNonFullSize; index++ ){
        
            String ricSymbol    = index+"EDZ7";
            String key          = ReferenceDataParser.createKey( exchange, ricSymbol );
            ReferenceDataEvent event  = new ReferenceDataEvent( index, key, exchange, sType,
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
        String key          = ReferenceDataParser.createKey( exchange, ricSymbol );
        ReferenceDataEvent event  = new ReferenceDataEvent( newIndex, key, exchange, sType,
                                            iType, ricSymbol, ricSymbol,
                                            "12182017", 0.005, 1000000, 2500 );
    
        boolean updated     = manager.update( event );
        assertThat( updated ).isFalse( );
        assertThat( manager.isFull( ) ).isTrue( );
        
    }
    
    
        
}
