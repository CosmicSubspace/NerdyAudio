//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.file;

import android.content.Context;
import android.util.Log;

import com.thirtyseventhpercentile.nerdyaudio.audio.AudioPlayer;
import com.thirtyseventhpercentile.nerdyaudio.audio.VisualizationBuffer;
import com.thirtyseventhpercentile.nerdyaudio.audio.Waveform;
import com.thirtyseventhpercentile.nerdyaudio.helper.ErrorLogger;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.CompletionListener;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.QueueElementUpdateListener;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.QueueListener;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.ProgressStringListener;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.SampleProgressListener;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.WaveformReturnListener;
import com.thirtyseventhpercentile.nerdyaudio.ui.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

//TODO : Seperate QueueManager and PlayManager or something.
public class QueueManager implements CompletionListener, SampleProgressListener, WaveformReturnListener {
    static final String LOG_TAG = "CS_AFN";


    AudioPlayer ap;
    FileManager fm;
    VisualizationBuffer vb;

    Context ctxt;

    ArrayList<MusicInformation> queue = new ArrayList<>();

    ArrayList<ProgressStringListener> psl = new ArrayList<>();
    ArrayList<QueueListener> nsl = new ArrayList<>();
    ArrayList<QueueElementUpdateListener> miul = new ArrayList<>();

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

    public void addProgressStringListener(ProgressStringListener psl) {
        this.psl.add(psl);
    }

    protected void notifyProgressStringListeners(String progress) {
        for (ProgressStringListener psl : this.psl) {
            psl.report(progress);
        }
    }

    public void addQueueListener(QueueListener nsl) {
        this.nsl.add(nsl);
    }

    protected void notifyNewSongListeners(MusicInformation newSong) {
        for (QueueListener nsl : this.nsl) {
            nsl.newSong(newSong);
        }
    }

    protected void notifyPlay() {
        for (QueueListener nsl : this.nsl) {
            nsl.playbackStarted();
        }
    }

    protected void notifyStop() {
        for (QueueListener nsl : this.nsl) {
            nsl.playbackStopped();
        }
    }

    protected void notifyNext() {
        for (QueueListener nsl : this.nsl) {
            nsl.nextSong();
        }
    }

    protected void notifyPrevious() {
        for (QueueListener nsl : this.nsl) {
            nsl.previousSong();
        }
    }



    public void addMusicInformationUpdateListener(QueueElementUpdateListener miul) {
        this.miul.add(miul);
    }

    public void removeMusicInformationUpdateListener(QueueElementUpdateListener miul) {
        this.miul.remove(miul);
    }

