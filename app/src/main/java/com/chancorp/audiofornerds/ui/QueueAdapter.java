package com.chancorp.audiofornerds.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.file.MusicInformation;
import com.chancorp.audiofornerds.file.QueueManager;

/**
 * Created by Chan on 2015-12-19.
 */
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
        public myViewHolder(View v) {
            super(v);
            title = (TextView)v.findViewById(R.id.music_list_element_title);
            artist=(TextView)v.findViewById(R.id.music_list_element_artist);
            container=(RelativeLayout)v.findViewById(R.id.music_list_element_container);
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
        final MusicInformation currentMusic=qm.getQueue().get(position);
        holder.title.setText(currentMusic.getTitle());
        holder.artist.setText(currentMusic.getArtist());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Clicked");
            }
        });
    }

    @Override
    public int getItemCount() {
        return qm.getQueue().size();
    }

}