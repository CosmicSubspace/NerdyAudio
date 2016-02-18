package com.chancorp.audiofornerds.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.helper.Log2;

/**
 * Created by Chan on 2/3/2016.
 */
public class SpectrumVisualSettings extends BaseSetting implements AdapterView.OnItemSelectedListener,
        SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
    private static final String LOG_TAG = "CS_AFN";
    private static final String PREF_IDENTIFIER = "com.chancorp.audiofornerds.settings.SpectrumVisualSettings";


    int fftSize = 2048;
    int bars = 100;
    float spacing = 0.0f;
    float startFreq = 20, endFreq = 1000;
    boolean logScale=false;
    float barHeight=1.0f;

    public int getFftSize() {
        return fftSize;
    }

    public void setFftSize(int fftSize) {
        Log2.log(2,this,"setFFTSize() Called",fftSize);
        this.fftSize = fftSize;

        if (fftSizeSpinner!=null) {
            for (int i = 0; i < fftSizes.length; i++) {
                if (Integer.parseInt(fftSizes[i]) == fftSize) {
                    fftSizeSpinner.setSelection(i, false);
                    return;
                }
            }
            Log.w(LOG_TAG, "SpectrumVisualSettings>setFftSize(): fftSize NOT in fftSizes[]!!");
        }

    }

    public float getBarHeight() {
        return barHeight;
    }

    public void setBarHeight(float height) {
        this.barHeight = height;

        if (barHeightTV!=null && barHeightSeekbar!=null) {
            barHeightTV.setText(Float.toString(barHeight));
            barHeightSeekbar.setProgress(Math.round(barHeight * 200));
        }
    }

    public boolean getLogScale() {
        return logScale;
    }

    public void setLogScale(boolean logScale) {
        this.logScale = logScale;

        if (logScaleSwitch!=null) {
            logScaleSwitch.setChecked(logScale);
        }
    }

    public int getBars() {
        return bars;
    }

    public void setBars(int bars) {
        this.bars = bars;

        if (barsTV!=null) {
            barsTV.setText(Integer.toString(bars));
            barsSeekbar.setProgress(bars);
        }
    }

    public float getSpacing() {
        return spacing;
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;

        if (spacingTV!=null) {
            spacingTV.setText(Float.toString(spacing));
            spacingSeekbar.setProgress(Math.round(spacing*100));
        }

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
        if (startFrqTV!=null && startFrqSeekbar!=null) {
            startFrqTV.setText(Float.toString(this.startFreq));
            startFrqSeekbar.setProgress((int) (this.startFreq / 10));
        }

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
        if (endFrqTV!=null && endFrqSeekbar!=null) {
            endFrqTV.setText(Float.toString(this.endFreq));
            endFrqSeekbar.setProgress((int) (this.endFreq / 10));
        }

    }


    public SpectrumVisualSettings(SidebarSettings sbs, Context c) {
        super(sbs,c);

    }

    private static final String[] fftSizes = {"256", "512", "1024", "2048", "4096", "8192"};
    Spinner fftSizeSpinner;

    SeekBar barsSeekbar, spacingSeekbar, startFrqSeekbar, endFrqSeekbar, barHeightSeekbar;
    TextView barsTV, spacingTV, startFrqTV, endFrqTV, barHeightTV;
    Switch logScaleSwitch;

    public View getSettingsView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.visuals_setting_spectrum, container, false);

        fftSizeSpinner = (Spinner) v.findViewById(R.id.vis_spectrum_setting_fftsize_selector);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(container.getContext(), android.R.layout.simple_spinner_item, fftSizes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fftSizeSpinner.setAdapter(adapter);

        //fftSizeSpinner.setOnItemSelectedListener(this);
        //We do this because otherwise the initialization of the spinner would fire a callback
        //Which would be undesirable.
        fftSizeSpinner.post(new Runnable() {
            public void run() {
                fftSizeSpinner.setOnItemSelectedListener(SpectrumVisualSettings.this);
            }
        });

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

        barHeightSeekbar = (SeekBar) v.findViewById(R.id.vis_spectrum_setting_height_seekbar);
        barHeightTV = (TextView) v.findViewById(R.id.vis_spectrum_setting_height_value);
        barHeightSeekbar.setOnSeekBarChangeListener(this);

        logScaleSwitch=(Switch) v.findViewById(R.id.vis_spectrum_setting_log_switch);
        logScaleSwitch.setOnCheckedChangeListener(this);

        load();
        return v;
    }


    @Override
    public int getType() {
        return BaseSetting.SPECTRUM;
    }

    @Override
    protected void save() {
        //Log2.log(2,this,"Saving:",fftSize,bars,spacing,startFreq,endFreq);
        SharedPreferences.Editor editor=getSharedPreferences(PREF_IDENTIFIER).edit();
        editor.putInt("fftSize",fftSize);
        editor.putInt("bars", bars);
        editor.putFloat("spacing", spacing);
        editor.putFloat("startFreq", startFreq);
        editor.putFloat("endFreq", endFreq);
        editor.putFloat("barHeight", barHeight);
        editor.putBoolean("logScale", logScale);
        editor.apply();
    }

    @Override
    protected void load() {
        SharedPreferences pref=getSharedPreferences(PREF_IDENTIFIER);
        //Log2.log(2, this, "initial", fftSize, bars, spacing, startFreq, endFreq);
        setFftSize(pref.getInt("fftSize", fftSize));
        setBars(pref.getInt("bars", bars));
        setSpacing(pref.getFloat("spacing", spacing));
        setStartFreq(pref.getFloat("startFreq", startFreq));
        setEndFreq(pref.getFloat("endFreq", endFreq));
        setBarHeight(pref.getFloat("barHeight", barHeight)); //TODO there's a bug where some of the settings do not load properly when app is restarted.
        setLogScale(pref.getBoolean("logScale", logScale));
        //Log2.log(2, this, "end", fftSize, bars, spacing, startFreq, endFreq);
        sbs.notifyUI(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.vis_spectrum_setting_fftsize_selector) {
            setFftSize(Integer.parseInt(fftSizes[position]));
        }
        save();
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
        }else if (seekBar.getId() == R.id.vis_spectrum_setting_height_seekbar) {
            setBarHeight(progress / 200.f); //0~5
        } else {
            Log.w(LOG_TAG, "I think I'm not the only seekbar around here....");
        }
        if (fromUser) {
            save();
            sbs.notifyUI(this);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId()==R.id.vis_spectrum_setting_log_switch) {
            setLogScale(buttonView.isChecked());
        }
        save();
        sbs.notifyUI(this);
    }
}
