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
import com.thirtyseventhpercentile.nerdyaudio.settings.SettingElement;
import com.thirtyseventhpercentile.nerdyaudio.settings.SliderElement;

import java.util.ArrayList;
import java.util.List;


public class LoudnessGraphVisuals extends BaseRenderer {

    SliderElement range = new SliderElement("Range", 1, 2048, 1024);
    SliderElement historySize = new SliderElement("History Size", 1, 64, 32);

    @Override
    public List<SettingElement> getSettings() {
        Log2.log(2, this, range, historySize);
        List<SettingElement> res = new ArrayList<>();
        res.add(range);
        res.add(historySize);

        return res;
    }


    @Override
    public String getKey() {
        return "LoudnessGraphVisuals";
    }


    Paint pt;

    float[] lAvgHistory, rAvgHistory, lPeakHistory, rPeakHistory;

    float textDp = 16;
    float barsDp = 100;


    public LoudnessGraphVisuals(Context ctxt) {
        super(ctxt);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
        initArrays();
        Log2.log(2, this, "Constructing", range, historySize);
    }

    private void initArrays() {
        Log2.log(1, this, "LoudnessGraphVisuals>InitArrays called. historySize=" + historySize);
        lAvgHistory = new float[historySize.getValue()];
        rAvgHistory = new float[historySize.getValue()];
        lPeakHistory = new float[historySize.getValue()];
        rPeakHistory = new float[historySize.getValue()];
    }

    private void pushArrays() {
        for (int i = historySize.getValue() - 1; i > 0; i--) {
            lAvgHistory[i] = lAvgHistory[i - 1];
            rAvgHistory[i] = rAvgHistory[i - 1];
            lPeakHistory[i] = lPeakHistory[i - 1];
            rPeakHistory[i] = rPeakHistory[i - 1];
        }
    }

    private int indexToColor(int idx) {
        if (idx == 0) return Color.rgb(255, 0, 0);
        else {
            int grayscale = (int) (255 * idx / (float) historySize.getValue());
            return Color.argb(255 - grayscale, 0, 0, 0);
        }
    }


    @Override
    public void drawVisuals(Canvas c, int w, int h) {

        int range = this.range.getValue();
        int historySize = this.historySize.getValue();
        //Log2.log(1,this,historySize,lAvgHistory.length);
        if (historySize != lAvgHistory.length) initArrays();

        long currentFrame = getCurrentFrame();
        try {
            float[] pcmL = getLSamples(currentFrame - range / 2 + 1, currentFrame + range / 2);
            float[] pcmR = getRSamples(currentFrame - range / 2 + 1, currentFrame + range / 2);
            deleteBefore(currentFrame - range / 2 + 1);


            double sumL = 0, sumR = 0, sumLSquared = 0, sumRSquared = 0;
            long numSamples = 0;
            float peakL = 0, peakR = 0;
            float avgL, avgR, rmsL, rmsR, peakRf, peakLf;


            for (int i = 0; i < pcmL.length; i++) {
                sumL += Math.abs(pcmL[i]);
                sumLSquared += pcmL[i] * pcmL[i];
                if (Math.abs(pcmL[i]) > peakL) peakL = Math.abs(pcmL[i]);

                sumR += Math.abs(pcmR[i]);
                sumRSquared += pcmR[i] * pcmR[i];
                if (Math.abs(pcmR[i]) > peakR) peakR = Math.abs(pcmR[i]);

                numSamples++;
            }


            rmsL = (float) (Math.sqrt(sumLSquared / (double) numSamples));
            avgL = (float) ((sumL / (double) numSamples));


            rmsR = (float) (Math.sqrt(sumRSquared / (double) numSamples));
            avgR = (float) ((sumR / (double) numSamples));


            pushArrays();
            lAvgHistory[0] = avgL;
            lPeakHistory[0] = peakL;
            rAvgHistory[0] = avgR;
            rPeakHistory[0] = peakR;

            for (int i = 0; i < historySize; i++) {
                pt.setColor(Color.RED);
                c.drawLine(barsDp * density / historySize * (historySize - i), (1 - lPeakHistory[i]) * h, barsDp * density / historySize * (historySize - i - 1), (1 - lPeakHistory[i]) * h, pt);
                c.drawLine((w - barsDp * density) + barsDp * density / historySize * (i), (1 - rPeakHistory[i]) * h, (w - barsDp * density) + barsDp * density / historySize * (i + 1), (1 - rPeakHistory[i]) * h, pt);
                //c.drawRect(barsDp * density / historySize * (historySize - i), (1 - lPeakHistory[i]) * h, barsDp * density / historySize * (historySize - i - 1), h, pt);
                //c.drawRect((w - barsDp * density) + barsDp * density / historySize * (i), (1 - rPeakHistory[i]) * h, (w - barsDp * density) + barsDp * density / historySize * (i + 1), h, pt);


                pt.setColor(indexToColor(i));
                c.drawRect(barsDp * density / historySize * (historySize - i), (1 - lAvgHistory[i]) * h, barsDp * density / historySize * (historySize - i - 1), h, pt);
                c.drawRect((w - barsDp * density) + barsDp * density / historySize * (i), (1 - rAvgHistory[i]) * h, (w - barsDp * density) + barsDp * density / historySize * (i + 1), h, pt);


            }


            pt.setColor(Color.BLACK);
            pt.setTextAlign(Paint.Align.CENTER);
            pt.setTextSize(textDp * density);
            c.drawText("Peak: " + String.format("%.3f | %.3f", peakL, peakR), w / 2, h / 2 - 30 * density, pt);
            c.drawText("RMS: " + String.format("%.3f | %.3f", rmsL, rmsR), w / 2, h / 2, pt);
            c.drawText("AVG: " + String.format("%.3f | %.3f", avgL, avgR), w / 2, h / 2 + 30 * density, pt);


        } catch (BufferNotPresentException e) {
            Log2.log(1, this, "Buffer not present! Requested around " + currentFrame);
        }

    }


    @Override
    public void dimensionsChanged(int w, int h) {

    }

}
