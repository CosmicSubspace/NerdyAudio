package com.chancorp.audiofornerds.filters;

import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.audio.Waveform;

/**
 * Created by Chan on 1/28/2016.
 */
public class AutoGainFilter extends BaseFilter {
    Waveform wf;
    public AutoGainFilter(FilterManager fm){
        super(fm);
        wf=Waveform.getInstance();
    }
    private float getGain(){
        return 32767.0f/wf.getPeak();
    }
    @Override
    public void filter(float[] data) {
        float gain=getGain();
        for (int i = 0; i < data.length; i++) {
            data[i]=data[i]*gain;
        }
    }

    @Override
    public String getName() {
        return "Auto Gain Filter";
    }

    @Override
    public View getContentView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.filter_auto_gain, container, false);

        return v;
    }

}
