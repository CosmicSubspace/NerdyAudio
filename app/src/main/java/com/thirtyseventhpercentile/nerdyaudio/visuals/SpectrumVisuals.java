//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.thirtyseventhpercentile.nerdyaudio.exceptions.BufferNotPresentException;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.settings.FloatSliderElement;
import com.thirtyseventhpercentile.nerdyaudio.settings.SettingElement;
import com.thirtyseventhpercentile.nerdyaudio.settings.SliderElement;

import java.util.List;


public class SpectrumVisuals extends FftRenderer {
    Paint pt;

    /*
    int bars=100;
    float spacing = 0.0f;
    float barHeightMultiplier=1.0f;
    */

    SliderElement bars = new SliderElement("Number of Bars", 1, 500, 100);
    FloatSliderElement spacing = new FloatSliderElement("Spacing", 0, 1, 0, 100);
    FloatSliderElement barHeightMultiplier = new FloatSliderElement("Bar Height", 0, 10, 2, 100);


    @Override
    public List<SettingElement> getSettings() {
        List<SettingElement> res = super.getSettings();
        res.add(bars);
        res.add(spacing);
        res.add(barHeightMultiplier);
        return res;
    }


    @Override
    public String getKey() {
        return "SpectrumVisuals";
    }

    public SpectrumVisuals(Context ctxt) {
        super(ctxt);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);

    }


    @Override
    public void drawVisuals(Canvas c, int w, int h) {
        int bars = this.bars.getValue();
        float spacing = this.spacing.getFloatValue();
        float barHeightMultiplier = this.barHeightMultiplier.getFloatValue();
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
            float betweenBars = w / (float) bars;
            for (int i = 0; i < bars; i++) {
                c.drawRect(i * betweenBars, h - barHeightMultiplier * getMagnitudeRatio(i / (float) bars), (i + 1 - spacing) * betweenBars, h, pt);
            }

            //c.drawRect(0,0,300,300,pt);


        } catch (BufferNotPresentException e) {
            Log2.log(1, this, "Buffer not present! Requested around " + getCurrentFrame());
        }


    }

    @Override
    public void dimensionsChanged(int w, int h) {

    }
}
