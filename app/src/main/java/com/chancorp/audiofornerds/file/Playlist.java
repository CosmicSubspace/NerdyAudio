package com.chancorp.audiofornerds.file;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chan on 3/12/2016.
 */
public class Playlist {
    public static final String LOG_TAG = "CS_AFN";
    String[] files;
    String playlistName;
    String playlistDescription;

    private Playlist() {

    }

    public Playlist(String name, String desc, List<MusicInformation> queue) {
        playlistName = name;
        playlistDescription = desc;
        files = new String[queue.size()];
        for (int i = 0; i < queue.size(); i++) {
            files[i] = queue.get(i).getFilepath();
        }
    }

    public Playlist(String name, String desc, String[] queue) {
        playlistName = name;
        playlistDescription = desc;
        files = queue;
    }

    private String getStringRepr() {
        StringBuilder sb = new StringBuilder();
        sb.append(playlistName);
        sb.append("\n");
        sb.append(playlistDescription);
        sb.append("\n");
        for (int i = 0; i < files.length; i++) {
            sb.append(files[i]);
        }
        return sb.toString();
    }

    public void save(Context c) throws IOException {
        FileOutputStream fos = c.openFileOutput("PL_" + playlistName, Context.MODE_PRIVATE);
        PrintWriter pw = new PrintWriter(fos);
        pw.println(playlistName);
        pw.println(playlistDescription);
        for (int i = 0; i < files.length; i++) {
            pw.println(files[i]);
        }

        pw.close();
        /*
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(this);
        os.close();
        fos.close();*/
    }

    public static Playlist load(Context c, String name) throws IOException, ClassCastException, ClassNotFoundException {
        FileInputStream fis = c.openFileInput("PL_" + name);

        InputStreamReader isr = new InputStreamReader(fis);  //or whatever encoding
        BufferedReader r = new BufferedReader(isr);

        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = r.readLine()) != null) {
            sb.append(line+"\n");
        }
        String res = sb.toString();
        Log.d(LOG_TAG, res);
        String[] resS = res.split("\\n");

        String[] files = new String[resS.length - 2];
        for (int i = 0; i < resS.length - 2; i++) {
            files[i] = resS[i + 2];
        }

        return new Playlist(resS[0], resS[1], files);

        /*
        ObjectInputStream is = new ObjectInputStream(fis);
        Playlist readPS = (Playlist) is.readObject();
        is.close();
        fis.close();
        return readPS;*/
    }

    public static String[] listPlaylists(Context c, boolean addDummy) {
        String[] files = c.fileList();
        List<String> res = new ArrayList<>();
        if (addDummy) res.add("[Current Queue]");
        for (String file : files) {
            if (file.startsWith("PL_")) {
                res.add(file.replace("PL_", ""));
            }
        }
        return res.toArray(new String[res.size()]);
    }
}
