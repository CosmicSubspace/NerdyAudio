//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.helper;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;


public class ErrorLogger {
    public static final int e=-1;
    public static void log(Exception e){

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        Log.e("CS_AFN","Error Handled:\n"+sw.toString());

    }
}
