package com.chancorp.audiofornerds.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.interfaces.SettingsUpdateListener;
import com.chancorp.audiofornerds.settings.BaseSetting;
import com.chancorp.audiofornerds.settings.VUMeterSettings;
import com.chancorp.audiofornerds.settings.VisualizationSettings;
import com.chancorp.audiofornerds.settings.WaveformVisualSettings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;


public class SidebarSettings implements AdapterView.OnItemSelectedListener, Serializable{
    transient private static SidebarSettings inst; //TODO am I overusing Singleton?
    public static SidebarSettings getInstance(){
        if (inst==null){
            inst=new SidebarSettings();
        }
        return inst;
    }




    transient ArrayList<SettingsUpdateListener> suls=new ArrayList<>();
    transient Spinner visSpinner;
    transient FrameLayout visual_setting_container;

    VisualizationSettings visualizationSettings=new VisualizationSettings();
    VUMeterSettings vuMeterSettings=new VUMeterSettings();
    WaveformVisualSettings waveformVisualSettings=new WaveformVisualSettings();


    public void addSettingsUpdateListener(SettingsUpdateListener sul){
        this.suls.add(sul);
    }
    private SidebarSettings(){
    }
    public View getView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.drawer, container, false);
        //TODO dynamic changing UI and other settings.
        visSpinner=(Spinner) v.findViewById(R.id.visuals_selector);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(container.getContext(),android.R.layout.simple_spinner_item, VisualizationSettings.visualizations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visSpinner.setAdapter(adapter);
        visSpinner.setOnItemSelectedListener(this);
        visual_setting_container=(FrameLayout)v.findViewById(R.id.visuals_setting_container);
        //TODO retrieve view from Settings object and add it to container.
        return v;
    }
    private void notifyUI(BaseSetting setting){
        for (SettingsUpdateListener sul:this.suls){
            sul.updated(setting);
        }
    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        switch (position){
            case 0:
                visualizationSettings.setActiveVisualization(VisualizationSettings.VU);
                visual_setting_container.removeAllViews();
                visual_setting_container.addView(vuMeterSettings.getSettingsView(li,visual_setting_container,null));
                break;
            case 1:
                visualizationSettings.setActiveVisualization(VisualizationSettings.WAVEFORM);
                visual_setting_container.removeAllViews();
                visual_setting_container.addView(waveformVisualSettings.getSettingsView(li, visual_setting_container, null));
                break;
            case 2:
                visualizationSettings.setActiveVisualization(VisualizationSettings.SPECTRUM);
                break;
            case 3:
                visualizationSettings.setActiveVisualization(VisualizationSettings.SPECTOGRAPH);
                break;

        }

        notifyUI(visualizationSettings);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
