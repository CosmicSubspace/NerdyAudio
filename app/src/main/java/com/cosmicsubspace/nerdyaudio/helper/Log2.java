//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.cosmicsubspace.nerdyaudio.helper;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

//Logging Convenience Class
public class Log2 {
    private static final int PERMANENT_LOG_LEVEL = 3;
    public static final String LOG_TAG = "CS_AFN";

    private static StringBuilder importantLogs = new StringBuilder();

    public static String getImportantLogs(boolean clear) {
        String res = importantLogs.toString();
        if (clear) importantLogs = new StringBuilder();
        return res;
    }

    public static void dumpLogsAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dumpLogs();
            }
        }).start();
    }

    private static void dumpLogs() {
        FileWriter f;
        try {
            f = new FileWriter(
                    new File(Environment.getExternalStorageDirectory() + "/AFN_log.txt")
                    , true);
            f.write("\n\n\n" + DateFormat.getDateTimeInstance().format(new Date()) + "\n");
            f.write(getImportantLogs(true));
            f.flush();
            f.close();
        } catch (IOException e1) {
            log(e1);
        } //Double exception?
    }


    public static void log(int level, Object callingClass, Object... arguments) {
        if (callingClass != null) log(level, callingClass.getClass(), arguments);
        else log(level, null, arguments);
    }

    public static void log(int level, Class callingClass, Object... arguments) {
        StringBuilder log = new StringBuilder();
        log.append(level + " ");
        log.append("[From ");
        if (callingClass != null) log.append(callingClass.getSimpleName());
        else log.append("NULL");
        log.append("]");
        for (Object arg : arguments) {
            log.append(" | ");
            try {
                log.append(arg.toString());
            } catch (NullPointerException e) {
                log.append("NULL");
            }
        }

        if (level >= PERMANENT_LOG_LEVEL) {
            importantLogs.append(log.toString());
            importantLogs.append("\n");
        }

        switch (level) {
            case 0:
                Log.v(LOG_TAG, log.toString());
                break;
            case 1:
                Log.d(LOG_TAG, log.toString());
                break;
            case 2:
                Log.i(LOG_TAG, log.toString());
                break;
            case 3:
                Log.w(LOG_TAG, log.toString());
                break;
            case 4:
                Log.e(LOG_TAG, log.toString());
                break;
            case 5:
                Log.wtf(LOG_TAG, log.toString());
        }
    }

    public static void log(Exception e) {
        log(4, ErrorLogger.class, "Error Handled:\n" + logToString(e));
    }

    public static String logToString(Throwable e) {
        return Log.getStackTraceString(e);
    }
}
