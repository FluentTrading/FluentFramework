package com.fluent.framework.util;

import com.fluent.framework.collection.*;
import com.fluent.framework.reference.provider.*;

import static com.fluent.framework.reference.provider.ReferenceDataSource.*;


public final class JUnitUtil{
    

    public final static FluentInDispatcher inDispatcher( ){
        return new FluentInDispatcher( );
    }

    public final static FluentOutDispatcher outDispatcher( ){
        return new FluentOutDispatcher( );
    }

    
    
    public static class DummyRefDataFileProvider extends ReferenceDataProvider{
        
        public DummyRefDataFileProvider( ){
            super( FILE_SOURCE, null, null );
        }
        
        public final void start( ){}
        
    }

}
