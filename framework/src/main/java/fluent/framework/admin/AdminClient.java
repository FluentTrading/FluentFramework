package fluent.framework.admin;
/*@formatter:off */

import java.io.*;
import java.util.*;
import com.typesafe.config.*;
import fluent.framework.core.*;
import com.j256.simplejmx.client.*;

import static fluent.framework.util.Toolkit.*;
import static fluent.framework.util.Constants.*;
import static fluent.framework.core.FluentConfig.*;


public final class AdminClient{
    
    private final JmxClient client;
    
    private final static String HELP        = "help";
    private final static String QUIT        = "quit";
    private final static String PROMPT      = "Admin >> ";
    private final static String DEFAULT_HOST= "localhost";
    private final static String ADMIN_CFG   = TOP_SECTION_KEY + "adminClient";
    private final static String JMX_PORT_CFG= "jmxPort";
    
    
    public AdminClient( int port ) throws Exception{
        this( DEFAULT_HOST, port );        
    }
    
    
    public AdminClient( String hostname, int port ) throws Exception{
        this.client  = new JmxClient( hostname, port );
    }
    
    
    public final void runCommands( ) throws IOException{
        
        System.out.println( "Admin client started, Type: [" + HELP + "] or [" + QUIT + "]" );
        System.out.print( PROMPT );
        
        try( Scanner scanner    = new Scanner(System.in) ){
        
            while( true ){
            
                String line     = scanner.nextLine( );
                if( line == null ) break;
                            
                boolean invalid = isBlank(line) || line.startsWith(HASH);
                if( invalid ) continue;
                
                boolean noCmd   = line.trim( ).equals(PROMPT);
                if( noCmd ) continue;
                
                String[] parts  = line.split( SPACE );
                String command  = parts[ ZERO ];
            
                if( command.startsWith(HELP) ){
                    handleHelp( );
                
                }else if( command.startsWith(QUIT) ){
                    break;
            
                }else{
                    handleCommand( command, parts );
                }
            
            }                
        
        }catch( Exception e ){
            e.printStackTrace( );
        }
        
    }
    
    
    protected final void handleCommand( String command, String[ ] parts ) {
        System.out.println( "Execute command >> " + command );
        System.out.println( "Unimplemented" );
    }



    protected final void handleHelp( ){
        System.out.println( EMPTY );
        System.out.println( padRight("Command", 25) + padRight("Usage", 25) );
        System.out.println( "---------------------------------------------------" );
        
        System.out.println( EMPTY );
        AdminCommand.printCommands( );
        //System.out.println( EMPTY );
    }
    


    public final static void main( String[] args ){
        
        try{
            
            boolean noArguments  = (args == null || args.length != 1);
            if( noArguments ){
                System.err.println( "FAILED to start AdminCommandManager as port number is missing" );
                return;
            }
            
            int portNumber      = Integer.parseInt( args[0] );
            AdminClient client  = new AdminClient( portNumber );
            client.runCommands( );
            
        }catch( Exception e ){
            System.err.println("FAILED to start Admin client on port: " + Arrays.deepToString(args) );
            System.err.println( e.getMessage( ) );
        }
        
    }


    public final static Config parseAdminConfig( FluentConfig config ){
        return config.getSectionConfig( ADMIN_CFG );
        
    }


    public final static int parsePort( Config adminConfig ){
        return adminConfig.getInt( JMX_PORT_CFG );
    }

}
