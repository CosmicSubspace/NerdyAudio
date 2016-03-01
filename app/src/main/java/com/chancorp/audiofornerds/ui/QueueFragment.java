//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.chancorp.audiofornerds.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.file.QueueManager;
import com.chancorp.audiofornerds.helper.ClansFABHelper;
import com.chancorp.audiofornerds.interfaces.MusicInformationUpdateListener;
import com.emtronics.dragsortrecycler.DragSortRecycler;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;


public class QueueFragment extends Fragment implements View.OnClickListener, MusicInformationUpdateListener{
    public static final String LOG_TAG="CS_AFN";

    RecyclerView mRecyclerView;
    QueueAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    FloatingActionMenu fam;
    FloatingActionButton[] fabs=new FloatingActionButton[4];
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


        //TODO list scrolling is not working well.
        //This _may_ conflict with the DragSortRecycler...
        //Probably not though.
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                qm.remove(viewHolder.getAdapterPosition());

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


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


        mAdapter = new QueueAdapter(qm);
        mRecyclerView.setAdapter(mAdapter);


        refreshBtn = (Button) v.findViewById(R.id.tab_queue_refresh);
        refreshBtn.setOnClickListener(this);

        //TODO more options for fam: shuffle, order, etc
        fam =(FloatingActionMenu)v.findViewById(R.id.queue_tab_fab);
        fam.setOnClickListener(this);
        ClansFABHelper.setScalingAnimation(fam, R.drawable.ic_close_white_24dp, R.drawable.ic_sort_white_24dp);

        fabs[0]=(FloatingActionButton)v.findViewById(R.id.queue_tab_fab_sub_shuffle);
        fabs[1]=(FloatingActionButton)v.findViewById(R.id.queue_tab_fab_sub_reverse);
        fabs[2]=(FloatingActionButton)v.findViewById(R.id.queue_tab_fab_sub_sort_artist);
        fabs[3]=(FloatingActionButton)v.findViewById(R.id.queue_tab_fab_sub_sort_name);
        fabs[0].setOnClickListener(this);
        fabs[1].setOnClickListener(this);
        fabs[2].setOnClickListener(this);
        fabs[3].setOnClickListener(this);


        return v;

    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if (id==R.id.tab_queue_refresh){
            mAdapter.notifyDataSetChanged();
        }else if (id==R.id.queue_tab_fab_sub_shuffle){
            qm.shuffleQueue();
            mAdapter.notifyDataSetChanged();
        }else if (id==R.id.queue_tab_fab_sub_sort_name){
            qm.sortByTitle();
            mAdapter.notifyDataSetChanged();
        }else if (id==R.id.queue_tab_fab_sub_sort_artist){
            qm.sortByArtist();
            mAdapter.notifyDataSetChanged();
        }else if (id==R.id.queue_tab_fab_sub_reverse){
            qm.reverseQueue();
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void musicInformationUpdated(int index) {
        if (index<0){
            mAdapter.notifyDataSetChanged();
        }else{
            //TODO this sometimes causes [java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling]
            mAdapter.notifyItemChanged(index);
        }

    }

    @Override
    public void onDetach(){
        qm.removeMusicInformationUpdateListener(this);
        super.onDetach();
    }


}
