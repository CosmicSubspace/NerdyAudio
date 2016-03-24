package com.thirtyseventhpercentile.nerdyaudio.visuals;

import com.thirtyseventhpercentile.nerdyaudio.exceptions.BufferNotPresentException;
import com.thirtyseventhpercentile.nerdyaudio.exceptions.InvalidParameterException;
import com.meapsoft.FFT;

/**
 * Created by Chan on 3/9/2016.
 */
public abstract class FftRenderer extends BaseRenderer {
    public FftRenderer(float density) {
        super(density);
        fft = new FFT(fftSize);
    }

    int fftSize = 2048;
    FFT fft;


    private boolean logScale=false;
    private float maxFreq=5000, minFreq=20;
    private double startLog=Math.log(20), endLog=Math.log(5000);

    double[] x,y;

    public void setFFTSize(int samples) {
        this.fftSize = samples;
        fft = new FFT(samples);
    }
    public float freqToBin(float frequency){
        int sr=ap.getSampleRate();
        float frqPerBin=sr/(float)this.fftSize;
        return (float)(frequency/frqPerBin);
    }
    public float getMagnitude(float frequency){

        float bin=freqToBin(frequency);
        int ceilBin=(int)Math.round(Math.ceil(bin));
        int floorBin=(int)Math.round(Math.floor(bin));

        //Linear Interpolation.
        float ceilFactor=(bin-floorBin)*((float) Math.sqrt(x[ceilBin] * x[ceilBin] + y[ceilBin] * y[ceilBin]));
        float floorFactor=(ceilBin-bin)*((float) Math.sqrt(x[floorBin] * x[floorBin] + y[floorBin] * y[floorBin]));
        return ceilFactor+floorFactor;
    }
    public void setLogScale(boolean logScale){
        this.logScale=logScale;
    }
    public void setFrequencyRange(float min, float max) throws InvalidParameterException {
        this.maxFreq=max;
        this.minFreq=min;
        this.startLog=Math.log(min);
        this.endLog=Math.log(max);
        if (maxFreq<=minFreq) throw new InvalidParameterException("Min is larger than Max.");
    }
    private float ratioToFrequency(float ratio){
        if (logScale){
            return (float)Math.exp(startLog+(endLog-startLog)*ratio);
        }else{
            return minFreq+(maxFreq-minFreq)*ratio;
        }
    }

    public float getMagnitudeRatio(float ratio){
        return getMagnitude(ratioToFrequency(ratio));
    }

    public void updateFFT(long currentFrame) throws BufferNotPresentException {

        short[] pcmL = getLSamples(currentFrame - fftSize / 2 + 1, currentFrame + fftSize / 2);
        short[] pcmR = getRSamples(currentFrame - fftSize / 2 + 1, currentFrame + fftSize / 2);
        deleteBefore(currentFrame - fftSize / 2 + 1);

        x = new double[fftSize];
        y = new double[fftSize];
        for (int i = 0; i < pcmL.length; i++) {
            x[i] = pcmL[i] / 32767.0;
            y[i] = 0;
        }

        fft.fft(x, y);
    }
}
