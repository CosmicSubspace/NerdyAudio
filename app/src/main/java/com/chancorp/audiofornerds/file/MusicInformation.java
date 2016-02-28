//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.chancorp.audiofornerds.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import com.chancorp.audiofornerds.audio.Waveform;

import java.io.File;

public class MusicInformation {

    public String getFilepath() {
        return filepath;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public boolean hasArt() {
        return hasArt;
    }


    public int getStatus() {
        if (isReady) {
            if (isPlaying) {
                return PLAYING;
            } else {
                return READY;
            }
        } else if (isCaching){
            if (isPlaying) {
                return PLAYING_WHILE_CACHING;
            } else {
                return CACHING;
            }
        } else {
            if (isPlaying) {
                return PLAYING_WITHOUT_DATA;
            } else {
                return NOT_READY;
            }
        }
    }

    public static final int NOT_READY = 613;
    public static final int READY = 135;
    public static final int PLAYING = 619;
    public static final int PLAYING_WITHOUT_DATA = 713;
    public static final int CACHING=123;
    public static final int PLAYING_WHILE_CACHING=691;

    String filepath;
    String title;
    String artist;
    boolean hasArt;
    boolean isReady = false, isPlaying = false, isCaching=false;


    public MusicInformation(String source) {
        this.filepath = source;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(source);
        title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if (title == null || title.equals("")) {
            title = new File(source).getName();
        }
        artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if (artist == null || artist.equals("")) {
            artist = "(No Artist Data)";
        }
        if (mmr.getEmbeddedPicture() == null) hasArt = false;
        else hasArt = true;
    }

    public byte[] getArtByteArray() {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(filepath);
        return mmr.getEmbeddedPicture();
    }

    public void updateReadyness(Context c) {
        if (Waveform.checkExistance(filepath, 1, c)) isReady=true;
        else isReady=false;
    }
    public void setReady(boolean ready){
        this.isReady=ready;
    }
    public void setPlaying(boolean playing){
        this.isPlaying=playing;
    }
    public void setCaching(boolean caching){
        this.isCaching=caching;
    }
    public boolean isReady(){
        return this.isReady;
    }
    public boolean isPlaying(){
        return this.isPlaying;
    }
    public boolean isCaching(){
        return this.isCaching;
    }
}
