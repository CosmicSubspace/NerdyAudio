//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.settings;

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

import com.thirtyseventhpercentile.nerdyaudio.R;


public class CircleVisualSettings extends BaseSetting implements AdapterView.OnItemSelectedListener,
        SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
    private static final String LOG_TAG = "CS_AFN";
    private static final String PREF_IDENTIFIER = "com.thirtyseventhpercentile.audiofornerds.settings.CircleVisualSettings";


    int fftSize = 2048;
    int bars = 100;
    float startFreq = 20, endFreq = 1000;
    boolean logScale=false;
    float sensitivity=1.0f,bassSensitivity=1.0f;

    public int getFftSize() {
        return fftSize;
    }

    public void setFftSize(int fftSize) {
        //Log2.log(2,this,"setFFTSize() Called",fftSize);
        this.fftSize = fftSize;

        if (fftSizeSpinner!=null) {
            for (int i = 0; i < fftSizes.length; i++) {
                if (Integer.parseInt(fftSizes[i]) == fftSize) {
                    fftSizeSpinner.setSelection(i, false);
                    return;
                }
            }
            Log.w(LOG_TAG, "circleVisualSettings>setFftSize(): fftSize NOT in fftSizes[]!!");
        }

    }

    public float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;

        if (sensitivityTV!=null && sensitivitySeekbar!=null) {
            sensitivityTV.setText(Float.toString(sensitivity));
            sensitivitySeekbar.setProgress(Math.round(sensitivity * 1000));
        }
    }

    public float getBassSensitivity() {
        return bassSensitivity;
    }

    public void setBassSensitivity(float bassSensitivity) {
        this.bassSensitivity = bassSensitivity;

        if (bassSensitivityTV!=null && bassSensitivitySeekbar!=null) {
            bassSensitivityTV.setText(Float.toString(bassSensitivity));
            bassSensitivitySeekbar.setProgress(Math.round(bassSensitivity * 1000));
        }
    }

    public int getBars() {
        return bars;
    }

    public void setBars(int bars) {
        this.bars = bars;

        if (barsSeekbar!=null && barsTV!=null) {
            barsTV.setText(Integer.toString(bars));
            barsSeekbar.setProgress(bars);
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


    public CircleVisualSettings(SidebarSettings sbs, Context c) {
        super(sbs,c);
    }

    private static final String[] fftSizes = {"256", "512", "1024", "2048", "4096", "8192"};
    Spinner fftSizeSpinner;

    SeekBar sensitivitySeekbar, bassSensitivitySeekbar, startFrqSeekbar, endFrqSeekbar, barsSeekbar;
    TextView sensitivityTV, bassSensitivityTV, startFrqTV, endFrqTV, barsTV;
    Switch logScaleSwitch;

    public View getSettingsView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.visuals_setting_circle, container, false);

        fftSizeSpinner = (Spinner) v.findViewById(R.id.vis_circle_setting_fftsize_selector);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(container.getContext(), android.R.layout.simple_spinner_item, fftSizes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fftSizeSpinner.setAdapter(adapter);

        //fftSizeSpinner.setOnItemSelectedListener(this);
        //We do this because otherwise the initialization of the spinner would fire a callback
        //Which would be undesirable.
        fftSizeSpinner.post(new Runnable() {
            public void run() {
                fftSizeSpinner.setOnItemSelectedListener(CircleVisualSettings.this);
            }
        });

        sensitivitySeekbar = (SeekBar) v.findViewById(R.id.vis_circle_setting_sensitivity_seekbar);
        sensitivityTV = (TextView) v.findViewById(R.id.vis_circle_setting_sensitivity_value);
        sensitivitySeekbar.setOnSeekBarChangeListener(this);

        bassSensitivitySeekbar = (SeekBar) v.findViewById(R.id.vis_circle_setting_bass_sensitivity_seekbar);
        bassSensitivityTV = (TextView) v.findViewById(R.id.vis_circle_setting_bass_sensitivity_value);
        bassSensitivitySeekbar.setOnSeekBarChangeListener(this);


        startFrqSeekbar = (SeekBar) v.findViewById(R.id.vis_circle_setting_frq_start_seekbar);
        startFrqTV = (TextView) v.findViewById(R.id.vis_circle_setting_frq_start_value);
        startFrqSeekbar.setOnSeekBarChangeListener(this);

        endFrqSeekbar = (SeekBar) v.findViewById(R.id.vis_circle_setting_frq_end_seekbar);
        endFrqTV = (TextView) v.findViewById(R.id.vis_circle_setting_frq_end_value);
        endFrqSeekbar.setOnSeekBarChangeListener(this);

        barsSeekbar = (SeekBar) v.findViewById(R.id.vis_circle_setting_bars_seekbar);
        barsTV = (TextView) v.findViewById(R.id.vis_circle_setting_bars_value);
        barsSeekbar.setOnSeekBarChangeListener(this);

        logScaleSwitch=(Switch) v.findViewById(R.id.vis_circle_setting_log_switch);
        logScaleSwitch.setOnCheckedChangeListener(this);

        load();
        return v;
    }


    @Override
    public int getType() {
        return BaseSetting.CIRCLE;
    }

    @Override
    protected void save() {
        //Log2.log(2,this,"Saving:",fftSize,bars,spacing,startFreq,endFreq);
        SharedPreferences.Editor editor=getSharedPreferences(PREF_IDENTIFIER).edit();
        editor.putInt("fftSize",fftSize);
        editor.putInt("bars", bars);
        editor.putFloat("startFreq", startFreq);
        editor.putFloat("endFreq", endFreq);
        editor.putFloat("sensitivity", sensitivity);
        editor.putFloat("bassSensitivity", bassSensitivity);
        editor.putBoolean("logScale", logScale);
        editor.apply();
    }

    @Override
    protected void load() {
        SharedPreferences pref=getSharedPreferences(PREF_IDENTIFIER);
        //Log2.log(2, this, "initial", fftSize, bars, spacing, startFreq, endFreq);
        setFftSize(pref.getInt("fftSize", fftSize));
        setBars(pref.getInt("bars", bars));
        setStartFreq(pref.getFloat("startFreq", startFreq));
        setEndFreq(pref.getFloat("endFreq", endFreq));
        setSensitivity(pref.getFloat("sensitivity", sensitivity)); //TODO there's a bug where some of the settings do not load properly when app is restarted.
        setBassSensitivity(pref.getFloat("bassSensitivity", bassSensitivity));
        setLogScale(pref.getBoolean("logScale", logScale));
        //Log2.log(2, this, "end", fftSize, bars, spacing, startFreq, endFreq);
        sbs.notifyUI(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.vis_circle_setting_fftsize_selector) {
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
        if (seekBar.getId() == R.id.vis_circle_setting_bars_seekbar) {
            setBars(progress);
        }else if (seekBar.getId() == R.id.vis_circle_setting_frq_start_seekbar) {
            setStartFreq(progress*10); //0~10000
        }else if (seekBar.getId() == R.id.vis_circle_setting_frq_end_seekbar) {
            setEndFreq(progress * 10); //0~10000
        }else if (seekBar.getId() == R.id.vis_circle_setting_sensitivity_seekbar) {
            setSensitivity(progress / 1000.f); //0~5
        } else if (seekBar.getId() == R.id.vis_circle_setting_bass_sensitivity_seekbar) {
            setBassSensitivity(progress / 1000.f); //0~5
        }else {
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
        if (buttonView.getId()==R.id.vis_circle_setting_log_switch) {
            setLogScale(buttonView.isChecked());
        }
        save();
        sbs.notifyUI(this);
    }
}
