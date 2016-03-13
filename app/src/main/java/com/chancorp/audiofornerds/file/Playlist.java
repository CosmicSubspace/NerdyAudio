package com.chancorp.audiofornerds.file;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chan on 3/12/2016.
 */
public class Playlist implements Serializable{
    String[] files;
    String playlistName;

    private Playlist(){

    }
    public Playlist(String name, List<MusicInformation> queue){
        playlistName=name;

        files=new String[queue.size()];
        for (int i = 0; i < queue.size(); i++) {
            files[i]=queue.get(i).getFilepath();
        }
    }



    public void save(Context c)throws IOException{
        FileOutputStream fos = c.openFileOutput("PL_" + playlistName, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(this);
        os.close();
        fos.close();
    }
    public static Playlist load(Context c, String name) throws IOException, ClassCastException, ClassNotFoundException{
        FileInputStream fis = c.openFileInput("PL_"+name);
        ObjectInputStream is = new ObjectInputStream(fis);
        Playlist readPS = (Playlist) is.readObject();
        is.close();
        fis.close();
        return readPS;
    }
    public static String[] listPlaylists(Context c, boolean addDummy){
        String[] files=c.fileList();
        List<String> res=new ArrayList<>();
        if (addDummy) res.add("[Current Queue]");
        for (String file:files){
            if (file.startsWith("PL_")){
                res.add(file.replace("PL_",""));
            }
        }
        return res.toArray(new String[res.size()]);
    }
}
