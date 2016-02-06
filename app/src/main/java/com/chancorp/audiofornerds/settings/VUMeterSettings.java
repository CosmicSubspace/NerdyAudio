package com.chancorp.audiofornerds.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chancorp.audiofornerds.R;

/**
 * Created by Chan on 2/3/2016.
 */
public class VUMeterSettings extends BaseSetting implements SeekBar.OnSeekBarChangeListener{
    private static final String LOG_TAG="CS_AFN";
    private static final String PREF_IDENTIFIER = "com.chancorp.audiofornerds.settings.VUMeterSettings";

    public int getType(){
        return BaseSetting.VU;
    }

    @Override
    protected void save() {
        SharedPreferences.Editor editor=getSharedPreferences(PREF_IDENTIFIER).edit();
        editor.putInt("range", range);
        editor.putInt("historySize", historySize);
        editor.apply();
    }

    @Override
    protected void load() {
        SharedPreferences pref=getSharedPreferences(PREF_IDENTIFIER);
        setRange(pref.getInt("range", range));
        setHistorySize(pref.getInt("historySize", historySize));
    }

    int range=4096,historySize=64;
    public void setRange(int range){
        this.range=range;
        if (lengthTV!=null && sb_len!=null) {
            lengthTV.setText(Integer.toString(range));
            sb_len.setProgress(range / 10);
        }

        save();
    }
    public int getRange(){
        return this.range;
    }
    public void setHistorySize(int size){
        this.historySize=size;
        if (historyTV!=null && sb_hist!=null) {
            historyTV.setText(Integer.toString(historySize));
            sb_hist.setProgress(size);
        }

        save();
    }
    public int getHistorySize(){
        return this.historySize;
    }

    public VUMeterSettings(SidebarSettings sbs, Context c){
        super(sbs,c);

        load();
        sbs.notifyUI(this);
    }



    SeekBar sb_hist,sb_len;
    TextView lengthTV,historyTV;
    public View getSettingsView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.visuals_setting_vu, container, false);
        sb_hist=(SeekBar)v.findViewById(R.id.vis_vu_setting_history_seekbar);
        historyTV=(TextView)v.findViewById(R.id.vis_vu_setting_history_value);
        sb_hist.setOnSeekBarChangeListener(this);

        sb_len=(SeekBar)v.findViewById(R.id.vis_vu_setting_length_seekbar);
        lengthTV=(TextView)v.findViewById(R.id.vis_vu_setting_length_value);
        sb_len.setOnSeekBarChangeListener(this);
        return v;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId()==R.id.vis_vu_setting_history_seekbar) {
            setHistorySize(progress);


        }else if (seekBar.getId()==R.id.vis_vu_setting_length_seekbar) {
            setRange(progress * 10);

        }else{
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
