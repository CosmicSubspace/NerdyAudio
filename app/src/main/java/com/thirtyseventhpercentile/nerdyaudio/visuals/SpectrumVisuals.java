//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.thirtyseventhpercentile.nerdyaudio.audio.AudioPlayer;
import com.thirtyseventhpercentile.nerdyaudio.exceptions.BufferNotPresentException;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.SettingsUpdateListener;
import com.thirtyseventhpercentile.nerdyaudio.settings.BaseSetting;
import com.thirtyseventhpercentile.nerdyaudio.settings.SidebarSettings;
import com.thirtyseventhpercentile.nerdyaudio.settings.SpectrumVisualSettings;


public class SpectrumVisuals extends FftRenderer{
    Paint pt;

    int bars=100;
    float spacing = 0.0f;
    float startFreq=20, endFreq=1000;
    double startLog=Math.log(20), endLog=Math.log(1000);
    float barHeightMultiplier=1.0f;

    SpectrumVisualSettings newSettings=null;

    public SpectrumVisuals(float density) {
        super(density);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);

        //I have a feeling that this would cause some nasty shit in the future.
        updated(sbs.getSetting(BaseSetting.SPECTRUM));
    }

    private void syncChanges(){
        if (newSettings!=null){
            setFFTSize(newSettings.getFftSize());
            Log.i(LOG_TAG, "Spectrum: size changing" + fftSize);

            bars=newSettings.getBars();
            spacing=newSettings.getSpacing();
            startFreq=newSettings.getStartFreq();
            startLog=Math.log(startFreq);
            endFreq=newSettings.getEndFreq();
            endLog=Math.log(endFreq);
            barHeightMultiplier=newSettings.getBarHeight();
            setLogScale(newSettings.getLogScale());

            newSettings=null;
        }
    }

    @Override
    public void drawVisuals(Canvas c, int w, int h) {
        syncChanges();

            long currentFrame = getCurrentFrame();
            try {
                updateFFT(currentFrame);
                //Log.d(LOG_TAG, "FFT size: " + canvasX.length);
/*
                    for (int i=0;i<canvasX.length;i++){
                        debugStr=debugStr+canvasX[i]+",";
                    }

                    Log.d(LOG_TAG,"Calculated Real FFT: "+debugStr);*/

                pt.setColor(Color.BLACK);
                float magnitude;
                //Log.d(LOG_TAG,"BetweenBars: "+betweenBars);
                float betweenBars=w/(float)bars;
                for (int i = 0; i < bars; i++) {
                    c.drawRect(i * betweenBars, h - barHeightMultiplier*getMagnitudeRatio(i / (float)bars), (i + 1 - spacing) * betweenBars, h, pt);
                }

                //c.drawRect(0,0,300,300,pt);


            } catch (BufferNotPresentException e) {
                Log.d(LOG_TAG, "Buffer not present! Requested around " + getCurrentFrame());
            }


    }





    @Override
    public void updated(BaseSetting setting) {
        Log.i(LOG_TAG,"SpectrumVisuals updated.");
        if (setting instanceof SpectrumVisualSettings){
            Log.i(LOG_TAG,"SpectrumVisuals settings match.");
            newSettings=(SpectrumVisualSettings)setting;
        }
    }
}
