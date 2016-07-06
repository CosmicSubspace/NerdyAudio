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
import com.thirtyseventhpercentile.nerdyaudio.settings.BooleanElement;
import com.thirtyseventhpercentile.nerdyaudio.settings.SettingElement;
import com.thirtyseventhpercentile.nerdyaudio.settings.SliderElement;

import java.util.ArrayList;
import java.util.List;

public class WaveformVisuals extends BaseRenderer {
    /*
    int range = 2048;
    int drawEvery = 1;
    boolean downmix=false;
    */

    SliderElement range = new SliderElement("Range", 100, 10000, 2048);
    BooleanElement downmix = new BooleanElement("Downmix", true);


    Paint pt;


    @Override
    public List<SettingElement> getSettings() {
        List<SettingElement> res = new ArrayList<>();
        res.add(range);
        res.add(downmix);
        return res;
    }


    @Override
    public String getKey() {
        return "WaveformVisuals";
    }

    public WaveformVisuals(Context ctxt) {
        super(ctxt);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);

    }


    @Override
    public void drawVisuals(Canvas c, int w, int h) {
        int range = this.range.getValue();
        boolean downmix = this.downmix.getValue();
        int drawEvery = range / 1024 + 1;


        long currentFrame = getCurrentFrame();
        try {

            pt.setColor(Color.BLACK);

            float[] pcmL = getLSamples(currentFrame - range + 1, currentFrame);
            float[] pcmR = getRSamples(currentFrame - range + 1, currentFrame);
            deleteBefore(currentFrame - range + 1);

            int numberOfLinePoints = pcmL.length / drawEvery;
            float[] lines = new float[numberOfLinePoints * 4];
            //float[] points = new float[numberOfLinePoints*2];
            assert pcmL.length == pcmR.length;

            int pcmIndex;
            //TODO Performance Improvements.
            if (downmix) {
                for (int i = 0; i < numberOfLinePoints - 1; i++) {
                    pcmIndex = i * drawEvery;
                    lines[i * 4] = i / (float) numberOfLinePoints * w;
                    lines[i * 4 + 1] = ((pcmL[pcmIndex] + pcmR[pcmIndex]) / 2.0f + 1) * h / 2.0f;
                    lines[i * 4 + 2] = (i + 1) / (float) numberOfLinePoints * w;
                    lines[i * 4 + 3] = ((pcmL[pcmIndex + drawEvery] + pcmR[pcmIndex + drawEvery]) / 2.0f + 1) * h / 2.0f;
                }
                c.drawLines(lines, pt);
            } else {
                for (int i = 0; i < numberOfLinePoints - 1; i++) {
                    pcmIndex = i * drawEvery;
                    lines[i * 4] = i / (float) numberOfLinePoints * w;
                    lines[i * 4 + 1] = (pcmL[pcmIndex] + 1) * h / 4.0f;
                    lines[i * 4 + 2] = (i + 1) / (float) numberOfLinePoints * w;
                    lines[i * 4 + 3] = (pcmL[pcmIndex + drawEvery] + 1) * h / 4.0f;
                }
                c.drawLines(lines, pt);

                for (int i = 0; i < numberOfLinePoints - 1; i++) {
                    pcmIndex = i * drawEvery;
                    lines[i * 4] = i / (float) numberOfLinePoints * w;
                    lines[i * 4 + 1] = (pcmR[pcmIndex] + 1) * h / 4.0f + h / 2.0f;
                    lines[i * 4 + 2] = (i + 1) / (float) numberOfLinePoints * w;
                    lines[i * 4 + 3] = (pcmR[pcmIndex + drawEvery] + 1) * h / 4.0f + h / 2.0f;

                }
                c.drawLines(lines, pt);
            }
            //c.drawPoints(points,pt);

        } catch (BufferNotPresentException e) {
            Log2.log(1, this, "Buffer not present! Requested around " + currentFrame);
        }

    }


    @Override
    public void dimensionsChanged(int w, int h) {

    }

}
