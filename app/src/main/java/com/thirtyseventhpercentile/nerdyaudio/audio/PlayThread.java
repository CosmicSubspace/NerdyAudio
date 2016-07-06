//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.audio;

import android.util.Log;

import com.ringdroid.soundfile.SoundFile;
import com.thirtyseventhpercentile.nerdyaudio.filters.FilterManager;
import com.thirtyseventhpercentile.nerdyaudio.helper.ErrorLogger;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.BufferFeedListener;


class PlayThread extends Thread{
    SoundFile sf;
    AudioPlayer ap; //TODO BAD PRACTICE!!!
    boolean stop=false;
    FilterManager fm;
    protected PlayThread(AudioPlayer ap, FilterManager fm){
        this.ap=ap;
        this.fm=fm;
    }
    public void run() {
        sf=new SoundFile();
        try {
            sf.ReadFileWithCallback(ap.source, new BufferFeedListener() {
                @Override
                public void feed(short[] buff) {
                    if (stop) return;
                    short[] filtered=fm.filterAll(buff);
                    //if (ap.bfl!=null) ap.bfl.feed(filtered);

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
                Thread.sleep(30); //Wait and block until it is done playing(we know it is done playing if it doesn't advance any in 30ms.)
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
