package com.chancorp.audiofornerds.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.exceptions.InvalidParameterException;
import com.chancorp.audiofornerds.helper.ErrorLogger;
import com.chancorp.audiofornerds.interfaces.SettingsUpdateListener;
import com.chancorp.audiofornerds.settings.BaseSetting;
import com.chancorp.audiofornerds.settings.SidebarSettings;
import com.chancorp.audiofornerds.settings.VisualizationSettings;
import com.chancorp.audiofornerds.visuals.SpectographVisuals;
import com.chancorp.audiofornerds.visuals.SpectrumVisuals;
import com.chancorp.audiofornerds.visuals.VUMeterVisuals;
import com.chancorp.audiofornerds.visuals.VisualizationView;
import com.chancorp.audiofornerds.visuals.WaveformVisuals;

/**
 * Created by Chan on 2015-12-16.
 */
public class NowPlayingFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener,SettingsUpdateListener{
    public static final String LOG_TAG="CS_AFN";

    VisualizationView vv;
    Button visSettingsBtn;
    SidebarSettings sbs;


    boolean settingsMode=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sbs=SidebarSettings.getInstance();
        sbs.addSettingsUpdateListener(this);

        View v=inflater.inflate(R.layout.tab_frag_nowplaying, container, false);


        vv=(VisualizationView)v.findViewById(R.id.visualization);
        if (vv == null) Log.e(LOG_TAG, "VisualizationView is null!");
        else if (vv.getRenderThread() == null) Log.e(LOG_TAG, "Renderer is null!");
        else{
            vv.getRenderThread().setMaxFPS(60);
        }


        visSettingsBtn=(Button)v.findViewById(R.id.visuals_settings_button);
        visSettingsBtn.setOnClickListener(this);

        return v;


    }

    @Override
    public void onClick(View view) {

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

    }

    public void onNothingSelected(AdapterView<?> parent) {
        Log.i(LOG_TAG,"Adapter > Nothing selected.");
    }

    @Override
    public void updated(BaseSetting setting) {
        if(setting.getType()==BaseSetting.VISUALIZATION){
            VisualizationSettings visSet=(VisualizationSettings)setting;
            if (visSet.getActiveVisualization()==VisualizationSettings.VU) {
                VUMeterVisuals vis=new VUMeterVisuals(getResources().getDisplayMetrics().density);
                vis.setRange(4096);
                vv.getRenderThread().setRenderer(vis);
            }else if (visSet.getActiveVisualization()==VisualizationSettings.SPECTRUM){
                SpectrumVisuals vis=new SpectrumVisuals(getResources().getDisplayMetrics().density);
                vis.setFFTSize(2048);
                vis.setSpacing(0.3f);
                vv.getRenderThread().setRenderer(vis);
            }else if (visSet.getActiveVisualization()==VisualizationSettings.WAVEFORM){
                WaveformVisuals vis=new WaveformVisuals(getResources().getDisplayMetrics().density);
                vis.setRange(8192);
                vis.drawEvery(16);
                vv.getRenderThread().setRenderer(vis);
            }else if (visSet.getActiveVisualization()==VisualizationSettings.SPECTOGRAPH){
                SpectographVisuals vis=new SpectographVisuals(getResources().getDisplayMetrics().density);
                vis.setFFTSize(2048);
                try {
                    vis.setFrequencyRange(20, 3000);
                } catch (InvalidParameterException e) {
                    ErrorLogger.log(e);
                }
                vis.setScrollPerRedraw(2);
                vis.setScale(SpectographVisuals.LINEAR_SCALE);
                //TODO Connect SpectographVisuals to AudioPlayer so it can receive Sampling Rate Data.
                vv.getRenderThread().setRenderer(vis);
            }else{
                Log.w(LOG_TAG,"WHAT? (NowPlayingFragment)");
            }
        }
    }
}
