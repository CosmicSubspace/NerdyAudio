//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.chancorp.audiofornerds.ui;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.file.FileManager;
import com.chancorp.audiofornerds.interfaces.CompletionListener;
import com.ninthavenue.FileChooser;

import java.io.File;


public class LibraryFragment extends Fragment implements View.OnClickListener {

    public static final String LOG_TAG="CS_AFN";

    RecyclerView mRecyclerView;
    MusicListAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    TextView currentDir, scanning;
    Button dirBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_frag_library, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.library_tab_recyclerview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MusicListAdapter(FileManager.getInstance().getMusics());
        mRecyclerView.setAdapter(mAdapter);


        dirBtn = (Button) v.findViewById(R.id.tab_library_dir);
        dirBtn.setOnClickListener(this);

        currentDir=(TextView)v.findViewById(R.id.library_tab_current_directory);
        scanning=(TextView)v.findViewById(R.id.library_tab_scanning);

        //TODO make file browser inside the tab, not on a popup thing.


        if (FileManager.getInstance().isScanning()) scanUI();
        else scanCompleteUI();
        updateUI();
        return v;

    }

    private void updateUI(){
        mAdapter.newData(FileManager.getInstance().getMusics());
        currentDir.setText(FileManager.getInstance().getCurrentDirectoryPath());
    }
    private void scanUI(){
        scanning.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }
    private void scanCompleteUI(){
        scanning.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        /*
        if (id == R.id.tab_library_refresh) {
            FileManager.getInstance().discover(FileManager.getInstance().getCurrentDirectoryPath(), new CompletionListener() {
                @Override
                public void onComplete(String s) {
                    LibraryFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.newData(FileManager.getInstance().getMusics());

                        }
                    });
                }
            });
        }else*/ if (id==R.id.tab_library_dir){
            new FileChooser(getActivity(),FileManager.getInstance().getCurrentDirectory()).setFileListener(new FileChooser.FileSelectedListener() {
                @Override public void fileSelected(final File file) {
                    Log.d(LOG_TAG,"File chosen:"+file.getAbsolutePath());
                    FileManager.getInstance().setDirectory(file);
                    scanUI();
                    FileManager.getInstance().discover(FileManager.getInstance().getCurrentDirectoryPath(), new CompletionListener() {
                        @Override
                        public void onComplete(String s) {
                            LibraryFragment.this.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateUI();

                                    scanCompleteUI();

                                }
                            });
                        }
                    });
                }}).showDialog();
        }
    }
}