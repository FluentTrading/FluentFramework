package com.fluent.framework.core;
/*@formatter:off */
import java.util.*;

import com.typesafe.config.*;

import static com.fluent.framework.util.FluentUtil.*;


public final class FluentContext{


    public enum Role{

        PRIMARY,
        BACKUP;

        public static Role getRole( Config configuration, String key ){

            Role role   = null;
            String name = EMPTY;

            try{
                
                name    = configuration.getString( key );
                role    = Role.valueOf( name );

            }catch( Exception e ){
                printUsageAndExit( key, name, Arrays.deepToString( Role.values( ) ) );
            }

            return role;
        }

    }


    public enum Region{

        NY,
        CHI,
        LON;

        
        public static Region getRegion( Config configuration, String key ){

            Region region   = null;
            String name     = EMPTY;

            try{
                name        = configuration.getString( key );
                region      = Region.valueOf( name );

            }catch( Exception e ){
                printUsageAndExit( key, name, Arrays.deepToString( Region.values( ) ) );
            }

            return region;
        }

    }


    public enum Environment{

        DEV ('D'),
        QA  ('Q'),
        SIM ('S'),
        UAT ('U'),
        PROD('P');
        
        private final char code;
        
        private Environment( char code ){
            this.code = code;
        }
        
        public final char getCode( ){
            return code;
        }
        

        public static Environment getEnvironment( Config configuration, String key ) {

            Environment env = null;
            String name     = EMPTY;

            try{
                name        = configuration.getString( key );
                env         = Environment.valueOf( name );

            }catch( Exception e ){
                printUsageAndExit( key, name, Arrays.deepToString( Environment.values( ) ) );
            }

            return env;
        }

    }



    private final static void printUsageAndExit( String propName, String propValue, String choices ) {

        StringBuilder usage = new StringBuilder( TWO * SIXTY_FOUR );
        usage.append( "[ERROR while starting Fluent Application]" );
        usage.append( NEWLINE );
        usage.append( propName ).append( COLON ).append( propValue ).append( " is NOT valid!" );
        usage.append( NEWLINE );
        usage.append( "Must specify one of [" ).append( choices ).append( "]" );
        usage.append( NEWLINE );

        System.err.println( usage.toString( ) );
        System.exit( ONE );

    }


}
