package com.chancorp.audiofornerds.helper;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Chan on 2015-12-09.
 */
public class ErrorLogger {
    public static final int e=-1;
    public static void log(Exception e){

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        Log.e("CS_AFN","Error Handled:\n"+sw.toString());

    }
}
