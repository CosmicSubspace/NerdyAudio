package com.chancorp.audiofornerds.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.exceptions.InvalidParameterException;
import com.chancorp.audiofornerds.helper.ErrorLogger;
import com.chancorp.audiofornerds.visuals.SpectographVisuals;
import com.chancorp.audiofornerds.visuals.SpectrumVisuals;
import com.chancorp.audiofornerds.visuals.VUMeterVisuals;
import com.chancorp.audiofornerds.visuals.VisualizationView;
import com.chancorp.audiofornerds.visuals.WaveformVisuals;

/**
 * Created by Chan on 2015-12-16.
 */
public class NowPlayingFragment extends Fragment implements View.OnClickListener{
    public static final String LOG_TAG="CS_AFN";
    VisualizationView vv;
    Button vuBt,specBtn, wavBtn, specLinBtn,specExpBtn, visSettingsBtn;
    FrameLayout settings_container;
    boolean settingsMode=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO Setting UI
        View v=inflater.inflate(R.layout.tab_frag_nowplaying, container, false);



        vuBt=(Button)v.findViewById(R.id.vis_vu);
        vuBt.setOnClickListener(this);

        specBtn=(Button)v.findViewById(R.id.vis_spec);
        specBtn.setOnClickListener(this);

        wavBtn =(Button)v.findViewById(R.id.vis_wavef);
        wavBtn.setOnClickListener(this);

        specLinBtn =(Button)v.findViewById(R.id.vis_specto_lin);
        specLinBtn.setOnClickListener(this);

        specExpBtn =(Button)v.findViewById(R.id.vis_specto_exp);
        specExpBtn.setOnClickListener(this);

        visSettingsBtn=(Button) v.findViewById(R.id.visuals_settings_button);
        visSettingsBtn.setOnClickListener(this);

        vv=(VisualizationView)v.findViewById(R.id.visualization);
        if (vv == null) Log.e(LOG_TAG, "VisualizationView is null!");
        else if (vv.getRenderThread() == null) Log.e(LOG_TAG, "Renderer is null!");
        else{
            vv.getRenderThread().setMaxFPS(60);
        }

        settings_container=(FrameLayout) v.findViewById(R.id.visuals_settings_container);


        return v;


    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.vis_vu) {
            VUMeterVisuals vis=new VUMeterVisuals(getResources().getDisplayMetrics().density);
            vis.setRange(4096);
            vv.getRenderThread().setRenderer(vis);

        }else if (view.getId()==R.id.vis_spec){
            SpectrumVisuals vis=new SpectrumVisuals(getResources().getDisplayMetrics().density);
            vis.setFFTSize(2048);
            vis.setSpacing(0.3f);
            vv.getRenderThread().setRenderer(vis);
        }else if (view.getId()==R.id.vis_wavef){
            WaveformVisuals vis=new WaveformVisuals(getResources().getDisplayMetrics().density);
            vis.setRange(8192);
            vis.drawEvery(16);
            vv.getRenderThread().setRenderer(vis);
        }else if (view.getId()==R.id.vis_specto_exp){
            SpectographVisuals vis=new SpectographVisuals(getResources().getDisplayMetrics().density);
            vis.setFFTSize(2048);
            try {
                vis.setFrequencyRange(20, 10000);
            } catch (InvalidParameterException e) {
                ErrorLogger.log(e);
            }
            vis.setScrollPerRedraw(2);
            vis.setScale(SpectographVisuals.LOG_SCALE);
            //TODO Connect SpectographVisuals to AudioPlayer so it can receive Sampling Rate Data.
            vv.getRenderThread().setRenderer(vis);

        }else if (view.getId()==R.id.vis_specto_lin){
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

        }else if (view.getId()==R.id.visuals_settings_button){
            if (!settingsMode) {
                settings_container.addView(vv.getRenderThread().getRenderer().getSettingsView(getLayoutInflater(null), settings_container, null));
                visSettingsBtn.setBackgroundResource(R.drawable.ic_close_black_36dp);
                settingsMode=true;
            }else{
                settings_container.removeAllViews();
                visSettingsBtn.setBackgroundResource(R.drawable.ic_settings_black_36dp);
                settingsMode=false;
            }
        }
    }
}
