package com.chancorp.audiofornerds.file;

import com.chancorp.audiofornerds.interfaces.FileListReturnListener;
import com.chancorp.audiofornerds.interfaces.ProgressStringListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Chan on 2015-12-17.
 */
public class FileLister extends Thread{
    String path;
    ArrayList<MusicInformation> musics=new ArrayList<>();
    FileListReturnListener flrl;
    ProgressStringListener psl;
    public static String[] getSupportedExtensions() {
        return new String[] {"mp3", "wav", "3gpp", "3gp", "amr", "aac", "m4a", "ogg"};
    }
    public FileLister(String path, ProgressStringListener psl , FileListReturnListener flrl){
        this.path=path;
        this.flrl=flrl;
        this.psl=psl;
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
                if (checkFileValidity(f.getAbsolutePath())) musics.add(new MusicInformation(f.getAbsolutePath()));
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