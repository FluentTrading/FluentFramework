package com.fluent.framework.reference.parser;

import org.junit.*;

import java.util.*;

import com.fluent.framework.market.core.*;
import com.fluent.framework.market.instrument.*;
import com.fluent.framework.reference.core.*;
import com.fluent.framework.reference.provider.*;

import static org.assertj.core.api.StrictAssertions.*;


public class RefDataDefaultParserTest{
    
    RefDataProvider provider;
    RefDataManager manager;
    
    private final static Exchange exchange      = Exchange.CME;
    private final static SpreadType sType       = SpreadType.OUTRIGHT;
    private final static InstrumentSubType iType= InstrumentSubType.ED_FUTURES;
    private final static String fileName        = "C:\\temp\\TestReferenceData.txt";
    private final static List<RefDataField>COLS = new ArrayList<>();
    
    
    @Before
    public void start( ){
        provider    = new RefDataFileProvider( fileName, new RefDataDefaultParser("\\|", COLS), null );    
        manager     = new RefDataManager( 10, provider, null );
    }
    
   
    @Test
    public void test( ){

    }
    
    
    @After
    public void stop( ){
        
    }
    
}
