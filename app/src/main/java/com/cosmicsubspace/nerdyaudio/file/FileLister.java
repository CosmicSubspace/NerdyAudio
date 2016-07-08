//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.cosmicsubspace.nerdyaudio.file;

import android.content.Context;

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
    private final static String LOG_TAG = "CS_AFN";
    String path;
    ArrayList<MusicInformation> musics = new ArrayList<>();
    FileListReturnListener flrl;
    ProgressStringListener psl;
    Context c;

    public FileLister(String path, ProgressStringListener psl, FileListReturnListener flrl, Context c) {
        this.path = path;
        this.flrl = flrl;
        this.psl = psl;
        this.c = c;
    }

    public void run() {
        discover(path);
        if (flrl != null) flrl.onReturn(musics);
    }

    public void discover(String path) {
        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                discover(f.getAbsolutePath());
                if (psl != null) psl.report("Scanned Directory:" + f.getAbsolutePath());
            } else {
                try {
                    if (checkFileValidity(f.getAbsolutePath()))
                        musics.add(new MusicInformation(f.getCanonicalPath(), c));
                } catch (IOException e) {
                    Log2.log(4, this, "Error while parsing canonical path.");
                    ErrorLogger.log(e);

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