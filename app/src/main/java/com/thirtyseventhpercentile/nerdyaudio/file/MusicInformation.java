//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.file;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import com.thirtyseventhpercentile.nerdyaudio.audio.Waveform;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.MusicListDisplayable;

import java.io.File;
import java.util.Comparator;

public class MusicInformation implements MusicListDisplayable {
    static class AlbumComparator implements Comparator<MusicInformation> {
        @Override
        public int compare(MusicInformation a, MusicInformation b) {
            return a.album.compareToIgnoreCase(b.album);
        }

        @Override
        public boolean equals(Object o) {
            return this.equals(o);
        }
    }

    static class TitleComparator implements Comparator<MusicInformation> {
        @Override
        public int compare(MusicInformation a, MusicInformation b) {
            return a.title.compareToIgnoreCase(b.title);
        }

        @Override
        public boolean equals(Object o) {
            return this.equals(o);
        }
    }

    static class ArtistComparator implements Comparator<MusicInformation> {
        @Override
        public int compare(MusicInformation a, MusicInformation b) {
            return a.artist.compareToIgnoreCase(b.artist);
        }

        @Override
        public boolean equals(Object o) {
            return this.equals(o);
        }
    }

    public String getFilepath() {
        return filepath;
    }


    @Override
    public boolean expanded() {
        return false;
    }

    @Override
    public boolean isAGroup() {
        return false;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getSubTitle() {
        return artist;
    }


    public String getArtist() {
        return artist;
    }

    public boolean hasArt() {
        return hasArt;
    }

    public String getAlbum() {
        return album;
    }


    public String getFolderName() {
        return folderName;
    }


    public int getStatus() {
        if (isReady) {
            if (isPlaying) {
                return PLAYING;
            } else {
                return READY;
            }
        } else if (isCaching) {
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
    public static final int CACHING = 123;
    public static final int PLAYING_WHILE_CACHING = 691;

    String filepath;
    String title;
    String artist;


    String album;
    String folderName;
    boolean hasArt;
    boolean isReady = false, isPlaying = false, isCaching = false;

    public MusicInformation(MusicInformation mi) {
        this.filepath = mi.filepath;
        this.title = mi.title;
        this.artist = mi.artist;
        this.hasArt = mi.hasArt;
        this.isReady = mi.isReady;
    }

    public MusicInformation(String source, Context c) {
        this.filepath = source;

        String[] split = source.split("\\/");
        try {
            this.folderName = split[split.length - 2];
        } catch (ArrayIndexOutOfBoundsException e) {
            this.folderName = "(No Folder?)";
        }

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
        album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        if (album == null || album.equals("")) {
            album = "(No Album Data)";
        }

        if (mmr.getEmbeddedPicture() == null) hasArt = false; //TODO : this causes lag.
        else hasArt = true;

        updateReadyness(c);
    }

    public byte[] getArtByteArray() {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(filepath);
        return mmr.getEmbeddedPicture();
    }

    public MusicInformation updateReadyness(Context c) {
        if (Waveform.checkExistance(filepath, 1, c)) isReady = true;
        else isReady = false;
        return this;
    }

    public void setReady(boolean ready) {
        this.isReady = ready;
    }

    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
    }

    public void setCaching(boolean caching) {
        this.isCaching = caching;
    }

    public boolean isReady() {
        return this.isReady;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public boolean isCaching() {
        return this.isCaching;
    }
}
