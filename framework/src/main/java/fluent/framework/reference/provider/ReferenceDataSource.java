package fluent.framework.reference.provider;

import com.typesafe.config.*;

public enum ReferenceDataSource{
    
    FILE_SOURCE,
    NETWORK_SOURCE;
    
    private final static String SOURCE_CONFIG = "sourceType";
    
    public final static ReferenceDataSource parseSource( Config config ){
        String sourceStr        = config.getString( SOURCE_CONFIG );
        ReferenceDataSource source    = ReferenceDataSource.valueOf( sourceStr );
        
        return source;
    }
    
}