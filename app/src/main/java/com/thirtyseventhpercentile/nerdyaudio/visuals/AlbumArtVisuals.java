package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.content.Context;
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
import com.thirtyseventhpercentile.nerdyaudio.settings.SettingElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chan on 3/24/2016.
 */
public class AlbumArtVisuals extends BaseRenderer implements QueueListener {
    RectF bounds=new RectF();
    Bitmap art;
    Paint pt;

    @Override
    public List<SettingElement> getSettings() {
        return new ArrayList<>();
    }


    @Override
    public String getKey() {
        return "AlbumArtVisuals";
    }

    public AlbumArtVisuals(Context ctxt) {
        super(ctxt);
        QueueManager.getInstance().addQueueListener(this);
        pt=new Paint();
        newSong(QueueManager.getInstance().getCurrentlyPlaying());
    }



    public void recalculateBounds(){
        if (art!=null) bounds=SimpleMaths.fit(new RectF(0,0,art.getWidth(),art.getHeight()),new RectF(0,0,w,h));
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
