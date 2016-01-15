package com.chancorp.audiofornerds.visuals;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.chancorp.audiofornerds.exceptions.BufferNotPresentException;
import com.meapsoft.FFT;

/**
 * Created by Chan on 2015-12-18.
 */
public class SpectrumVisuals extends BaseRenderer {
    Paint pt;
    int fftSize = 2048;
    float spacing = 0.0f;
    FFT fft;

    public SpectrumVisuals(float density) {
        super(density);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
        fft = new FFT(fftSize);
    }

    public void setFFTSize(int samples) {
        this.fftSize = samples;
        fft = new FFT(samples);
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
    }

    @Override
    public void draw(Canvas c, int w, int h) {
        if (vb != null && ap != null) {

            long currentFrame = getCurrentFrame();
            try {
                short[] pcmL = getLSamples(currentFrame - fftSize / 2 + 1, currentFrame + fftSize / 2);
                short[] pcmR = getRSamples(currentFrame - fftSize / 2 + 1, currentFrame + fftSize / 2);
                deleteBefore(currentFrame - fftSize / 2 + 1);


                //TODO set cropping
                //TODO frequency range setting
                //TODO dB scale
                double[] x = new double[fftSize], y = new double[fftSize];
                for (int i = 0; i < pcmL.length; i++) {
                    x[i] = pcmL[i] / 32767.0;
                    y[i] = 0;
                }
                //Log.d(LOG_TAG,"Starting FFT");
                fft.fft(x, y);
                //Log.d(LOG_TAG, "FFT Done");

                String debugStr = "";
                //Log.d(LOG_TAG, "FFT size: " + canvasX.length);
/*
                    for (int i=0;i<canvasX.length;i++){
                        debugStr=debugStr+canvasX[i]+",";
                    }

                    Log.d(LOG_TAG,"Calculated Real FFT: "+debugStr);*/

                pt.setColor(Color.BLACK);
                float magnitude;
                float betweenBars = w / (float) x.length * 20;
                //Log.d(LOG_TAG,"BetweenBars: "+betweenBars);

                for (int i = 0; i < x.length / 20; i++) { //TODO This is a temporary zoom
                    magnitude = (float) Math.sqrt(x[i] * x[i] + y[i] * y[i]);
                    //Log.d(LOG_TAG,"Drawing box: "+(i * betweenBars)+" | " +(h-100)+" | "+ ((i-spacing)*betweenBars)+" | "+ h);
                    c.drawRect(i * betweenBars, h - magnitude * 1, (i + 1 - spacing) * betweenBars, h, pt);
                }

                //c.drawRect(0,0,300,300,pt);


            } catch (BufferNotPresentException e) {
                Log.d(LOG_TAG, "Buffer not present! Requested around " + currentFrame);
            }

        }
    }
}
