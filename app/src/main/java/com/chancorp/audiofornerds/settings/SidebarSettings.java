package com.chancorp.audiofornerds.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.interfaces.SettingsUpdateListener;

import java.io.Serializable;
import java.util.ArrayList;


public class SidebarSettings implements AdapterView.OnItemSelectedListener, Serializable {
    transient private static SidebarSettings inst; //TODO am I overusing Singleton?

    public static SidebarSettings getInstance() {
        if (inst == null) {
            inst = new SidebarSettings();
        }
        return inst;
    }


    private static final String LOG_TAG = "CS_AFN";

    transient ArrayList<SettingsUpdateListener> suls = new ArrayList<>();
    transient Spinner visSpinner;
    transient FrameLayout visual_setting_container;

    VisualizationSettings visualizationSettings = new VisualizationSettings();
    VUMeterSettings vuMeterSettings = new VUMeterSettings(this);
    WaveformVisualSettings waveformVisualSettings = new WaveformVisualSettings(this);
    SpectrumVisualSettings spectrumVisualSettings=new SpectrumVisualSettings(this);


    public void addSettingsUpdateListener(SettingsUpdateListener sul) {

        //Log.d(LOG_TAG, "addSettingsUpdateListener() Entered");
        this.suls.add(sul);

    }

    public void removeSettingsUpdateListener(SettingsUpdateListener sul) {

        //Log.d(LOG_TAG, "removeSettingsUpdateListener() Entered");
        this.suls.remove(sul);

    }

    private SidebarSettings() {
    }

    public View getView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.drawer, container, false);

        visSpinner = (Spinner) v.findViewById(R.id.visuals_selector);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(container.getContext(), android.R.layout.simple_spinner_item, VisualizationSettings.visualizations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

        //Log.d(LOG_TAG, "NotifyUI() Entered");
        for (SettingsUpdateListener sul : tSuls) {
            sul.updated(setting);
        }
        //Log.d(LOG_TAG, "NotifyUI() Exited");

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
                visualizationSettings.setActiveVisualization(VisualizationSettings.SPECTOGRAPH);
                break;

        }

        notifyUI(visualizationSettings);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
