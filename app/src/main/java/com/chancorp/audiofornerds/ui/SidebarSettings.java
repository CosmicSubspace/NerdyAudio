package com.chancorp.audiofornerds.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.interfaces.SettingsUpdateListener;
import com.chancorp.audiofornerds.settings.BaseSetting;
import com.chancorp.audiofornerds.settings.VisualizationSettings;

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

    VisualizationSettings visualizationSettings;


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
        return v;
    }
    private void notifyUI(BaseSetting setting){
        for (SettingsUpdateListener sul:this.suls){
            sul.updated(setting);
        }
    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                visualizationSettings.setActiveVisualization(VisualizationSettings.VU);
                break;
            case 1:
                visualizationSettings.setActiveVisualization(VisualizationSettings.WAVEFORM);
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
