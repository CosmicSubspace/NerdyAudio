//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.filters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thirtyseventhpercentile.nerdyaudio.R;

abstract public class BaseFilter implements View.OnClickListener{
    FilterManager fm;
    View mainView;
    Button closeBtn, upBtn, downBtn;
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

        upBtn=(Button) mainView.findViewById(R.id.filter_move_up);
        upBtn.setOnClickListener(this);

        downBtn=(Button) mainView.findViewById(R.id.filter_move_down);
        downBtn.setOnClickListener(this);

        title=(TextView) mainView.findViewById(R.id.filter_title);
        title.setText(getName());

        content=(FrameLayout) mainView.findViewById(R.id.filter_content);
        View innerContent=getContentView(inflater,content);
        if (innerContent!=null) content.addView(innerContent);

        return mainView;
    }
    public abstract View getContentView(LayoutInflater inflater, ViewGroup container);
    public void deleteSelf(){
        fm.deleteFilter(this);
    }
    public void moveUp(){

        fm.moveUp(this);
    }
    public void moveDown(){
        fm.moveDown(this);
    }
    @Override
    public void onClick(View v){
        if (v.getId()==R.id.filter_close){
            deleteSelf();
        }else if (v.getId()==R.id.filter_move_up){
            moveUp();
        }else if (v.getId()==R.id.filter_move_down){
            moveDown();
        }
    }
}
