package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.thirtyseventhpercentile.nerdyaudio.animation.EasingEquations;
import com.thirtyseventhpercentile.nerdyaudio.file.MusicInformation;
import com.thirtyseventhpercentile.nerdyaudio.file.QueueManager;
import com.thirtyseventhpercentile.nerdyaudio.helper.BitmapConversions;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.NewSongListener;
import com.thirtyseventhpercentile.nerdyaudio.settings.AlbumArtSettings;
import com.thirtyseventhpercentile.nerdyaudio.settings.BaseSetting;
import com.thirtyseventhpercentile.nerdyaudio.ui.QueueAdapter;

/**
 * Created by Chan on 3/24/2016.
 */
public class AlbumArtVisuals extends BaseRenderer implements NewSongListener {
    AlbumArtSettings newSettings = null;

    Bitmap art;
    Paint pt;
    public AlbumArtVisuals(float density) {
        super(density);
        QueueManager.getInstance().addNewSongListener(this);
        pt=new Paint();
        newSong(QueueManager.getInstance().getCurrentlyPlaying());
    }

    private void syncChanges() {
        if (newSettings != null) {
            //pass
        }
    }

    @Override
    public void updated(BaseSetting setting) {
        if (setting instanceof AlbumArtSettings) {
            newSettings = (AlbumArtSettings) setting;
        }
    }

    @Override
    public void drawVisuals(Canvas c, int w, int h) {
        syncChanges();
        if (art!=null) c.drawBitmap(art,(w-art.getWidth())/2.0f,(h-art.getHeight())/2.0f,pt);
    }

    @Override
    public void newSong(MusicInformation mi) {
        if (mi!=null && mi.hasArt()) {
            art = BitmapConversions.decodeSampledBitmapFromResource(mi.getArtByteArray(), w, h);
        }
    }
}
