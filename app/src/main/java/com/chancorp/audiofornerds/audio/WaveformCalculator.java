package com.chancorp.audiofornerds.audio;

import android.content.Context;
import android.util.Log;

import com.chancorp.audiofornerds.helper.ErrorLogger;
import com.chancorp.audiofornerds.interfaces.BufferFeedListener;
import com.chancorp.audiofornerds.interfaces.WaveformReturnListener;
import com.chancorp.audiofornerds.interfaces.SampleProgressListener;
import com.ringdroid.soundfile.SoundFile;

import java.io.File;
import java.util.ArrayList;

public class WaveformCalculator extends Thread implements BufferFeedListener {
    public static final String LOG_TAG="CS_AFN";

    File file;
    String filename;
    Context c;
    float[] data;
    ArrayList<Long> sumData= new ArrayList<Long>();
    float maxAmp=0.0f;
    double divisionEvery;
    SampleProgressListener spl;
    WaveformReturnListener wrl;


    int sampleRate;
    int channels;
    double currentTimeInSeconds=0;
    double timePerSample;
    int currentSample=0;
    int currentBar=0;

    public WaveformCalculator(String filename, double divisionEvery, Context ctxt){
        super();
        this.filename=filename;
        this.file=new File(filename);
        this.divisionEvery=divisionEvery;
this.c=ctxt;
    }
    public void setSampleProgressListener(SampleProgressListener spl){
        this.spl=spl;
    }
    public void setWaveformReturnListener(WaveformReturnListener wrl){
        this.wrl=wrl;
    }
    @Override
    public void run(){

        //TODO Stereo support (Currently R and L channels are mixed and then averaged.)


        SoundFile sf=new SoundFile();
        try {
            sf.ReadFileMetaData(file);

            sampleRate=sf.getSampleRate();
            timePerSample=1.0/(double)sampleRate;
            channels=sf.getChannels();

            Log.d(LOG_TAG, "Sample rate:" + sampleRate);
            Log.d(LOG_TAG, "Channels:" + channels);


            sf.ReadFileWithCallback(file, this);
        } catch (Exception e) {
           ErrorLogger.log(e);
        }


        sf=null;

        data=new float[sumData.size()];
        int samplesPerBar=(int)(sampleRate*divisionEvery);
        for (int i=0;i<sumData.size();i++){
            //data[i]=(float)Math.sqrt((double)sumData.get(i)/(double)samplesPerBar);
            data[i]=(float)((double)sumData.get(i)/(double)samplesPerBar);
        }



        Waveform res=new Waveform(filename,divisionEvery);
        res.analyze(data, maxAmp, currentSample / channels, sampleRate, channels);
        res.saveToFile(c);
        if (wrl!=null) wrl.onReturn(res);
    }

    @Override
    public void feed(short[] buff) {
        //TODO performance improvement needed here. This loop runs millions of times when analyzing a music file.
        //Log.v(LOG_TAG,"WaveformVisuals: Fed shorts. length="+buff.length);
        for (int i=0; i<buff.length;i++){
            if (i%4==0) {//TODO HASTY SPEED IMPROVEMENT
                currentTimeInSeconds = currentSample * timePerSample / (double) channels; //TODO This is a hasty fix for stereo support.
                currentBar = (int) (currentTimeInSeconds / divisionEvery);
                if (sumData.size() <= currentBar) {
                    sumData.add(0L);
                    Log.d(LOG_TAG, "Adding bar. (number " + sumData.size() + ", " + currentTimeInSeconds + " seconds)");
                }
                sumData.set(currentBar, sumData.get(currentBar) + Math.abs(buff[i]));
                if (maxAmp < buff[i]) maxAmp = buff[i];
            }
            currentSample++;
        }
        if (spl!=null) spl.report(currentSample);
    }
    /*
    @Override
    public void run(){
        SoundFile sf= null;
        try {
            sf = SoundFile.create(filename, new SoundFile.ProgressListener() {
                @Override
                public boolean reportProgress(double fractionComplete) {
                    Log.v(LOG_TAG,"WaveformVisuals>Decoding..."+fractionComplete);
                    return true;
                }
            });
        } catch (Exception e) {
            ErrorLogger.log(e);
        }

        ShortBuffer sb=sf.getSamples();
        int size=sf.getNumSamples();
        int divisions=data.length;
        int currentDivision=0;
        float samplesPerDivision=size/(float)divisions;
        long[] sums = new long[divisions];
        int[] numOfSamples=new int[divisions];

        for (int i=0;i<divisions;i++){
            sums[i]=0;
            numOfSamples[i]=0;
        }

        for (int i=0;i<size;i++){
            if (i>samplesPerDivision*(currentDivision+1)) {
                currentDivision++;
                Log.v(LOG_TAG, "On division" + currentDivision);
            }
            if (currentDivision>size){
                Log.w(LOG_TAG, "Rounding error?");
                currentDivision=divisions-1;
            }
            sums[currentDivision]+=sb.get(i)*sb.get(i);
            numOfSamples[currentDivision]++;
            if (Math.abs(sb.get(i))>maxAmp) maxAmp=Math.abs(sb.get(i));
        }

        for (int i=0;i<divisions;i++){
            data[i]=(float)(Math.sqrt((double)sums[i]/(double)numOfSamples[i]));
        }


        sf=null;
        sb=null;

        if (this.owrl!=null) owrl.onReturn(data, maxAmp);
    }*/
}