package com.chancorp.audiofornerds.helper;

import android.util.Log;

//Logging Convenience Class
public class Log2 {
    public static final String LOG_TAG="CS_AFN";
    public static void log(int level,Object callingClass,Object... arguments){
        StringBuilder log=new StringBuilder();
        log.append("[From ");
        log.append(callingClass.getClass().getSimpleName());
        log.append("]");
        for (Object arg:arguments){
            log.append(" | ");
            log.append(arg.toString());
        }

        switch (level){
            case 0:
                Log.v(LOG_TAG,log.toString());
                break;
            case 1:
                Log.d(LOG_TAG,log.toString());
                break;
            case 2:
                Log.i(LOG_TAG,log.toString());
                break;
            case 3:
                Log.w(LOG_TAG,log.toString());
                break;
            case 4:
                Log.e(LOG_TAG,log.toString());
                break;
        }
    }
}
