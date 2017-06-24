package com.fluent.framework.reference.parser;

import org.junit.*;

import java.nio.file.*;

import com.fluent.framework.market.core.*;
import com.fluent.framework.reference.core.*;
import com.fluent.framework.reference.parser.*;
import com.fluent.framework.reference.provider.*;
import com.fluent.framework.util.*;

import static org.assertj.core.api.StrictAssertions.*;


public class RefDataDefaultParserTest{
    
    RefDataProvider provider;
    RefDataManager manager;
    
    private final static Exchange exchange      = Exchange.CME;
    private final static SpreadType sType       = SpreadType.OUTRIGHT;
    private final static InstrumentSubType iType= InstrumentSubType.ED_FUTURES;
    private final static String fileName        = "C:\\temp\\TestReferenceData.txt";
    private final static RefDataField[] COLS    = {};
    
    
    @Before
    public void start( ){
        provider    = new RefDataFileProvider( fileName, new RefDataDefaultParser("\\|", COLS) );    
        manager     = new RefDataManager( 10, provider );
    }
    
   
    @Test
    public void test( ){

    }
    
    
    @After
    public void stop( ){
        
    }
    
}
