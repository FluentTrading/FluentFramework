package com.fluent.framework.core;

import org.slf4j.*;

import java.io.*;
/*@formatter:off */
import java.util.*;
import com.typesafe.config.*;

import com.fluent.framework.core.FluentContext.*;
import com.fluent.framework.runner.*;

import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.util.FluentToolkit.*;
import static com.fluent.framework.util.FluentTimeUtil.*;


public class FluentConfiguration{

    private final Role          role;
    private final Region        region;
    private final Environment   environment;
    private final String        cfgFileName;
    private final Config        rawConfig;

    private final String        appInstance;
    private final String        appRawOpenTime;
    private final long          appOpenTime;
    private final String        appRawCloseTime;
    private final long          appCloseTime;
    private final String        workingHours;
    private final TimeZone      appTimeZone;

    // Application
    public final static String     TOP_SECTION_KEY         = "fluent.";
    private final static String     APPLICATION_SECTION_KEY = TOP_SECTION_KEY + "application.";
    private final static String     APP_ROLE_KEY            = APPLICATION_SECTION_KEY + "role";
    private final static String     APP_REGION_KEY          = APPLICATION_SECTION_KEY + "region";
    private final static String     APP_ENVIRONMENT_KEY     = APPLICATION_SECTION_KEY + "environment";
    private final static String     APP_INSTANCE_KEY        = APPLICATION_SECTION_KEY + "instance";
    private final static String     APP_OPEN_TIME_KEY       = APPLICATION_SECTION_KEY + "openTime";
    private final static String     APP_CLOSE_TIME_KEY      = APPLICATION_SECTION_KEY + "closeTime";
    private final static String     APP_TIMEZONE_KEY        = APPLICATION_SECTION_KEY + "timeZone";

    private final static String NAME                        = FluentConfiguration.class.getSimpleName();
    private final static Logger LOGGER                      = LoggerFactory.getLogger( NAME );

    
    public FluentConfiguration( String configFileName ) throws Exception{

        this.cfgFileName    = notBlank( configFileName, "Config file name is invalid." );
        this.rawConfig      = loadConfigs( configFileName );

        this.region         = Region.getRegion( rawConfig, APP_REGION_KEY );
        this.environment    = Environment.getEnvironment( rawConfig, APP_ENVIRONMENT_KEY );
        this.role           = Role.getRole( rawConfig, APP_ROLE_KEY );
        this.appInstance    = rawConfig.getString( APP_INSTANCE_KEY );
        this.appTimeZone    = parseTimeZone( rawConfig.getString( APP_TIMEZONE_KEY ) );
        this.appRawOpenTime = rawConfig.getString( APP_OPEN_TIME_KEY );
        this.appRawCloseTime= rawConfig.getString( APP_CLOSE_TIME_KEY );
        this.appOpenTime    = getAdjustedOpen( appRawOpenTime, appRawCloseTime, appTimeZone, System.currentTimeMillis( ) );
        this.appCloseTime   = getAdjustedClose( appRawOpenTime, appRawCloseTime, appTimeZone, System.currentTimeMillis( ) );
        this.workingHours   = appRawOpenTime + DASH + appRawCloseTime;

    }

    
    public final Config getRawConfig( ){
        return rawConfig;
    }
    
    
    public final Config getSectionConfig( String sectionName ){
        return rawConfig.getConfig( sectionName );
    }
    
    
    public final Role getRole( ){
        return role;
    }


    public final Region getRegion( ){
        return region;
    }


    public final Environment getEnvironment( ){
        return environment;
    }


    public final boolean isProd( ){
        return (Environment.PROD == environment);
    }


    public final String getCfgFileName( ){
        return cfgFileName;
    }


    public final String getAppInstance( ){
        return appInstance;
    }


    public final TimeZone getAppTimeZone( ){
        return appTimeZone;
    }


    public final String getRawAppOpenTime( ){
        return appRawOpenTime;
    }


    public final long getAppOpenTime( ){
        return appOpenTime;
    }


    public final String getRawAppCloseTime( ){
        return appRawCloseTime;
    }


    public final long getAppCloseTime( ){
        return appCloseTime;
    }


    public final String getWorkingHours( ){
        return workingHours;
    }


    public final String getConfigInfo( ){

        StringBuilder builder = new StringBuilder( TWO * SIXTY_FOUR );

        builder.append( L_BRACKET );
        builder.append( "Instance:" ).append( appInstance );
        builder.append( ", Environment:" ).append( environment );
        builder.append( ", Region:" ).append( region );
        builder.append( ", Role:" ).append( role );
        builder.append( ", Process:" ).append( getFullProcessName( ) );
        builder.append( R_BRACKET );

        return builder.toString( );

    }


    protected final Config loadConfigs( String fileName ){

        Config configuration    = null;

        try{
            File file           = new File( fileName );
            configuration       = ConfigFactory.parseFile( file );
            LOGGER.info("Loaded {}", configuration );
            
        }catch( Exception e ){
            throw new RuntimeException( "Failed to load " + fileName, e );
        }

        return configuration;

    }


    @Override
    public String toString( ){
        return getCfgFileName( ) + COMMASP + rawConfig.toString( );
    }

}
