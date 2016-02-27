//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.chancorp.audiofornerds.file;

import android.util.Log;

import com.chancorp.audiofornerds.audio.AudioPlayer;
import com.chancorp.audiofornerds.audio.VisualizationBuffer;
import com.chancorp.audiofornerds.audio.Waveform;
import com.chancorp.audiofornerds.helper.ErrorLogger;
import com.chancorp.audiofornerds.interfaces.CompletionListener;
import com.chancorp.audiofornerds.interfaces.MusicInformationUpdateListener;
import com.chancorp.audiofornerds.interfaces.NewSongListener;
import com.chancorp.audiofornerds.interfaces.ProgressStringListener;
import com.chancorp.audiofornerds.interfaces.SampleProgressListener;
import com.chancorp.audiofornerds.interfaces.WaveformReturnListener;
import com.chancorp.audiofornerds.ui.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class QueueManager implements CompletionListener, SampleProgressListener, WaveformReturnListener {
    static final String LOG_TAG = "CS_AFN";



    AudioPlayer ap;
    FileManager fm;
    VisualizationBuffer vb;

    MainActivity ma;

    ArrayList<MusicInformation> queue = new ArrayList<>();

    ArrayList<ProgressStringListener> psl=new ArrayList<>();
    ArrayList<NewSongListener> nsl=new ArrayList<>();
    ArrayList<MusicInformationUpdateListener> miul=new ArrayList<>();

    int currentlyCachingIndex=-1;

    static QueueManager inst;

    int currentMusicIndex = 0;


    public static QueueManager getInstance() {
        if (inst == null) inst = new QueueManager();
        return inst;

    }

    protected QueueManager() {
        ap = AudioPlayer.getInstance();
        ap.setCompletionListener(this);
        fm = FileManager.getInstance();
        vb = VisualizationBuffer.getInstance();
    }

    public void addProgressStringListener(ProgressStringListener psl){
        this.psl.add(psl);
    }

    protected void notifyProgressStringListeners(String progress){
        for (ProgressStringListener psl:this.psl){
            psl.report(progress);
        }
    }

    public void addNewSongListener(NewSongListener nsl){
        this.nsl.add(nsl);
    }

    protected void notifyNewSongListeners(MusicInformation newSong){
        for (NewSongListener nsl:this.nsl){
            nsl.newSong(newSong);
        }
    }

    public void addMusicInformationUpdateListener(MusicInformationUpdateListener miul){
        this.miul.add(miul);
    }
    public void removeMusicInformationUpdateListener(MusicInformationUpdateListener miul){
        this.miul.remove(miul);
    }
    private void notifyMusicInformationUpdateListeners(int index){
        for (MusicInformationUpdateListener miul:this.miul){
            miul.musicInformationUpdated(index);
        }
    }

    public void parseQueueFromFile(File file) {
        ArrayList<String> files = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                files.add(line);
            }
            br.close();
        } catch (IOException e) {
            ErrorLogger.log(e);
        }
        getQueueFromFileList(files);
    }

    public void getQueueFromFileList(ArrayList<String> list) {
        MusicInformation current;
        for (int i = 0; i < list.size(); i++) {
            current=new MusicInformation(list.get(i));
            current.updateReadyness(ma);
            addMusic(current);
        }
        //prepareWaveform();
    }

    public ArrayList<MusicInformation> getQueue() {
        return queue;
    }

    public void addMusic(MusicInformation mi) {
        mi.updateReadyness(ma);
        queue.add(mi);
        prepareWaveform();
    }
/*
    public void addMusicWithoutWaveformPreparation(MusicInformation mi) {
        queue.add(mi);
    }*/

    public void passActivity(MainActivity ma) {
        this.ma = ma;
    }

    public void play() {
        Log.d(LOG_TAG, "Playing file.");

        queue.get(currentMusicIndex).setPlaying(true);
        notifyNewSongListeners(queue.get(currentMusicIndex));
        notifyMusicInformationUpdateListeners(currentMusicIndex);


        vb.clear();
        ap.killThread();
        ap.release();
        ap.setFileStr(queue.get(currentMusicIndex).getFilepath());
        ap.playAudio();
        if (queue.get(currentMusicIndex).isReady()) {
            Waveform.getInstance().loadFromFile(queue.get(currentMusicIndex).getFilepath(), 1.0, ma);
        }
        else Waveform.getInstance().loadBlank();
    }
    public void playFile(MusicInformation mi){
        for (int i = 0; i < queue.size(); i++) {
            if(queue.get(i).equals(mi)){
                playFile(i);
                break;
            }
        }
    }
    public void playFile(int index){
        setCurrentMusic(index);
        play();
    }

    public void playNextFile() {
        Log.d(LOG_TAG, "Playing next file.");
        nextFile();
        play();
    }

    public void playPreviousFile() {
        Log.d(LOG_TAG, "Playing Previous file.");
        previousFile();
        play();
    }

    private void nextFile() {
        setCurrentMusic(currentMusicIndex+1);
    }

    private void previousFile() {
        setCurrentMusic(currentMusicIndex-1);

    }

    private void firstFile() {
        setCurrentMusic(0);
    }

    private void setCurrentMusic(int i) {
        queue.get(currentMusicIndex).setPlaying(false);
        notifyMusicInformationUpdateListeners(currentMusicIndex);
        if (i < 0) i = 0;
        if (i>=queue.size()){
            i=queue.size()-1;
        }
        currentMusicIndex=i;
    }


    @Override
    public void onComplete(String s) {
        Log.d(LOG_TAG, "Playback Finished: " + s);
        playNextFile();
    }

    private void prepareWaveform() {
        if (currentlyCachingIndex <0) {
            for (int i = currentMusicIndex; i < queue.size(); i++) {
                if (!queue.get(i).isReady()) {//TODO Thousands of file I/O everythime this method is called. Fix that.
                    Log.i(LOG_TAG, "Starting Calculation of: " + queue.get(i).getFilepath());
                    currentlyCachingIndex=i;
                    queue.get(currentlyCachingIndex).setCaching(true);
                    notifyMusicInformationUpdateListeners(currentlyCachingIndex);
                    Waveform.calculateIfDoesntExist(queue.get(i).getFilepath(), 1, ma, this, this);
                    break;
                }
            }
        }
    }

    public void shuffleQueue() {
        ArrayList<MusicInformation> newQueue = new ArrayList<>(queue.size());
        Random rand = new Random();
        while (queue.size() > 0) {

            int idx = rand.nextInt(queue.size());
            newQueue.add(queue.get(idx));
            queue.remove(idx);
        }

        queue = newQueue;

        firstFile();
    }


    public MusicInformation getCurrentMusic() {
        try {
            return queue.get(currentMusicIndex);
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public void report(long l) {
        notifyProgressStringListeners( "Caching: " + queue.get(currentlyCachingIndex).getTitle() + " (" + l + " Samples)");
    }

    @Override
    public void onReturn(Waveform wf) {
        queue.get(currentlyCachingIndex).setCaching(false);
        queue.get(currentlyCachingIndex).setReady(true);
        notifyMusicInformationUpdateListeners(currentlyCachingIndex);
        currentlyCachingIndex = -1;
        prepareWaveform();
        notifyProgressStringListeners("Caching Complete.");
    }
}
