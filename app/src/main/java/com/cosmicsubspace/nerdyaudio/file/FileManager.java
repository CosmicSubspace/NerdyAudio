//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.cosmicsubspace.nerdyaudio.file;

import android.content.Context;
import android.os.Environment;

import com.cosmicsubspace.nerdyaudio.helper.Log2;
import com.cosmicsubspace.nerdyaudio.interfaces.CompletionListener;
import com.cosmicsubspace.nerdyaudio.interfaces.FileListReturnListener;
import com.cosmicsubspace.nerdyaudio.interfaces.ProgressStringListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class FileManager implements FileListReturnListener {
    static final String LOG_TAG = "CS_AFN";

    public static final int GROUPING_NONE = 1;
    public static final int GROUPING_DIRECTORY = 2;
    public static final int GROUPING_TITLE_FIRST = 3;
    public static final int GROUPING_ARTIST = 4;
    public static final int GROUPING_ALBUM = 5;

    public static final int SORTING_NONE = 10;
    public static final int SORTING_TITLE = 11;
    public static final int SORTING_ARTIST = 12;
    public static final int SORTING_ALBUM = 13;

    static FileManager inst;

    ProgressStringListener psl;
    CompletionListener cl;
    File directory;

    boolean scanning = false;
    //ArrayList<String> fileList=new ArrayList<String>();

    ArrayList<MusicInformation> musics = new ArrayList<>();

    ArrayList<MusicGroup> grouped = new ArrayList<>();

    public static FileManager getInstance() {
        if (inst == null) inst = new FileManager();
        return inst;
    }

    private FileManager() {
        directory = Environment.getExternalStorageDirectory();
    }

    public void setDirectory(File f) {
        this.directory = f;
    }

    public void setProgressStringListener(ProgressStringListener psl) {
        this.psl = psl;
    }

    public void discover(String path, CompletionListener cl, Context c) {
        if (scanning) return;
        scanning = true;
        FileLister fl = new FileLister(path, psl, this, c);
        this.cl = cl;
        fl.start();
    }

    public String getCurrentDirectoryPath() {
        return directory.getAbsolutePath();
    }

    public File getCurrentDirectory() {
        return directory;
    }

    public ArrayList<MusicInformation> getMusics() {
        return this.musics;
    }


    public boolean isScanning() {
        return scanning;
    }


    private void regroup() {
        setGrouping(lastGrouping);
    }

    boolean grouping = false;
    int lastGrouping = GROUPING_NONE;

    public void setGrouping(int mode) {

        lastGrouping = mode;

        if (mode == GROUPING_NONE) {
            grouping = false;
            return;
        }

        grouping = true;
        grouped.clear();
        for (MusicInformation mi : musics) {
            String groupIdent = "";
            if (mode == GROUPING_TITLE_FIRST)
                groupIdent = Character.toString(mi.getTitle().charAt(0)).toUpperCase();
            else if (mode == GROUPING_ALBUM) groupIdent = mi.getAlbum();
            else if (mode == GROUPING_DIRECTORY) groupIdent = mi.getFolderName();
            else if (mode == GROUPING_ARTIST) groupIdent = mi.getArtist();


            boolean success = false;
            for (MusicGroup group : grouped) { //Try to put this in one of the groups.
                if (group.getIdentity().equals(groupIdent)) {
                    success = true;
                    group.addMusic(mi);
                    break;
                }
            }

            if (!success) {//No group.. Create a new one!
                MusicGroup newGroup = new MusicGroup(groupIdent);
                newGroup.addMusic(mi);
                grouped.add(newGroup);
            }

        }
    }

    public void setSorting(int mode) {
        switch (mode) {
            case SORTING_ALBUM:
                Collections.sort(musics, new MusicInformation.AlbumComparator());
                regroup();
                break;
            case SORTING_ARTIST:
                Collections.sort(musics, new MusicInformation.ArtistComparator());
                regroup();
                break;
            case SORTING_NONE:
                //TODO : Wat do?
                break;
            case SORTING_TITLE:
                Collections.sort(musics, new MusicInformation.TitleComparator());
                regroup();
                break;
        }
    }

    public ArrayList updateMusicList() {
        ArrayList res = new ArrayList<>();
        if (!grouping) { //Don't group....
            res = new ArrayList(musics);
        } else {
            for (MusicGroup group : grouped) {
                res.add(group);

                if (group.expanded()) {
                    res.addAll(group.getElements());
                }
            }
        }

        return res;
    }

    @Override
    public void onReturn(ArrayList<MusicInformation> musics) {
        scanning = false;
        this.musics = musics;
        if (cl != null) cl.onComplete("");
        Log2.log(1, this, "Files List Returned!");
    }
}