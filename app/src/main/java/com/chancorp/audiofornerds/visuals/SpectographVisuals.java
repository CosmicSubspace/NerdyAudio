package com.chancorp.audiofornerds.visuals;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.chancorp.audiofornerds.exceptions.BufferNotPresentException;
import com.chancorp.audiofornerds.exceptions.FFTOutOfBoundsException;
import com.chancorp.audiofornerds.exceptions.InvalidParameterException;
import com.meapsoft.FFT;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by Chan on 2015-12-18.
 */


public class SpectographVisuals extends BaseRenderer{
    public static final int LOG_SCALE=1235236;
    public static final int LINEAR_SCALE=4537;
    Paint pt;
    int fftSize = 2048;
    FFT fft;
    Bitmap graph;
    int canvasX, canvasY;
    int resolution=10;
    IntBuffer graphBuffer;
    int samplingRate=44100;
    int maxFreq=5000, minFreq=20;
    int scrollPxPerRedraw=1;
    boolean logScale=false;

    public void newGraph(int w, int h){
        canvasX=w;
        canvasY=h;
        graphBuffer=IntBuffer.allocate(w*h);
        graph=Bitmap.createBitmap(canvasX,canvasY, Bitmap.Config.ARGB_8888);
    }

    public SpectographVisuals(float density) {
        super(density);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
        fft = new FFT(fftSize);
    }

    public void setFFTSize(int samples) {
        this.fftSize = samples;
        fft = new FFT(samples);
    }
    public void setSamplingRate(int samplingRate){
        this.samplingRate=samplingRate;
    }
    public void setFrequencyRange(int min, int max) throws InvalidParameterException {
        this.maxFreq=max;
        this.minFreq=min;
        if (maxFreq<=minFreq) throw new InvalidParameterException("Min is larger than Max.");
    }
    public void setScrollPerRedraw(int pixels){
        this.scrollPxPerRedraw=pixels;
    }
    public void setScale(int option){
        if (option==LOG_SCALE) logScale=true;
        else if (option==LINEAR_SCALE) logScale=false;
        else Log.e(LOG_TAG,"Invalid Option!(SpectographVisuals>SetScale)");
    }


    @Override
    public void draw(Canvas c, int w, int h) {
        if (vb != null && ap != null) {
            if (w!=canvasX||h!=canvasY) newGraph(w, h);
            long currentFrame = getCurrentFrame();
            try {
                //TODO interpolation between bins so that it would be smoother
                //TODO increase temporal resolution
                short[] pcmL = getLSamples(currentFrame - fftSize / 2 + 1, currentFrame + fftSize / 2);
                short[] pcmR = getRSamples(currentFrame - fftSize / 2 + 1, currentFrame + fftSize / 2);
                deleteBefore(currentFrame - fftSize / 2 + 1);

                double[] x = new double[fftSize], y = new double[fftSize];
                for (int i = 0; i < pcmL.length; i++) {
                    x[i] = pcmL[i] / 32767.0;
                    y[i] = 0;
                }

                fft.fft(x, y);

                graphBuffer.position(canvasX *scrollPxPerRedraw);
                graphBuffer.compact();
                graphBuffer.position(graphBuffer.limit()-canvasX*scrollPxPerRedraw-1);
                int targetBin;
                int newColors[]=new int[canvasX];
                for (int i=0;i<canvasX;i++){
                    try {
                        targetBin=frequencyToBinNumber(pixelNumberToFrequency(i));
                    }catch (FFTOutOfBoundsException fftoobe){
                        continue;
                    }
                    newColors[i]=magnitudeToColor(magnitude(x[targetBin],y[targetBin]));
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
    private int magnitudeToColor(double mag){
        int inten=(int)mag*2;
        if (inten>255) inten=255;
        return Color.argb(255,inten,inten,inten);
    }
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
            //TODO Optimization may be needed. This is called hundreds of times per redraw.
            return (float)Math.exp((Math.log(maxFreq)-Math.log(minFreq))*(pixelNum/(double)canvasX)+Math.log(minFreq));

        }
    }
}


/*
public class SpectographVisuals extends BaseRenderer{
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

    public SpectographVisuals(float density) {
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

                //TODO Performance improvement needed here, so we can use full-res graphs.

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