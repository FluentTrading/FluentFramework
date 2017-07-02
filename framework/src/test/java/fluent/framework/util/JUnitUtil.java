package fluent.framework.util;

import static fluent.framework.reference.provider.ReferenceDataSource.*;

import fluent.framework.collection.*;
import fluent.framework.reference.provider.*;


public final class JUnitUtil{
    

    public final static FluentInDispatcher inDispatcher( ){
        return new FluentInDispatcher( 1, 1000 );
    }

    public final static FluentOutDispatcher outDispatcher( ){
        return new FluentOutDispatcher( 1000 );
    }

    
    
    public static class DummyRefDataFileProvider extends ReferenceDataProvider{
        
        public DummyRefDataFileProvider( ){
            super( FILE_SOURCE, null, null );
        }
        
        public final void start( ){}
        
    }

}
