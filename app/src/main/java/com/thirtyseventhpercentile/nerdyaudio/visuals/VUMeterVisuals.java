//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.thirtyseventhpercentile.nerdyaudio.exceptions.BufferNotPresentException;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.SettingsUpdateListener;
import com.thirtyseventhpercentile.nerdyaudio.settings.BaseSetting;
import com.thirtyseventhpercentile.nerdyaudio.settings.SidebarSettings;
import com.thirtyseventhpercentile.nerdyaudio.settings.VUMeterSettings;


public class VUMeterVisuals extends BaseRenderer{
    Paint pt;
    int range = 2048; //Should be Synchronized.
    float[] lAvgHistory,rAvgHistory,lPeakHistory,rPeakHistory;

    int historySize=64; //Should be Synchronized.

    float textDp=16; //No need for Synchronization.
    float barsDp=100; //No need for Synchronization.

    //These temporary values are for concurrency. Changing member variables while being drawn can lead to crashes.
    VUMeterSettings newSetting=null;


    public VUMeterVisuals(float density) {

        super(density);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
        initArrays();

        updated(sbs.getSetting(BaseSetting.VU));

    }
    private void initArrays(){
        Log2.log(1,this,"VUMeterVisuals>InitArrays called. historySize="+historySize);
        lAvgHistory=new float[historySize];
        rAvgHistory=new float[historySize];
        lPeakHistory=new float[historySize];
        rPeakHistory=new float[historySize];
    }

    private void pushArrays(){
        for (int i = historySize-1; i >0; i--) {
            lAvgHistory[i]=lAvgHistory[i-1];
            rAvgHistory[i]=rAvgHistory[i-1];
            lPeakHistory[i]=lPeakHistory[i-1];
            rPeakHistory[i]=rPeakHistory[i-1];
        }
    }
    private int indexToColor(int idx){
        if (idx==0) return Color.rgb(255, 0, 0);
        else{
            int grayscale=(int)(255*idx/(float)historySize);
            return Color.argb(255-grayscale, 0,0,0);
        }
    }


    private void syncChanges(){
        if (newSetting!=null){
            range=newSetting.getRange();
            historySize=newSetting.getHistorySize();
            initArrays();
            newSetting=null;
        }
    }


    @Override
    public void drawVisuals(Canvas c, int w, int h) {
        syncChanges();


            long currentFrame = getCurrentFrame();
            try {
                float[] pcmL = getLSamples(currentFrame - range / 2 + 1, currentFrame + range / 2);
                float[] pcmR = getRSamples(currentFrame - range / 2 + 1, currentFrame + range / 2);
                deleteBefore(currentFrame - range / 2 + 1);


                double sumL = 0, sumR = 0, sumLSquared=0,sumRSquared=0;
                long numSamples = 0;
                float peakL=0,peakR=0;
                float avgL, avgR,rmsL,rmsR,peakRf,peakLf;


                for (int i = 0; i < pcmL.length; i++) {
                    sumL += Math.abs(pcmL[i]);
                    sumLSquared += pcmL[i]*pcmL[i];
                    if (Math.abs(pcmL[i])>peakL) peakL=Math.abs(pcmL[i]);

                    sumR += Math.abs(pcmR[i]);
                    sumRSquared += pcmR[i]*pcmR[i];
                    if (Math.abs(pcmR[i])>peakR) peakR=Math.abs(pcmR[i]);

                    numSamples++;
                }


                rmsL = (float) (Math.sqrt(sumLSquared / (double) numSamples));
                avgL = (float) ((sumL / (double) numSamples));


                rmsR = (float) (Math.sqrt(sumRSquared / (double) numSamples));
                avgR = (float) ((sumR / (double) numSamples));



                pushArrays();
                lAvgHistory[0]=avgL;
                lPeakHistory[0]=peakL;
                rAvgHistory[0]=avgR;
                rPeakHistory[0]=peakR;

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
                c.drawText("Peak: " + String.format("%.3f | %.3f", peakL,peakR), w / 2, h / 2-30*density, pt);
                c.drawText("RMS: "+String.format("%.3f | %.3f",rmsL,rmsR),w/2,h/2,pt);
                c.drawText("AVG: "+String.format("%.3f | %.3f",avgL,avgR),w/2,h/2+30*density,pt);


            } catch (BufferNotPresentException e) {
                Log2.log(1,this, "Buffer not present! Requested around " + currentFrame);
            }

    }


    @Override
    public void updated(BaseSetting setting) {
        if (setting instanceof VUMeterSettings){
            newSetting=(VUMeterSettings)setting;
        }
    }

    @Override
    public void dimensionsChanged(int w, int h) {

    }

}
