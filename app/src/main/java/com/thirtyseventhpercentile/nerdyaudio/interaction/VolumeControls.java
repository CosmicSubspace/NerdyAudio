package com.thirtyseventhpercentile.nerdyaudio.interaction;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.view.KeyEvent;

import com.thirtyseventhpercentile.nerdyaudio.file.QueueManager;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;

import java.util.ArrayList;

/**
 * Created by Chan on 6/16/2016.
 */
public class VolumeControls {
    AudioManager audio;
    QueueManager qm;

    ArrayList<TimedKeyEvent> interaction = new ArrayList<>();
    TimedKeyEvent lastEvent = new TimedKeyEvent();

    int interactionTimeout = 300;

    //TODO make this more generic by making a UserInputListener or something.
    public VolumeControls(Context c, QueueManager qm) {
        audio = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
        this.qm = qm;
    }

    public boolean event(KeyEvent ke) {
        int action = ke.getAction();
        int keyCode = ke.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            addEvent(new TimedKeyEvent(ke));
            return true;
        } else {
            return false;
        }

    }


    private Handler interactionFinalizerHandler = new Handler();
    private Runnable interactionFinalizerRunnable = new Runnable() {
        @Override
        public void run() {
            endInteraction();
        }
    };


    private void endInteraction() {
        //Log2.log(2, this, getEvent(0), getEvent(1));
        int finalEvent = getInteractionSize() - 1;
        if (finalEvent == 0) { //1-event interaction: Press and hold.
            if (getEvent(0).type == TimedKeyEvent.UP_PRESS) {
                qm.playNextFile();
            } else if (getEvent(0).type == TimedKeyEvent.DOWN_PRESS) {
                qm.playPreviousFile();
            } else {
                Log2.log(3, this, "endInteraction > Something is off. 2");
            }
        } else if (finalEvent == 1) {//2-event interaction: Short press, or double press and hold.
            if (getEvent(1).type == TimedKeyEvent.UP_RELEASE && getEvent(0).type == TimedKeyEvent.UP_PRESS) { //Short up press
                incrementVolume();
            } else if (getEvent(1).type == TimedKeyEvent.DOWN_RELEASE && getEvent(0).type == TimedKeyEvent.DOWN_PRESS) { //Short up press
                decrementVolume();
            } else if ((getEvent(0).type == TimedKeyEvent.UP_PRESS && getEvent(1).type == TimedKeyEvent.DOWN_PRESS)
                    || (getEvent(1).type == TimedKeyEvent.UP_PRESS && getEvent(0).type == TimedKeyEvent.DOWN_PRESS)) {//Both press and hold
                qm.togglePlay();
            } else {
                Log2.log(3, this, "endInteraction > Something is off. 1");
            }
        }

        clearEvents();
    }


    private void addEvent(TimedKeyEvent tke) {
        if (tke.isSameType(lastEvent)) {
            //Log2.log(2,this,"Same Event. passing.",tke,getEvent(getInteractionSize()-1));
            return;
        } else {
            lastEvent = tke;
        }
        Log2.log(1, this, "Processing event.", tke);

        interactionFinalizerHandler.removeCallbacks(interactionFinalizerRunnable);

        interactionFinalizerHandler.postDelayed(interactionFinalizerRunnable, interactionTimeout);

        interaction.add(tke);

        if (tke.isRelease()) { //Temporary.
            interactionFinalizerHandler.removeCallbacks(interactionFinalizerRunnable);
            interactionFinalizerRunnable.run();
        }

    }


    private TimedKeyEvent getEvent(int index) {
        if (index < 0 || index >= interaction.size()) return new TimedKeyEvent(); //I'm lazy.
        return interaction.get(index);
    }


    private int getInteractionSize() {
        return interaction.size();
    }

    private void clearEvents() {
        interaction.clear();
    }


    private void incrementVolume() {
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }

    private void decrementVolume() {
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }

}


class TimedKeyEvent {
    public static final int UP_RELEASE = 1;
    public static final int UP_PRESS = 2;
    public static final int DOWN_RELEASE = 3;
    public static final int DOWN_PRESS = 4;
    public int type = -1;
    public long time = -1;

    public TimedKeyEvent() {
        this.type = -1;
        this.time = -1;
    }

    public TimedKeyEvent(int type, long time) {
        this.type = type;
        this.time = time;
    }

    public TimedKeyEvent(KeyEvent ke) {
        int action = ke.getAction();
        int keyCode = ke.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && action == KeyEvent.ACTION_UP) {
            type = UP_RELEASE;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && action == KeyEvent.ACTION_DOWN) {
            type = UP_PRESS;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && action == KeyEvent.ACTION_UP) {
            type = DOWN_RELEASE;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && action == KeyEvent.ACTION_DOWN) {
            type = DOWN_PRESS;
        } else {
            Log2.log(3, this, "Invalid Event.");
        }

        time = ke.getEventTime();
    }

    public boolean isSameType(TimedKeyEvent tke) {
        if (tke.type == this.type) return true;
        else return false;
    }

    public boolean isRelease() {
        return type == DOWN_RELEASE || type == UP_RELEASE;
    }

    public boolean isPress() {
        return type == DOWN_PRESS || type == UP_PRESS;
    }

    @Override
    public String toString() {
        return "TimedKeyEvent: " + time + " | " + type;
    }
}