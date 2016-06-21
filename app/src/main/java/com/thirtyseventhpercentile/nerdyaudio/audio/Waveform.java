//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.audio;

import android.content.Context;
import android.util.Log;

import com.thirtyseventhpercentile.nerdyaudio.helper.ErrorLogger;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.SampleProgressListener;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.WaveformReturnListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;



//TODO I don't think Singleton is the way to do this...
//TODO seperate this calss into a manager and a non-singleton class.
public class Waveform implements Serializable {
    static final long serialVersionUID = 1L;

    public static final String LOG_TAG = "CS_AFN";

    private boolean isReady = false;

    private String filename;
    private float[] data;

    private float peak;
    private float rmsDataPeak = 0;
    private double barEvery;

    private long numOfFrames;

    private int sampleRate;
    private int channels;

    transient Context c;
    transient static Waveform inst;

    public String getFilename() {
        return filename;
    }

    public static boolean checkExistance(String filename, double barEvery, Context c) {
        try{
            c.openFileInput(getUniqueID(filename, barEvery));
            return true;
        }catch(FileNotFoundException e){
            return false;
        }
    }

    public static void calculateIfDoesntExist(String filename, double barEvery, Context c, SampleProgressListener spl, WaveformReturnListener wrl) {
        if (checkExistance(filename, barEvery, c)) return;
        else {
            calculate(filename, barEvery, c, spl, wrl);
        }
    }

    public static Waveform getWaveform(String filename, double barEvery, Context c) throws IOException, ClassNotFoundException {
        Log2.log(2,Waveform.class, "Loading from file.");

        FileInputStream fis = c.openFileInput(getUniqueID(filename, barEvery));
        ObjectInputStream is = new ObjectInputStream(fis);
        Waveform readObject = (Waveform) is.readObject();
        is.close();
        fis.close();
        return readObject;

    }

    public static Waveform getInstance() {
        if (inst == null) inst = new Waveform();
        return inst;
    }

    public static String getUniqueID(String filename, double barEvery) {
        //TODO Hash this or something
        String res;
        res = filename.replace("/", "") + barEvery;
        Log2.log(1,Waveform.class, "Generating Unique ID: " + res);
        return res;
    }


    static void calculate(String filename, double barEvery, Context ctxt, SampleProgressListener spl, WaveformReturnListener wrl) {
        WaveformCalculator wc = new WaveformCalculator(filename, barEvery, ctxt);
        wc.setSampleProgressListener(spl);
        wc.setWaveformReturnListener(wrl);
        wc.start();
    }

    protected Waveform(String filename, double barEvery) {
        this.filename = filename;
        this.barEvery = barEvery;
    }

    protected Waveform() {

    }

    public float getPeak() {
        return peak;
    }

    private void copy(Waveform w) {
        Log2.log(1,this, "Copying to instance...");
        this.filename = w.filename;
        this.data = w.data;
        this.peak = w.peak;
        this.rmsDataPeak = w.rmsDataPeak;
        this.barEvery = w.barEvery;
        this.numOfFrames = w.numOfFrames;
        this.sampleRate = w.sampleRate;
        this.channels = w.channels;
        this.isReady = w.isReady;
    }

    public float getRatio(int i) {
        return data[i] / rmsDataPeak;
    }

    public String getDebugData() {
        String res = new String();
        for (int i = 0; i < data.length; i++) {
            //res=res+i+" : "+data[i]+" | ";
        }
        res = res + "Peak: " + peak + "\n";
        res = res + "RMS Data Peak: " + rmsDataPeak + "\n";
        res = res + "Frame count: " + numOfFrames;
        return res;
    }

    public int getDivisions() {
        return data.length;
    }

    public long getNumOfFrames() {
        return numOfFrames;
    }

    public float frameNumberToRatio(long frameNumber) {
        return (float) ((double) frameNumber / (double) numOfFrames);
    }

    public String frameNumberToTimeStamp(long frameNumber) {
        long sec = frameNumber / sampleRate;
        return "" + (sec / 60) + ":" + (sec % 60);
    }

    public boolean isReady() {
        return isReady;
    }


    public void analyze(float[] data, float maxAmp, long numOfFrames, int sampleRate, int channels) {
        this.data = data;
        this.peak = maxAmp;

        for (int i = 0; i < data.length; i++) {
            if (this.rmsDataPeak < data[i]) this.rmsDataPeak = data[i];
        }

        this.numOfFrames = numOfFrames;
        this.sampleRate = sampleRate;
        this.channels = channels;

        this.isReady = true;

        // this.saveToFile(c);

        Log2.log(1,this, getDebugData());
    }


    public void saveToFile(Context c) {
        Log2.log(2,this, "Saving...");
        try {
            String name = getUniqueID(filename, barEvery);
            FileOutputStream fos = c.openFileOutput(name, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();
            Log2.log(2,this, "WaveformVisuals saved to " + name);
        } catch (Exception e) {
            ErrorLogger.log(e);
        }
    }


    public void loadFromFile(String filename, double barEvery, Context c) {
        Log2.log(2,this, "Loading...");
        try {
            copy(Waveform.getWaveform(filename, barEvery, c));
        }catch(Exception e){
            ErrorLogger.log(e);
        }
    }

    public void loadBlank() {
        isReady = false;
    }
}

