//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.graphics.Canvas;

import com.thirtyseventhpercentile.nerdyaudio.audio.AudioPlayer;
import com.thirtyseventhpercentile.nerdyaudio.audio.VisualizationBuffer;
import com.thirtyseventhpercentile.nerdyaudio.exceptions.BufferNotPresentException;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.SettingsUpdateListener;
import com.thirtyseventhpercentile.nerdyaudio.settings.BaseSetting;
import com.thirtyseventhpercentile.nerdyaudio.settings.SidebarSettings;


public abstract class BaseRenderer implements SettingsUpdateListener{
    public static final String LOG_TAG="CS_AFN";


    VisualizationBuffer vb;
    AudioPlayer ap;
    SidebarSettings sbs;

    int w,h;

    float density;

    public BaseRenderer(float density) {
        this.density = density;
        this.vb=VisualizationBuffer.getInstance();
        this.ap=AudioPlayer.getInstance();
        sbs= SidebarSettings.getInstance();
        sbs.addSettingsUpdateListener(this);
    }

    @Override
    abstract public void updated(BaseSetting setting);
    public void draw(Canvas c, int w, int h){
        if (this.w!=w || this.h!=h){
            this.w=w;
            this.h=h;
            dimensionsChanged(w,h);
        }
        if (vb != null && ap != null) {
            drawVisuals(c,w,h);
        }
    }
    abstract public void dimensionsChanged(int w, int h);
    abstract public void drawVisuals(Canvas c, int w, int h);
    public void release(){
        sbs.removeSettingsUpdateListener(this);
    }

    public void setVisualizationBuffer(VisualizationBuffer vb) {
        this.vb = vb;
    }

    public void setAudioPlayer(AudioPlayer ap) {
        this.ap = ap;
    }
    public float[] getLSamples(long start, long end) throws BufferNotPresentException{
        if (vb != null) {
            return vb.getFrames(start, end, VisualizationBuffer.LEFT_CHANNEL);

        }else return null;
    }
    public float[] getRSamples(long start, long end) throws BufferNotPresentException {
        if (vb != null) {

            return vb.getFrames(start, end, VisualizationBuffer.RIGHT_CHANNEL);

        }else return null;
    }
    public void deleteBefore(long samp){
        if (vb != null) {


            vb.deleteBefore(samp);

        }
    }

    public long getCurrentFrame() {
        if (ap != null) {
            return ap.getCurrentFrame();
        }else return 0;
    }

}
