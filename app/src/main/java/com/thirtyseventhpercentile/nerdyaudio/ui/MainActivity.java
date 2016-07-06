//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.animation.MixNode;
import com.thirtyseventhpercentile.nerdyaudio.animation.TestMixable;
import com.thirtyseventhpercentile.nerdyaudio.audio.AudioPlayer;
import com.thirtyseventhpercentile.nerdyaudio.audio.VisualizationBuffer;
import com.thirtyseventhpercentile.nerdyaudio.audio.Waveform;
import com.thirtyseventhpercentile.nerdyaudio.file.FileManager;
import com.thirtyseventhpercentile.nerdyaudio.file.MusicInformation;
import com.thirtyseventhpercentile.nerdyaudio.file.QueueManager;
import com.thirtyseventhpercentile.nerdyaudio.helper.ErrorLogger;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.interaction.VolumeControls;
import com.thirtyseventhpercentile.nerdyaudio.service.BackgroundMusicService;
import com.thirtyseventhpercentile.nerdyaudio.settings.SidebarSettings;
import com.thirtyseventhpercentile.nerdyaudio.visuals.PlayControlsView;
import com.thirtyseventhpercentile.nerdyaudio.visuals.VisualizationView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;


//TODO : Numerical Setting Input
//TODO : Prettier Queue Elements
//TODO : Better Library Browser
//TODO : Colorize Visuals
//TODO : Better UI Colors
//TODO : Shuffle & Repeat
//TODO : Waveform color always accent?
//TODO : Ball Visuals more settings
//TODO : Playlist/state save on exit
//TODO : Headphone Controls
//TODO : Playback device selection
//TODO : Artwork existance check on actual play
//TODO : Dynamic Range Compression
//TODO : CircleVisuals base radius setting
//TODO : Lowpass on FFT result
//TODO : Spline interpolation on FFT result
//TODO : Pocket Play / Cinema Mode
//TODO : Display audio length on queue/file
//TODO : ClashWithDash.mp3 error
//TODO : L/R Balance Filter
//TODO : Filters enable toggle
//TODO : Replace assertioons with exceptions.


