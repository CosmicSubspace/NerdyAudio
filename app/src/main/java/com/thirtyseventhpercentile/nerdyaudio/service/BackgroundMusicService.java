package com.thirtyseventhpercentile.nerdyaudio.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.audio.AudioPlayer;
import com.thirtyseventhpercentile.nerdyaudio.file.QueueManager;
import com.thirtyseventhpercentile.nerdyaudio.filters.FilterManager;
import com.thirtyseventhpercentile.nerdyaudio.ui.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Chan on 5/29/2016.
 */

//TODO : Is this REALLY kill-safe?
public class BackgroundMusicService extends Service {
    public final String LOG_TAG="CS_AFN";
    int notificationID = 47129;

    AudioPlayer ap;
    FilterManager fm;
    QueueManager qm;

    public static final String START_SERVICE = "com.thirtyseventhpercentile.nerdyaudio.service.BackgroundMusicService.START_SERVICE";
    public static final String STOP_SERVICE = "com.thirtyseventhpercentile.nerdyaudio.service.BackgroundMusicService.STOP_SERVICE";

    @Override
    public void onCreate() {
        super.onCreate();

        ap=AudioPlayer.getInstance();
        fm=FilterManager.getInstance();
        qm=QueueManager.getInstance();
        //lbm=LocalBroadcastManager.getInstance(this);
        Log.d(LOG_TAG, "Service Created.");


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
            raiseNotification();
        }else if (intent.getAction().equals(STOP_SERVICE)) {
            stopSelf();
        }

        return START_NOT_STICKY;
    }



    public void raiseNotification() {
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this);

        nb.setSmallIcon(R.mipmap.ic_launcher);


        PendingIntent pendInt=PendingIntent.getActivity(this, 18274,new Intent(this,MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);


        nb.setContentIntent(pendInt);
        nb.setContentTitle("NerdyAudio!");
        nb.setOngoing(true);

        nb.setContentText("Playing song...");

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        startForeground(notificationID, nb.build());


    }

}