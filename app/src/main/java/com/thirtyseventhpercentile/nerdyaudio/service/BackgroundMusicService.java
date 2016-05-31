package com.thirtyseventhpercentile.nerdyaudio.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.audio.AudioPlayer;
import com.thirtyseventhpercentile.nerdyaudio.file.MusicInformation;
import com.thirtyseventhpercentile.nerdyaudio.file.QueueManager;
import com.thirtyseventhpercentile.nerdyaudio.filters.FilterManager;
import com.thirtyseventhpercentile.nerdyaudio.helper.BitmapConversions;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.QueueListener;
import com.thirtyseventhpercentile.nerdyaudio.ui.MainActivity;

/**
 * Created by Chan on 5/29/2016.
 */

//TODO : Is this REALLY kill-safe?
public class BackgroundMusicService extends Service implements QueueListener{
    public final String LOG_TAG="CS_AFN";
    int notificationID = 47129;

    private NotificationCompat.Builder nb;

    int largeIconW,largeIconH;

    AudioPlayer ap;
    FilterManager fm;
    QueueManager qm;



    public static final String START_SERVICE = "com.thirtyseventhpercentile.nerdyaudio.service.BackgroundMusicService.START_SERVICE";
    public static final String STOP_SERVICE = "com.thirtyseventhpercentile.nerdyaudio.service.BackgroundMusicService.STOP_SERVICE";

    public static final String PLAY_PAUSE = "com.thirtyseventhpercentile.nerdyaudio.service.BackgroundMusicService.PLAY_PAUSE";
    public static final String NEXT = "com.thirtyseventhpercentile.nerdyaudio.service.BackgroundMusicService.NEXT";
    public static final String PREVIOUS = "com.thirtyseventhpercentile.nerdyaudio.service.BackgroundMusicService.PREVIOUS";

    @Override
    public void onCreate() {
        super.onCreate();

        ap=AudioPlayer.getInstance();
        fm=FilterManager.getInstance();
        qm=QueueManager.getInstance();
        //lbm=LocalBroadcastManager.getInstance(this);
        Log.d(LOG_TAG, "Service Created.");

        nb=new NotificationCompat.Builder(getApplicationContext());


        largeIconH= getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
        largeIconW= getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Canceling notification...");
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(notificationID);
        Log.i(LOG_TAG, "Service Destroyed.");

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(LOG_TAG, "Service Starting");
        if (intent.getAction() == null) {
            //Do nothing.
        }else if (intent.getAction().equals(START_SERVICE)) {
            startNotification();
        }else if (intent.getAction().equals(STOP_SERVICE)) {
            lowerNotification();
        }else if (intent.getAction().equals(PLAY_PAUSE)){
            qm.togglePlay();
        }else if (intent.getAction().equals(NEXT)){
            qm.playNextFile();
        }else if (intent.getAction().equals(PREVIOUS)){
            qm.playPreviousFile();
        }

        return START_NOT_STICKY;
    }



    public void startNotification() {




        nb.setOnlyAlertOnce(true);
        nb.setSmallIcon(R.drawable.ic_icon_notif_00000);


        PendingIntent pendInt=PendingIntent.getActivity(this, 18274, new Intent(this,MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);


        nb.setContentIntent(pendInt);
        nb.setContentTitle("NerdyAudio!");
        nb.setOngoing(true);

        nb.setContentText("Service started.");

        nb.addAction(R.drawable.ic_skip_previous_white_36dp,null,
                PendingIntent.getService(this,12941,
                        new Intent(this,BackgroundMusicService.class).setAction(PREVIOUS),
                        PendingIntent.FLAG_UPDATE_CURRENT));

        nb.addAction(R.drawable.ic_play_arrow_white_36dp,null,
                PendingIntent.getService(this,12941,
                        new Intent(this,BackgroundMusicService.class).setAction(PLAY_PAUSE),
                        PendingIntent.FLAG_UPDATE_CURRENT));

        nb.addAction(R.drawable.ic_skip_next_white_36dp,null,
                PendingIntent.getService(this,12941,
                        new Intent(this,BackgroundMusicService.class).setAction(NEXT),
                        PendingIntent.FLAG_UPDATE_CURRENT));
        //nb.addAction(R.drawable.ic_add_white_24dp,null,null);
        //nb.addAction(R.drawable.ic_add_white_24dp,null,null);
// mId allows you to update the notification later on.
        startForeground(notificationID, nb.build());


    }

    public void updateNotification(MusicInformation mi){

        if (mi!=null){
            nb.setContentTitle(mi.getTitle());
            nb.setContentText(mi.getArtist());
            nb.setLargeIcon(BitmapConversions.decodeSampledBitmapFromResource(mi.getArtByteArray(),largeIconW,largeIconH));
        }else{
            nb.setContentTitle("NerdyAudio");
            nb.setContentText("Nothing playing.");
            nb.setLargeIcon(null);
        }


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify( notificationID, nb.build());
    }

    public void lowerNotification(){



        stopSelf();
    }

    @Override
    public void newSong(MusicInformation mi) {
        updateNotification(mi);
    }

    @Override
    public void playbackStarted() {

    }

    @Override
    public void playbackStopped() {

    }

    @Override
    public void nextSong() {

    }

    @Override
    public void previousSong() {

    }
}