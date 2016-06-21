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
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.SettingsUpdateListener;
import com.thirtyseventhpercentile.nerdyaudio.settings.BaseSetting;
import com.thirtyseventhpercentile.nerdyaudio.settings.SidebarSettings;
import com.thirtyseventhpercentile.nerdyaudio.settings.VisualizationSettings;
import com.thirtyseventhpercentile.nerdyaudio.visuals.AlbumArtVisuals;
import com.thirtyseventhpercentile.nerdyaudio.visuals.BallsVisuals;
import com.thirtyseventhpercentile.nerdyaudio.visuals.CircleVisuals;
import com.thirtyseventhpercentile.nerdyaudio.visuals.PlayControlsView;
import com.thirtyseventhpercentile.nerdyaudio.visuals.SpectrogramVisuals;
import com.thirtyseventhpercentile.nerdyaudio.visuals.SpectrumVisuals;
import com.thirtyseventhpercentile.nerdyaudio.visuals.VUMeterVisuals;
import com.thirtyseventhpercentile.nerdyaudio.visuals.VisualizationView;
import com.thirtyseventhpercentile.nerdyaudio.visuals.WaveformVisuals;


public class NowPlayingFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, SettingsUpdateListener {
    public static final String LOG_TAG = "CS_AFN";

    VisualizationView vv;

    SidebarSettings sbs;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sbs = SidebarSettings.getInstance();
        sbs.addSettingsUpdateListener(this);

        View v = inflater.inflate(R.layout.tab_frag_nowplaying, container, false);


        vv = (VisualizationView) v.findViewById(R.id.visualization);
        if (vv == null) Log2.log(4,this, "VisualizationView is null!");
        else if (vv.getRenderThread() == null) Log2.log(4,this, "Renderer is null!");
        else {
            //vv.getRenderThread().setMaxFPS(60);
        }

        updated(sbs.getSetting(BaseSetting.VISUALIZATION));


        v.post(new Runnable() {
            @Override
            public void run() {
                if (PlayControlsView.getInstance()!=null) PlayControlsView.getInstance().expand(true);
            }
        });

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
        Log2.log(2,this, "Adapter > Nothing selected.");
    }

    @Override
    public void updated(BaseSetting setting) {
        if (setting.getType() == BaseSetting.VISUALIZATION) {
            VisualizationSettings visSet = (VisualizationSettings) setting;
            if (visSet.getActiveVisualization() == VisualizationSettings.VU) {
                VUMeterVisuals vis = new VUMeterVisuals(getResources().getDisplayMetrics().density);

                vv.getRenderThread().setRenderer(vis);
            } else if (visSet.getActiveVisualization() == VisualizationSettings.SPECTRUM) {
                SpectrumVisuals vis = new SpectrumVisuals(getResources().getDisplayMetrics().density);

                vv.getRenderThread().setRenderer(vis);
            } else if (visSet.getActiveVisualization() == VisualizationSettings.WAVEFORM) {
                WaveformVisuals vis = new WaveformVisuals(getResources().getDisplayMetrics().density);
                vv.getRenderThread().setRenderer(vis);
            } else if (visSet.getActiveVisualization() == VisualizationSettings.SPECTROGRAM) {
                SpectrogramVisuals vis = new SpectrogramVisuals(getResources().getDisplayMetrics().density);

                vv.getRenderThread().setRenderer(vis);
            } else if (visSet.getActiveVisualization() == VisualizationSettings.CIRCLE) {
                CircleVisuals vis = new CircleVisuals(getResources().getDisplayMetrics().density);

                vv.getRenderThread().setRenderer(vis);
            } else if (visSet.getActiveVisualization() == VisualizationSettings.ALBUM_ART) {
                AlbumArtVisuals vis = new AlbumArtVisuals(getResources().getDisplayMetrics().density);
                vv.getRenderThread().setRenderer(vis);
            } else if (visSet.getActiveVisualization() == VisualizationSettings.BALLS) {
                BallsVisuals vis = new BallsVisuals(getResources().getDisplayMetrics().density);
                vv.getRenderThread().setRenderer(vis);
            } else {
                Log2.log(3,this, "WHAT? (NowPlayingFragment)");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        sbs.removeSettingsUpdateListener(this);
    }
}
