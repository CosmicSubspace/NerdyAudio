//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.settings;

import android.content.Context;
import android.content.SharedPreferences;


public abstract class BaseSetting {

    //TODO : Remove variables altogether : use VisualizationSettings for variable storage.

    public static final int VISUALIZATION=124;
    public static final int VU=129642;
    public static final int WAVEFORM=984132;
    public static final int SPECTROGRAM =685321;
    public static final int NONE=1243634;
    public static final int SPECTRUM=12529368;
    public static final int CIRCLE=152968;
    public static final int ALBUM_ART=12395;
    public static final int BALLS=1573;

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
