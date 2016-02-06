package com.chancorp.audiofornerds.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.chancorp.audiofornerds.R;

/**
 * Created by Chan on 2/3/2016.
 */
public class SpectrumVisualSettings extends BaseSetting implements AdapterView.OnItemSelectedListener,
        SeekBar.OnSeekBarChangeListener {
    private static final String LOG_TAG = "CS_AFN";

    int fftSize = 2048;
    int bars = 100;
    float spacing = 0.0f;
    float startFreq = 20, endFreq = 1000;

    public int getFftSize() {
        return fftSize;
    }

    public void setFftSize(int fftSize) {
        Log.i(LOG_TAG,"SetFFTSize() - Inf.Loop?");
        this.fftSize = fftSize;
        for (int i=0;i<fftSizes.length;i++) {
            if (Integer.parseInt(fftSizes[i])==fftSize)
            fftSizeSpinner.setSelection(i);
            return;
        }
        Log.w(LOG_TAG,"SpectrumVisualSettings>setFftSize(): fftSize NOT in fftSizes[]!!");
    }

    public int getBars() {
        return bars;
    }

    public void setBars(int bars) {
        this.bars = bars;
        barsTV.setText(Integer.toString(bars));
    }

    public float getSpacing() {
        return spacing;
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
        spacingTV.setText(Float.toString(spacing));
    }

    public float getStartFreq() {
        return startFreq;
    }

    public void setStartFreq(float startFreq) {
        //if end and start order is reversed(or is very close), weird shit would happen. So we do this
        if (this.endFreq<startFreq+100){
            //don't do anything
        }else{
            this.startFreq = startFreq;
        }
        startFrqTV.setText(Float.toString(this.startFreq));
        startFrqSeekbar.setProgress((int)(this.startFreq/10));
    }

    public float getEndFreq() {
        return endFreq;
    }

    public void setEndFreq(float endFreq) {

        //if end and start order is reversed(or is very close), weird shit would happen. So we do this
        if (endFreq<this.startFreq+100){
            //don't do anything
        }else{
            this.endFreq = endFreq;
        }
        endFrqTV.setText(Float.toString(this.endFreq));
        endFrqSeekbar.setProgress((int)(this.endFreq/10));

    }


    SidebarSettings sbs;

    public SpectrumVisualSettings(SidebarSettings sbs) {
        this.sbs = sbs;
    }

    private static final String[] fftSizes = {"256", "512", "1024", "2048", "4096", "8192"};
    Spinner fftSizeSpinner;

    SeekBar barsSeekbar, spacingSeekbar, startFrqSeekbar, endFrqSeekbar;
    TextView barsTV, spacingTV, startFrqTV, endFrqTV;

    public View getSettingsView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.visuals_setting_spectrum, container, false);

        fftSizeSpinner = (Spinner) v.findViewById(R.id.vis_spectrum_setting_fftsize_selector);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(container.getContext(), android.R.layout.simple_spinner_item, fftSizes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fftSizeSpinner.setAdapter(adapter);
        fftSizeSpinner.setOnItemSelectedListener(this);

        barsSeekbar = (SeekBar) v.findViewById(R.id.vis_spectrum_setting_bars_seekbar);
        barsTV = (TextView) v.findViewById(R.id.vis_spectrum_setting_bars_value);
        barsSeekbar.setOnSeekBarChangeListener(this);

        spacingSeekbar = (SeekBar) v.findViewById(R.id.vis_spectrum_setting_spacing_seekbar);
        spacingTV = (TextView) v.findViewById(R.id.vis_spectrum_setting_spacing_value);
        spacingSeekbar.setOnSeekBarChangeListener(this);

        startFrqSeekbar = (SeekBar) v.findViewById(R.id.vis_spectrum_setting_frq_start_seekbar);
        startFrqTV = (TextView) v.findViewById(R.id.vis_spectrum_setting_frq_start_value);
        startFrqSeekbar.setOnSeekBarChangeListener(this);

        endFrqSeekbar = (SeekBar) v.findViewById(R.id.vis_spectrum_setting_frq_end_seekbar);
        endFrqTV = (TextView) v.findViewById(R.id.vis_spectrum_setting_frq_end_value);
        endFrqSeekbar.setOnSeekBarChangeListener(this);

        return v;
    }


    @Override
    public int getType() {
        return BaseSetting.SPECTRUM;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.vis_spectrum_setting_fftsize_selector) {
            setFftSize(Integer.parseInt(fftSizes[position]));
        }
        sbs.notifyUI(this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.vis_spectrum_setting_bars_seekbar) {
            setBars(progress);

        }else if (seekBar.getId() == R.id.vis_spectrum_setting_spacing_seekbar) {
            setSpacing(progress / 100.0f);

        }else if (seekBar.getId() == R.id.vis_spectrum_setting_frq_start_seekbar) {
            setStartFreq(progress*10); //0~10000
        }else if (seekBar.getId() == R.id.vis_spectrum_setting_frq_end_seekbar) {
            setEndFreq(progress * 10); //0~10000
        } else {
            Log.w(LOG_TAG, "I think I'm not the only seekbar around here....");
        }

        sbs.notifyUI(this);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
