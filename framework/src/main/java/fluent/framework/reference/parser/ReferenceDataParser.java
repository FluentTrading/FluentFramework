package fluent.framework.reference.parser;
/*@formatter:off */

import static fluent.framework.util.Toolkit.*;
import static fluent.framework.util.Constants.*;

import java.util.*;

import fluent.framework.market.core.*;
import fluent.framework.market.instrument.*;
import fluent.framework.reference.core.*;


public class ReferenceDataParser{

    private final String delimiter;
    private final List<ReferenceDataField> columns;
    
    //public final static String DELIMITER   = "\\" + PIPE;
    //public final static int EXPECTED_LENGTH= RefDataDefaultParser.values( ).length;
    
    public ReferenceDataParser( String delimiter, List<ReferenceDataField> columns ){
        this.delimiter  = delimiter;
        this.columns    = columns;
    }

    
    public ReferenceDataEvent parse( String line ){
        
        boolean isInvalid       = isBlank( line );
        if( isInvalid ) return null;
        
        boolean isComment       = line.startsWith( HASH );
        if( isComment ) return null;
                
        String[] tokens         = line.split( delimiter );
        boolean tkInvalid       = (tokens == null);
        if( tkInvalid ) return null;
        
        int expectedLength      = columns.size( );
        boolean tokensMismatch  = expectedLength != tokens.length;
        if( tokensMismatch ) return null;
                
        int index               = ZERO;
        Exchange exchange       = null;
        SpreadType spreadType   = SpreadType.fromName( parse( tokens, index++, EMPTY ) );
        InstrumentSubType iType = InstrumentSubType.fromName( parse( tokens, index++, EMPTY ) );
        String ricSymbol        = parse( tokens, index++, EMPTY );
        String exchangeSymbol   = parse( tokens, index++, EMPTY );
        String expiryDate       = parse( tokens, index++, EMPTY );
        double tickSize         = Double.valueOf( parse( tokens, index++, ZERO_STRING ));
        double lotSize          = Double.valueOf( parse( tokens, index++, ZERO_STRING ));
        double pointValue       = Double.valueOf( parse( tokens, index++, ZERO_STRING ));
        
        String instKey          = createKey( exchange, ricSymbol );
        ReferenceDataEvent event      = new ReferenceDataEvent( instKey, exchange, spreadType, iType,
                                                    ricSymbol, exchangeSymbol, expiryDate,
                                                    tickSize, lotSize, pointValue );
        
        
        return event;
        
    }


    private final static String parse( String[ ] tokens, int index, String defaultValue ){
        String data = tokens[index ++];
        data        = isBlank(data) ? defaultValue : data.trim( );
        
        return data;
    }
    
    
    public final static String createKey( Exchange exchange, String ricSymbol ){
        return exchange + DOT + ricSymbol;        
    }
    
}