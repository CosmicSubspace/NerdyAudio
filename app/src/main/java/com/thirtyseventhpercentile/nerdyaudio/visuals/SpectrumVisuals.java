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
import com.thirtyseventhpercentile.nerdyaudio.exceptions.InvalidParameterException;
import com.thirtyseventhpercentile.nerdyaudio.helper.ErrorLogger;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.SettingsUpdateListener;
import com.thirtyseventhpercentile.nerdyaudio.settings.BaseSetting;
import com.thirtyseventhpercentile.nerdyaudio.settings.SidebarSettings;
import com.thirtyseventhpercentile.nerdyaudio.settings.SpectrumVisualSettings;


public class SpectrumVisuals extends FftRenderer{
    Paint pt;

    int bars=100;
    float spacing = 0.0f;
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
            Log2.log(2,this, "Spectrum: size changing" + fftSize);

            bars=newSettings.getBars();
            spacing=newSettings.getSpacing();

            try {
                setFrequencyRange(newSettings.getStartFreq(),newSettings.getEndFreq());
            } catch (InvalidParameterException e) {
                ErrorLogger.log(e);
            }


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
                //Log2.log(1,this, "FFT size: " + canvasX.length);
/*
                    for (int i=0;i<canvasX.length;i++){
                        debugStr=debugStr+canvasX[i]+",";
                    }

                    Log2.log(1,this,"Calculated Real FFT: "+debugStr);*/

                pt.setColor(Color.BLACK);
                float magnitude;
                //Log2.log(1,this,"BetweenBars: "+betweenBars);
                float betweenBars=w/(float)bars;
                for (int i = 0; i < bars; i++) {
                    c.drawRect(i * betweenBars, h - barHeightMultiplier*getMagnitudeRatio(i / (float)bars), (i + 1 - spacing) * betweenBars, h, pt);
                }

                //c.drawRect(0,0,300,300,pt);


            } catch (BufferNotPresentException e) {
                Log2.log(1,this, "Buffer not present! Requested around " + getCurrentFrame());
            }


    }





    @Override
    public void updated(BaseSetting setting) {
        Log2.log(2,this,"SpectrumVisuals updated.");
        if (setting instanceof SpectrumVisualSettings){
            Log2.log(2,this,"SpectrumVisuals settings match.");
            newSettings=(SpectrumVisualSettings)setting;
        }
    }

    @Override
    public void dimensionsChanged(int w, int h) {

    }
}
