package com.fluent.framework.util;
/*@formatter:off */
import java.io.*;
import java.nio.file.*;
import java.util.*;

import com.fluent.framework.core.*;


public final class FluentIOUtil{

   
    protected FluentIOUtil( ){}


    public final static List<String> loadFile( String filename ) throws FluentException, IOException{
        Path filePath   = Paths.get(filename);
        if( !Files.exists(filePath) ){
            throw new FluentException( "FAILED to load " + filePath + " as it doesn't exist." );
        }
    
        List<String> all= Files.readAllLines( filePath );
        
        return all;
    
    }

}
