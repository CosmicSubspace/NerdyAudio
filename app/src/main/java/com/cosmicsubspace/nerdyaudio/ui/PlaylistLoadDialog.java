package com.cosmicsubspace.nerdyaudio.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cosmicsubspace.nerdyaudio.R;
import com.cosmicsubspace.nerdyaudio.file.Playlist;
import com.cosmicsubspace.nerdyaudio.file.QueueManager;
import com.cosmicsubspace.nerdyaudio.helper.ErrorLogger;
import com.cosmicsubspace.nerdyaudio.helper.Log2;

import java.util.ArrayList;

public class PlaylistLoadDialog {
    public static interface LoadedListener {
        void loaded();
    }

    ListView list;
    Spinner mode;

    LoadedListener ll;
    Context c;
    String title;

    public PlaylistLoadDialog(Context c) {
        this.c = c;
        title = "";
    }

    public PlaylistLoadDialog setTitle(String s) {
        this.title = s;
        return this;
    }

    public PlaylistLoadDialog setLoadedListener(LoadedListener ll) {
        this.ll = ll;
        return this;
    }


    String[] playlistModes = {"Add", "Subtract", "Intersect"};
    int[] playlistConstants = {QueueManager.ADD, QueueManager.SUBTRACT, QueueManager.INTERSECT};

    public void init() {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view;

        view = inflater.inflate(R.layout.playlist_load_dialog, null);

        builder.setView(view);


        list = (ListView) view.findViewById(R.id.playlist_load_list);
        mode = (Spinner) view.findViewById(R.id.playlist_load_mode_selector);

        final ArrayAdapter<String> adpt = new ArrayAdapter<>(c, android.R.layout.select_dialog_multichoice, Playlist.listPlaylists(c, true));
        list.setAdapter(adpt);

        list.setItemChecked(0, true);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(c, R.layout.visuals_spinner_element, playlistModes);
        //adapter.setDropDownViewResource(R.layout.visuals_spinner_element);
        mode.setAdapter(adapter);
        //mode.setOnItemSelectedListener(this);

        builder.setMessage(title)
                .setPositiveButton("Load", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try {
                            ArrayList<Playlist> playlists = new ArrayList<Playlist>();

                            SparseBooleanArray checked = list.getCheckedItemPositions();

                            if (checked.get(0)) {
                                playlists.add(new Playlist("[Current Queue]", "", QueueManager.getInstance().getQueue()));
                            }

                            for (int j = 1; j < adpt.getCount(); j++) { //index 0 is always the main queue
                                if (checked.get(j)) {
                                    playlists.add(Playlist.load(c, adpt.getItem(j)));
                                }
                            }

                            QueueManager.getInstance().parsePlaylists(
                                    playlists.toArray(new Playlist[playlists.size()])
                                    , playlistConstants[mode.getSelectedItemPosition()]
                                    , c
                            );

                            if (ll != null) ll.loaded();
                        } catch (Exception e) {
                            Log2.log(e);
                            Toast.makeText(c, "Save Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        builder.show();

    }

}
