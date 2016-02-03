package com.chancorp.audiofornerds.settings;

import java.io.Serializable;

/**
 * Created by Chan on 2/3/2016.
 */
public class VisualizationSettings extends BaseSetting implements Serializable {
    @Override
    public int getType(){
        return BaseSetting.VISUALIZATION;
    }

    int type;
    public static final String[] visualizations=new String[]{"VU Meter","Waveform","Spectrum","Spectograph"};
    int visualization_active=0;

    public int getActiveVisualization(){
        return visualization_active;
    }
    public void setActiveVisualization(int type){
        this.visualization_active=type;
    }

}
