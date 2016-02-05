package com.chancorp.audiofornerds.settings;

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
public class VUMeterSettings  implements SeekBar.OnSeekBarChangeListener{
    private static final String LOG_TAG="CS_AFN";
    public int getType(){
        return BaseSetting.VU;
    }
    int range,historySize;
    public void setRange(int range){
        this.range=range;
    }
    public int getRange(){
        return this.range;
    }
    public void setHistorySize(int size){
        this.historySize=size;
    }
    public int getHistorySize(){
        return this.historySize;
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
            historyTV.setText(Integer.toString(historySize));
        }else if (seekBar.getId()==R.id.vis_vu_setting_length_seekbar) {
            setRange(progress*10);
            lengthTV.setText(Integer.toString(range));
        }else{
            Log.w(LOG_TAG, "I think I'm not the only seekbar around here....");
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
