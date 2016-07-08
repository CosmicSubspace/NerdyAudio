//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.cosmicsubspace.nerdyaudio.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ninthavenue.FileChooser;
import com.cosmicsubspace.nerdyaudio.R;
import com.cosmicsubspace.nerdyaudio.file.FileManager;
import com.cosmicsubspace.nerdyaudio.helper.Log2;
import com.cosmicsubspace.nerdyaudio.interfaces.CompletionListener;
import com.cosmicsubspace.nerdyaudio.visuals.PlayControlsView;

import java.io.File;


public class LibraryFragment extends Fragment implements View.OnClickListener {

    public static final String LOG_TAG = "CS_AFN";

    RecyclerView mRecyclerView;
    MusicListAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    TextView currentDir, currentGrouping, currentSorting, scanning;
    View dirBtn, groupingBtn, sortBtn;

    PopupMenu groupingPopup, sortingPopup;

    FileManager fm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fm = FileManager.getInstance();

        View v = inflater.inflate(R.layout.tab_frag_library, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.library_tab_recyclerview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MusicListAdapter(fm);
        mRecyclerView.setAdapter(mAdapter);


        dirBtn = v.findViewById(R.id.tab_library_dir);
        dirBtn.setOnClickListener(this);

        groupingBtn = v.findViewById(R.id.library_tab_group_btn);
        groupingBtn.setOnClickListener(this);

        sortBtn = v.findViewById(R.id.library_tab_sort_btn);
        sortBtn.setOnClickListener(this);

        currentDir = (TextView) v.findViewById(R.id.library_tab_current_directory);
        currentGrouping = (TextView) v.findViewById(R.id.library_tab_current_grouping);
        currentSorting = (TextView) v.findViewById(R.id.library_tab_current_sort);


        groupingPopup = new PopupMenu(getContext(), groupingBtn);
        groupingPopup.getMenuInflater().inflate(R.menu.library_group, groupingPopup.getMenu());
        groupingPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_lib_group_none:
                        fm.setGrouping(FileManager.GROUPING_NONE);
                        currentGrouping.setText("No Grouping.");
                        break;
                    case R.id.menu_lib_group_album:
                        fm.setGrouping(FileManager.GROUPING_ALBUM);
                        currentGrouping.setText("Album");
                        break;
                    case R.id.menu_lib_group_artist:
                        fm.setGrouping(FileManager.GROUPING_ARTIST);
                        currentGrouping.setText("Artist");
                        break;
                    case R.id.menu_lib_group_directory:
                        fm.setGrouping(FileManager.GROUPING_DIRECTORY);
                        currentGrouping.setText("Directory");
                        break;
                    case R.id.menu_lib_group_first_letter:
                        fm.setGrouping(FileManager.GROUPING_TITLE_FIRST);
                        currentGrouping.setText("Title First Letter");
                        break;

                }
                updateUI();
                return true;
            }
        });

        sortingPopup = new PopupMenu(getContext(), sortBtn);
        sortingPopup.getMenuInflater().inflate(R.menu.library_sort, sortingPopup.getMenu());
        sortingPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_lib_sort_none:
                        fm.setSorting(FileManager.SORTING_NONE);
                        currentSorting.setText("No Sorting.");
                        break;
                    case R.id.menu_lib_sort_album:
                        fm.setSorting(FileManager.SORTING_ALBUM);
                        currentSorting.setText("Album");
                        break;
                    case R.id.menu_lib_sort_name:
                        fm.setSorting(FileManager.SORTING_TITLE);
                        currentSorting.setText("Title");
                        break;
                    case R.id.menu_lib_sort_artist:
                        fm.setSorting(FileManager.SORTING_ARTIST);
                        currentSorting.setText("Artist");
                        break;
                }
                updateUI();
                return true;
            }
        });


        scanning = (TextView) v.findViewById(R.id.library_tab_scanning);

        //TODO make file browser inside the tab, not on a popup thing.

        if (fm.isScanning()) scanUI();
        else scanCompleteUI();
        updateUI();

        v.post(new Runnable() {
            @Override
            public void run() {
                if (PlayControlsView.getInstance() != null)
                    PlayControlsView.getInstance().expand(false);
            }
        });

        return v;
    }

    private void updateUI() {
        mAdapter.updateMusicList();
        currentDir.setText(fm.getCurrentDirectoryPath());
    }

    private void scanUI() {
        scanning.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void scanCompleteUI() {
        scanning.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        /*
        if (id == R.id.tab_library_refresh) {
            fm.discover(fm.getCurrentDirectoryPath(), new CompletionListener() {
                @Override
                public void onComplete(String s) {
                    LibraryFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.newData(fm.getMusics());

                        }
                    });
                }
            });
        }else*/
        if (id == R.id.tab_library_dir) {
            new FileChooser(getActivity(), fm.getCurrentDirectory()).setFileListener(new FileChooser.FileSelectedListener() {
                @Override
                public void fileSelected(final File file) {
                    Log2.log(1, this, "File chosen:" + file.getAbsolutePath());
                    fm.setDirectory(file);
                    scanUI();
                    fm.discover(fm.getCurrentDirectoryPath(), new CompletionListener() {
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
                    }, getContext());
                }
            }).showDialog();
        } else if (id == R.id.library_tab_group_btn) {
            groupingPopup.show();
        } else if (id == R.id.library_tab_sort_btn) {
            sortingPopup.show();
        }
    }

}