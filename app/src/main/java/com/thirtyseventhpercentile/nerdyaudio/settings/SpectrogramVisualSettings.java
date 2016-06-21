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
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;


public class SpectrogramVisualSettings extends BaseSetting implements AdapterView.OnItemSelectedListener,
        SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
    private static final String LOG_TAG = "CS_AFN";
    private static final String PREF_IDENTIFIER = "com.thirtyseventhpercentile.audiofornerds.settings.SpectrogramVisualSettings";

    int fftSize = 2048;
    float startFreq = 20, endFreq = 1000;
    boolean logScale=false;
    int scrollSpeed=2;
    float contrast=2;

    public boolean getLogScale(){
        return logScale;
    }
    public void setLogScale(boolean log){
        this.logScale=log;
        if (s!=null){
            s.setChecked(log);
        }
    }

    public int getScrollSpeed(){
        return scrollSpeed;
    }
    public void setScrollSpeed(int pixelsPerFrame){
        this.scrollSpeed=pixelsPerFrame;
        if (scrollSpeedSeekbar!=null && scrollSpeedTV!=null){
            scrollSpeedSeekbar.setProgress(this.scrollSpeed);
            scrollSpeedTV.setText(Integer.toString(this.scrollSpeed));
        }
    }

    public float getContrast(){
        return contrast;
    }
    public void setContrast(float contrast){
        this.contrast=contrast;
        if (contrastSeekbar!=null && convtastTV!=null){
            contrastSeekbar.setProgress(Math.round(this.contrast*100));
            convtastTV.setText(Float.toString(this.contrast));
        }
    }

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
            Log2.log(3,this, "SpectrogramVisualSettings>setFftSize(): fftSize NOT in fftSizes[]!!");
        }
    }

    public float getStartFreq() {
        return startFreq;
    }

    public void setStartFreq(float startFreq) {
        //if end and start order is reversed(or is very close), weird shit would happen. So we do this
        if (this.endFreq<startFreq+100){
            //don't do anything
        }else if(startFreq<20){
            this.startFreq = 20;
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
        }else if(endFreq<20){
            this.endFreq = 20;
        }else{
            this.endFreq = endFreq;
        }
        if (endFrqTV!=null && endFrqSeekbar!=null) {
            endFrqTV.setText(Float.toString(this.endFreq));
            endFrqSeekbar.setProgress((int) (this.endFreq / 10));
        }

    }


    public SpectrogramVisualSettings(SidebarSettings sbs, Context c) {
        super(sbs,c);

    }

    private static final String[] fftSizes = {"256", "512", "1024", "2048", "4096", "8192"};
    Spinner fftSizeSpinner;

    SeekBar startFrqSeekbar, endFrqSeekbar, contrastSeekbar, scrollSpeedSeekbar;
    TextView startFrqTV, endFrqTV, convtastTV, scrollSpeedTV;
    Switch s;

    public View getSettingsView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.visuals_setting_spectrogram, container, false);

        fftSizeSpinner = (Spinner) v.findViewById(R.id.vis_spectrogram_setting_fftsize_selector);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(container.getContext(), android.R.layout.simple_spinner_item, fftSizes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fftSizeSpinner.setAdapter(adapter);

        //fftSizeSpinner.setOnItemSelectedListener(this);
        //We do this because otherwise the initialization of the spinner would fire a callback
        //Which would be undesirable.
        fftSizeSpinner.post(new Runnable() {
            public void run() {
                fftSizeSpinner.setOnItemSelectedListener(SpectrogramVisualSettings.this);
            }
        });


        startFrqSeekbar = (SeekBar) v.findViewById(R.id.vis_spectrogram_setting_frq_start_seekbar);
        startFrqTV = (TextView) v.findViewById(R.id.vis_spectrogram_setting_frq_start_value);
        startFrqSeekbar.setOnSeekBarChangeListener(this);

        endFrqSeekbar = (SeekBar) v.findViewById(R.id.vis_spectrogram_setting_frq_end_seekbar);
        endFrqTV = (TextView) v.findViewById(R.id.vis_spectrogram_setting_frq_end_value);
        endFrqSeekbar.setOnSeekBarChangeListener(this);

        contrastSeekbar = (SeekBar) v.findViewById(R.id.vis_spectrogram_setting_contrast_seekbar);
        convtastTV = (TextView) v.findViewById(R.id.vis_spectrogram_setting_contrast_value);
        contrastSeekbar.setOnSeekBarChangeListener(this);

        scrollSpeedSeekbar = (SeekBar) v.findViewById(R.id.vis_spectrogram_setting_scroll_seekbar);
        scrollSpeedTV = (TextView) v.findViewById(R.id.vis_spectrogram_setting_scroll_value);
        scrollSpeedSeekbar.setOnSeekBarChangeListener(this);

        s=(Switch) v.findViewById(R.id.vis_spectrogram_setting_log_switch);
        s.setOnCheckedChangeListener(this);

        load();
        return v;
    }


    @Override
    public int getType() {
        return BaseSetting.SPECTROGRAM;
    }

    @Override
    protected void save() {
        //Log2.log(2,this,"Saving:",fftSize,bars,spacing,startFreq,endFreq);
        SharedPreferences.Editor editor=getSharedPreferences(PREF_IDENTIFIER).edit();
        editor.putInt("fftSize", fftSize);

        editor.putFloat("startFreq", startFreq);
        editor.putFloat("endFreq", endFreq);
        editor.putBoolean("logScale", logScale);
        editor.putFloat("contrast",contrast);
        editor.putInt("scrollSpeed", scrollSpeed);
        editor.apply();
    }

    @Override
    protected void load() {
        SharedPreferences pref=getSharedPreferences(PREF_IDENTIFIER);
        //Log2.log(2, this, "initial", fftSize, bars, spacing, startFreq, endFreq);
        setFftSize(pref.getInt("fftSize", fftSize));
        setStartFreq(pref.getFloat("startFreq", startFreq));
        setEndFreq(pref.getFloat("endFreq", endFreq));
        setLogScale(pref.getBoolean("logScale", logScale));
        setContrast(pref.getFloat("contrast", contrast));
        setScrollSpeed(pref.getInt("scrollSpeed",scrollSpeed));
        //Log2.log(2, this, "end", fftSize, bars, spacing, startFreq, endFreq);
        sbs.notifyUI(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.vis_spectrogram_setting_fftsize_selector) {
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
        if (seekBar.getId() == R.id.vis_spectrogram_setting_frq_start_seekbar) {
            setStartFreq(progress * 10); //0~10000
        }else if (seekBar.getId() == R.id.vis_spectrogram_setting_frq_end_seekbar) {
            setEndFreq(progress * 10); //0~10000
        } else if (seekBar.getId() == R.id.vis_spectrogram_setting_contrast_seekbar) {
            setContrast(progress / 100.0f); //0~10
        }else if (seekBar.getId() == R.id.vis_spectrogram_setting_scroll_seekbar) {
            setScrollSpeed(progress); //0~10
        }else {
            Log2.log(3,this, "I think I'm not the only seekbar around here....");
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
        if (buttonView.getId()==R.id.vis_spectrogram_setting_log_switch) {
            setLogScale(buttonView.isChecked());
        }
        save();
        sbs.notifyUI(this);
    }
}
