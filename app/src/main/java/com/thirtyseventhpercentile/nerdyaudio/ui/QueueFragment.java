//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emtronics.dragsortrecycler.DragSortRecycler;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.file.QueueManager;
import com.thirtyseventhpercentile.nerdyaudio.helper.ClansFABHelper;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.QueueElementUpdateListener;
import com.thirtyseventhpercentile.nerdyaudio.visuals.PlayControlsView;


public class QueueFragment extends Fragment implements View.OnClickListener, QueueElementUpdateListener {
    public static final String LOG_TAG="CS_AFN";

    RecyclerView mRecyclerView;
    QueueAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    FloatingActionMenu fam, famPlaceholder;
    FloatingActionButton[] fabs=new FloatingActionButton[6];
    QueueManager qm;

    TextView refreshBtn, saveBtn,loadBtn;

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

        //mRecyclerView.setItemAnimator(null);


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
        dragSortRecycler.setViewHandleId(R.id.queue_list_element_handle); //View you wish to use as the handle

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


        refreshBtn = (TextView) v.findViewById(R.id.queue_tab_refresh);
        refreshBtn.setOnClickListener(this);
        saveBtn = (TextView) v.findViewById(R.id.queue_tab_save);
        saveBtn.setOnClickListener(this);
        loadBtn = (TextView) v.findViewById(R.id.queue_tab_load);
        loadBtn.setOnClickListener(this);


        fam =(FloatingActionMenu)v.findViewById(R.id.queue_tab_fab);



        fabs[0]=(FloatingActionButton)v.findViewById(R.id.queue_tab_fab_sub_shuffle);
        fabs[1]=(FloatingActionButton)v.findViewById(R.id.queue_tab_fab_sub_reverse);
        fabs[2]=(FloatingActionButton)v.findViewById(R.id.queue_tab_fab_sub_sort_artist);
        fabs[3]=(FloatingActionButton)v.findViewById(R.id.queue_tab_fab_sub_sort_name);
        fabs[4]=(FloatingActionButton)v.findViewById(R.id.queue_tab_fab_sub_rm_dups);
        fabs[5]=(FloatingActionButton)v.findViewById(R.id.queue_tab_fab_sub_rm_all);
        fabs[0].setOnClickListener(this);
        fabs[1].setOnClickListener(this);
        fabs[2].setOnClickListener(this);
        fabs[3].setOnClickListener(this);
        fabs[4].setOnClickListener(this);
        fabs[5].setOnClickListener(this);

        ClansFABHelper.setScalingAnimation(fam, R.drawable.ic_close_white_24dp, R.drawable.ic_sort_white_24dp);

        v.post(new Runnable() {
            @Override
            public void run() {
                if (PlayControlsView.getInstance()!=null) PlayControlsView.getInstance().expand(false);
            }
        });


        return v;

    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if (id==R.id.queue_tab_refresh){
            mAdapter.notifyDataSetChanged();
        }else if (id==R.id.queue_tab_save){
            new PlaylistSaveDialog(getContext(),qm.getQueue()).init();
        }else if (id==R.id.queue_tab_load){
            new PlaylistLoadDialog(getContext()).setLoadedListener(new PlaylistLoadDialog.LoadedListener() {
                @Override
                public void loaded() {
                    mAdapter.notifyDataSetChanged();
                }
            }).init();
            /*
            try {
                qm.parsePlaylist(Playlist.load(getContext(), "TEST"),QueueManager.OVERWRITE, getContext());
            }catch (Exception e) {
                ErrorLogger.log(e);
                Toast.makeText(getContext(), "Error while Loading!", Toast.LENGTH_SHORT).show();
            }*/
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
        }else if (id==R.id.queue_tab_fab_sub_rm_all){
            qm.removeAll();
            mAdapter.notifyDataSetChanged();
        }else if (id==R.id.queue_tab_fab_sub_rm_dups){
            qm.removeDuplicates();
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void elementUpdated(int index) {
        if (index<0){
            mAdapter.notifyDataSetChanged();
        }else{
            //TODO this sometimes causes [java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling]
            for (int i = 0; i < 5; i++) {
                //TODO and this is a _very_ ugly fix.
                try {
                    mAdapter.notifyItemChanged(index);
                    break;
                }catch (IllegalStateException e){ //lol I fixed it
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        }
    }

    @Override
    public void onDetach(){
        qm.removeMusicInformationUpdateListener(this);
        super.onDetach();
    }


}
