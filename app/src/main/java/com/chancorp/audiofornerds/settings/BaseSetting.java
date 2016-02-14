package com.chancorp.audiofornerds.settings;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Chan on 2/3/2016.
 */
public abstract class BaseSetting {
    public static final int VISUALIZATION=124;
    public static final int VU=129642;
    public static final int WAVEFORM=984132;
    public static final int SPECTOGRAPH=685321;
    public static final int NONE=1243634;
    public static final int SPECTRUM=12529368;

    SidebarSettings sbs;
    Context ctxt;

    public BaseSetting(SidebarSettings sbs, Context c){
        this.sbs=sbs;
        this.ctxt=c;
    }

    public abstract int getType();

    protected abstract void save();
    protected abstract void load();

    protected SharedPreferences getSharedPreferences(String name){
        return ctxt.getSharedPreferences(name,Context.MODE_PRIVATE);
    }

}