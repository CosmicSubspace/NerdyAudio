//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.cosmicsubspace.nerdyaudio.filters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cosmicsubspace.nerdyaudio.R;

abstract public class BaseFilter implements View.OnClickListener {
    FilterManager fm;
    View mainView;
    Button closeBtn, upBtn, downBtn, enableBtn;
    TextView title;
    FrameLayout content;
    boolean enabled=true;

    public BaseFilter(FilterManager fm) {
        this.fm = fm;
    }

    abstract public void filter(float[] data);

    abstract public String getName();

    public View getView(LayoutInflater inflater, ViewGroup container) {
        mainView = inflater.inflate(R.layout.filter_base, container, false);

        closeBtn = (Button) mainView.findViewById(R.id.filter_close);
        closeBtn.setOnClickListener(this);

        upBtn = (Button) mainView.findViewById(R.id.filter_move_up);
        upBtn.setOnClickListener(this);

        downBtn = (Button) mainView.findViewById(R.id.filter_move_down);
        downBtn.setOnClickListener(this);

        enableBtn = (Button) mainView.findViewById(R.id.filter_enable);
        enableBtn.setOnClickListener(this);
        updateEnabledButton();

        title = (TextView) mainView.findViewById(R.id.filter_title);
        title.setText(getName());
        title.setSelected(true);

        content = (FrameLayout) mainView.findViewById(R.id.filter_content);
        View innerContent = getContentView(inflater, content);
        if (innerContent != null) content.addView(innerContent);

        return mainView;
    }

    public abstract View getContentView(LayoutInflater inflater, ViewGroup container);

    public void deleteSelf() {
        fm.deleteFilter(this);
    }

    public void moveUp() {

        fm.moveUp(this);
    }

    public void moveDown() {
        fm.moveDown(this);
    }

    public void toggleEnable(){
        enabled=!enabled;
        updateEnabledButton();
    }
    public void updateEnabledButton(){
        if (enabled) enableBtn.setBackgroundResource(R.drawable.ic_volume_up_black_36dp);
        else enableBtn.setBackgroundResource(R.drawable.ic_volume_off_black_36dp);
    }
    public boolean isEnabled(){
        return enabled;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.filter_close) {
            deleteSelf();
        } else if (v.getId() == R.id.filter_move_up) {
            moveUp();
        } else if (v.getId() == R.id.filter_move_down) {
            moveDown();
        }else if (v.getId() == R.id.filter_enable) {
            toggleEnable();
        }
    }
}
