//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.chancorp.audiofornerds.file;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

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

    public boolean hasArt(){
        return hasArt;
    }

    String filepath;
    String title;
    String artist;
    boolean hasArt;
    public MusicInformation(String source){
        this.filepath=source;
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();
        mmr.setDataSource(source);
        title=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if (title==null || title.equals("")){
            title=new File(source).getName();
        }
        artist=mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if (artist==null || artist.equals("")){
            artist="(No Artist Data)";
        }
        if (mmr.getEmbeddedPicture()==null) hasArt=false;
        else hasArt=true;
    }
    public byte[] getArtByteArray(){
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();
        mmr.setDataSource(filepath);
        return mmr.getEmbeddedPicture();
    }
}
