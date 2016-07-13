//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.cosmicsubspace.nerdyaudio.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cosmicsubspace.nerdyaudio.filters.StereoFilter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.cosmicsubspace.nerdyaudio.R;
import com.cosmicsubspace.nerdyaudio.filters.AutoGainFilter;
import com.cosmicsubspace.nerdyaudio.filters.BaseFilter;
import com.cosmicsubspace.nerdyaudio.filters.FilterManager;
import com.cosmicsubspace.nerdyaudio.filters.IirHighPassFilter;
import com.cosmicsubspace.nerdyaudio.filters.IirLowPassFilter;
import com.cosmicsubspace.nerdyaudio.filters.VolumeFilter;
import com.cosmicsubspace.nerdyaudio.helper.ClansFABHelper;
import com.cosmicsubspace.nerdyaudio.visuals.PlayControlsView;

import java.util.ArrayList;


public class FiltersFragment extends Fragment implements View.OnClickListener, FilterManager.FilterListChangeListener {
    private static final String LOG_TAG = "CS_AFN";
    LinearLayout lv;
    FloatingActionMenu fam;
    FilterManager fm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fm = FilterManager.getInstance();
        fm.addFilterListChangeListener(this);
        View v = inflater.inflate(R.layout.tab_frag_filters, container, false);
        lv = (LinearLayout) v.findViewById(R.id.filters_tab_filters);
        fam = (FloatingActionMenu) v.findViewById(R.id.filters_tab_fab);
        fam.setClosedOnTouchOutside(true);
        ClansFABHelper.setScalingAnimation(fam, R.drawable.ic_close_white_24dp, R.drawable.ic_add_white_24dp);

        v.findViewById(R.id.filters_tab_fab_sub_1).setOnClickListener(this);
        v.findViewById(R.id.filters_tab_fab_sub_2).setOnClickListener(this);
        v.findViewById(R.id.filters_tab_fab_sub_3).setOnClickListener(this);
        v.findViewById(R.id.filters_tab_fab_sub_4).setOnClickListener(this);
        v.findViewById(R.id.filters_tab_fab_sub_5).setOnClickListener(this);

        //I don't think there will be too many filters in here, so we'll just use a linearlayout with scrollbar
        //instead of a listview.
        updateUI();


        v.post(new Runnable() {
            @Override
            public void run() {
                if (PlayControlsView.getInstance() != null)
                    PlayControlsView.getInstance().expand(false);
            }
        });

        return v;
    }

    public void updateUI() {
        lv.removeAllViews();
        ArrayList<BaseFilter> filters = fm.getFilters();
        for (int i = 0; i < filters.size(); i++) {
            lv.addView(filters.get(i).getView(getLayoutInflater(null), lv));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fm.removeFilterListChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.filters_tab_fab_sub_1) {
            BaseFilter newFilter = new VolumeFilter(FilterManager.getInstance());
            fm.addFilter(newFilter);
            lv.addView(newFilter.getView(getLayoutInflater(null), lv));
        } else if (id == R.id.filters_tab_fab_sub_2) {
            BaseFilter newFilter = new AutoGainFilter(FilterManager.getInstance());
            fm.addFilter(newFilter);
            View vvvvv = newFilter.getView(getLayoutInflater(null), lv);
            lv.addView(vvvvv);
        } else if (id == R.id.filters_tab_fab_sub_3) {
            BaseFilter newFilter = new IirLowPassFilter(FilterManager.getInstance());
            fm.addFilter(newFilter);
            View vvvvv = newFilter.getView(getLayoutInflater(null), lv);
            lv.addView(vvvvv);
        } else if (id == R.id.filters_tab_fab_sub_4) {
            BaseFilter newFilter = new IirHighPassFilter(FilterManager.getInstance());
            fm.addFilter(newFilter);
            View vvvvv = newFilter.getView(getLayoutInflater(null), lv);
            lv.addView(vvvvv);
        } else if (id == R.id.filters_tab_fab_sub_5) {
            BaseFilter newFilter = new StereoFilter(FilterManager.getInstance());
            fm.addFilter(newFilter);
            View vvvvv = newFilter.getView(getLayoutInflater(null), lv);
            lv.addView(vvvvv);
        }
    }

    @Override
    public void filterListChanged() {
        updateUI();
    }
}