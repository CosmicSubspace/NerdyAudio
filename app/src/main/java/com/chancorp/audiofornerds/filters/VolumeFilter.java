package com.chancorp.audiofornerds.filters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chancorp.audiofornerds.R;

/**
 * Created by Chan on 2015-12-18.
 */
public class VolumeFilter extends BaseFilter implements SeekBar.OnSeekBarChangeListener, View.OnClickListener{
    FilterManager fm;
    View mainView;
    SeekBar sb;
    TextView valueText, warningText;
    Button closeBtn;
    float multiplier=1.0f;
    int progress=100;
    public VolumeFilter(FilterManager fm){
        this.fm=fm;
    }
    @Override
    public void filter(float[] data) { //We use the existing array so that GC won't have to suffer from all those arrays.
        for (int i = 0; i < data.length; i++) {
            data[i]=data[i]*multiplier;
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        mainView = inflater.inflate(R.layout.filter_volume, container, false);
        sb=(SeekBar) mainView.findViewById(R.id.filter_volume_seekbar);
        sb.setMax(200);
        sb.setOnSeekBarChangeListener(this);

        valueText=(TextView) mainView.findViewById(R.id.filter_volume_value);
        warningText=(TextView)mainView.findViewById(R.id.filter_volume_warning);

        sb.setProgress(progress);
        //onProgressChanged(null,status,false);

        closeBtn=(Button) mainView.findViewById(R.id.filter_volume_close);
        closeBtn.setOnClickListener(this);

        return mainView;
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

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.filter_volume_close){
            fm.deleteFilter(this);
            ((LinearLayout)closeBtn.getParent().getParent().getParent()).removeView(mainView);
        }
    }
}
