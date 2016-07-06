//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.file.FileManager;
import com.thirtyseventhpercentile.nerdyaudio.file.MusicGroup;
import com.thirtyseventhpercentile.nerdyaudio.file.MusicInformation;
import com.thirtyseventhpercentile.nerdyaudio.file.QueueManager;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.MusicListDisplayable;

import java.util.ArrayList;


public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.myViewHolder> {
    private FileManager fm;
    private ArrayList dataset;


    public static final String LOG_TAG = "CS_AFN";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class myViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView artist;
        public RelativeLayout container;
        public ImageView expander;

        public myViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.music_list_element_title);
            artist = (TextView) v.findViewById(R.id.music_list_element_artist);
            container = (RelativeLayout) v.findViewById(R.id.music_list_element_container);
            expander = (ImageView) v.findViewById(R.id.music_list_element_expander);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MusicListAdapter(FileManager fm) {
        this.fm = fm;
        updateMusicList();
    }

    public void updateMusicList() {
        this.dataset = this.fm.updateMusicList();
        notifyDataSetChanged();
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
        final MusicListDisplayable currentMusic = (MusicListDisplayable) dataset.get(position);
        holder.title.setText(currentMusic.getTitle());
        holder.artist.setText(currentMusic.getSubTitle());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log2.log(2, this, "Clicked");
                if (!currentMusic.isAGroup()) { //Is a single music.
                    QueueManager.getInstance().addMusic(new MusicInformation(((MusicInformation) currentMusic)));
                } else { //It's a group -- Expand and update list.
                    ((MusicGroup) currentMusic).toggleExpand();
                    updateMusicList();
                }
            }
        });
        if (!currentMusic.isAGroup()) {
            holder.expander.setImageResource(android.R.color.transparent);
        } else if (currentMusic.expanded()) {
            holder.expander.setImageResource(R.drawable.ic_expand_less_black_24dp);
        } else {
            holder.expander.setImageResource(R.drawable.ic_expand_more_black_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}