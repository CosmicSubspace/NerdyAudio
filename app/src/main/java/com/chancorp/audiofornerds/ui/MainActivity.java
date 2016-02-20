package com.chancorp.audiofornerds.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chancorp.audiofornerds.audio.AudioPlayer;
import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.file.FileManager;
import com.chancorp.audiofornerds.file.MusicInformation;
import com.chancorp.audiofornerds.file.QueueManager;
import com.chancorp.audiofornerds.helper.ErrorLogger;
import com.chancorp.audiofornerds.settings.SidebarSettings;
import com.chancorp.audiofornerds.sql.DBManager;
import com.chancorp.audiofornerds.visuals.PlayControlsView;
import com.chancorp.audiofornerds.audio.VisualizationBuffer;
import com.chancorp.audiofornerds.audio.Waveform;
import com.chancorp.audiofornerds.visuals.VisualizationView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

//TODO : Animations
//TODO : Numerical Setting Input
//TODO : Prettier Queue Elements
//TODO : Queue Remove/reorder
//TODO : Better Library Browser



public class MainActivity extends AppCompatActivity implements View.OnClickListener, DrawerLayout.DrawerListener{
    public final String LOG_TAG="CS_AFN";
    Waveform wf;
    PlayControlsView wfv;
    AudioPlayer ap;
    VisualizationBuffer vb;
    QueueManager qm;
    FileManager fm;
    SidebarSettings sbs;

    private Handler mHandler = new Handler();

    VisualizationView vv;

    DrawerLayout dl;

    RelativeLayout settingBtn;
    ScrollView sideContainer;

    SharedPreferences sf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Log.i(LOG_TAG, "MainActivity Created!");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //TODO Nessasary?

        sf = getPreferences(Context.MODE_PRIVATE);

        String[] files=fileList();
        for (String i:files){
            Log.d(LOG_TAG,"File: "+i);
        }


        qm=QueueManager.getInstance();
        ap=AudioPlayer.getInstance();
        vb=VisualizationBuffer.getInstance();
        fm=FileManager.getInstance();
        wf=Waveform.getInstance();
        sbs=SidebarSettings.instantiate(getApplicationContext());

        ap.setBufferFeedListener(vb);

        qm.passActivity(this);



        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

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
        getSupportFragmentManager().beginTransaction().replace(R.id.tab_area,new LibraryFragment()).commit();

        wfv=(PlayControlsView)findViewById(R.id.waveform);
        wfv.setSpacing(0);
        wfv.setTimestampVisibility(true);
        wfv.setTimestampSize(16);
        wfv.setTimestampColor(Color.WHITE);
        wfv.setPlayedColor(Color.argb(128, 0, 0, 0));
        wfv.setRemainingColor(Color.argb(128, 128, 128, 128));
        wfv.setTimestampOffset(30, 10);
        wfv.setTimestampBackgroundColor(Color.argb(128, 0, 0, 0));

        wfv.setWaveform(wf);
        qm.addNewSongListener(wfv);
        qm.addProgressStringListener(wfv);
/*
        play=(Button) findViewById(R.id.controls_play);
        rewind=(Button) findViewById(R.id.controls_rewind);
        forward=(Button) findViewById(R.id.controls_fastforward);

        play.setOnClickListener(this);
        rewind.setOnClickListener(this);
        forward.setOnClickListener(this);

        title=(TextView) findViewById(R.id.controls_title);
        title.setSelected(true);
        artist=(TextView) findViewById(R.id.controls_artist);

        art=(ImageView) findViewById(R.id.controls_art);
*/
        vv=(VisualizationView)findViewById(R.id.visualization);

        dl=(DrawerLayout)findViewById(R.id.drawer_layout);

        dl.setDrawerListener(this);

        settingBtn=(RelativeLayout)findViewById(R.id.settings_btn);
        settingBtn.setOnClickListener(this);

        //statusText=(TextView)findViewById(R.id.status);

        sideContainer=(ScrollView)findViewById(R.id.drawer_scroll);
        sideContainer.addView(sbs.getView(getLayoutInflater(),sideContainer,null));





