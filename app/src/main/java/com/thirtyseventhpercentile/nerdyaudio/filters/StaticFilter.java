package com.thirtyseventhpercentile.nerdyaudio.filters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.audio.VisualizationBuffer;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;

//Used to display Input, Output, and Visuals filter blocks.
//Input and Output filters don't actually do anything.
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
        if (type!=VISUALS && type!=INPUT && type !=OUTPUT){
            throw new RuntimeException("lol");
        }
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
        if (type==OUTPUT) return "Audio Out";
        if (type==INPUT) return "Audio In";
        else return "wut";
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        View v=super.getView(inflater, container);
        Log2.log(2,this,v.findViewById(R.id.filter_close));
        Log2.log(2,this,v);
        v.findViewById(R.id.filter_close).setVisibility(View.GONE);
        if (type!=VISUALS){
            v.findViewById(R.id.filter_move_down).setVisibility(View.GONE);
            v.findViewById(R.id.filter_move_up).setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public View getContentView(LayoutInflater inflater, ViewGroup container) {

        return null;
    }
}
