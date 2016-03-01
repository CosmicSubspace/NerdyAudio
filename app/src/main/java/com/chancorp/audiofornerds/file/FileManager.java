//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.chancorp.audiofornerds.file;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.chancorp.audiofornerds.interfaces.CompletionListener;
import com.chancorp.audiofornerds.interfaces.FileListReturnListener;
import com.chancorp.audiofornerds.interfaces.ProgressStringListener;

import java.io.File;
import java.util.ArrayList;


public class FileManager implements FileListReturnListener{
    static final String LOG_TAG="CS_AFN";

    static FileManager inst;

    ProgressStringListener psl;
    CompletionListener cl;
    File directory;

    boolean scanning=false;
    //ArrayList<String> fileList=new ArrayList<String>();

    ArrayList<MusicInformation> musics=new ArrayList<>();

    public static FileManager getInstance(){
        if (inst==null) inst=new FileManager();
        return inst;
    }

    private FileManager(){
        directory= Environment.getExternalStorageDirectory();
    }

    public void setDirectory(File f){
        this.directory=f;
    }

    public void setProgressStringListener(ProgressStringListener psl){
        this.psl=psl;
    }

    public void discover(String path, CompletionListener cl, Context c){
        if (scanning) return;
        scanning=true;
        FileLister fl=new FileLister(path,psl,this,c);
        this.cl=cl;
        fl.start();
    }
    public String getCurrentDirectoryPath(){
        return directory.getAbsolutePath();
    }
    public File getCurrentDirectory(){
        return directory;
    }

    public ArrayList<MusicInformation> getMusics(){
        return this.musics;
    }


    public boolean isScanning(){
        return scanning;
    }

    @Override
    public void onReturn(ArrayList<MusicInformation> musics) {
        scanning=false;
        this.musics=musics;
        if (cl!=null) cl.onComplete("");
        Log.d(LOG_TAG,"Files List Returned!");
    }
}