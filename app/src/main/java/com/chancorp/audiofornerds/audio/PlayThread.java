package com.chancorp.audiofornerds.audio;

import android.util.Log;

import com.chancorp.audiofornerds.file.FileManager;
import com.chancorp.audiofornerds.filters.FilterManager;
import com.chancorp.audiofornerds.helper.ErrorLogger;
import com.chancorp.audiofornerds.interfaces.BufferFeedListener;
import com.ringdroid.soundfile.SoundFile;

/**
 * Created by Chan on 2015-12-22.
 */
class PlayThread extends Thread{
    SoundFile sf;
    AudioPlayer ap; //TODO BAD PRACTICE!!!
    boolean stop=false;
    protected PlayThread(AudioPlayer ap){
        this.ap=ap;
    }
    public void run() {
        sf=new SoundFile();
        try {
            sf.ReadFileWithCallback(ap.source, new BufferFeedListener() {
                @Override
                public void feed(short[] buff) {
                    if (stop) return;
                    short[] filtered=FilterManager.getInstance().filterAll(buff);//TODO getinstance() may slow down perf.
                    if (ap.bfl!=null) ap.bfl.feed(filtered);

                    while (ap.isPaused()){
                        try {
                            Thread.sleep(30); //Wait and block until it is unpaused
                        } catch (Exception e) {
                            ErrorLogger.log(e);
                        }
                    }
                    ap.mAudioTrack.write(filtered,0,buff.length);

                }
            });
        } catch (Exception e) {
            ErrorLogger.log(e);
        }
        if (stop) return;

        Log.d(ap.LOG_TAG, "Play Thread finished feeding samples.");

        long lastSample=0; //TODO Lazy fix.

        while (ap.getCurrentFrame()!=lastSample){
            try {
                lastSample=ap.getCurrentFrame();
                Thread.sleep(30); //Wait and block until it is done playing
            } catch (Exception e) {
                ErrorLogger.log(e);
            }
        }
        if (stop) return;

        Log.d(ap.LOG_TAG,"Play Thread finished. Releasing and stopping thread.");



        ap.release();


        if (ap.cl!=null) ap.cl.onComplete(ap.sourceString);

    }
    public void stahp(){
        if (sf!=null) sf.stop=true; //TODO What?
        this.stop=true;
    }
}
