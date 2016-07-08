//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.cosmicsubspace.nerdyaudio.visuals;

import android.content.Context;
import android.graphics.Canvas;

import com.cosmicsubspace.nerdyaudio.audio.AudioPlayer;
import com.cosmicsubspace.nerdyaudio.audio.VisualizationBuffer;
import com.cosmicsubspace.nerdyaudio.exceptions.BufferNotPresentException;
import com.cosmicsubspace.nerdyaudio.helper.ErrorLogger;
import com.cosmicsubspace.nerdyaudio.helper.Log2;
import com.cosmicsubspace.nerdyaudio.settings.SettingElement;
import com.cosmicsubspace.nerdyaudio.ui.SidebarSettings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;


public abstract class BaseRenderer {

    //Get all the SettingElements in a list.
    public abstract List<SettingElement> getSettings();

    //Initialize all the SettingElements, using the values
    public final void putSettings(List<SettingElement> elements) {
        if (elements == null) {
            Log2.log(1, this, "Putsettings():", "elements is null. Returning immediately.");
            return;
        }
        for (SettingElement orig : getSettings()) {
            for (SettingElement other : elements) {
                //Log2.log(2, this, this.getKey(), orig, other);
                if (orig.getName().equals(other.getName())) {
                    orig.fromElement(other);
                    orig.applyValue();
                }
            }
        }
    }

    //Get the string identifier for the save
    public abstract String getKey();

    //Call the sync
    public final void syncChanges() { //Temporary final.
        for (SettingElement e : getSettings()) {
            e.applyValue();
        }
    }

    public void saveSettings(List<SettingElement> toSave) {
        saveSettings(toSave, getKey());
    }

    public void saveSettings(List<SettingElement> toSave, String key) {
        Log2.log(1, this, "Saving settings...");
        try {
            FileOutputStream fos = ctxt.openFileOutput("vis_save_" + key, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(toSave);
            os.close();
            fos.close();
        } catch (IOException e) {
            ErrorLogger.log(e);
        }
    }

    public void saveSettingsAsync() {
        //I don't think this is really safe.
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveSettings(getSettings());
            }
        }).run();
    }

    public List<SettingElement> loadSettings() {
        return loadSettings(getKey());
    }

    public List<SettingElement> loadSettings(String key) {
        Log2.log(1, this, "Loading settings.");
        try {
            FileInputStream fis = ctxt.openFileInput("vis_save_" + key);
            ObjectInputStream is = new ObjectInputStream(fis);
            List<SettingElement> data = (List<SettingElement>) is.readObject();
            is.close();
            fis.close();
            return data;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            ErrorLogger.log(e);
        }
        return null;
    }


    public static final String LOG_TAG = "CS_AFN";

    VisualizationBuffer vb;
    AudioPlayer ap;
    SidebarSettings sbs;

    int w, h;
    float density;
    Context ctxt;

    public BaseRenderer(Context ctxt) { //Context needed for file input/output
        this.density = ctxt.getResources().getDisplayMetrics().density;
        this.ctxt = ctxt;
        this.vb = VisualizationBuffer.getInstance();
        this.ap = AudioPlayer.getInstance();
    }

    public void draw(Canvas c, int w, int h) {
        if (this.w != w || this.h != h) {
            this.w = w;
            this.h = h;
            dimensionsChanged(w, h);
        }
        syncChanges();
        if (vb != null && ap != null) {
            drawVisuals(c, w, h);
        }
    }

    abstract public void dimensionsChanged(int w, int h);

    abstract public void drawVisuals(Canvas c, int w, int h);

    public void release() {
        saveSettingsAsync();
    }

    public void setVisualizationBuffer(VisualizationBuffer vb) {
        this.vb = vb;
    }

    public void setAudioPlayer(AudioPlayer ap) {
        this.ap = ap;
    }

    public float[] getLSamples(long start, long end) throws BufferNotPresentException {
        if (vb != null) {
            return vb.getFrames(start, end, VisualizationBuffer.LEFT_CHANNEL);

        } else return null;
    }

    public float[] getRSamples(long start, long end) throws BufferNotPresentException {
        if (vb != null) {

            return vb.getFrames(start, end, VisualizationBuffer.RIGHT_CHANNEL);

        } else return null;
    }

    public void deleteBefore(long samp) {
        if (vb != null) {


            vb.deleteBefore(samp);

        }
    }

    public long getCurrentFrame() {
        if (ap != null) {
            return ap.getCurrentFrame();
        } else return 0;
    }


}
