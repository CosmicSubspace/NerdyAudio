package com.chancorp.audiofornerds.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.interfaces.SettingsUpdateListener;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Chan on 2/1/2016.
 */
public class SidebarSettings {
    ArrayList<SettingsUpdateListener> suls=new ArrayList<>();

    public void addSettingsUpdateListener(SettingsUpdateListener sul){
        this.suls.add(sul);
    }
    public SidebarSettings(){

    }
    public View getView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.drawer, container, false);
        //TODO dynamic changing UI and other settings.
        return v;
    }
}
