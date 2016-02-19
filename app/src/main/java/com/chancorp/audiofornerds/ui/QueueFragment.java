package com.chancorp.audiofornerds.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.file.QueueManager;

/**
 * Created by Chan on 2015-12-16.
 */
public class QueueFragment extends Fragment implements View.OnClickListener{
    public static final String LOG_TAG="CS_AFN";

    RecyclerView mRecyclerView;
    QueueAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    FloatingActionButton fab;

    Button refreshBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_frag_queue, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.queue_tab_recyclerview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new QueueAdapter(QueueManager.getInstance());
        mRecyclerView.setAdapter(mAdapter);


        refreshBtn = (Button) v.findViewById(R.id.tab_queue_refresh);
        refreshBtn.setOnClickListener(this);

        //TODO more options for fab: shuffle, order, etc
        fab=(FloatingActionButton)v.findViewById(R.id.tab_queue_fab);
        fab.setOnClickListener(this);
        return v;

    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if (id==R.id.tab_queue_refresh){
            mAdapter.notifyDataSetChanged();
        }else if (id==R.id.tab_queue_fab){
            QueueManager.getInstance().shuffleQueue();
            mAdapter.notifyDataSetChanged();
        }
    }
}