        randomToast();




    }

    public void randomToast(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DBManager dbmanager = new DBManager(MainActivity.this);
                    Random rand=new Random();
                    ArrayList<String> messages=dbmanager.getMessages();
                    final String msg=messages.get(rand.nextInt(messages.size()));
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    ErrorLogger.log(e);
                }
            }
        }).start();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id==R.id.db_1){
            qm.prepareWaveform();

        }else if (id==R.id.db_5){
            for (int i=0;i<fm.getMusics().size();i++) {
                qm.addMusicWithoutWaveformPreparation(fm.getMusics().get(i));
            }
            qm.prepareWaveform();

        }else if (id==R.id.db_2){
            randomToast();
        }else if (id==R.id.db_3){
            qm.parseQueueFromFile(new File("storage/sdcard0/PlaylistBackup/R1.txt"));
        }else if (id==R.id.db_4){
            qm.addMusic(new MusicInformation("storage/extSdCard/00_Personal_DATA/1_Music/AC24/M_5PM.mp3"));
        }else if (id==R.id.db_6){
            ap.seekTo(10);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.removeCallbacks(mUpdateClockTask);
        mHandler.postDelayed(mUpdateClockTask, 100);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mUpdateClockTask);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
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
                    qm.playFile();
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
        if (id==R.id.settings_btn){
            dl.openDrawer(Gravity.RIGHT);
        }
    }

    public void updateUI(){
        //title.setText(qm.getCurrentMusic().getTitle());
        //artist.setText(qm.getCurrentMusic().getArtist());
        //art.setImageBitmap(qm.getCurrentMusic().getArt());
        //TODO implement this in the custom view
    }

    public void updateStatusString(){
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
/*
    private Handler mHandler = new Handler();

    @Override
    public void onResume() {
        super.onResume();
        mHandler.removeCallbacks(mUpdateClockTask);
        mHandler.postDelayed(mUpdateClockTask, 100);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mUpdateClockTask);
    }

    private Runnable mUpdateClockTask = new Runnable() {
        public void run() {
            updateUI();
            mHandler.postDelayed(mUpdateClockTask, 1000);
        }
    };

    public void updateUI() {
        Log.v(LOG_TAG,"Updating UI...");
        if (ap!=null) {
            long currentFrame = ap.getCurrentFrame();
            Log.v(LOG_TAG, "Reported: " + currentFrame);
            Log.v(LOG_TAG, "Setting position to: " + wf.frameNumberToRatio(currentFrame));
            wfv.setCurrentPosition(wf.frameNumberToRatio(currentFrame));
            wfv.invalidate();
        }
    }*/

    /*
    class AudioLoadThread extends Thread {


        private void PlayAudioFileViaAudioTrack(String filePath) throws IOException {
// We keep temporarily filePath globally as we have only two sample sounds now..
            if (filePath == null)
                return;

            int intSize = android.media.AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT);

            AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT, intSize, AudioTrack.MODE_STREAM);


            if (at == null) {
                Log.d(LOG_TAG, "audio track is not initialized ");
                return;
            }

            int count = 64 * 1024; // 64 kb
            //Reading the file..
            byte[] byteData = null;
            File file = null;

            file = new File(filePath);

            byteData = new byte[(int) count];
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);

            } catch (FileNotFoundException e) {
                Log.e(LOG_TAG, "FNFE");
            }

            int bytesread = 0, ret = 0;
            int size = (int) file.length();
            at.play();
            while (bytesread < size) {
                Log.v("AudioForNerds", "read bytes: " + bytesread);
                ret = in.read(byteData, 0, count);
                if (ret != -1) { // Write the byte array to the track
                    at.write(byteData, 0, ret);
                    bytesread += ret;
                } else break;
            }
            in.close();
            at.stop();
            at.release();
        }

        @Override
        public void run() {
            try {
                //PlayAudioFileViaAudioTrack("mnt/sdcard/AE_RenderSuccess.wav");
                PlayAudioFileViaAudioTrack("/mnt/sdcard/marbleWAV.wav");
            } catch (IOException e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                Log.e(LOG_TAG, "IOException\n" + e.toString());
            }
        }
    }*/
}


