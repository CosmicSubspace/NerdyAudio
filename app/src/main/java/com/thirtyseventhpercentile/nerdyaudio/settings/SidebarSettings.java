//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.ui.VisualizationManager;
import com.thirtyseventhpercentile.nerdyaudio.visuals.AlbumArtVisuals;
import com.thirtyseventhpercentile.nerdyaudio.visuals.BallsVisuals;
import com.thirtyseventhpercentile.nerdyaudio.visuals.CircleVisuals;
import com.thirtyseventhpercentile.nerdyaudio.visuals.LoudnessGraphVisuals;
import com.thirtyseventhpercentile.nerdyaudio.visuals.SpectrogramVisuals;
import com.thirtyseventhpercentile.nerdyaudio.visuals.SpectrumVisuals;
import com.thirtyseventhpercentile.nerdyaudio.visuals.WaveformVisuals;

import java.io.Serializable;


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

    transient Spinner visSpinner;
    transient FrameLayout visual_setting_container;

    VisualizationSettings visualizationSettings;

    VisualizationManager vm;



    Context ctxt;
    private SidebarSettings(Context c) {
        visualizationSettings = new VisualizationSettings(this,c);


        vm=VisualizationManager.getInstance();
        this.ctxt=c;
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



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        visual_setting_container.removeAllViews();
        switch (position) {
            case 0:
                visualizationSettings.setActiveVisualization(VisualizationSettings.VU);
                vm.setActiveRenderer(new LoudnessGraphVisuals(ctxt));


                break;
            case 1:
                visualizationSettings.setActiveVisualization(VisualizationSettings.WAVEFORM);
                vm.setActiveRenderer(new WaveformVisuals(ctxt));
                break;
            case 2:
                visualizationSettings.setActiveVisualization(VisualizationSettings.SPECTRUM);
                vm.setActiveRenderer(new SpectrumVisuals(ctxt));
                break;
            case 3:
                visualizationSettings.setActiveVisualization(VisualizationSettings.SPECTROGRAM);
                vm.setActiveRenderer(new SpectrogramVisuals(ctxt));
                break;
            case 4:
                visualizationSettings.setActiveVisualization(VisualizationSettings.CIRCLE);
                vm.setActiveRenderer(new CircleVisuals(ctxt));
                break;
            case 5:
                visualizationSettings.setActiveVisualization(VisualizationSettings.ALBUM_ART);
                vm.setActiveRenderer(new AlbumArtVisuals(ctxt));
                break;
            case 6:
                visualizationSettings.setActiveVisualization(VisualizationSettings.BALLS);
                vm.setActiveRenderer(new BallsVisuals(ctxt));
                break;

        }

        vm.getActiveRenderer().putSettings(vm.getActiveRenderer().loadSettings());


        //We use the context from the parent view, since the ctxt variable (ApplicationContext) does not have the matching style.
        visual_setting_container.addView(SettingsUiFactory.generateSettings(vm.getActiveRenderer().getSettings(),parent.getContext(),null));



        visualizationSettings.save();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
