package com.chancorp.audiofornerds.ui;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.filters.BaseFilter;
import com.chancorp.audiofornerds.filters.FilterManager;
import com.chancorp.audiofornerds.filters.VolumeFilter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;

/**
 * Created by Chan on 2015-12-16.
 */
public class FiltersFragment extends Fragment implements View.OnClickListener{
    LinearLayout lv;
    FloatingActionMenu fab;
    FloatingActionButton[] fabs=new FloatingActionButton[3];
    FilterManager fm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fm=FilterManager.getInstance();
        View v=inflater.inflate(R.layout.tab_frag_filters, container, false);
        lv=(LinearLayout)v.findViewById(R.id.filters_tab_filters);
        fab=(FloatingActionMenu)v.findViewById(R.id.filters_tab_fab);

        fabs[0]=(FloatingActionButton) v.findViewById(R.id.filters_tab_fab_sub_1);
        fabs[0].setOnClickListener(this);

        ArrayList<BaseFilter> filters=fm.getFilters();
        for (int i=0;i<filters.size();i++){
            lv.addView(filters.get(i).getView(getLayoutInflater(null),lv));
        }
        return v;
    }

    public void updateUI(){
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if (id==R.id.filters_tab_fab_sub_1){
            BaseFilter newFilter=new VolumeFilter(FilterManager.getInstance());
            fm.addFilter(newFilter);
            lv.addView(newFilter.getView(getLayoutInflater(null),lv));
        }
    }
}