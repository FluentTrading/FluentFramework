package fluent.framework.util;
import static fluent.framework.util.Toolkit.*;

/*@formatter:off */
import java.io.*;


public final class Constants{

    public final static int            ZERO                = 0;
    public final static int            ONE                 = 1;
    public final static int            TWO                 = 2;
    public final static int            THREE               = 3;
    public final static int            FOUR                = 4;
    public final static int            FIVE                = 5;
    public final static int            SIX                 = 6;
    public final static int            SEVEN               = 7;
    public final static int            EIGHT               = 8;
    public final static int            TEN                 = 10;
    public final static int            SIXTEEN             = 16;
    public final static int            TWENTY_FOUR         = 24;
    public final static int            THIRTY_TWO          = 32;
    public final static int            SIXTY               = 60;
    public final static int            SIXTY_FOUR          = 64;
    public final static int            HUNDRED             = 100;
    public final static int            THOUSAND            = 1000;
    public final static int            MILLION             = 1000 * 1000;
    public final static int            NEGATIVE_ONE        = -1;
    public final static long           _24_HOURS_IN_MILLIS = 24 * 60 * 60 * 60;

    public final static char           COLON_CHAR          = ':';
    public final static byte[ ]        EMPTY_BYTE          = { };

    public final static double         ZERO_DOUBLE         = 0.0d;
    public final static double         PRICE_TOLERANCE     = 1.0e-8;


    public final static String         DOT                 = ".";
    public final static String         DASH                = "-";
    public final static String         TAB                 = "\t";
    public final static String         PIPE                = "|";
    public final static String         PLUS                = "+";
    public final static String         EMPTY               = "";
    public final static String         SPACE               = " ";
    public final static String         HASH                = "#";
    public final static String         COMMA               = ",";
    public final static String         COMMASP             = ", ";
    public final static String         COLON               = ":";
    public final static String         QUOTE               = "\"";
    public final static String         NEWLINE             = "\n";
    public final static String         X_SPACE             = " x ";
    public final static String         L_CURL              = "{";
    public final static String         R_CURL              = "}";
    public final static String         L_BRACKET           = "[";
    public final static String         R_BRACKET           = "]";
    public final static String         SEMICOLON           = ".";
    public final static String         UNDERSCORE          = "_";

    public final static String         ZERO_STRING         = "0";
    
    public final static String         SLASH               = File.separator;
    public final static boolean        IS_WINDOWS          = isWindows( );
    public final static boolean        IS_LINUX            = isLinux( );
    public final static String         HOSTNAME            = getHostName( );

    protected Constants( ){}


    public final static String getConfigFile( String key ){
        
        String cfgFile  = System.getProperty( key );
        boolean valid   = !isBlank(cfgFile); 
        if( valid ){
            return cfgFile;
        }

        throw new RuntimeException( "Config file must be specified as a system parameter [" + key + "]" );

    }



}
