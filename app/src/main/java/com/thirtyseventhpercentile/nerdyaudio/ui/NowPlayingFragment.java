//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.SettingsUpdateListener;
import com.thirtyseventhpercentile.nerdyaudio.settings.BaseSetting;
import com.thirtyseventhpercentile.nerdyaudio.settings.SidebarSettings;
import com.thirtyseventhpercentile.nerdyaudio.settings.VisualizationSettings;
import com.thirtyseventhpercentile.nerdyaudio.visuals.SpectrogramVisuals;
import com.thirtyseventhpercentile.nerdyaudio.visuals.SpectrumVisuals;
import com.thirtyseventhpercentile.nerdyaudio.visuals.VUMeterVisuals;
import com.thirtyseventhpercentile.nerdyaudio.visuals.VisualizationView;
import com.thirtyseventhpercentile.nerdyaudio.visuals.WaveformVisuals;


public class NowPlayingFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener,SettingsUpdateListener{
    public static final String LOG_TAG="CS_AFN";

    VisualizationView vv;

    SidebarSettings sbs;


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

        updated(sbs.getSetting(BaseSetting.VISUALIZATION));
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

                vv.getRenderThread().setRenderer(vis);
            }else if (visSet.getActiveVisualization()==VisualizationSettings.SPECTRUM){
                SpectrumVisuals vis=new SpectrumVisuals(getResources().getDisplayMetrics().density);

                vv.getRenderThread().setRenderer(vis);
            }else if (visSet.getActiveVisualization()==VisualizationSettings.WAVEFORM){
                WaveformVisuals vis=new WaveformVisuals(getResources().getDisplayMetrics().density);
                vv.getRenderThread().setRenderer(vis);
            }else if (visSet.getActiveVisualization()==VisualizationSettings.SPECTROGRAM){
                SpectrogramVisuals vis=new SpectrogramVisuals(getResources().getDisplayMetrics().density);

                vv.getRenderThread().setRenderer(vis);
            }else{
                Log.w(LOG_TAG,"WHAT? (NowPlayingFragment)");
            }
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        sbs.removeSettingsUpdateListener(this);
    }
}
