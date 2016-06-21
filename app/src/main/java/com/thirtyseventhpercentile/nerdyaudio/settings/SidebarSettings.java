//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.settings;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.SettingsUpdateListener;

import java.io.Serializable;
import java.util.ArrayList;


public class SidebarSettings implements AdapterView.OnItemSelectedListener, Serializable {
    transient private static SidebarSettings inst; //TODO am I overusing Singleton?

    public static SidebarSettings instantiate(Context c){
        inst=new SidebarSettings(c); //TODO something tells me this is not the way of doing things.
        return inst;
    }

    public static SidebarSettings getInstance() {
        if (inst == null) {
            Log2.log(4,SidebarSettings.class,"getInstance() called before instantiate() was called. Expect a NullPopinterException soon.");
        }
        return inst;
    }


    private static final String LOG_TAG = "CS_AFN";

    transient ArrayList<SettingsUpdateListener> suls = new ArrayList<>();
    transient Spinner visSpinner;
    transient FrameLayout visual_setting_container;

    VisualizationSettings visualizationSettings;
    VUMeterSettings vuMeterSettings;
    WaveformVisualSettings waveformVisualSettings;
    SpectrumVisualSettings spectrumVisualSettings;
    SpectrogramVisualSettings spectrogramVisualSettings;
    CircleVisualSettings circleVisualSettings;
    AlbumArtSettings albumArtSettings;
    BallsVisualSettings ballsVisualSettings;


    public void addSettingsUpdateListener(SettingsUpdateListener sul) {

        //Log2.log(1,this, "addSettingsUpdateListener() Entered");
        this.suls.add(sul);

    }

    public void removeSettingsUpdateListener(SettingsUpdateListener sul) {

        //Log2.log(1,this, "removeSettingsUpdateListener() Entered");
        this.suls.remove(sul);

    }

    private SidebarSettings(Context c) {
        visualizationSettings = new VisualizationSettings(this,c);
        vuMeterSettings = new VUMeterSettings(this,c);
        waveformVisualSettings = new WaveformVisualSettings(this,c);
        spectrumVisualSettings=new SpectrumVisualSettings(this,c);
        spectrogramVisualSettings =new SpectrogramVisualSettings(this,c);
        circleVisualSettings=new CircleVisualSettings(this,c);
        albumArtSettings=new AlbumArtSettings(this,c);
        ballsVisualSettings=new BallsVisualSettings(this,c);
    }

    public View getView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.drawer, container, false);

        visSpinner = (Spinner) v.findViewById(R.id.visuals_selector);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(container.getContext(), R.layout.visuals_spinner_element, VisualizationSettings.visualizations);
        //adapter.setDropDownViewResource(R.layout.visuals_spinner_element);
        visSpinner.setAdapter(adapter);
        visSpinner.setOnItemSelectedListener(this);
        visual_setting_container = (FrameLayout) v.findViewById(R.id.visuals_setting_container);

        return v;
    }

    protected void notifyUI(BaseSetting setting) {
        //Since updated() invokes a Renderer construction, which in turns registers itself as a SettingsUpdateListener,
        //A ConcurrentModificationException is raised.
        //Therefore, we copy the arraylist before iterating though it.
        ArrayList<SettingsUpdateListener> tSuls = new ArrayList<>(this.suls);

        Log2.log(2,this, "Notifying UI...");
        //Log2.log(1,this, "NotifyUI() Entered");
        for (SettingsUpdateListener sul : tSuls) {
            sul.updated(setting);
        }
        //Log2.log(1,this, "NotifyUI() Exited");

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        switch (position) {
            case 0:
                visualizationSettings.setActiveVisualization(VisualizationSettings.VU);
                visual_setting_container.removeAllViews();
                visual_setting_container.addView(vuMeterSettings.getSettingsView(li, visual_setting_container, null));
                break;
            case 1:
                visualizationSettings.setActiveVisualization(VisualizationSettings.WAVEFORM);
                visual_setting_container.removeAllViews();
                visual_setting_container.addView(waveformVisualSettings.getSettingsView(li, visual_setting_container, null));
                break;
            case 2:
                visualizationSettings.setActiveVisualization(VisualizationSettings.SPECTRUM);
                visual_setting_container.removeAllViews();
                visual_setting_container.addView(spectrumVisualSettings.getSettingsView(li, visual_setting_container, null));
                break;
            case 3:
                visualizationSettings.setActiveVisualization(VisualizationSettings.SPECTROGRAM);
                visual_setting_container.removeAllViews();
                visual_setting_container.addView(spectrogramVisualSettings.getSettingsView(li, visual_setting_container, null));
                break;
            case 4:
                visualizationSettings.setActiveVisualization(VisualizationSettings.CIRCLE);
                visual_setting_container.removeAllViews();
                visual_setting_container.addView(circleVisualSettings.getSettingsView(li, visual_setting_container, null));
                break;
            case 5:
                visualizationSettings.setActiveVisualization(VisualizationSettings.ALBUM_ART);
                visual_setting_container.removeAllViews();
                visual_setting_container.addView(albumArtSettings.getSettingsView(li, visual_setting_container, null));
                break;
            case 6:
                visualizationSettings.setActiveVisualization(VisualizationSettings.BALLS);
                visual_setting_container.removeAllViews();
                visual_setting_container.addView(ballsVisualSettings.getSettingsView(li, visual_setting_container, null));
                break;

        }
        visualizationSettings.save();
        notifyUI(visualizationSettings);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public BaseSetting getSetting(int type){
        switch (type){
            case BaseSetting.SPECTRUM:
                return spectrumVisualSettings;
            case BaseSetting.VU:
                return vuMeterSettings;
            case BaseSetting.WAVEFORM:
                return waveformVisualSettings;
            case BaseSetting.SPECTROGRAM:
                return spectrogramVisualSettings;
            case BaseSetting.VISUALIZATION:
                return visualizationSettings;
            case BaseSetting.CIRCLE:
                return circleVisualSettings;
            case BaseSetting.ALBUM_ART:
                return albumArtSettings;
            case BaseSetting.BALLS:
                return ballsVisualSettings;
        }
        return null;
    }
}
