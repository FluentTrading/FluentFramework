package com.fluent.framework.core;
import static com.fluent.framework.util.FluentUtil.*;


public class FluentException extends Exception{

    private static final long serialVersionUID = 1L;

    public FluentException( String message ){
        this( message, null );
    }

    public FluentException( Throwable e ){
        this( EMPTY, e );
    }

    public FluentException( String message, Throwable e ){
        super( message, e );
    }


}
