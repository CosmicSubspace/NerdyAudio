//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.cosmicsubspace.nerdyaudio.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.cosmicsubspace.nerdyaudio.R;
import com.cosmicsubspace.nerdyaudio.helper.Log2;
import com.cosmicsubspace.nerdyaudio.settings.SettingsUiFactory;
import com.cosmicsubspace.nerdyaudio.visuals.AlbumArtVisuals;
import com.cosmicsubspace.nerdyaudio.visuals.BallsVisuals;
import com.cosmicsubspace.nerdyaudio.visuals.CircleVisuals;
import com.cosmicsubspace.nerdyaudio.visuals.LoudnessGraphVisuals;
import com.cosmicsubspace.nerdyaudio.visuals.SpectrogramVisuals;
import com.cosmicsubspace.nerdyaudio.visuals.SpectrumVisuals;
import com.cosmicsubspace.nerdyaudio.visuals.WaveformVisuals;


public class SidebarSettings implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    transient private static SidebarSettings inst; //TODO am I overusing Singleton?

    public static SidebarSettings instantiate(Context c) {
        inst = new SidebarSettings(c); //TODO something tells me this is not the way of doing things.
        return inst;
    }

    public static SidebarSettings getInstance() {
        if (inst == null) {
            Log2.log(4, SidebarSettings.class, "getInstance() called before instantiate() was called. Expect a NullPopinterException soon.");
        }
        return inst;
    }


    private static final String LOG_TAG = "CS_AFN";

    Spinner visSpinner;
    FrameLayout visual_setting_container;
    Switch volumeControlsToggle;

    VisualizationManager vm;
    Context ctxt;

    int visualizationSpinnerSelection;
    boolean volumeControlsEnabled;

    public boolean getVolumeControlsEnabled(){return volumeControlsEnabled;}

    public static String PREF_IDENTIFIER="com.thirtyseventhpercentile.nerdyaudio.ui.SidebarSettings";
    public void save() {
        Log2.log(1,this,"Saving settings",visualizationSpinnerSelection, volumeControlsEnabled);
        SharedPreferences.Editor editor = ctxt.getSharedPreferences(PREF_IDENTIFIER, Context.MODE_PRIVATE).edit();
        editor.putInt("visualizationSpinnerSelection",visualizationSpinnerSelection);
        editor.putBoolean("volumeControlsEnabled", volumeControlsEnabled);
        editor.apply();
    }


    protected void load() {

        SharedPreferences pref = ctxt.getSharedPreferences(PREF_IDENTIFIER, Context.MODE_PRIVATE);
        visualizationSpinnerSelection=pref.getInt("visualizationSpinnerSelection", -1);
        volumeControlsEnabled =pref.getBoolean("volumeControlsEnabled", false);
        Log2.log(1,this,"Loading settings.",visualizationSpinnerSelection, volumeControlsEnabled);
    }

    private SidebarSettings(Context c) {
        vm = VisualizationManager.getInstance();
        this.ctxt = c;
        //TODO I/O in UI thread.
        load();
    }

    public View getView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.drawer, container, false);

        visSpinner = (Spinner) v.findViewById(R.id.visuals_selector);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(container.getContext(), R.layout.visuals_spinner_element,visualizations);
        //adapter.setDropDownViewResource(R.layout.visuals_spinner_element);
        visSpinner.setAdapter(adapter);

        visual_setting_container = (FrameLayout) v.findViewById(R.id.visuals_setting_container);
        volumeControlsToggle=(Switch) v.findViewById(R.id.vbc_toggle);

        volumeControlsToggle.setOnCheckedChangeListener(SidebarSettings.this);
        visSpinner.setOnItemSelectedListener(SidebarSettings.this);

        visSpinner.setSelection(visualizationSpinnerSelection);
        volumeControlsToggle.setChecked(volumeControlsEnabled);

        return v;
    }
    public static final String[] visualizations = new String[]{"Loudness Graph", "Waveform", "Spectrum", "Spectrogram", "Circle", "Album Art", "Ball Physics"};

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Log2.log(2,this,"Spinner Selected",position);
        visual_setting_container.removeAllViews();

        visualizationSpinnerSelection=position;

        switch (position) {
            case 0:
                vm.setActiveRenderer(new LoudnessGraphVisuals(ctxt));
                break;
            case 1:
                vm.setActiveRenderer(new WaveformVisuals(ctxt));
                break;
            case 2:
                vm.setActiveRenderer(new SpectrumVisuals(ctxt));
                break;
            case 3:
                vm.setActiveRenderer(new SpectrogramVisuals(ctxt));
                break;
            case 4:
                vm.setActiveRenderer(new CircleVisuals(ctxt));
                break;
            case 5:
                vm.setActiveRenderer(new AlbumArtVisuals(ctxt));
                break;
            case 6:
                vm.setActiveRenderer(new BallsVisuals(ctxt));
                break;

        }

        vm.getActiveRenderer().putSettings(vm.getActiveRenderer().loadSettings());


        //We use the context from the parent view, since the ctxt variable (ApplicationContext) does not have the matching style.
        visual_setting_container.addView(SettingsUiFactory.generateSettings(vm.getActiveRenderer().getSettings(), parent.getContext(), null));
        save();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId()==R.id.vbc_toggle){
            volumeControlsEnabled =isChecked;
            save();
        }else{
            Log2.log(3,this,"More buttons???");
        }
    }
}
