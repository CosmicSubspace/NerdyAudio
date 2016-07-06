package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.content.Context;

import com.thirtyseventhpercentile.nerdyaudio.exceptions.BufferNotPresentException;
import com.thirtyseventhpercentile.nerdyaudio.exceptions.FFTException;
import com.meapsoft.FFT;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.settings.BooleanElement;
import com.thirtyseventhpercentile.nerdyaudio.settings.FloatSliderElement;
import com.thirtyseventhpercentile.nerdyaudio.settings.SettingElement;
import com.thirtyseventhpercentile.nerdyaudio.settings.SpinnerElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chan on 3/9/2016.
 */
public abstract class FftRenderer extends BaseRenderer {
    public FftRenderer(Context ctxt) {
        super(ctxt);
        fft = new FFT(4096);

        maxFreq=new FloatSliderElement("Maximum Frequency",20,10000,10000,1000);
        minFreq=new FloatSliderElement("Minimum Frequency",20,10000,20,1000);

        ArrayList<String> spinnerEntries=new ArrayList<>();
        ArrayList<Integer> mappedValues=new ArrayList<>();

        spinnerEntries.add("256"); mappedValues.add(256);
        spinnerEntries.add("512"); mappedValues.add(512);
        spinnerEntries.add("1024"); mappedValues.add(1024);
        spinnerEntries.add("2048"); mappedValues.add(2048);
        spinnerEntries.add("4096"); mappedValues.add(4096);
        spinnerEntries.add("8192"); mappedValues.add(8192);

        fftSize=new SpinnerElement<>("FFT Size",spinnerEntries,mappedValues,4);

        logScale=new BooleanElement("Log Scale",false);
    }

    FloatSliderElement  maxFreq,minFreq;
    SpinnerElement<Integer> fftSize;
    BooleanElement logScale;

    @Override
    public List<SettingElement> getSettings() {
        List<SettingElement> res=new ArrayList<>();
        res.add(maxFreq);
        res.add(minFreq);
        res.add(fftSize);
        res.add(logScale);
        return res;
    }


    FFT fft;

    /*
    int fftSize = 2048;
    private boolean logScale=false;
    private float maxFreq=5000, minFreq=20;
    */



    double[] x,y;

    public float freqToBin(float frequency){
        int sr=ap.getSampleRate();
        float frqPerBin=sr/(float)this.fftSize.getValue();
        return (float)(frequency/frqPerBin);
    }
    public float getMagnitudeBin(int bin){
        return  (float)Math.sqrt(x[bin] * x[bin] + y[bin] * y[bin]);
    }
    public float getMagnitudeFreq(float frequency){

        float bin=freqToBin(frequency);
        int ceilBin=(int)Math.round(Math.ceil(bin));
        int floorBin=(int)Math.round(Math.floor(bin));

        //Linear Interpolation.
        float ceilFactor=(bin-floorBin)*(getMagnitudeBin(ceilBin));
        float floorFactor=(ceilBin-bin)*(getMagnitudeBin(floorBin));
        return ceilFactor+floorFactor;
    }
    private float ratioToFrequency(float ratio){
        float minFreq=this.minFreq.getFloatValue();
        float maxFreq=this.maxFreq.getFloatValue();
        if (maxFreq<minFreq) maxFreq=minFreq+100;
        if (logScale.getValue()){
            //TODO performance here.
            return (float)Math.exp(Math.log(minFreq)+(Math.log(maxFreq)-Math.log(minFreq))*ratio);
        }else{
            return minFreq+(maxFreq-minFreq)*ratio;
        }
    }

    public float getMagnitudeRatio(float ratio){
        return getMagnitudeFreq(ratioToFrequency(ratio));
    }

    //temp variables..
    float startBin,endBin,res,numSummed,binAddStart,binAddEnd, addLength;
    int addStartBin, addEndBin;
    public float getMagnitudeRange(float startFreq, float endFreq, boolean normalize){
        startBin=freqToBin(startFreq);
        endBin=freqToBin(endFreq);
        res=0;
        numSummed=0;
        addStartBin=Math.round(startBin);
        addEndBin=Math.round(endBin);

        for (int i = addStartBin; i <=addEndBin; i++) {
            binAddStart=i-0.5f;
            binAddEnd=i+0.5f;
            if (binAddEnd>endBin) binAddEnd=endBin;
            if (binAddStart<startBin) binAddStart=startBin;
            addLength=binAddEnd-binAddStart;
            numSummed+=addLength;
            res+= getMagnitudeBin(i)*addLength;
        }

        if (normalize) return res/numSummed;
        else return res;
    }

    public void updateFFT(long currentFrame) throws BufferNotPresentException {

        int fftSize=this.fftSize.getValue();
        if (fftSize!=fft.getFftSize()){
            fft=new FFT(fftSize);
        }


        float[] pcmL = getLSamples(currentFrame - fftSize / 2 + 1, currentFrame + fftSize / 2);
        float[] pcmR = getRSamples(currentFrame - fftSize / 2 + 1, currentFrame + fftSize / 2);
        deleteBefore(currentFrame - fftSize / 2 + 1);

        x = new double[fftSize];
        y = new double[fftSize];
        for (int i = 0; i < pcmL.length; i++) {
            x[i] = pcmL[i];
            y[i] = 0;
        }

        try {
            fft.fft(x, y);
        }catch(FFTException e){ //FFT Size Mismatch

                Log2.log(4,this,"FFT Size Mismatch Exception. wtf?");

        }

    }
}
