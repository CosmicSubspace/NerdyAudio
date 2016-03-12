package com.chancorp.audiofornerds.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.file.MusicInformation;
import com.chancorp.audiofornerds.file.Playlist;
import com.chancorp.audiofornerds.helper.ErrorLogger;
import com.chancorp.audiofornerds.interfaces.PlaylistInformationReturnListener;

import java.util.List;

public class PlaylistSaveDialog {

    public final static String LOG_TAG="CS_Pinger";


    EditText description, name;
    PlaylistInformationReturnListener pirl;
    Context c;
    String title;
    List<MusicInformation> queue;

    public PlaylistSaveDialog(Context c, List<MusicInformation> queue){
        this.c=c;
        title="";
        this.queue=queue;
    }

    public PlaylistSaveDialog setTitle(String s){
        this.title=s;
        return this;
    }

    public PlaylistSaveDialog setOnReturnListener(PlaylistInformationReturnListener pirl){
        this.pirl=pirl;
        return this;
    }

    public void init(){

        AlertDialog.Builder builder = new AlertDialog.Builder(c);

        LayoutInflater inflater = (LayoutInflater) c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View view;

        view=inflater.inflate(R.layout.playlist_save_dialog, null);

        builder.setView(view);


        description = (EditText) view.findViewById(R.id.playlist_save_desc_input);
        name = (EditText) view.findViewById(R.id.playlist_save_name_input);

        builder.setMessage(title)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (pirl!=null) pirl.onReturn(name.getText().toString(),description.getText().toString());

                        try {
                            new Playlist(name.getText().toString(), queue).save(c);
                        }catch (Exception e){
                            ErrorLogger.log(e);
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
