//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.helper;

import android.util.Log;

//Logging Convenience Class
public class Log2 {
    public static final String LOG_TAG="CS_AFN";
    public static void log(int level,Object callingClass,Object... arguments){
        if (callingClass!=null) log(level,callingClass.getClass(), arguments);
        else log(level,null, arguments);
    }
    public static void log(int level,Class callingClass,Object... arguments){
        StringBuilder log=new StringBuilder();
        log.append("[From ");
        if (callingClass!=null) log.append(callingClass.getSimpleName());
        else log.append("NULL");
        log.append("]");
        for (Object arg:arguments){
            log.append(" | ");
            try {
                log.append(arg.toString());
            }catch(NullPointerException e){
                log.append("NULL");
            }
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
