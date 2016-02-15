package com.chancorp.audiofornerds.visuals;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.chancorp.audiofornerds.audio.AudioPlayer;
import com.chancorp.audiofornerds.exceptions.BufferNotPresentException;
import com.chancorp.audiofornerds.interfaces.SettingsUpdateListener;
import com.chancorp.audiofornerds.settings.BaseSetting;
import com.chancorp.audiofornerds.settings.SidebarSettings;
import com.chancorp.audiofornerds.settings.SpectrumVisualSettings;
import com.meapsoft.FFT;

/**
 * Created by Chan on 2015-12-18.
 */
public class SpectrumVisuals extends BaseRenderer implements SettingsUpdateListener{
    Paint pt;
    int fftSize = 2048;
    int bars=100;
    float spacing = 0.0f;
    float startFreq=20, endFreq=1000;
    //TODO Log Scale

    SpectrumVisualSettings newSettings=null;


    FFT fft;

    AudioPlayer ap;

    SidebarSettings sbs;

    public SpectrumVisuals(float density) {
        super(density);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
        fft = new FFT(fftSize);
        ap=AudioPlayer.getInstance();
        sbs= SidebarSettings.getInstance();
        sbs.addSettingsUpdateListener(this);

        //I have a feeling that this would cause some nasty shit in the future.
        updated(sbs.getSetting(BaseSetting.SPECTRUM));
    }

    private void syncChanges(){
        if (newSettings!=null){
            fftSize=newSettings.getFftSize();
            Log.i(LOG_TAG,"Spectrum: size changing"+fftSize);
            fft = new FFT(fftSize);

            bars=newSettings.getBars();
            spacing=newSettings.getSpacing();
            startFreq=newSettings.getStartFreq();
            endFreq=newSettings.getEndFreq();

            newSettings=null;
        }
    }

    @Override
    public void draw(Canvas c, int w, int h) {
        syncChanges();
        if (vb != null && ap != null) {
            long currentFrame = getCurrentFrame();
            try {
                short[] pcmL = getLSamples(currentFrame - fftSize / 2 + 1, currentFrame + fftSize / 2);
                short[] pcmR = getRSamples(currentFrame - fftSize / 2 + 1, currentFrame + fftSize / 2);
                deleteBefore(currentFrame - fftSize / 2 + 1);

                double[] x = new double[fftSize], y = new double[fftSize];
                for (int i = 0; i < pcmL.length; i++) {
                    x[i] = (pcmL[i]+pcmR[i]) / 65536.0;
                    y[i] = 0;
                }
                //Log.d(LOG_TAG,"Starting FFT");
                fft.fft(x, y);
                //Log.d(LOG_TAG, "FFT Done");

                //Log.d(LOG_TAG, "FFT size: " + canvasX.length);
/*
                    for (int i=0;i<canvasX.length;i++){
                        debugStr=debugStr+canvasX[i]+",";
                    }

                    Log.d(LOG_TAG,"Calculated Real FFT: "+debugStr);*/

                pt.setColor(Color.BLACK);
                float magnitude;
                //Log.d(LOG_TAG,"BetweenBars: "+betweenBars);
                float betweenBars=w/(float)bars;
                for (int i = 0; i < bars; i++) {

                    c.drawRect(i * betweenBars, h - getMagnitude(x,y,startFreq+(endFreq-startFreq)*i/(float)bars) * 1, (i + 1 - spacing) * betweenBars, h, pt);
                }

                //c.drawRect(0,0,300,300,pt);


            } catch (BufferNotPresentException e) {
                Log.d(LOG_TAG, "Buffer not present! Requested around " + currentFrame);
            }

        }
    }

    private float getMagnitude(double[] x, double[] y, double frequency){
        int sr=ap.getSampleRate();
        double frqPerBin=sr/(double)this.fftSize;
        float bin=(float)(frequency/frqPerBin);
        int ceilBin=(int)Math.round(Math.ceil(bin));
        int floorBin=(int)Math.round(Math.floor(bin));

        //Linear Interpolation.
        float ceilFactor=(bin-floorBin)*((float) Math.sqrt(x[ceilBin] * x[ceilBin] + y[ceilBin] * y[ceilBin]));
        float floorFactor=(ceilBin-bin)*((float) Math.sqrt(x[floorBin] * x[floorBin] + y[floorBin] * y[floorBin]));
        return ceilFactor+floorFactor;
    }

    @Override
    public void release() {
        sbs.removeSettingsUpdateListener(this);
    }

    @Override
    public void updated(BaseSetting setting) {
        Log.i(LOG_TAG,"SpectrumVisuals updated.");
        if (setting instanceof SpectrumVisualSettings){
            Log.i(LOG_TAG,"SpectrumVisuals settings match.");
            newSettings=(SpectrumVisualSettings)setting;
        }
    }
}
