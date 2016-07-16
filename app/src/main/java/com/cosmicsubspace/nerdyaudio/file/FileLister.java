//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.cosmicsubspace.nerdyaudio.file;

import android.content.Context;

import com.cosmicsubspace.nerdyaudio.interfaces.CompletionListener;
import com.ringdroid.soundfile.SoundFile;
import com.cosmicsubspace.nerdyaudio.helper.ErrorLogger;
import com.cosmicsubspace.nerdyaudio.helper.Log2;
import com.cosmicsubspace.nerdyaudio.interfaces.FileListReturnListener;
import com.cosmicsubspace.nerdyaudio.interfaces.ProgressStringListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class FileLister extends Thread {
    public interface DiscoveryListener{
        public void musicDiscovered(MusicInformation mi);
    }

    String path;
    ArrayList<MusicInformation> musics = new ArrayList<>();

    ArrayList<ProgressStringListener> psls=new ArrayList<>();
    ArrayList<DiscoveryListener> dls=new ArrayList<>();
    ArrayList<CompletionListener> cls=new ArrayList<>();
    Context c;

    public FileLister(String path, Context c) {
        this.path = path;

        this.c = c;
    }
    public void addProgressStringListener(ProgressStringListener psl){
        psls.add(psl);
    }
    public void addDiscoveryListener(DiscoveryListener dl){
        dls.add(dl);
    }
    public void addCompletionListener(CompletionListener cl){
        cls.add(cl);
    }
    public void run() {
        discover(path);
        for (CompletionListener cl:cls) cl.onComplete("");
    }

    public void discover(String path) {
        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                discover(f.getAbsolutePath());
                for (ProgressStringListener psl:psls) psl.report("Scanned Directory:" + f.getAbsolutePath());
            } else {
                try {
                    if (checkFileValidity(f.getAbsolutePath())) {
                        MusicInformation newMusic=new MusicInformation(f.getCanonicalPath(), c);
                        for (DiscoveryListener dl:dls) dl.musicDiscovered(newMusic);
                        musics.add(newMusic);
                    }
                } catch (IOException e) {
                    Log2.log(4, this, "Error while parsing canonical path.");
                    Log2.log(e);

                }
            }
        }
    }

    public boolean checkFileValidity(String s) {
        String[] components = s.split("\\.");
        if (components.length < 2) {
            return false;
        }
        if (!Arrays.asList(SoundFile.getSupportedExtensions()).contains(components[components.length - 1])) {
            return false;
        }
        return true;
    }
}