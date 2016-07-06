//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.settings;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;


public class VisualizationSettings implements Serializable {

    private static final String PREF_IDENTIFIER = "com.thirtyseventhpercentile.audiofornerds.settings.VisualizationSettings";
    Context c;

    public VisualizationSettings(SidebarSettings sbs, Context c) {
        this.c = c;

        load();

    }


    protected void save() {
        SharedPreferences.Editor editor = c.getSharedPreferences(PREF_IDENTIFIER, Context.MODE_PRIVATE).edit();
        editor.putInt("activeVisualization", activeVisualization);
        editor.apply();
    }


    protected void load() {
        SharedPreferences pref = c.getSharedPreferences(PREF_IDENTIFIER, Context.MODE_PRIVATE);
        setActiveVisualization(pref.getInt("activeVisualization", activeVisualization));

    }

    public static final String[] visualizations = new String[]{"Loudness Graph", "Waveform", "Spectrum", "Spectrogram", "Circle", "Album Art", "Ball Physics"};
    int activeVisualization = 0;

    public int getActiveVisualization() {
        return activeVisualization;
    }

    public void setActiveVisualization(int type) {
        this.activeVisualization = type;
    }

}
