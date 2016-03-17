//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.filters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.audio.Waveform;

public class AutoGainFilter extends BaseFilter {
    private static final String LOG_TAG="CS_AFN";
    Waveform wf;
    public AutoGainFilter(FilterManager fm){
        super(fm);
        wf=Waveform.getInstance();
    }
    private float getGain(){
        Log.d(LOG_TAG,"Peak:"+wf.getPeak());
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
        return "Auto Gain";
    }

    @Override
    public View getContentView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.filter_auto_gain, container, false);
        return v;
    }

}
