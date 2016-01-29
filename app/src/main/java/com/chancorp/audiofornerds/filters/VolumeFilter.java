package com.chancorp.audiofornerds.filters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chancorp.audiofornerds.R;

public class VolumeFilter extends BaseFilter implements SeekBar.OnSeekBarChangeListener, View.OnClickListener{

    SeekBar sb;
    TextView valueText, warningText;
    float multiplier=1.0f;
    int progress=100;
    public VolumeFilter(FilterManager fm){
        super(fm);
    }
    @Override
    public void filter(float[] data) { //We use the existing array so that GC won't have to suffer from all those arrays.
        for (int i = 0; i < data.length; i++) {
            data[i]=data[i]*multiplier;
        }
    }

    @Override
    public String getName() {
        return "Volume Filter";
    }


    @Override
    public View getContentView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.filter_volume, container, false);
        sb=(SeekBar) v.findViewById(R.id.filter_volume_seekbar);
        sb.setMax(200);
        sb.setOnSeekBarChangeListener(this);

        valueText=(TextView) v.findViewById(R.id.filter_volume_value);
        warningText=(TextView)v.findViewById(R.id.filter_volume_warning);

        sb.setProgress(progress);



        return v;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        progress=i;
        multiplier=progress/100.0f;
        valueText.setText(progress+"%");
        if (progress>100) warningText.setVisibility(View.VISIBLE);
        else warningText.setVisibility(View.GONE);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


}
