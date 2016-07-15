//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.cosmicsubspace.nerdyaudio.filters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.cosmicsubspace.nerdyaudio.R;
import com.cosmicsubspace.nerdyaudio.helper.Log2;
import com.cosmicsubspace.nerdyaudio.helper.SimpleMaths;

public class StereoFilter extends BaseFilter implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener{

    public StereoFilter(FilterManager fm) {
        super(fm);

    }


    @Override
    public void filter(float[] data) {
        if (downmix){
            for (int i = 0; i < data.length/2; i++) {
                data[i*2+1]=data[i*2]=(data[i*2+1]+data[i*2])/2.0f;
            }
        }

        for (int i = 0; i < data.length/2; i++) {

            data[i*2]*=leftMult;
            data[i*2+1]*=rightMult;
        }

    }

    @Override
    public String getName() {
        return "Auto Gain";
    }

    float leftMult=1.0f, rightMult=1.0f;
    int progress=50;
    boolean downmix=false;

    SeekBar balanceBar;
    Button resetBtn;
    CheckBox downmixToggle;


    @Override
    public View getContentView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.filter_stereo, container, false);
        balanceBar=(SeekBar) v.findViewById(R.id.filter_stereo_balance);
        balanceBar.setProgress(progress);
        balanceBar.setOnSeekBarChangeListener(this);
        resetBtn=(Button) v.findViewById(R.id.filter_stereo_reset);

        resetBtn.setOnClickListener(this);
        downmixToggle =(CheckBox) v.findViewById(R.id.filter_stereo_mono);
        downmixToggle.setChecked(downmix);
        downmixToggle.setOnCheckedChangeListener(this);
        return v;
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        if (v.getId()==R.id.filter_stereo_reset){
            balanceBar.setProgress(50);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId()==R.id.filter_stereo_mono){
            downmix=isChecked;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId()==R.id.filter_stereo_balance){
            this.progress=progress;
            leftMult=1.0f;
            rightMult=1.0f;
            if (progress>50){
                leftMult= 1.0f-(progress-50)/50.0f;

            }else if (progress<50){
                rightMult= progress/50.0f;
            }
            Log2.log(2,this,progress,rightMult,leftMult);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