    private void notifyMusicInformationUpdateListeners(int index) {
        for (QueueElementUpdateListener miul : this.miul) {
            miul.elementUpdated(index);
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
            current = new MusicInformation(list.get(i), ctxt);
            addMusic(current);
        }
        prepareWaveform(); //why was this commented?
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

    public void passContext(Context ma) {
        this.ctxt = ma;
    }

    private void newPlay() { //plays the CurrentlyPlaying.
        if (queue.size() == 0) return;
        Log.d(LOG_TAG, "Playing file.");
        if (currentlyPlaying == null) { //first newPlay
            setCurrentMusicIndex(0);
        }

        currentlyPlaying.setPlaying(true);
        notifyNewSongListeners(currentlyPlaying);
        notifyMusicInformationUpdateListeners(currentlyPlayingIndex());


        vb.reset();
        ap.killThread();
        ap.release();
        ap.setFileStr(currentlyPlaying.getFilepath());
        ap.playAudio();
        if (currentlyPlaying.updateReadyness(ctxt).isReady()) {
            Waveform.getInstance().loadFromFile(currentlyPlaying.getFilepath(), 1.0, ctxt);
        } else Waveform.getInstance().loadBlank();
    }

    public void playFile(MusicInformation mi) {
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).equals(mi)) {
                playFile(i);
                break;
            }
        }
    }

    public void playCurrent(){
        if (ap != null) {
            if (ap.isPaused()) {
                ap.playAudio();
            } else {
                newPlay();
            }
        }

        notifyPlay();
    }

    public void playFile(int index) {
        setCurrentMusicIndex(index);
        newPlay();
        notifyPlay();
    }

    public void playNextFile() {
        Log.d(LOG_TAG, "Playing next file.");
        nextFile();
        notifyNext();
        newPlay();
        notifyPlay();
    }

    public void playPreviousFile() {
        Log.d(LOG_TAG, "Playing Previous file.");
        previousFile();
        notifyPrevious();
        newPlay();
        notifyPlay();
    }

    public void pause(){
        if (ap != null) {
            if (ap.isPlaying()) {
                ap.pause();
            }
        }
        notifyStop();
    }

    private void nextFile() {
        setCurrentMusicIndex(currentlyPlayingIndex() + 1);
    }

    private void previousFile() {
        setCurrentMusicIndex(currentlyPlayingIndex() - 1);
    }


    private void setCurrentMusicIndex(int i) {
        if (currentlyPlaying != null) currentlyPlaying.setPlaying(false);
        notifyMusicInformationUpdateListeners(currentlyPlayingIndex());
        if (i < 0) i = 0;
        if (i >= queue.size()) {
            i = queue.size() - 1;
        }
        currentlyPlaying = queue.get(i);
    }

    private int getIndex(MusicInformation mi) {
        int idx = this.queue.indexOf(mi);
        if (idx < 0) idx = 0;
        return idx;
    }

    private int currentlyPlayingIndex() {
        return getIndex(currentlyPlaying);
    }


    @Override
    public void onComplete(String s) {
        Log.d(LOG_TAG, "Playback Finished: " + s);
        playNextFile();
    }

    private void prepareWaveform() {
        if (currentlyCaching == null) {
            //Songs that are in front of the currently playing gets priority.
            for (int i = currentlyPlayingIndex(); i < queue.size(); i++) {
                if (!queue.get(i).isReady()) {
                    Log.i(LOG_TAG, "Starting Calculation of: " + queue.get(i).getFilepath());
                    currentlyCaching = queue.get(i);
                    currentlyCaching.setCaching(true);
                    notifyMusicInformationUpdateListeners(i);
                    Waveform.calculateIfDoesntExist(queue.get(i).getFilepath(), 1, ctxt, this, this);
                    return;
                }
            }
            //But the ones in the back should be calculated too.
            for (int i = 0; i < queue.size(); i++) {
                if (!queue.get(i).isReady()) {
                    Log.i(LOG_TAG, "Starting Calculation of: " + queue.get(i).getFilepath());
                    currentlyCaching = queue.get(i);
                    currentlyCaching.setCaching(true);
                    notifyMusicInformationUpdateListeners(i);
                    Waveform.calculateIfDoesntExist(queue.get(i).getFilepath(), 1, ctxt, this, this);
                    return;
                }
            }
        }
    }

    public MusicInformation getCurrentlyPlaying(){
        return currentlyPlaying;
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
            newQueue.add(queue.get(queue.size() - i - 1));
        }
        queue = newQueue;
    }

    public void sortByTitle() {
        Collections.sort(queue, new MusicInformation.TitleComparator());

    }

    public void sortByArtist() {
        Collections.sort(queue, new MusicInformation.ArtistComparator());
    }

    public void removeAll(){
        queue.clear();
    }
    public void removeDuplicates(){
        ArrayList<Integer> toBeRemoved=new ArrayList<>();
        for (int i = 0; i < queue.size(); i++) {
            int j=i+1;
            while (j<queue.size()) {
                if (queue.get(i).getFilepath().equals(queue.get(j).getFilepath())){
                    queue.remove(j);
                }else j++;
            }
        }

    }

    public void move(int from, int to) {
        MusicInformation target = queue.get(from);
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

    public void remove(int index) {
        if (queue.get(index) == currentlyPlaying) {
            playFile(currentlyPlayingIndex() + 1);
        }
        if (queue.get(index) == currentlyCaching) {

        }
        queue.remove(index);
        notifyMusicInformationUpdateListeners(-1);
    }

    private int shiftIndex(int index, int from, int to) {
        if (index < from && index < to) {
            //do nothing
        } else if (index > from && index > to) {
            //do nothing
        } else if (index < from && index > to) {
            index++;
        } else if (index > from && index < to) {
            index--;
        } else if (index == from) {
            index = to;
        } else if (index < from && index == to) {
            index++;
        } else if (index > from && index == to) {
            index--;
        }
        return index;
    }

    public static final int ADD = 6422235;
    public static final int SUBTRACT = 6423916;
    public static final int INTERSECT = 27523;

    public void parsePlaylists(Playlist[] playlists, int mode, Context c) {
        queue.clear();
        if (mode == ADD) {
            for (Playlist playlist : playlists) {
                for (String music : playlist.files) {
                    queue.add(new MusicInformation(music, c));
                }
            }
        } else if (mode == SUBTRACT) {
            for (String music : playlists[0].files) {
                queue.add(new MusicInformation(music, c));
            }
            ArrayList<MusicInformation> toBeRemoved = new ArrayList<>();
            for (int i = 1; i < playlists.length; i++) {
                for (String music : playlists[i].files) {
                    for (MusicInformation target : queue) {
                        if (target.getFilepath().equals(music)) {
                            toBeRemoved.add(target);
                        }
                    }
                }
            }
            for (MusicInformation del : toBeRemoved) {
                queue.remove(del);
            }
        } else if (mode == INTERSECT) {
            Playlist base = playlists[0];
            for (int i = 0; i < playlists.length; i++) {
                if (playlists[i].files.length < base.files.length) {
                    base = playlists[i];
                }
            }

            for (String music : base.files) {
                boolean valid=true;
                for (Playlist playlist : playlists) {
                    boolean match = false;
                    for (int i = 0; i < playlist.files.length; i++) {
                        if (playlist.files[i].equals(music)) {
                            match = true;
                            break;
                        }
                    }
                    if (!match){
                        valid=false;
                    }
                }
                if (valid) queue.add(new MusicInformation(music, c));
            }

        }

        prepareWaveform();
    }





/*
    public static final int APPEND=6422235;
    public static final int LOGICAL_AND=6423916;
    public static final int LOGICAL_OR=27523;
    public static final int LOGICAL_XOR=361236;
    public static final int SUBTRACT=6324648;
    public void parsePlaylist(Playlist pl, int mode, Context c){

        switch (mode){
            case AND:
                queue.reset();
                for (int i = 0; i < pl.files.length; i++) {
                    addMusic(new MusicInformation(pl.files[i],c));
                }
                break;
            case OR:
                break;
            case XOR:
                break;
            case NOT:
                break;
        }


    }

    public void parsePlaylists(Playlist[] playlists, int mode, Context c){
        if (mode==LOGICAL_AND||mode==LOGICAL_OR||mode==LOGICAL_XOR){

            List<Set<String>> sets=new ArrayList<Set<String>>();
            for (int i = 0; i < playlists.length; i++) {
                sets.add(new HashSet<String>(Arrays.asList(playlists[i].files)));
            }

            Set<String> res=sets.get(0);
            if (mode==LOGICAL_OR) {
                for (Set<String> set : sets) {
                    res.addAll(set);
                }
            }else if (mode==LOGICAL_AND) {
                for (Set<String> set : sets) {
                    res.retainAll(set);
                }
            }
        }else{

        }
    }
*/

    @Override
    public void report(long l) {
        notifyProgressStringListeners("Caching: " + currentlyCaching.getTitle() + " (" + l + " Samples)");
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

