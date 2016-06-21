package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.thirtyseventhpercentile.nerdyaudio.file.MusicInformation;
import com.thirtyseventhpercentile.nerdyaudio.file.QueueManager;
import com.thirtyseventhpercentile.nerdyaudio.helper.BitmapConversions;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.helper.SimpleMaths;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.QueueListener;
import com.thirtyseventhpercentile.nerdyaudio.settings.AlbumArtSettings;
import com.thirtyseventhpercentile.nerdyaudio.settings.BaseSetting;

/**
 * Created by Chan on 3/24/2016.
 */
public class AlbumArtVisuals extends BaseRenderer implements QueueListener {
    AlbumArtSettings newSettings = null;
    RectF bounds=new RectF();
    Bitmap art;
    Paint pt;
    public AlbumArtVisuals(float density) {
        super(density);
        QueueManager.getInstance().addQueueListener(this);
        pt=new Paint();
        newSong(QueueManager.getInstance().getCurrentlyPlaying());
    }



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
        //Log2.log(1,this,"Drawing visuals.");
        syncChanges();

        //We're not using any samples, but clear the buffer anyway.
        //TODO art does not draw sometimes...?
        deleteBefore(getCurrentFrame());
        if (art!=null) {
            Log2.log(1,this,"we gonn draw shit.",art,bounds,pt);
            c.drawBitmap(art,null,bounds,pt);
        }
        //Log2.log(1,this,"Drawing finished.");
    }

    @Override
    public void newSong(MusicInformation mi) {
        //Log2.log(2,this,"New Song!");
        if (mi!=null && mi.hasArt()) {
            Log2.log(2,this,"New song has art.");
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
