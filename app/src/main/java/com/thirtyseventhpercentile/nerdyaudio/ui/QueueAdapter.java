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
import com.thirtyseventhpercentile.nerdyaudio.file.MusicInformation;
import com.thirtyseventhpercentile.nerdyaudio.file.QueueManager;


public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.myViewHolder> {
    private QueueManager qm;

    public static final String LOG_TAG="CS_AFN";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class myViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView artist;
        public RelativeLayout container;
        public ImageView status;
        public myViewHolder(View v) {
            super(v);
            title = (TextView)v.findViewById(R.id.queue_list_element_title);
            artist=(TextView)v.findViewById(R.id.queue_list_element_artist);
            container=(RelativeLayout)v.findViewById(R.id.queue_list_element_container);
            status=(ImageView)v.findViewById(R.id.queue_list_element_status);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public QueueAdapter(QueueManager qm) {
        this.qm=qm;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.queue_list_element, parent, false);
        // set the view's size, margins, paddings and layout parameters
        myViewHolder vh = new myViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(myViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final MusicInformation currentMusic=qm.getQueue().get(position);
        holder.title.setText(currentMusic.getTitle());
        holder.artist.setText(currentMusic.getArtist());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qm.playFile(position);
            }
        });
        if (qm.getQueue().get(position).getStatus()==MusicInformation.NOT_READY){
            holder.status.setImageResource(R.drawable.ic_close_black_24dp);
        }else if (qm.getQueue().get(position).getStatus()==MusicInformation.READY){
            holder.status.setImageResource(R.drawable.ic_check_black_24dp);
        }else if (qm.getQueue().get(position).getStatus()==MusicInformation.PLAYING){
            holder.status.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }else if (qm.getQueue().get(position).getStatus()==MusicInformation.PLAYING_WITHOUT_DATA){
            holder.status.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }else if (qm.getQueue().get(position).getStatus()==MusicInformation.CACHING){
            holder.status.setImageResource(R.drawable.ic_refresh_black_24dp);
        }else if (qm.getQueue().get(position).getStatus()==MusicInformation.PLAYING_WHILE_CACHING){
            holder.status.setImageResource(R.drawable.ic_refresh_black_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return qm.getQueue().size();
    }

}