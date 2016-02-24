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
import com.chancorp.audiofornerds.interfaces.NewSongListener;
import com.chancorp.audiofornerds.interfaces.ProgressStringListener;
import com.chancorp.audiofornerds.interfaces.SampleProgressListener;
import com.chancorp.audiofornerds.interfaces.WaveformReturnListener;
import com.chancorp.audiofornerds.ui.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
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

    MusicInformation currentlyCaching = null;

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

    public void getQueueFromFileList(String[] list) {
        for (int i = 0; i < list.length; i++) {
            addMusicWithoutWaveformPreparation(new MusicInformation(list[i]));
        }
        prepareWaveform();
    }

    public void getQueueFromFileList(ArrayList<String> list) {
        for (int i = 0; i < list.size(); i++) {
            addMusicWithoutWaveformPreparation(new MusicInformation(list.get(i)));
        }
        prepareWaveform();
    }

    public ArrayList<MusicInformation> getQueue() {
        return queue;
    }

    public void addMusic(MusicInformation mi) {
        queue.add(mi);
        prepareWaveform();
    }

    public void addMusicWithoutWaveformPreparation(MusicInformation mi) {
        queue.add(mi);
    }

    public void passActivity(MainActivity ma) {
        this.ma = ma;
    }

    public void playFile() {
        Log.d(LOG_TAG, "Playing file.");
        notifyNewSongListeners(queue.get(currentMusicIndex));
        vb.clear();
        ap.killThread();
        ap.release();
        ap.setFileStr(queue.get(currentMusicIndex).getFilepath());
        ap.playAudio();
        if (Waveform.checkExistance(queue.get(currentMusicIndex).getFilepath(), 1.0, ma))
            Waveform.getInstance().loadFromFile(queue.get(currentMusicIndex).getFilepath(), 1.0, ma);
        else Waveform.getInstance().loadBlank();
        updateUI();


    }

    public void playNextFile() {
        Log.d(LOG_TAG, "Playing next file.");
        nextFile();
        playFile();
    }

    public void playPreviousFile() {
        Log.d(LOG_TAG, "Playing Previous file.");
        previousFile();
        playFile();
    }

    private void nextFile() {
        currentMusicIndex++;
        if (currentMusicIndex >= queue.size()) {
            currentMusicIndex = 0;
        }

    }

    private void previousFile() {
        currentMusicIndex--;
        if (currentMusicIndex < 0) currentMusicIndex = 0;

    }

    private void firstFile() {
        currentMusicIndex = 0;
    }

    @Override
    public void onComplete(String s) {
        Log.d(LOG_TAG, "Playback Finished: " + s);
        playNextFile();
    }

    private void updateUI() {

        if (ma != null) ma.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ma.updateUI();
            }
        });
    }

    public void prepareWaveform() {
        if (currentlyCaching == null) {
            for (int i = currentMusicIndex; i < queue.size(); i++) {
                if (!Waveform.checkExistance(queue.get(i).getFilepath(), 1, ma)) {//TODO Thousands of file I/O everythime this method is called. Fix that.
                    Log.i(LOG_TAG, "Starting Calculation of: " + queue.get(i).getFilepath());
                    currentlyCaching = queue.get(i);
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
        notifyProgressStringListeners( "Caching: " + currentlyCaching.getTitle() + " (" + l + " Samples)");
    }

    @Override
    public void onReturn(Waveform wf) {
        currentlyCaching = null;
        prepareWaveform();
        notifyProgressStringListeners("Caching Complete.");
    }
}
