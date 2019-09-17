package com.fluent.framework.logger;

import java.nio.*;
import java.nio.charset.*;

import sun.nio.cs.*;

/**
 * Customized version of the JDK7 UTF8 encoder targeting the use-case of
 * encoding strings that should fit into a byte buffer.
 * 
 * @author nitsan
 * 
 */
public class CustomUtf8Encoder {

    // as opposed to the JDK version where this is allocated lazily if required
    private final Surrogate.Parser sgp = new Surrogate.Parser();
    // taking these off the stack seems to make it go faster
    private int lastSp;
    private long lastDp;

    /**
     * Encodes a string into the byte buffer using the UTF-8 encoding. Like the
     * JDK encoder this will return UNDERFLOW on success and ERROR/OVERFLOW
     * otherwise, but unlike the JDK encode it does not allow resuming the
     * operation and will not move the byte buffer position should the string
     * not fit in it.
     * 
     * @param src
     * @param dst
     * @return
     */
    public final CoderResult encodeString(String src, ByteBuffer dst) {
        return encodeStringToHeap(src, dst);
    }

    public CoderResult encodeStringToHeap(String src, ByteBuffer dst) {
        int lastDp = 0;
        int arrayOffset = dst.arrayOffset();
        int dp = arrayOffset + dst.position();
        int dl = arrayOffset + dst.limit();

        int spCurr = UnsafeString.getOffset(src);
        int sl = src.length();

        try {
            CoderResult result = encode(UnsafeString.getChars(src), spCurr, sl, dst.array(), dp, dl);
            dst.position(lastDp - arrayOffset);
            return result;
        } catch (ArrayIndexOutOfBoundsException e) {
            return CoderResult.OVERFLOW;
        }

    }
    


    private CoderResult encode(char[] sa, int spCurr, int sl, byte[] da, int dp, int dl) {
        lastSp = spCurr;
        int dlASCII = dp + Math.min(sl - lastSp, dl - dp);
        // handle ascii encoded strings in an optimised loop
        while (dp < dlASCII && sa[lastSp] < 128)
            da[dp++] = (byte) sa[lastSp++];

        // we are counting on the JVM array boundary checks to throw an
        // exception rather then
        // checkin boundaries ourselves... no nice, and potentailly not that
        // much of a
        // performance enhancement.
        while (lastSp < sl) {
            int c = sa[lastSp];
            if (c < 128) {
                da[dp++] = (byte) c;
            } else if (c < 2048) {
                da[dp++] = (byte) (0xC0 | (c >> 6));
                da[dp++] = (byte) (0x80 | (c & 0x3F));
            } else if (Surrogate.is(c)) {
                int uc = sgp.parse((char) c, sa, lastSp, sl);
                if (uc < 0) {
                    lastDp = dp;
                    return sgp.error();
                }
                da[(dp++)] = (byte) (0xF0 | uc >> 18);
                da[(dp++)] = (byte) (0x80 | uc >> 12 & 0x3F);
                da[(dp++)] = (byte) (0x80 | uc >> 6 & 0x3F);
                da[(dp++)] = (byte) (0x80 | uc & 0x3F);
                ++lastSp;
            } else {
                da[(dp++)] = (byte) (0xE0 | c >> 12);
                da[(dp++)] = (byte) (0x80 | c >> 6 & 0x3F);
                da[(dp++)] = (byte) (0x80 | c & 0x3F);
            }
            ++lastSp;
        }
        
        lastDp = dp;
        return CoderResult.UNDERFLOW;
        
    }
}