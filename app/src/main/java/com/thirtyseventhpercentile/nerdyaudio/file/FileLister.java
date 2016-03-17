//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.file;

import android.content.Context;

import com.thirtyseventhpercentile.nerdyaudio.interfaces.FileListReturnListener;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.ProgressStringListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class FileLister extends Thread{
    String path;
    ArrayList<MusicInformation> musics=new ArrayList<>();
    FileListReturnListener flrl;
    ProgressStringListener psl;
    Context c;
    public static String[] getSupportedExtensions() {
        return new String[] {"mp3", "wav", "3gpp", "3gp", "amr", "aac", "m4a", "ogg"};
    }
    public FileLister(String path, ProgressStringListener psl , FileListReturnListener flrl, Context c){
        this.path=path;
        this.flrl=flrl;
        this.psl=psl;
        this.c=c;
    }
    public void run(){
        discover(path);
        if (flrl!=null) flrl.onReturn(musics);
    }
    public void discover(String path){
        File root = new File( path );
        File[] list = root.listFiles();

        if (list == null) return;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
                discover(f.getAbsolutePath());
                if (psl!=null) psl.report("Scanned Directory:" + f.getAbsolutePath());
            }
            else {
                if (checkFileValidity(f.getAbsolutePath())) musics.add(new MusicInformation(f.getAbsolutePath(),c));
            }
        }
    }
    public boolean checkFileValidity(String s){
        String[] components = s.split("\\.");
        if (components.length < 2) {
            return false;
        }
        if (!Arrays.asList(getSupportedExtensions()).contains(components[components.length - 1])) {
            return false;
        }
        return true;
    }
}