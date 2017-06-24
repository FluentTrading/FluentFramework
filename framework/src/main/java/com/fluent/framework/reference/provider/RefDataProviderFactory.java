package com.fluent.framework.reference.provider;
/*@formatter:off */
import com.fluent.framework.core.*;
import com.fluent.framework.reference.provider.RefDataProvider.*;

public final class RefDataProviderFactory{
    
    
    public final static RefDataProvider create( FluentServices services ){
        
        Source source = null;
        RefDataProvider provider = null;
        
        switch( source ){
            
            case FILE:
                provider = createFileProvider( services );
                break;
                
            case NETWORK:
                provider = createNetworkProvider( services );
                break;
                
                
            default:
                throw new RuntimeException( "Unsupported Reference data provider: " + source );
                
        }
        
        
        return provider;
        
    }


    protected final static RefDataProvider createFileProvider( FluentServices services ){
        RefDataProvider provider = new RefDataFileProvider( services );
        return provider;
    }

    
    
    protected final static RefDataProvider createNetworkProvider( FluentServices services ){
        RefDataProvider provider = new RefDataNetworkProvider( services );
        return provider;
    }
   
    

}
