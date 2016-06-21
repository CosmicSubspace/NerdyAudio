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
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;


public class WaveformVisualSettings extends BaseSetting implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener{
    private static final String LOG_TAG="CS_AFN";
    private static final String PREF_IDENTIFIER = "com.thirtyseventhpercentile.audiofornerds.settings.WaveformVisualSettings";

    int range=8192;
    boolean downmix=true;

    public void setRange(int range){
        this.range=range;
        if (lengthTV!=null && sb!=null) {
            lengthTV.setText(Integer.toString(range));
            sb.setProgress(range / 20);
        }

    }
    public int getRange(){
        return this.range;
    }
    public void setDownmix(boolean downmix){
        this.downmix=downmix;
        if (s!=null) {
            s.setChecked(downmix);
        }

    }
    public boolean getDownmix(){
        return this.downmix;
    }


    public WaveformVisualSettings(SidebarSettings sbs, Context c){
        super(sbs,c);

        load();
    }


    Switch s;
    SeekBar sb;
    TextView lengthTV;
    public View getSettingsView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.visuals_setting_waveform, container, false);
        sb=(SeekBar)v.findViewById(R.id.vis_waveform_setting_length_seekbar);
        s=(Switch) v.findViewById(R.id.vis_waveform_setting_stereo_switch);
        lengthTV=(TextView)v.findViewById(R.id.vis_waveform_setting_length_value);
        sb.setOnSeekBarChangeListener(this);
        s.setOnCheckedChangeListener(this);
        load();
        return v;

    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId()==R.id.vis_waveform_setting_length_seekbar) {
            setRange(progress * 20);

        }else{
            Log2.log(3,this, "I think I'm not the only seekbar around here....");
        }
        if (fromUser){
        save();
        sbs.notifyUI(this);}
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId()==R.id.vis_waveform_setting_stereo_switch){
            setDownmix(buttonView.isChecked());
        }
        save();
        sbs.notifyUI(this);
    }

    @Override
    public int getType() {
        return BaseSetting.WAVEFORM;
    }

    @Override
    protected void save() {
        SharedPreferences.Editor editor=getSharedPreferences(PREF_IDENTIFIER).edit();
        editor.putInt("range", range);
        editor.putBoolean("downmix", downmix);
        editor.apply();
    }

    @Override
    protected void load() {
        SharedPreferences pref=getSharedPreferences(PREF_IDENTIFIER);
        setDownmix(pref.getBoolean("downmix",downmix));
        setRange(pref.getInt("range",range));
        sbs.notifyUI(this);
    }
}
