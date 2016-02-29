//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.chancorp.audiofornerds.ui;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.filters.AutoGainFilter;
import com.chancorp.audiofornerds.filters.BaseFilter;
import com.chancorp.audiofornerds.filters.FilterManager;
import com.chancorp.audiofornerds.filters.VolumeFilter;
import com.chancorp.audiofornerds.helper.ClansFABHelper;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;


public class FiltersFragment extends Fragment implements View.OnClickListener{
    private static final String LOG_TAG="CS_AFN";
    LinearLayout lv;
    FloatingActionMenu fam;
    FloatingActionButton[] fabs=new FloatingActionButton[3];
    FilterManager fm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fm=FilterManager.getInstance();
        View v=inflater.inflate(R.layout.tab_frag_filters, container, false);
        lv=(LinearLayout)v.findViewById(R.id.filters_tab_filters);
        fam =(FloatingActionMenu)v.findViewById(R.id.filters_tab_fab);
        fam.setClosedOnTouchOutside(true);
        ClansFABHelper.setScalingAnimation(fam, R.drawable.ic_close_white_24dp, R.drawable.ic_add_white_24dp);

        fabs[0]=(FloatingActionButton) v.findViewById(R.id.filters_tab_fab_sub_1);
        fabs[0].setOnClickListener(this);

        fabs[1]=(FloatingActionButton) v.findViewById(R.id.filters_tab_fab_sub_2);
        fabs[1].setOnClickListener(this);

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
            lv.addView(newFilter.getView(getLayoutInflater(null), lv));
            //fam.close(true);
        }else if (id==R.id.filters_tab_fab_sub_2){
            BaseFilter newFilter=new AutoGainFilter(FilterManager.getInstance());
            Log.i(LOG_TAG,"BaseFilter:"+newFilter);
            fm.addFilter(newFilter);
            Log.i(LOG_TAG, "fm:" + fm);
            View vvvvv=newFilter.getView(getLayoutInflater(null),lv);
            Log.i(LOG_TAG, "vvvvv:" + vvvvv);
            Log.i(LOG_TAG, "lv:" + lv);
            lv.addView(vvvvv);
            //fam.close(true);
        }
    }
}