//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.ringdroid.soundfile.SoundFile;
import com.thirtyseventhpercentile.nerdyaudio.filters.FilterManager;
import com.thirtyseventhpercentile.nerdyaudio.helper.ErrorLogger;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.CompletionListener;

import java.io.File;



public class AudioPlayer {
    public final String LOG_TAG="CS_AFN";

    protected int mSampleRate;
    protected int mChannels;
    protected int mNumSamples;  // Number of samples per channel.
    protected int bufferSize;
    protected long seekOffsetus=0;

    protected AudioTrack mAudioTrack;
    protected PlayThread mPlayThread;
    protected File source;
    protected String sourceString;
    protected boolean mKeepPlaying;
    //protected BufferFeedListener bfl;
    protected CompletionListener cl;

    static AudioPlayer inst;

    public String getSourceString(){
        return sourceString;
    }

    public static AudioPlayer getInstance(){
        if (inst==null) inst=new AudioPlayer();
        return inst;
    }
    protected AudioPlayer(){

    };

    public int getSampleRate(){
        return mSampleRate;
    }


    /*public synchronized void setBufferFeedListener(BufferFeedListener bfl){
        this.bfl=bfl;
    }*/
    public synchronized void setCompletionListener(CompletionListener cl){this.cl=cl;}
    public synchronized void getInfoFromFile(){
        SoundFile sf=new SoundFile();
        try {
            sf.ReadFileMetaData(source);
            mSampleRate =sf.getSampleRate();
            mChannels = sf.getChannels();
            mNumSamples = sf.getNumSamples();

            bufferSize = AudioTrack.getMinBufferSize(
                    mSampleRate,
                    mChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT);
            // make sure minBufferSize can contain at least 1 second of audio (16 bits sample).
            if (bufferSize < mChannels * mSampleRate * 2) {
                bufferSize = mChannels * mSampleRate * 2;
            }
        } catch (Exception e) {
            ErrorLogger.log(e);
        }

    }
    public synchronized void setFileStr(String fileStr){
        source=new File(fileStr);
        sourceString=fileStr;
    }
    public synchronized void initializeAudio(){
        mAudioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                mSampleRate,
                mChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);

    }





    public synchronized boolean isPlaying() {
        if (mAudioTrack!=null) return mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
        else return false;
    }

    public synchronized boolean isPaused() {
        if (mAudioTrack!=null) return mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED;
        else return false;
    }

    public synchronized void seekTo(float time){
        //TODO : This is a hasty fix for a NullPointerException. It is unclear why mPlayThread's sf returns null sometimes.
        int i=0;
        while(i<5) {
            try {
                Log2.log(1, this, mPlayThread, mPlayThread.sf);
                seekOffsetus = mPlayThread.sf.requestSeek(Math.round(((double) time) * 1000 * 1000)); //TODO because we seek to the _nearest_ frame, there could be a time shift.
                mAudioTrack.flush(); //TODO there is a significant time delay after the seek. Fix that.
                break;
            } catch (NullPointerException e) {
                Log2.log(1, this, "NullPointerException in seekTo. Retrying.");
                try {
                    Thread.sleep(1);
                }catch (Exception e1){}
                i++;
            }
        }
    }


    public synchronized long getCurrentFrame(){ //Frame no. without taking seeks into account.
        if (mAudioTrack!=null){
            if (isPlaying()||isPaused()) return mAudioTrack.getPlaybackHeadPosition();
            else return 0;
        }
        else return 0;
    }
    public synchronized long getMusicCurrentFrame(){
        return getCurrentFrame()-Math.round(seekOffsetus/1000000.0*getSampleRate());
    }

    public synchronized void playAudio() {
        Log2.log(1,this, "PlayAudio called.");
        Log2.log(1,this, "mAudioTrack is null:"+(mAudioTrack==null));
        if (isPlaying()) {
            Log2.log(1,this,"isPlaying");
            return;
        }
        if (isPaused()){
            Log2.log(1,this,"isPaused");
            mAudioTrack.play();
            return;
        }
        Log2.log(1,this,"Normal Startup");

        getInfoFromFile();
        initializeAudio();

        seekOffsetus=0;

        mKeepPlaying = true;
        mAudioTrack.flush();
        mAudioTrack.play();
        // Setting thread feeding the audio samples to the audio hardware.
        // (Assumes mChannels = 1 or 2).
        mPlayThread = new PlayThread (this, FilterManager.getInstance()) ;
        mPlayThread.start();
    }



    public synchronized void pause() {
        Log2.log(2,this, "Ckecking if able to pause...");
        if (isPlaying()) {
            mAudioTrack.pause();
            Log2.log(2,this, "Pausing...");
            // mAudioTrack.write() should block if it cannot write.
        }
    }

    public synchronized void stop() {
        Log2.log(2,this, "Ckecking if able to stop...");
        if (mAudioTrack!=null){
        if (isPlaying() || isPaused()) {
            Log2.log(2,this,"Stopping...");
            mKeepPlaying = false;
            mAudioTrack.pause();  // pause() stops the playback immediately.
            mAudioTrack.stop();   // Unblock mAudioTrack.write() to avoid deadlocks.
            mAudioTrack.flush();  // just in case...
        }
        }
    }
    public synchronized void killThread(){
        if (mPlayThread!=null) mPlayThread.stahp(); //TODO Better way to do this.
        mPlayThread=null;
        /*
        if (mPlayThread != null) {
            try {
                mPlayThread.join();
            } catch (InterruptedException e) {
            }
            mPlayThread = null;
        }*/
    }

    public synchronized void release() {
        stop();
        if (mAudioTrack!=null) mAudioTrack.release();
    }


}
