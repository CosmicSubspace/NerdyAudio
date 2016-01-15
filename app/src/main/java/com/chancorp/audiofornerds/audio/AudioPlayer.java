package com.chancorp.audiofornerds.audio;

import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.chancorp.audiofornerds.helper.ErrorLogger;
import com.chancorp.audiofornerds.interfaces.BufferFeedListener;
import com.chancorp.audiofornerds.interfaces.CompletionListener;
import com.ringdroid.soundfile.SoundFile;

import java.io.File;

/**
 * Created by Chan on 2015-12-09.
 */
public class AudioPlayer {
    public final String LOG_TAG="CS_AFN";

    protected int mSampleRate;
    protected int mChannels;
    protected int mNumSamples;  // Number of samples per channel.
    protected int bufferSize;

    protected AudioTrack mAudioTrack;
    protected PlayThread mPlayThread;
    protected File source;
    protected String sourceString;
    protected boolean mKeepPlaying;
    protected BufferFeedListener bfl;
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
    public synchronized void setBufferFeedListener(BufferFeedListener bfl){
        this.bfl=bfl;
    }
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


    public synchronized long getCurrentFrame(){
        if (mAudioTrack!=null){
            if (isPlaying()||isPaused()) return mAudioTrack.getPlaybackHeadPosition();
            else return 0;
        }
        else return 0;
    }

    public synchronized void playAudio() {
        Log.d(LOG_TAG, "PlayAudio called.");
        Log.d(LOG_TAG, "mAudioTrack is null:"+(mAudioTrack==null));
        if (isPlaying()) {
            Log.d(LOG_TAG,"isPlaying");
            return;
        }
        if (isPaused()){
            Log.d(LOG_TAG,"isPaused");
            mAudioTrack.play();
            return;
        }
        Log.d(LOG_TAG,"Normal Startup");

        getInfoFromFile();
        initializeAudio();

        mKeepPlaying = true;
        mAudioTrack.flush();
        mAudioTrack.play();
        // Setting thread feeding the audio samples to the audio hardware.
        // (Assumes mChannels = 1 or 2).
        mPlayThread = new PlayThread (this) ;
        mPlayThread.start();
    }

    public synchronized void seekTo(float time){
        //TODO Implementation!
    }

    public synchronized void pause() {
        Log.i(LOG_TAG, "Ckecking if able to pause...");
        if (isPlaying()) {
            mAudioTrack.pause();
            Log.i(LOG_TAG, "Pausing...");
            // mAudioTrack.write() should block if it cannot write.
        }
    }

    public synchronized void stop() {
        Log.i(LOG_TAG, "Ckecking if able to stop...");
        if (mAudioTrack!=null){
        if (isPlaying() || isPaused()) {
            Log.i(LOG_TAG,"Stopping...");
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
