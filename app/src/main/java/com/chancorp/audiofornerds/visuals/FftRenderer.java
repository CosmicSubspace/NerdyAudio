package com.chancorp.audiofornerds.visuals;

import com.chancorp.audiofornerds.exceptions.BufferNotPresentException;
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

    double[] x,y;

    public void setFFTSize(int samples) {
        this.fftSize = samples;
        fft = new FFT(samples);
    }

    public float getMagnitude(float frequency){

        int sr=ap.getSampleRate();
        float frqPerBin=sr/(float)this.fftSize;
        float bin=(float)(frequency/frqPerBin);
        int ceilBin=(int)Math.round(Math.ceil(bin));
        int floorBin=(int)Math.round(Math.floor(bin));

        //Linear Interpolation.
        float ceilFactor=(bin-floorBin)*((float) Math.sqrt(x[ceilBin] * x[ceilBin] + y[ceilBin] * y[ceilBin]));
        float floorFactor=(ceilBin-bin)*((float) Math.sqrt(x[floorBin] * x[floorBin] + y[floorBin] * y[floorBin]));
        return ceilFactor+floorFactor;
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
