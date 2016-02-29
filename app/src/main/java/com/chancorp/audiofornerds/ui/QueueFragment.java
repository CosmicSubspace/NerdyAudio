//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.chancorp.audiofornerds.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.file.QueueManager;
import com.chancorp.audiofornerds.interfaces.MusicInformationUpdateListener;
import com.emtronics.dragsortrecycler.DragSortRecycler;


public class QueueFragment extends Fragment implements View.OnClickListener, MusicInformationUpdateListener{
    public static final String LOG_TAG="CS_AFN";

    RecyclerView mRecyclerView;
    QueueAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    FloatingActionButton fab;
    QueueManager qm;

    Button refreshBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        qm=QueueManager.getInstance();
        qm.addMusicInformationUpdateListener(this);
        View v = inflater.inflate(R.layout.tab_frag_queue, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.queue_tab_recyclerview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setItemAnimator(null);

        DragSortRecycler dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.music_list_element_handle); //View you wish to use as the handle

        dragSortRecycler.setOnItemMovedListener(new DragSortRecycler.OnItemMovedListener() {
            @Override
            public void onItemMoved(int from, int to) {
                qm.move(from, to);
            }
        });

        mRecyclerView.addItemDecoration(dragSortRecycler);
        mRecyclerView.addOnItemTouchListener(dragSortRecycler);
        mRecyclerView.setOnScrollListener(dragSortRecycler.getScrollListener());

        // specify an adapter (see also next example)
        mAdapter = new QueueAdapter(qm);
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

    @Override
    public void musicInformationUpdated(int index) {
        if (index<0){
            mAdapter.notifyDataSetChanged();
        }else{
            mAdapter.notifyItemChanged(index);
        }

    }

    @Override
    public void onDetach(){
        qm.removeMusicInformationUpdateListener(this);
        super.onDetach();
    }


}
