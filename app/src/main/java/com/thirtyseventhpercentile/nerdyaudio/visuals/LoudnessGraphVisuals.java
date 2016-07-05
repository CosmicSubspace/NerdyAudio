//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.thirtyseventhpercentile.nerdyaudio.exceptions.BufferNotPresentException;
import com.thirtyseventhpercentile.nerdyaudio.helper.ErrorLogger;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.NewSettingsUpdateListener;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.SettingsUpdateListener;
import com.thirtyseventhpercentile.nerdyaudio.settings.BaseSetting;
import com.thirtyseventhpercentile.nerdyaudio.settings.SettingsUiFactory;
import com.thirtyseventhpercentile.nerdyaudio.settings.SidebarSettings;
import com.thirtyseventhpercentile.nerdyaudio.settings.VUMeterSettings;

import java.util.Set;


public class LoudnessGraphVisuals extends BaseRenderer {


    public static class RendererParameters extends BaseRenderer.RendererParameters {
        public int range = 2048;
        public int historySize = 64;
    }

    SettingsUiFactory.SliderElement range = new SettingsUiFactory.SliderElement("Range", 1, 2048, 1024);
    SettingsUiFactory.SliderElement historySize = new SettingsUiFactory.SliderElement("History Size", 1, 64, 32);

    public SettingsUiFactory.SettingElement[] getSettingUI() {
        SettingsUiFactory.SettingElement[] elements = new SettingsUiFactory.SettingElement[2];
        elements[0] = range;
        elements[1] = historySize;
        return elements;
    }


    RendererParameters params, newParams;

    Paint pt;

    float[] lAvgHistory, rAvgHistory, lPeakHistory, rPeakHistory;

    float textDp = 16; //No need for Synchronization.
    float barsDp = 100; //No need for Synchronization.


    public LoudnessGraphVisuals(float density) {

        super(density);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
        initArrays();

        updated(sbs.getSetting(BaseSetting.VU));

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


    private void syncChanges() {
        range.applyValue();
        historySize.applyValue();
    }


    @Override
    public void drawVisuals(Canvas c, int w, int h) {
        syncChanges();
        int range = this.range.getValue();
        int historySize = this.historySize.getValue();
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
    public void updated(BaseSetting setting) {

    }

    @Override
    public void dimensionsChanged(int w, int h) {

    }

}
