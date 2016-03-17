//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.file.MusicInformation;
import com.thirtyseventhpercentile.nerdyaudio.file.QueueManager;

import java.util.ArrayList;


public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.myViewHolder> {
    private ArrayList<MusicInformation> dataset;

    public static final String LOG_TAG="CS_AFN";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class myViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView artist;
        public RelativeLayout container;
        public myViewHolder(View v) {
            super(v);
            title = (TextView)v.findViewById(R.id.music_list_element_title);
            artist=(TextView)v.findViewById(R.id.music_list_element_artist);
            container=(RelativeLayout)v.findViewById(R.id.music_list_element_container);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MusicListAdapter(ArrayList<MusicInformation> informations) {
        dataset = informations;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_list_element, parent, false);
        // set the view's size, margins, paddings and layout parameters
        myViewHolder vh = new myViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final MusicInformation currentMusic=dataset.get(position);
        holder.title.setText(currentMusic.getTitle());
        holder.artist.setText(currentMusic.getArtist());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Clicked");
                QueueManager.getInstance().addMusic(new MusicInformation(currentMusic));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void newData(ArrayList<MusicInformation> info){
        dataset=info;
        notifyDataSetChanged();
    }
}