package com.chancorp.audiofornerds.visuals;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.exceptions.BufferNotPresentException;

/**
 * Created by Chan on 2015-12-18.
 */
public class VUMeterVisuals extends BaseRenderer implements SeekBar.OnSeekBarChangeListener{
    Paint pt;
    int range = 2048; //Should be Synchronized.
    float[] lAvgHistory,rAvgHistory,lPeakHistory,rPeakHistory; //TODO circular buffers for performance
    int historySize=64; //Should be Synchronized.

    float textDp=16; //No need for Synchronization.
    float barsDp=100; //No need for Synchronization.

    //These temporary values are for concurrency. Changing member variables while being drawn can lead to crashes.
    boolean stateChanged=false;
    int rangeN;
    int historySizeN;


    public VUMeterVisuals(float density) {
        super(density);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
        initArrays();
    }
    private void initArrays(){
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

    public void setHistorySize(int size){
        this.historySizeN=size;
        stateChanged=true;
    }

    public void setRange(int samples) {
        this.rangeN = samples;
        stateChanged=true;
    }

    private void syncChanges(){
        if (stateChanged){
            range=rangeN;
            historySize=historySizeN;
            initArrays();
            stateChanged=false;
        }
    }

    @Override
    public void draw(Canvas c, int w, int h) {
        syncChanges();
        if (vb != null && ap != null) {

            long currentFrame = getCurrentFrame();
            try {
                short[] pcmL = getLSamples(currentFrame - range / 2 + 1, currentFrame + range / 2);
                short[] pcmR = getRSamples(currentFrame - range / 2 + 1, currentFrame + range / 2);
                deleteBefore(currentFrame - range / 2 + 1);


                long sumL = 0, sumR = 0, sumLSquared=0,sumRSquared=0;
                long numSamples = 0;
                int peakL=0,peakR=0;
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
                rmsL = (float) (Math.sqrt(sumLSquared / (double) numSamples) / 32767.0);
                avgL = (float) ((sumL / (double) numSamples) / 32767.0);


                rmsR = (float) (Math.sqrt(sumRSquared / (double) numSamples) / 32767.0);
                avgR = (float) ((sumR / (double) numSamples) / 32767.0);


                peakLf=peakL/32767.0f;
                peakRf=peakR/32767.0f;


                pushArrays();
                lAvgHistory[0]=avgL;
                lPeakHistory[0]=peakLf;
                rAvgHistory[0]=avgR;
                rPeakHistory[0]=peakRf;

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
                c.drawText("Peak: " + String.format("%.3f | %.3f", peakLf,peakRf), w / 2, h / 2-30*density, pt);
                c.drawText("RMS: "+String.format("%.3f | %.3f",rmsL,rmsR),w/2,h/2,pt);
                c.drawText("AVG: "+String.format("%.3f | %.3f",avgL,avgR),w/2,h/2+30*density,pt);


            } catch (BufferNotPresentException e) {
                Log.d(LOG_TAG, "Buffer not present! Requested around " + currentFrame);
            }
        }
    }



    SeekBar sb_hist,sb_len;
    TextView lengthTV,historyTV;
    public View getSettingsView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.visuals_setting_vu, container, false);
        sb_hist=(SeekBar)v.findViewById(R.id.vis_vu_setting_history_seekbar);
        historyTV=(TextView)v.findViewById(R.id.vis_vu_setting_history_value);
        sb_hist.setOnSeekBarChangeListener(this);

        sb_len=(SeekBar)v.findViewById(R.id.vis_vu_setting_length_seekbar);
        lengthTV=(TextView)v.findViewById(R.id.vis_vu_setting_length_value);
        sb_len.setOnSeekBarChangeListener(this);
        return v;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId()==R.id.vis_vu_setting_history_seekbar) {
            setHistorySize(progress);
            historyTV.setText(Integer.toString(historySize));
        }else if (seekBar.getId()==R.id.vis_vu_setting_length_seekbar) {
            setRange(progress*10);
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
