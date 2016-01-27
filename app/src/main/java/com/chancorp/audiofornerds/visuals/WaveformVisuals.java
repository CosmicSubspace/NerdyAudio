package com.chancorp.audiofornerds.visuals;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.exceptions.BufferNotPresentException;

/**
 * Created by Chan on 2015-12-18.
 */
public class WaveformVisuals extends BaseRenderer  implements SeekBar.OnSeekBarChangeListener, View.OnClickListener{
    int range = 2048;
    int drawEvery = 1;
    boolean downmix=false;
    Paint pt;

    public WaveformVisuals(float density) {
        super(density);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setRange(int samples) {
        this.range = samples;
        drawEvery=range/1024;
        if (drawEvery<1) drawEvery=1;
        Log.i(LOG_TAG,""+range+" | "+drawEvery);
    }

    public void drawEvery(int i) {
        this.drawEvery = i;
    }

    @Override
    public void draw(Canvas c, int w, int h) {
        if (vb != null && ap != null) {

            long currentFrame = getCurrentFrame();
            try {
                short[] pcmL = getLSamples(currentFrame - range / 2 + 1, currentFrame + range / 2);
                short[] pcmR = getRSamples(currentFrame - range / 2 + 1, currentFrame + range / 2);
                deleteBefore(currentFrame - range / 2 + 1);

                int numberOfLinePoints = pcmL.length / drawEvery;
                //float[] lines = new float[numberOfLinePoints * 4];
                float[] points = new float[numberOfLinePoints];

                int pcmIndex;
                for (int i = 0; i < numberOfLinePoints - 1; i++) {
                    pcmIndex = i * drawEvery;
                    //lines[i * 4] = i / (float) numberOfLinePoints * w;
                    //lines[i * 4 + 1] = (pcmL[pcmIndex] / 32767.0f + 1) * h / 2.0f;
                    //lines[i * 4 + 2] = (i + 1) / (float) numberOfLinePoints * w;
                    //lines[i * 4 + 3] = (pcmL[pcmIndex + drawEvery] / 32767.0f + 1) * h / 2.0f;
                    points[i] = (pcmL[pcmIndex] / 32767.0f + 1) * h / 2.0f;
                }


                pt.setColor(Color.BLACK);


                //c.drawLines(lines, pt);
                c.drawPoints(points,pt);

            } catch (BufferNotPresentException e) {
                Log.d(LOG_TAG, "Buffer not present! Requested around " + currentFrame);
            }
        }
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
        s.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId()==R.id.vis_waveform_setting_length_seekbar) {
            setRange(progress * 20);
            lengthTV.setText(Integer.toString(range));
        }else{
            Log.w(LOG_TAG,"I think I'm not the only seekbar around here....");
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
