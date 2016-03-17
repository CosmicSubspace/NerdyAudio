//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.helper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class ByteConversions {
    public static short[] bytesToShorts(byte[] b){
        short[] shorts = new short[b.length/2];
    // to turn bytes to shorts as either big endian or little endian.
        ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        return shorts;
    }
}
