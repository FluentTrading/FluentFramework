package com.fluent.framework.core;

/*@formatter:off */
import org.slf4j.*;

import java.util.*;

import com.fluent.framework.admin.core.*;
import com.fluent.framework.core.FluentContext.*;
import com.fluent.framework.market.core.*;
import com.typesafe.config.*;

import static com.fluent.framework.util.FluentTimeUtil.*;
import static com.fluent.framework.util.FluentToolkit.*;
import static com.fluent.framework.util.FluentUtil.*;


public final class FluentConfiguration{

    private final Region                         region;
    private final Environment                    environment;
    private final Role                           role;
    private final String                         cfgFileName;
    private final Config                         configuration;

    private final String                         appInstance;
    private final String                         appRawOpenTime;
    private final long                           appOpenTime;
    private final String                         appRawCloseTime;
    private final long                           appCloseTime;
    private final String                         workingHours;
    private final TimeZone                       appTimeZone;

    private final Map<Exchange, ExchangeDetails> exchangeMap;

    // Application
    private final static String                  APPLICATION_SECTION_KEY = "fluent.application.";
    private final static String                  APP_ROLE_KEY            = APPLICATION_SECTION_KEY + "role";
    private final static String                  APP_REGION_KEY          = APPLICATION_SECTION_KEY + "region";
    private final static String                  APP_ENVIRONMENT_KEY     = APPLICATION_SECTION_KEY + "environment";
    private final static String                  APP_INSTANCE_KEY        = APPLICATION_SECTION_KEY + "instance";
    private final static String                  APP_OPEN_TIME_KEY       = APPLICATION_SECTION_KEY + "openTime";
    private final static String                  APP_CLOSE_TIME_KEY      = APPLICATION_SECTION_KEY + "closeTime";
    private final static String                  APP_TIMEZONE_KEY        = APPLICATION_SECTION_KEY + "timeZone";

    public final static String                   EXCHANGE_SECTION_KEY    = "fluent.exchanges";
    public final static String                   MD_ADAPTORS_SECTION_KEY = "fluent.mdAdaptors";


    private final static String                  NAME                    = FluentConfiguration.class.getSimpleName( );
    private final static Logger                  LOGGER                  = LoggerFactory.getLogger( NAME );


    public FluentConfiguration( String configFileName ) throws FluentException{

        this.cfgFileName    = notBlank( configFileName, "Config file name is invalid." );
        this.configuration  = loadConfigs( configFileName );

        this.region         = Region.getRegion( configuration, APP_REGION_KEY );
        this.environment    = Environment.getEnvironment( configuration, APP_ENVIRONMENT_KEY );
        this.role           = Role.getRole( configuration, APP_ROLE_KEY );
        this.appInstance    = configuration.getString( APP_INSTANCE_KEY );
        this.appTimeZone    = parseTimeZone( configuration.getString( APP_TIMEZONE_KEY ) );
        this.appRawOpenTime = configuration.getString( APP_OPEN_TIME_KEY );
        this.appRawCloseTime= configuration.getString( APP_CLOSE_TIME_KEY );
        this.appOpenTime    = getAdjustedOpen( appRawOpenTime, appRawCloseTime, appTimeZone, System.currentTimeMillis( ) );
        this.appCloseTime   = getAdjustedClose( appRawOpenTime, appRawCloseTime, appTimeZone, System.currentTimeMillis( ) );
        this.workingHours   = appRawOpenTime + DASH + appRawCloseTime;

        this.exchangeMap    = parseExchangeDetails( );

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


    // Exchange Specific
    // --------------------------------------------------------------
    public final Map<Exchange, ExchangeDetails> getExchangeMap( ){
        return exchangeMap;
    }


    public final Config getConfig( ){
        return configuration;
    }


    public final String getFrameworkInfo( ){

        StringBuilder builder = new StringBuilder( TWO * SIXTY_FOUR );

        builder.append( L_BRACKET );
        builder.append( "Instance:" ).append( appInstance );
        builder.append( ", Environment:" ).append( environment );
        builder.append( ", Region:" ).append( region );
        builder.append( ", Role:" ).append( role );
        builder.append( ", State:" ).append( StateManager.getState( ) );
        builder.append( ", Process:" ).append( getFullProcessName( ) );
        builder.append( R_BRACKET );

        return builder.toString( );

    }


    protected final Config loadConfigs( String fileName ){

        Config configuration = null;

        try{

            configuration = ConfigFactory.load( fileName );

        }catch( Exception e ){
            throw new RuntimeException( "Failed to load " + fileName, e );
        }

        return configuration;

    }

    public final Map<Exchange, ExchangeDetails> getExchangeDetailsMap( ){
        return exchangeMap;
    }


    protected final Map<Exchange, ExchangeDetails> parseExchangeDetails( ) throws FluentException {

        Map<Exchange, ExchangeDetails> eMAP = new HashMap<>( );
        List<? extends Config> eConfigList = configuration.getConfigList( EXCHANGE_SECTION_KEY );

        for( Config eConfig : eConfigList ){

            String exchangeKey = eConfig.getString( "name" );
            String openTime = eConfig.getString( "openTime" );
            String closeTime = eConfig.getString( "closeTime" );
            String timeZone = eConfig.getString( "timeZone" );
            String speedLimit = eConfig.getString( "speedLimit" );

            ExchangeDetails details = new ExchangeDetails( exchangeKey, openTime, closeTime, timeZone, speedLimit );
            eMAP.put( details.getExchange( ), details );

        }

        return eMAP;

    }


    public final List<? extends Config> getMDAdaptorConfigs( ) {
        return configuration.getConfigList( MD_ADAPTORS_SECTION_KEY );
    }


    @Override
    public String toString( ){
        return getCfgFileName( ) + COMMASP + configuration.toString( );
    }

}
