//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.visuals.PlayControlsView;
import com.thirtyseventhpercentile.nerdyaudio.visuals.VisualizationView;


public class NowPlayingFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static final String LOG_TAG = "CS_AFN";

    VisualizationView vv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.tab_frag_nowplaying, container, false);


        vv = (VisualizationView) v.findViewById(R.id.visualization);
        if (vv == null) Log2.log(4, this, "VisualizationView is null!");
        else if (vv.getRenderThread() == null) Log2.log(4, this, "Renderer is null!");
        else {
            //vv.getRenderThread().setMaxFPS(60);
        }


        v.post(new Runnable() {
            @Override
            public void run() {
                if (PlayControlsView.getInstance() != null)
                    PlayControlsView.getInstance().expand(true);
            }
        });

        return v;


    }

    @Override
    public void onClick(View view) {

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

    }

    public void onNothingSelected(AdapterView<?> parent) {
        Log2.log(2, this, "Adapter > Nothing selected.");
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }
}
