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
        this.fftSize = fftSize;
    }

    public int getBars() {
        return bars;
    }

    public void setBars(int bars) {
        this.bars = bars;
    }

    public float getSpacing() {
        return spacing;
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
    }

    public float getStartFreq() {
        return startFreq;
    }

    public void setStartFreq(float startFreq) {
        this.startFreq = startFreq;
    }

    public float getEndFreq() {
        return endFreq;
    }

    public void setEndFreq(float endFreq) {
        this.endFreq = endFreq;
    }


    SidebarSettings sbs;

    public SpectrumVisualSettings(SidebarSettings sbs) {
        this.sbs = sbs;
    }

    private static final String[] fftSizes = {"256", "512", "1024", "2048", "4096", "8192"};
    Spinner fftSizeSpinner;

    SeekBar barsSeekbar, spacingSeekbar;
    TextView barsTV, spacingTV;

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

        return v;
    }


    @Override
    public int getType() {
        return BaseSetting.SPECTRUM;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (view.getId() == R.id.vis_spectrum_setting_fftsize_selector) {
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
            barsTV.setText(Integer.toString(bars));
        }else if (seekBar.getId() == R.id.vis_spectrum_setting_spacing_seekbar) {
            setSpacing(progress/100.0f);
            spacingTV.setText(Float.toString(spacing));
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
