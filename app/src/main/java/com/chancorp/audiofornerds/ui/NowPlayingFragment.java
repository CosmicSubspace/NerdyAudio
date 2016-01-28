package com.chancorp.audiofornerds.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

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
public class NowPlayingFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    public static final String LOG_TAG="CS_AFN";
    String[] visualizations=new String[]{"VU Meter","Waveform","Spectrum","Spectograph(Linear)","Spectograph(Exponential)"};
    VisualizationView vv;
    Button visSettingsBtn;
    Spinner visSpinner;
    FrameLayout settings_container;
    boolean settingsMode=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO Setting UI
        View v=inflater.inflate(R.layout.tab_frag_nowplaying, container, false);


        vv=(VisualizationView)v.findViewById(R.id.visualization);
        if (vv == null) Log.e(LOG_TAG, "VisualizationView is null!");
        else if (vv.getRenderThread() == null) Log.e(LOG_TAG, "Renderer is null!");
        else{
            vv.getRenderThread().setMaxFPS(60);
        }

        settings_container=(FrameLayout) v.findViewById(R.id.visuals_settings_container);

        visSpinner=(Spinner) v.findViewById(R.id.visuals_selector);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, visualizations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visSpinner.setAdapter(adapter);
        visSpinner.setOnItemSelectedListener(this);

        visSettingsBtn=(Button)v.findViewById(R.id.visuals_settings_button);
        visSettingsBtn.setOnClickListener(this);

        return v;


    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.visuals_settings_button){
            if (!settingsMode) {
                settings_container.addView(vv.getRenderThread().getRenderer().getSettingsView(getLayoutInflater(null), settings_container, null));
                settings_container.setBackgroundColor(Color.argb(200,255,255,255));
                visSettingsBtn.setBackgroundResource(R.drawable.ic_close_black_36dp);
                settingsMode=true;
            }else{
                settings_container.removeAllViews();
                visSettingsBtn.setBackgroundResource(R.drawable.ic_settings_black_36dp);
                settings_container.setBackgroundColor(Color.argb(0, 255, 255, 255));
                settingsMode=false;
            }
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Log.i(LOG_TAG,"Adapter > "+pos);
        if (pos==0) {
            VUMeterVisuals vis=new VUMeterVisuals(getResources().getDisplayMetrics().density);
            vis.setRange(4096);
            vv.getRenderThread().setRenderer(vis);

        }else if (pos==2){
            SpectrumVisuals vis=new SpectrumVisuals(getResources().getDisplayMetrics().density);
            vis.setFFTSize(2048);
            vis.setSpacing(0.3f);
            vv.getRenderThread().setRenderer(vis);
        }else if (pos==1){
            WaveformVisuals vis=new WaveformVisuals(getResources().getDisplayMetrics().density);
            vis.setRange(8192);
            vis.drawEvery(16);
            vv.getRenderThread().setRenderer(vis);
        }else if (pos==4){
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

        }else if (pos==3){
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

    public void onNothingSelected(AdapterView<?> parent) {
        Log.i(LOG_TAG,"Adapter > Nothing selected.");
    }
}
