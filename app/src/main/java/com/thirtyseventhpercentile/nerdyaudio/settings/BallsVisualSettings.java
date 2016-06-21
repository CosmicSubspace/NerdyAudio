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


public class BallsVisualSettings extends BaseSetting implements AdapterView.OnItemSelectedListener,
        SeekBar.OnSeekBarChangeListener{
    private static final String LOG_TAG = "CS_AFN";
    private static final String PREF_IDENTIFIER = "com.thirtyseventhpercentile.audiofornerds.settings.BallsVisualSettings";


    int fftSize = 2048;
    int iter=10;
    float bounciness=30;
    float sensitivity=100;

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
            Log2.log(3,this, "ballsVisualSettings>setFftSize(): fftSize NOT in fftSizes[]!!");
        }

    }

    public int getIter() {
        return iter;
    }

    public void setIter(int iter) {
        this.iter=iter;
        if (iterationsSeekbar!=null && iterationsTV!=null) {
            iterationsTV.setText(Integer.toString(this.iter));
            iterationsSeekbar.setProgress(this.iter);
        }
    }

    public float getBounciness() {
        return bounciness;
    }

    public void setBounciness(float bounciness) {
        this.bounciness=bounciness;
        if (bouncinessSeekbar!=null && bouncinessTV!=null) {
            bouncinessTV.setText(Float.toString(this.bounciness));
            bouncinessSeekbar.setProgress((int)(this.bounciness*10));
        }
    }

    public float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity=sensitivity;
        if (sensitivitySeekbar!=null && sensitivityTV!=null) {
            sensitivityTV.setText(Float.toString(this.sensitivity));
            sensitivitySeekbar.setProgress((int)(this.sensitivity*10));
        }
    }


    public BallsVisualSettings(SidebarSettings sbs, Context c) {
        super(sbs,c);
    }

    private static final String[] fftSizes = {"256", "512", "1024", "2048", "4096", "8192"};
    Spinner fftSizeSpinner;

    SeekBar bouncinessSeekbar, sensitivitySeekbar, iterationsSeekbar;
    TextView  bouncinessTV, sensitivityTV, iterationsTV;

    public View getSettingsView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.visuals_setting_ball, container, false);

        fftSizeSpinner = (Spinner) v.findViewById(R.id.vis_balls_setting_fftsize_selector);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(container.getContext(), android.R.layout.simple_spinner_item, fftSizes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fftSizeSpinner.setAdapter(adapter);

        //fftSizeSpinner.setOnItemSelectedListener(this);
        //We do this because otherwise the initialization of the spinner would fire a callback
        //Which would be undesirable.
        fftSizeSpinner.post(new Runnable() {
            public void run() {
                fftSizeSpinner.setOnItemSelectedListener(BallsVisualSettings.this);
            }
        });

        bouncinessSeekbar = (SeekBar) v.findViewById(R.id.vis_balls_setting_bounciness_seekbar);
        bouncinessTV = (TextView) v.findViewById(R.id.vis_balls_setting_bounciness_value);
        bouncinessSeekbar.setOnSeekBarChangeListener(this);

        sensitivitySeekbar = (SeekBar) v.findViewById(R.id.vis_balls_setting_sensitivity_seekbar);
        sensitivityTV = (TextView) v.findViewById(R.id.vis_balls_setting_sensitivity_value);
        sensitivitySeekbar.setOnSeekBarChangeListener(this);

        iterationsSeekbar = (SeekBar) v.findViewById(R.id.vis_balls_setting_iter_seekbar);
        iterationsTV = (TextView) v.findViewById(R.id.vis_balls_setting_iter_value);
        iterationsSeekbar.setOnSeekBarChangeListener(this);

        load();
        return v;
    }


    @Override
    public int getType() {
        return BaseSetting.BALLS;
    }

    @Override
    protected void save() {

        SharedPreferences.Editor editor=getSharedPreferences(PREF_IDENTIFIER).edit();
        editor.putInt("fftSize", fftSize);
        editor.putInt("iter",iter);
        editor.putFloat("bounciness", bounciness);
        editor.putFloat("sensitivity",sensitivity);

        editor.apply();
    }

    @Override
    protected void load() {
        SharedPreferences pref=getSharedPreferences(PREF_IDENTIFIER);

        setFftSize(pref.getInt("fftSize", fftSize));
        setIter(pref.getInt("iter", iter));
        setBounciness(pref.getFloat("bounciness", bounciness));
        setSensitivity(pref.getFloat("sensitivity", sensitivity));

        sbs.notifyUI(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.vis_balls_setting_fftsize_selector) {
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
        if (seekBar.getId() == R.id.vis_balls_setting_iter_seekbar) {
            setIter(progress);
        }else if (seekBar.getId() == R.id.vis_balls_setting_sensitivity_seekbar) {
            setSensitivity(progress/ 10.f);
            Log2.log(2,this,"!!!");
        }else if (seekBar.getId() == R.id.vis_balls_setting_bounciness_seekbar) {
            setBounciness(progress / 10.f);
        } else {
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

}
