package com.chancorp.audiofornerds.filters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chancorp.audiofornerds.R;

import java.util.logging.Filter;

/**
 * Created by Chan on 2015-12-18.
 */
abstract public class BaseFilter implements View.OnClickListener{
    FilterManager fm;
    View mainView;
    Button closeBtn;
    TextView title;
    FrameLayout content;
    public BaseFilter(FilterManager fm){
        this.fm=fm;
    }
    abstract public void filter(float[] data);
    abstract public String getName();
    public View getView(LayoutInflater inflater, ViewGroup container){
        mainView = inflater.inflate(R.layout.filter_base, container, false);

        closeBtn=(Button) mainView.findViewById(R.id.filter_close);
        closeBtn.setOnClickListener(this);

        title=(TextView) mainView.findViewById(R.id.filter_title);
        title.setText(getName());

        content=(FrameLayout) mainView.findViewById(R.id.filter_content);
        content.addView(getContentView(inflater,content));

        return mainView;
    }
    public abstract View getContentView(LayoutInflater inflater, ViewGroup container);
    public void deleteSelf(){
        fm.deleteFilter(this);
        ((LinearLayout)mainView.getParent()).removeView(mainView);
    }
    @Override
    public void onClick(View v){
        if (v.getId()==R.id.filter_close){
            deleteSelf();
        }
    }
}
