package com.fluent.framework.test;

import org.slf4j.*;

import java.lang.Thread.*;

import com.fluent.common.util.*;
import com.fluent.framework.core.*;
import com.fluent.framework.service.*;

import static com.fluent.common.util.Constants.*;


public final class TestLauncher{
	
    private final static String NAME	= TestLauncher.class.getSimpleName();
	private final static Logger LOGGER	= LoggerFactory.getLogger( NAME );

    static{
        Thread.setDefaultUncaughtExceptionHandler( new UncaughtExceptionHandler(){
            @Override
            public void uncaughtException( Thread thread, Throwable ex ){
                LOGGER.warn("CAUGHT unhandled exception in Thread [{}]", thread.getName() );
                LOGGER.warn("Exception: ", ex );
            }
        });
    }
    

	public static void main( String args [] ){
		
		try{

		    String configFileKey= "fluent.configFile";
		    String cfgFileName  = System.getProperty( configFileKey );
	        if( Toolkit.isBlank(cfgFileName) ){
	            throw new RuntimeException( "Config file must be specified as a system parameter [" + configFileKey + "]" );
	        }
	        
	        FluentConfig fluentConfig    = new FluentConfig( cfgFileName );
	        LOGGER.info("Starting [{}]{}", fluentConfig.getApplicationInfo( ), NEWLINE);
	                
			FluentServiceManager service = new FluentServiceManager(fluentConfig);
			Runtime.getRuntime().addShutdownHook( new ShutdownThread(service) );
	        service.start();
						
		}catch( Exception e ){
			LOGGER.warn("ERROR starting application!", e);
			System.exit( ONE );
		}

    }
	
	
	public final static class ShutdownThread extends Thread{

        private final FluentServiceManager service;
        
        public ShutdownThread( FluentServiceManager service ){
            this.service = service;
        }

        @Override
        public final void run( ){

            try{
            
                LOGGER.info("Shutdown hook called, will attempt to stop all services in the controller.");
                service.stop();
                
                LOGGER.info("Shutdown hook executed successfully.");
                LOGGER.info("---------------------------------------");
                
            }catch( Exception e ){
                LOGGER.warn("Exception while running shut-down hook.", e);
            }
    
        }
    
    }

	
}

