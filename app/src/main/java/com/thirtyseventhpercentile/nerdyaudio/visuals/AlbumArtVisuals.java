package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.thirtyseventhpercentile.nerdyaudio.file.MusicInformation;
import com.thirtyseventhpercentile.nerdyaudio.file.QueueManager;
import com.thirtyseventhpercentile.nerdyaudio.helper.BitmapConversions;
import com.thirtyseventhpercentile.nerdyaudio.helper.SimpleMaths;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.QueueListener;
import com.thirtyseventhpercentile.nerdyaudio.settings.AlbumArtSettings;
import com.thirtyseventhpercentile.nerdyaudio.settings.BaseSetting;

/**
 * Created by Chan on 3/24/2016.
 */
public class AlbumArtVisuals extends BaseRenderer implements QueueListener {
    AlbumArtSettings newSettings = null;

    Bitmap art;
    Paint pt;
    public AlbumArtVisuals(float density) {
        super(density);
        QueueManager.getInstance().addQueueListener(this);
        pt=new Paint();
        newSong(QueueManager.getInstance().getCurrentlyPlaying());
    }

    RectF bounds=new RectF();

    public void recalculateBounds(){
        if (art!=null) bounds=SimpleMaths.fit(new RectF(0,0,art.getWidth(),art.getHeight()),new RectF(0,0,w,h));
    }

    private void syncChanges() {
        if (newSettings != null) {
            newSong(QueueManager.getInstance().getCurrentlyPlaying());
            newSettings=null;
        }
    }

    @Override
    public void updated(BaseSetting setting) {
        if (setting instanceof AlbumArtSettings) {
            newSettings = (AlbumArtSettings) setting;
        }
    }

    @Override
    public void dimensionsChanged(int w, int h) {
        recalculateBounds();
    }


    @Override
    public void drawVisuals(Canvas c, int w, int h) {
        syncChanges();
        if (art!=null) c.drawBitmap(art,null,bounds,pt);
    }

    @Override
    public void newSong(MusicInformation mi) {
        if (mi!=null && mi.hasArt()) {
            art = BitmapConversions.decodeSampledBitmapFromResource(mi.getArtByteArray(), w, h);
            recalculateBounds();
        }
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
