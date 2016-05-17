package com.thirtyseventhpercentile.nerdyaudio.filters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.thirtyseventhpercentile.nerdyaudio.R;

/**
 * Created by Chan on 4/19/2016.
 */
//TODO implement simple IIR Low-Pass Filter. (x=ix+(1-i)x)
public class IirHighPassFilter extends BaseFilter implements SeekBar.OnSeekBarChangeListener {

    float ratio=0.7f;
    float current=0.0f, lastData=0.0f;

    public IirHighPassFilter(FilterManager fm){
        super(fm);
    }
    @Override
    public void filter(float[] data) {
        for (int i = 0; i < data.length; i++) {
            //y[i] := α * (y[i-1] + x[i] - x[i-1])
            current=ratio*(current+data[i]-lastData);
            lastData=data[i];
            data[i]=current;
        }
    }

    @Override
    public String getName() {
        return "Simple IIR High-Pass";
    }

    SeekBar sb;
    TextView valueText;
    @Override
    public View getContentView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.filter_iirlowpass, container, false);
        sb=(SeekBar) v.findViewById(R.id.filter_lowpass_seekbar);
        sb.setMax(100);
        sb.setOnSeekBarChangeListener(this);

        valueText =(TextView) v.findViewById(R.id.filter_lowpass_value);

        sb.setProgress((int)(ratio*100));



        return v;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        ratio=i/100.0f;
        valueText.setText(""+ratio);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
