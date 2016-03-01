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
import java.util.Collection;
import java.util.Collections;
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

    //int currentlyCachingIndex=-1;

    static QueueManager inst;

    //int currentMusicIndex = 0;

    MusicInformation currentlyPlaying, currentlyCaching;


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
            current=new MusicInformation(list.get(i),ma);
            addMusic(current);
        }
        //prepareWaveform();
    }

    public ArrayList<MusicInformation> getQueue() {
        return queue;
    }

    public void addMusic(MusicInformation mi) {
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

    public void play() { //plays the CurrentlyPlaying.
        if (queue.size()==0) return;
        Log.d(LOG_TAG, "Playing file.");
        if (currentlyPlaying==null){ //first play
            setCurrentMusicIndex(0);
        }

        currentlyPlaying.setPlaying(true);
        notifyNewSongListeners(currentlyPlaying);
        notifyMusicInformationUpdateListeners(currentlyPlayingIndex());


        vb.clear();
        ap.killThread();
        ap.release();
        ap.setFileStr(currentlyPlaying.getFilepath());
        ap.playAudio();
        if (currentlyPlaying.updateReadyness(ma).isReady()) {
            Waveform.getInstance().loadFromFile(currentlyPlaying.getFilepath(), 1.0, ma);
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
        setCurrentMusicIndex(index);
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
        setCurrentMusicIndex(currentlyPlayingIndex() + 1);
    }

    private void previousFile() {
        setCurrentMusicIndex(currentlyPlayingIndex()-1);
    }


    private void setCurrentMusicIndex(int i) {
        if (currentlyPlaying!=null) currentlyPlaying.setPlaying(false);
        notifyMusicInformationUpdateListeners(currentlyPlayingIndex());
        if (i < 0) i = 0;
        if (i>=queue.size()){
            i=queue.size()-1;
        }
        currentlyPlaying=queue.get(i);
    }
    private int getIndex(MusicInformation mi){
        int idx=this.queue.indexOf(mi);
        if (idx<0) idx=0;
        return idx;
    }
    private int currentlyPlayingIndex(){
        return getIndex(currentlyPlaying);
    }


    @Override
    public void onComplete(String s) {
        Log.d(LOG_TAG, "Playback Finished: " + s);
        playNextFile();
    }

    private void prepareWaveform() {
        if (currentlyCaching==null) {
            //Songs that are in front of the currently playing gets priority.
            for (int i = currentlyPlayingIndex(); i < queue.size(); i++) {
                if (!queue.get(i).isReady()) {
                    Log.i(LOG_TAG, "Starting Calculation of: " + queue.get(i).getFilepath());
                    currentlyCaching=queue.get(i);
                    currentlyCaching.setCaching(true);
                    notifyMusicInformationUpdateListeners(i);
                    Waveform.calculateIfDoesntExist(queue.get(i).getFilepath(), 1, ma, this, this);
                    break;
                }
            }
            //But the ones in the back should be calculated too.
            for (int i = 0; i < queue.size(); i++) {
                if (!queue.get(i).isReady()) {
                    Log.i(LOG_TAG, "Starting Calculation of: " + queue.get(i).getFilepath());
                    currentlyCaching=queue.get(i);
                    currentlyCaching.setCaching(true);
                    notifyMusicInformationUpdateListeners(i);
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
    }
    public void reverseQueue() {
        ArrayList<MusicInformation> newQueue = new ArrayList<>(queue.size());
        for (int i = 0; i < queue.size(); i++) {
            newQueue.add(queue.get(queue.size()-i-1));
        }
        queue = newQueue;
    }
    public void sortByTitle() {
        Collections.sort(queue,new MusicInformation.TitleComparator());
    }
    public void sortByArtist() {
        Collections.sort(queue, new MusicInformation.ArtistComparator());
    }



    public void move(int from, int to){
        MusicInformation target=queue.get(from);
        queue.remove(from);
        queue.add(to, target);
        /*
        int origIdx=currentMusicIndex;

        currentMusicIndex=shiftIndex(currentMusicIndex,from,to);
        currentlyCachingIndex=shiftIndex(currentlyCachingIndex,from,to);

        Log.i(LOG_TAG,"Shift from "+from+" to "+to);
        Log.i(LOG_TAG,"CurrentMusicIndex Shift from "+origIdx+" to "+currentMusicIndex);*/
        notifyMusicInformationUpdateListeners(-1);
    }

    public void remove(int index){
        if (queue.get(index)==currentlyPlaying){
            playFile(currentlyPlayingIndex()+1);
        }
        if (queue.get(index)==currentlyCaching){

        }
        queue.remove(index);
        notifyMusicInformationUpdateListeners(-1);
    }

    private int shiftIndex(int index, int from, int to){
        if (index<from && index<to) {
            //do nothing
        }else if (index>from && index>to) {
            //do nothing
        }else if (index<from && index>to) {
            index++;
        }else if (index>from && index<to) {
            index--;
        }else if (index==from) {
            index=to;
        }else if (index<from && index==to) {
            index++;
        }else if (index>from && index==to) {
            index--;
        }
        return index;
    }


    @Override
    public void report(long l) {
        notifyProgressStringListeners( "Caching: " + currentlyCaching.getTitle() + " (" + l + " Samples)");
    }

    @Override
    public void onReturn(Waveform wf) {
        currentlyCaching.setCaching(false);
        currentlyCaching.setReady(true);
        notifyMusicInformationUpdateListeners(getIndex(currentlyCaching));
        currentlyCaching = null;
        prepareWaveform();
        notifyProgressStringListeners("Caching Complete.");
    }
}
