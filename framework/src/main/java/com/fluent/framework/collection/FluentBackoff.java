package com.fluent.framework.collection;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import static java.util.concurrent.TimeUnit.*;
import static com.fluent.framework.util.FluentUtil.*;

//TODO: Could we replace this with Agrona's

public enum FluentBackoff{

    SPIN_YIELD,
    SPIN_PARK,
    SPIN_BUSY;
    
    private final static Map<String, FluentBackoff> MAP;
    
    static{
        MAP = new HashMap<>();
        
        for( FluentBackoff backoff : FluentBackoff.values( ) ){
            MAP.put( backoff.name( ), backoff );
        }
    }

    
    public final static FluentBackoff get( String name ){
        FluentBackoff backoff   = MAP.get( name );
        backoff                         = ( backoff == null ) ? SPIN_PARK : backoff;
        
        return backoff;
    }

    
    public final static void backoff( ){
        backoff( ONE, NANOSECONDS );
    }
    

    public final static void backoff( long time, TimeUnit unit ){
        if( IS_WINDOWS ){
            long timeMillis = (MILLISECONDS == unit) ? time : MILLISECONDS.convert(time, unit);
            yieldMillis( timeMillis );
            
        }else{
            long timeNanos  = (NANOSECONDS == unit) ? time : NANOSECONDS.convert(time, unit);
            LockSupport.parkNanos( timeNanos );
        }

    }


    private final static void yieldMillis( long timeMillis ){
        long startTime  = System.currentTimeMillis( );
        
        while( (System.currentTimeMillis( ) - startTime) < timeMillis ){
            Thread.yield( );
        }
    }




    public final static int backoff( FluentBackoff backoff, int counter, int total ){
        
        switch( backoff ){
            
            case SPIN_YIELD:{
                
                if( counter < total ){
                    ++counter;
                }else{
                    Thread.yield( );
                    counter = ZERO;
                }
                
                return counter;
                
            }

            case SPIN_BUSY:{
                
                if( counter < total ){
                    ++counter;
                }else{
                    counter = ZERO;
                }
                
                return counter;
                
            }
                
            default:
            case SPIN_PARK:{
                
                if( counter < total ){
                    ++counter;
                }else{
                    LockSupport.park( );
                    counter = ZERO;
                }
                
                return counter;
                
            }
            
        }
        
    }



}
