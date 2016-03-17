//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.settings;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;


public class VisualizationSettings extends BaseSetting implements Serializable {

    private static final String PREF_IDENTIFIER = "com.thirtyseventhpercentile.audiofornerds.settings.VisualizationSettings";

    public VisualizationSettings(SidebarSettings sbs, Context c){
        super(sbs,c);

        load();
        sbs.notifyUI(this);
    }
    @Override
    public int getType(){
        return BaseSetting.VISUALIZATION;
    }

    @Override
    protected void save() {
        SharedPreferences.Editor editor=getSharedPreferences(PREF_IDENTIFIER).edit();
        editor.putInt("activeVisualization",activeVisualization);
        editor.apply();
    }

    @Override
    protected void load() {
        SharedPreferences pref=getSharedPreferences(PREF_IDENTIFIER);
        setActiveVisualization(pref.getInt("activeVisualization", activeVisualization));
        sbs.notifyUI(this);
    }

    int type;
    public static final String[] visualizations=new String[]{"VU Meter","Waveform","Spectrum","Spectrogram"};
    int activeVisualization =0;

    public int getActiveVisualization(){
        return activeVisualization;
    }
    public void setActiveVisualization(int type){
        this.activeVisualization =type;
    }

}
