package com.chancorp.audiofornerds.filters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.logging.Filter;

/**
 * Created by Chan on 2015-12-18.
 */
abstract public class BaseFilter {
    FilterManager fm;
    View mainView;
    public BaseFilter(FilterManager fm){
        this.fm=fm;
    }
    abstract public void filter(float[] data);
    abstract public String getName();
    abstract public View getView(LayoutInflater inflater, ViewGroup container);
    public void deleteSelf(){
        fm.deleteFilter(this);
        ((LinearLayout)mainView.getParent()).removeView(mainView);
    }
}
