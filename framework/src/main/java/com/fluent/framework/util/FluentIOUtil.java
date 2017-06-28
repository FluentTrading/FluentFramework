package com.fluent.framework.util;
/*@formatter:off */

import java.nio.file.*;
import java.util.*;


public final class FluentIOUtil{

   
    protected FluentIOUtil( ){}


    public final static List<String> loadFile( String filename ) throws Exception{
        Path filePath   = Paths.get(filename);
        if( !Files.exists(filePath) ){
            throw new Exception( "FAILED to load " + filePath + " as it doesn't exist." );
        }
    
        List<String> all= Files.readAllLines( filePath );
        
        return all;
    
    }

}
