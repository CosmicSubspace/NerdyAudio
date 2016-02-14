package com.chancorp.audiofornerds.settings;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

/**
 * Created by Chan on 2/3/2016.
 */
public class VisualizationSettings extends BaseSetting implements Serializable {

    private static final String PREF_IDENTIFIER = "com.chancorp.audiofornerds.settings.VisualizationSettings";

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
    public static final String[] visualizations=new String[]{"VU Meter","Waveform","Spectrum","Spectograph"};
    int activeVisualization =0;

    public int getActiveVisualization(){
        return activeVisualization;
    }
    public void setActiveVisualization(int type){
        this.activeVisualization =type;
    }

}
