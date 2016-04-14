package com.thirtyseventhpercentile.nerdyaudio.filters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.audio.VisualizationBuffer;

//Used to display Input, Output, and Visuals filter blocks.
public class StaticFilter extends BaseFilter{

    public static final int INPUT=513;
    public static final int OUTPUT=113634;
    public static final int VISUALS=62354;

    private int type;
    VisualizationBuffer vb;

    public StaticFilter(FilterManager fm, int type){
        super(fm);
        this.type=type;
        if (type==VISUALS) vb=VisualizationBuffer.getInstance();
    }

    @Override
    public void filter(float[] data) {
        if (type==VISUALS){
            vb.feed(FilterManager.floatToShort(data)); //TODO performance...
        }
    }

    @Override
    public String getName() {
        if (type==VISUALS) return "Visualization Feed";
        else return "wut";
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        ViewGroup v=(ViewGroup)super.getView(inflater, container);
        v.removeView(v.findViewById(R.id.filter_close));
        return v;
    }

    @Override
    public View getContentView(LayoutInflater inflater, ViewGroup container) {

        return null;
    }
}
