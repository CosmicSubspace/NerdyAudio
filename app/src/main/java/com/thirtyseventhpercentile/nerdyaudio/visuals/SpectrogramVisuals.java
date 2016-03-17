//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.thirtyseventhpercentile.nerdyaudio.exceptions.BufferNotPresentException;
import com.thirtyseventhpercentile.nerdyaudio.exceptions.InvalidParameterException;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.SettingsUpdateListener;
import com.thirtyseventhpercentile.nerdyaudio.settings.BaseSetting;
import com.thirtyseventhpercentile.nerdyaudio.settings.SidebarSettings;
import com.thirtyseventhpercentile.nerdyaudio.settings.SpectrogramVisualSettings;

import java.nio.IntBuffer;



public class SpectrogramVisuals extends FftRenderer implements SettingsUpdateListener{
    Paint pt;

    Bitmap graph;
    int canvasX, canvasY;
    IntBuffer graphBuffer;
    int scrollPxPerRedraw=1;

    float contrast=2.0f;

    SidebarSettings sbs;
    SpectrogramVisualSettings newSettings=null;

    private void syncChanges(){
        if (newSettings!=null){
            setFFTSize(newSettings.getFftSize());
            Log.i(LOG_TAG, "Spectrum: size changing" + fftSize);

            try {
                setFrequencyRange(newSettings.getStartFreq(), newSettings.getEndFreq());
            }catch(InvalidParameterException e){
                Log.w(LOG_TAG,"SpectrogramVisuals>syncChanges() wut?");
            }
            setLogScale(newSettings.getLogScale());
            setScrollPerRedraw(newSettings.getScrollSpeed());
            setContrast(newSettings.getContrast());
            newSettings=null;
        }
    }

    public void newGraph(int w, int h){
        canvasX=w;
        canvasY=h;
        graphBuffer=IntBuffer.allocate(w*h);
        graph=Bitmap.createBitmap(canvasX,canvasY, Bitmap.Config.ARGB_8888);
    }

    public SpectrogramVisuals(float density) {
        super(density);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
        sbs= SidebarSettings.getInstance();
        sbs.addSettingsUpdateListener(this);

        //I have a feeling that this would cause some nasty shit in the future.
        updated(sbs.getSetting(BaseSetting.SPECTROGRAM));
    }



    public void setScrollPerRedraw(int pixels){
        this.scrollPxPerRedraw=pixels;
    }

    public void setContrast(float contrast){
        this.contrast=contrast;
    }


    @Override
    public void draw(Canvas c, int w, int h) {
        syncChanges();

        if (vb != null && ap != null) {
            if (w!=canvasX||h!=canvasY) newGraph(w, h);
            long currentFrame = getCurrentFrame();
            try {
                updateFFT(currentFrame);

                graphBuffer.position(canvasX *scrollPxPerRedraw);
                graphBuffer.compact();
                graphBuffer.position(graphBuffer.limit()-canvasX*scrollPxPerRedraw-1);
                int targetBin;
                int newColors[]=new int[canvasX];
                for (int i=0;i<canvasX;i++){
                    /*
                    try {
                        targetBin=frequencyToBinNumber(pixelNumberToFrequency(i));
                    }catch (FFTOutOfBoundsException fftoobe){
                        continue;
                    }*/
                    //newColors[i]=magnitudeToColor(magnitude(x[targetBin],y[targetBin]));
                    newColors[i]=magnitudeToColor(getMagnitudeRatio(i/(float)canvasX));
                }
                for (int i=0;i<scrollPxPerRedraw;i++) graphBuffer.put(newColors);
                graphBuffer.rewind();
                graph.copyPixelsFromBuffer(graphBuffer);


                c.drawBitmap(graph,0,0,pt);


            } catch (BufferNotPresentException e) {
                Log.d(LOG_TAG, "Buffer not present! Requested around " + currentFrame);
            }

        }
    }

    @Override
    public void release() {
        sbs.removeSettingsUpdateListener(this);
    }

    private int magnitudeToColor(double mag){
        int inten=(int)(mag*this.contrast);
        if (inten>255) inten=255;
        return Color.argb(255,inten,inten,inten);
    }





