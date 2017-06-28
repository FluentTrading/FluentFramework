package com.fluent.framework.reference.parser;
/*@formatter:off */

import java.util.*;

import com.fluent.framework.market.core.*;
import com.fluent.framework.market.instrument.*;
import com.fluent.framework.reference.core.*;

import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.util.FluentToolkit.*;


public class RefDataDefaultParser extends RefDataParser{
    
    private final String delimiter;
    private final List<RefDataField> columns;
    
    //public final static String DELIMITER   = "\\" + PIPE;
    //public final static int EXPECTED_LENGTH= RefDataDefaultParser.values( ).length;
    
    public RefDataDefaultParser( String delimiter, List<RefDataField> columns ){
        this.delimiter  = delimiter;
        this.columns    = columns;
    }

    @Override
    public final RefDataEvent parse( String line ){
        
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
        RefDataEvent event      = new RefDataEvent( instKey, exchange, spreadType, iType,
                                                    ricSymbol, exchangeSymbol, expiryDate,
                                                    tickSize, lotSize, pointValue );
        
        
        return event;
        
    }


    private final static String parse( String[ ] tokens, int index, String defaultValue ){
        String data = tokens[index ++];
        data        = isBlank(data) ? defaultValue : data.trim( );
        
        return data;
    }
    

}