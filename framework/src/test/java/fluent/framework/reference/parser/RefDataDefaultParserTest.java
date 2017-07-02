package fluent.framework.reference.parser;

import org.junit.*;

import java.util.*;

import fluent.framework.market.core.*;
import fluent.framework.market.instrument.*;
import fluent.framework.reference.core.*;
import fluent.framework.reference.parser.*;
import fluent.framework.reference.provider.*;

import static org.assertj.core.api.StrictAssertions.*;


public class RefDataDefaultParserTest{
    
    ReferenceDataProvider provider;
    ReferenceDataManager manager;
    
    private final static Exchange exchange      = Exchange.CME;
    private final static SpreadType sType       = SpreadType.OUTRIGHT;
    private final static InstrumentSubType iType= InstrumentSubType.ED_FUTURES;
    private final static String fileName        = "C:\\temp\\TestReferenceData.txt";
    private final static List<ReferenceDataField>COLS = new ArrayList<>();
    
    
    @Before
    public void start( ){
        provider    = new ReferenceDataFileProvider( fileName, new ReferenceDataParser("\\|", COLS), null );    
        manager     = new ReferenceDataManager( 10, provider, null );
    }
    
   
    @Test
    public void test( ){

    }
    
    
    @After
    public void stop( ){
        
    }
    
}
