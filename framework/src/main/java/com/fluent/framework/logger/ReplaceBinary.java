package com.fluent.framework.logger;

public class ReplaceBinary{
    
    
    /**
     * Returns the start position of the first occurrence of the specified {@code
     * target} within {@code array}, or {@code -1} if there is no such occurrence.
     *
     * <p>More formally, returns the lowest index {@code i} such that
     * {@code Arrays.copyOfRange(array, i, i + target.length)} contains exactly the same elements as
     * {@code target}.
     *
     * @param array the array to search for the sequence {@code target}
     * @param target the array to search for as a sub-sequence of {@code array}
     */
    public static int indexOf(byte[] array, byte[] target) {
      
      if( target.length == 0 ){
        return 0;
      }

      outer:
      for(int i = 0; i < array.length - target.length + 1; i++ ){
        for( int j = 0; j < target.length; j++ ){
          if (array[i + j] != target[j]) {
            continue outer;
          }
        }
        return i;
      }
      return -1;
    }
    
    
    
    public static byte[] indexOf( byte[] array, byte[] target, byte[] replace ){
        
        byte[] replacedArray    = new byte[ array.length ];
        
        if( target.length == 0 ){
            return array;
        }

        outer:
        for( int i = 0; i < array.length - target.length + 1; i++ ){
          for( int j = 0; j < target.length; j++ ){
            if( array[i + j] != target[j] ){
                replacedArray[i] = array[i];
                continue outer;
            }
          }
          
          //FOUND: Use an Expandable array and replace
          System.arraycopy( array, 0, replacedArray, i, replace.length );
          return replacedArray;
        
        }
        
        return array;
      
    }
    
    

    public static void main( String[ ] args ){

        String dataStr  = "This is {} data.";
        byte[] dataByte = dataStr.getBytes( );
        
        byte[] placePatt= "{}".getBytes( );
        byte[] replacedArr  = indexOf( dataByte, placePatt, "test".getBytes( ) );
        
        System.err.println( ">> " + new String(replacedArr) );
        

    }

}
