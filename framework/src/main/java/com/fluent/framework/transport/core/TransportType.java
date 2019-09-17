package com.fluent.framework.transport.core;

public enum TransportType {

    FILE,
    SOCKET,
    TIBCO,
    UNKNOWN;


    public final static boolean isValid( String typeName ) {

        for( TransportType type : TransportType.values( ) ){
            if( UNKNOWN == type )
                continue;

            if( type.name( ).equalsIgnoreCase( typeName ) ){
                return true;
            }
        }

        return false;

    }



}