    /*
    private double magnitude(double x, double y){
        return Math.sqrt(x * x + y * y);
    }
    private int coordsToIndex(int w, int h){
        return w+h*canvasX;
    }
    private float binNumberToFrequency(int binNumber){
        return binNumber*samplingRate/(float)fftSize;
    }
    private int frequencyToBinNumber(float frequency) throws FFTOutOfBoundsException{
        int res=Math.round(frequency*fftSize/(float)samplingRate);
        if (res>fftSize/2 || res<0) throw new FFTOutOfBoundsException("");
        return res;
    }
    private float pixelNumberToFrequency(int pixelNum){
        if (!logScale) return (pixelNum/(float)canvasX)*(maxFreq-minFreq)+minFreq;
        else{

            return (float)Math.exp((Math.log(maxFreq)-Math.log(minFreq))*(pixelNum/(double)canvasX)+Math.log(minFreq));

        }
    }*/

    @Override
    public void updated(BaseSetting setting) {
        if (setting instanceof SpectrogramVisualSettings){
            Log.i(LOG_TAG,"SpectrumVisuals settings match.");
            newSettings=(SpectrogramVisualSettings)setting;
        }
    }
}


/*
public class SpectrogramVisuals extends BaseRenderer{
    Paint pt;
    int fftSize = 2048;
    FFT fft;
    Bitmap[] graphPortions;
    int canvasX, canvasY;
    int resolution=10;
    public void createGraphBitmaps(int w, int h){
        canvasX=w;
        canvasY=h;
        graphPortions=new Bitmap[w/resolution];
        for (int i=0;i<graphPortions.length;i++) {
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            graphPortions[i] = Bitmap.createBitmap(1, h/resolution, conf); // this creates a MUTABLE bitmap
        }
    }

    public SpectrogramVisuals(float density) {
        super(density);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
        fft = new FFT(fftSize);
    }

    public void setFFTSize(int samples) {
        this.fftSize = samples;
        fft = new FFT(samples);
    }


    @Override
    public void draw(Canvas c, int w, int h) {
        if (vb != null && ap != null) {
            if (w!=canvasX||h!=canvasY) createGraphBitmaps(w, h);
            long currentFrame = getCurrentFrame();
            try {
                short[] pcmL = getLSamples(currentFrame - fftSize / 2 + 1, currentFrame + fftSize / 2);
                short[] pcmR = getRSamples(currentFrame - fftSize / 2 + 1, currentFrame + fftSize / 2);
                deleteBefore(currentFrame - fftSize / 2 + 1);



                double[] x = new double[fftSize], y = new double[fftSize];
                for (int i = 0; i < pcmL.length; i++) {
                    x[i] = pcmL[i] / 32767.0;
                    y[i] = 0;
                }

                fft.fft(x, y);



                for (int i=0;i<graphPortions.length-1;i++){
                    graphPortions[i]=graphPortions[i+1];
                }

                Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                graphPortions[graphPortions.length-1] = Bitmap.createBitmap(1, h/resolution, conf); // this creates a MUTABLE bitmap
                for (int i=0;i<x.length;i++){
                    if (i>=graphPortions[i].getHeight()) break;
                    graphPortions[graphPortions.length-1].setPixel(0,i,magnitudeToColor(magnitude(x[i],y[i])));
                }

                for (int i=0;i<graphPortions.length;i++){
                    if (graphPortions[i]!=null) c.drawBitmap(graphPortions[i],new Rect(0,0,1,graphPortions[i].getHeight()),new Rect(i*resolution,0,(i+1)*resolution,canvasY),null);
                }




            } catch (BufferNotPresentException e) {
                Log.d(LOG_TAG, "Buffer not present! Requested around " + currentFrame);
            }

        }
    }
    private int magnitudeToColor(double mag){
        int inten=(int)mag;
        if (inten>255) inten=255;
        return Color.argb(255,inten,inten,inten);
    }
    private double magnitude(double x, double y){
        return Math.sqrt(x*x+y*y);
    }
}
*/