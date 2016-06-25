//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.thirtyseventhpercentile.nerdyaudio.R;


public class AlbumArtSettings extends BaseSetting{
    private static final String PREF_IDENTIFIER = "com.thirtyseventhpercentile.audiofornerds.settings.AlbumArtSettings";

    public int getType(){
        return BaseSetting.ALBUM_ART;
    }

    @Override
    protected void save() {
        SharedPreferences.Editor editor=getSharedPreferences(PREF_IDENTIFIER).edit();

        editor.apply();
    }

    @Override
    protected void load() {
        SharedPreferences pref=getSharedPreferences(PREF_IDENTIFIER);

        sbs.notifyUI(this);
    }


    public AlbumArtSettings(SidebarSettings sbs, Context c){
        super(sbs,c);

        load();
    }


    public View getSettingsView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.visuals_setting_album_art, container, false);

        load();
        return v;
    }

}
