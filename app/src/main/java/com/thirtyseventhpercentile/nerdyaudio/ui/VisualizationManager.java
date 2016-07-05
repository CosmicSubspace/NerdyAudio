package com.thirtyseventhpercentile.nerdyaudio.ui;

import com.thirtyseventhpercentile.nerdyaudio.visuals.BaseRenderer;

/**
 * Created by Chan on 7/5/2016.
 */

//Singleton because screw good practice.
public class VisualizationManager {
    static VisualizationManager inst=new VisualizationManager();
    private VisualizationManager(){}
    public static VisualizationManager getInstance(){return inst;}

    BaseRenderer renderer;
    public void setActiveRenderer(BaseRenderer r){
        renderer=r;

    }
    public BaseRenderer getActiveRenderer(){
        return renderer;
    }
}