/**
 * Other Libraries:
 * Clans FAB [Apache]
 * Ringdroid [Apache]
 * Ninthavenue FileChooser [PD]
 * Meapsoft FFT [GPL v2]
 * Dragsortrecycler [Apache]
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DrawerLayout.DrawerListener {
    public final String LOG_TAG = "CS_AFN";
    Waveform wf;
    PlayControlsView wfv;
    AudioPlayer ap;
    VisualizationBuffer vb;
    QueueManager qm;
    FileManager fm;
    VisualizationManager vm;
    SidebarSettings sbs;

    private Handler mHandler = new Handler();

    VisualizationView vv;

    DrawerLayout dl;

    RelativeLayout settingBtn;
    ScrollView sideContainer;

    SharedPreferences sf;

    VolumeControls volCtrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Thread.UncaughtExceptionHandler orig = Thread.getDefaultUncaughtExceptionHandler();
        final Context c = getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                Log2.log(0, this, "UncaughtException logged:", ErrorLogger.logToString(e));
                //Toast.makeText(c, ErrorLogger.logToString(e), Toast.LENGTH_SHORT).show();
                FileWriter f;
                try {
                    f = new FileWriter(
                            new File(Environment.getExternalStorageDirectory() + "/AFN_exceptions.txt")
                            , true);
                    f.write("\n\n\n" + DateFormat.getDateTimeInstance().format(new Date()) + "\n");
                    f.write(ErrorLogger.logToString(e));
                    f.flush();
                    f.close();
                } catch (IOException e1) {
                    ErrorLogger.log(e1);
                } //Double exception?

                Log2.dumpLogsAsync();

                orig.uncaughtException(thread, e);
            }
        });


        requestPermission();

        Log2.log(1, this, "MainActivity Created!");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sf = getPreferences(Context.MODE_PRIVATE);

        String[] files = fileList();
        for (String i : files) {
            Log2.log(1, this, "File: " + i);
        }


        qm = QueueManager.getInstance();
        ap = AudioPlayer.getInstance();
        vb = VisualizationBuffer.getInstance();
        fm = FileManager.getInstance();
        wf = Waveform.getInstance();
        sbs = SidebarSettings.instantiate(getApplicationContext());
        vm = VisualizationManager.getInstance();

        volCtrl = new VolumeControls(getApplicationContext(), qm);

        //ap.setBufferFeedListener(vb);

        qm.passContext(getApplicationContext());


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Library"));
        tabLayout.addTab(tabLayout.newTab().setText("Queue"));
        tabLayout.addTab(tabLayout.newTab().setText("Filters"));
        tabLayout.addTab(tabLayout.newTab().setText("Now Playing"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                int pos = tab.getPosition();
                if (pos == 0) ft.replace(R.id.tab_area, new LibraryFragment());
                else if (pos == 1) ft.replace(R.id.tab_area, new QueueFragment());
                else if (pos == 2) ft.replace(R.id.tab_area, new FiltersFragment());
                else if (pos == 3) ft.replace(R.id.tab_area, new NowPlayingFragment());

                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.tab_area, new LibraryFragment()).commit();

        wfv = (PlayControlsView) findViewById(R.id.waveform);
        wfv.setSpacing(0);
        wfv.setTimestampVisibility(true);
        wfv.setTimestampSize(16);
        wfv.setTimestampColor(Color.WHITE);

        wfv.setTimestampOffset(30, 10);
        wfv.setTimestampBackgroundColor(Color.argb(128, 0, 0, 0));

        wfv.setWaveform(wf);
        qm.addQueueListener(wfv);
        qm.addProgressStringListener(wfv);

        vv = (VisualizationView) findViewById(R.id.visualization);

        dl = (DrawerLayout) findViewById(R.id.drawer_layout);

        dl.setDrawerListener(this);

        settingBtn = (RelativeLayout) findViewById(R.id.settings_btn);
        settingBtn.setOnClickListener(this);

        //statusText=(TextView)findViewById(R.id.status);

        sideContainer = (ScrollView) findViewById(R.id.drawer_scroll);
        sideContainer.addView(sbs.getView(getLayoutInflater(), sideContainer, null));

    }

    private static final int PERM_REQ_INTENT = 217;

    private void requestPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERM_REQ_INTENT);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERM_REQ_INTENT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();

                } else {
                    Log2.log(3, this, "Ext. Storage permission denied.");
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "You won't be able to play music...", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.db_5) {
            for (int i = 0; i < fm.getMusics().size(); i++) {
                qm.addMusic(fm.getMusics().get(i));
            }
            //qm.prepareWaveform();

        } else if (id == R.id.db_3) {
            qm.parseQueueFromFile(new File("storage/sdcard0/PlaylistBackup/R1.txt"));
        } else if (id == R.id.db_4) {
            qm.addMusic(new MusicInformation("storage/extSdCard/00_Personal_DATA/1_Music/AC24/M_5PM.mp3", this));
        } else if (id == R.id.db_1) {
            MixNode<TestMixable> mn = new MixNode<TestMixable>("T");
            MixNode<TestMixable> mn1 = new MixNode<TestMixable>("T1", new TestMixable(0));
            MixNode<TestMixable> mn2 = new MixNode<TestMixable>("T2", new TestMixable(10));
            mn.addNode(mn1);
            mn.addNode(mn2);

            for (int i = 0; i < 100; i++) {
                mn1.getInfluence().set(i / 100.0f);
                mn2.getInfluence().set(1.0f - i / 100.0f);
                Log2.log(2, this, mn.getValue(0).value);
            }
        } else if (id == R.id.db_6) {
            Log2.log(2, this, "Starting Service....");
            Intent itt = new Intent(this, BackgroundMusicService.class);
            itt.setAction(BackgroundMusicService.START_SERVICE);
            startService(itt);
        } else if (id == R.id.db_7) {
            Log2.log(2, this, "Stopping Service....");
            Intent itt = new Intent(this, BackgroundMusicService.class);
            itt.setAction(BackgroundMusicService.STOP_SERVICE);
            startService(itt);
            //int a=0/0;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.removeCallbacks(mUpdateClockTask);
        mHandler.postDelayed(mUpdateClockTask, 100);
        if (wfv != null) wfv.refreshPlaying();

        Log2.log(2, this, "Stopping Service....");
        Intent itt = new Intent(this, BackgroundMusicService.class);
        itt.setAction(BackgroundMusicService.STOP_SERVICE);
        startService(itt);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mUpdateClockTask);
        vm.saveSettings();

        if (ap.isPlaying()) {
            Log2.log(2, this, "Starting Service....");
            Intent itt = new Intent(this, BackgroundMusicService.class);
            itt.setAction(BackgroundMusicService.START_SERVICE);
            startService(itt);
        }
    }


    @Override
    public void onStop() {
        Log2.dumpLogsAsync();

        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log2.log(2, this, "I'm dyingggggggggg");
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        /*
        if (id==R.id.controls_play){
            if (ap!=null){
                if (ap.isPlaying()){
                    ap.pause();
                    play.setBackgroundResource(R.drawable.ic_play_arrow_white_48dp);
                }else if (ap.isPaused()){
                    ap.playAudio();
                    play.setBackgroundResource(R.drawable.ic_pause_white_48dp);
                }else{
                    qm.play();
                    play.setBackgroundResource(R.drawable.ic_pause_white_48dp);
                }
            }
        }else if (id==R.id.settings_btn){
            dl.openDrawer(Gravity.RIGHT);
        }else if (id==R.id.controls_fastforward){
            qm.playNextFile();
        }else if (id==R.id.controls_rewind){
            qm.playPreviousFile();
        }*/
        if (id == R.id.settings_btn) {
            dl.openDrawer(Gravity.RIGHT);
        }
    }

    public void updateStatusString() {
        //statusText.setText(qm.getStatusString());
    }

    private Runnable mUpdateClockTask = new Runnable() {
        public void run() {
            updateStatusString();
            mHandler.postDelayed(mUpdateClockTask, 1000);
        }
    };


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!volCtrl.event(event)) {
            return super.dispatchKeyEvent(event);
        } else return true;
    }

}


