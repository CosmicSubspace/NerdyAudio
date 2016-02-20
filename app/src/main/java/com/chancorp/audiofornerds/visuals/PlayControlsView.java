package com.chancorp.audiofornerds.visuals;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.animation.AnimatableShape;
import com.chancorp.audiofornerds.animation.PrimitivePaths;
import com.chancorp.audiofornerds.audio.AudioPlayer;
import com.chancorp.audiofornerds.audio.Waveform;
import com.chancorp.audiofornerds.file.MusicInformation;
import com.chancorp.audiofornerds.file.QueueManager;
import com.chancorp.audiofornerds.helper.BitmapConversions;
import com.chancorp.audiofornerds.interfaces.NewSongListener;
import com.chancorp.audiofornerds.interfaces.ProgressStringListener;

public class PlayControlsView extends View implements ProgressStringListener, NewSongListener {
    int w, h;
    Waveform wf;
    Paint pt;
    RectF playBtnBounds, prevBtnBounds, nextBtnBounds, waveformBounds, artBounds;
    int menuColor, buttonColor, textPrimary, textSecondary;
    int playedColor = Color.BLACK, remainingColor = Color.GRAY, timestampColor = Color.WHITE, timestampBackgroundColor = Color.BLACK;
    int timestampSize = 24;
    int albumArtSize = 100;//dp
    int waveformSize = 50; //dp
    int buttonsSize = 36;//dp
    int albumArtMargin = 12;//dp
    float spacing = 0.3f;
    float currentPosition = 0.5f;
    float timestampOffsetX = 100, timestampOffsetY = 100;
    AudioPlayer ap;
    QueueManager qm;
    boolean displayTimeStamp = true;
    public static final String LOG_TAG = "CS_AFN";
    float density;

    AnimatableShape playBtn;

    public PlayControlsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
        density = getResources().getDisplayMetrics().density;
        qm = QueueManager.getInstance();
        ap = AudioPlayer.getInstance();

        menuColor = getResources().getColor(R.color.colorPrimary);
        buttonColor = Color.WHITE;
        textPrimary = getResources().getColor(R.color.colorLightPrimaryText);
        textSecondary = getResources().getColor(R.color.colorLightSecondaryText);

        prepareLayout();
    }

    public void setWaveform(Waveform w) {
        this.wf = w;
        invalidate();
    }
    Path temp;
    protected void prepareLayout() {
        float buttonsAreaW = w - albumArtSize * density;
        float buttonsAreaIni = albumArtSize * density;
        float halfButtonPx = buttonsSize * density / 2.0f;
        prevBtnBounds = new RectF(buttonsAreaIni + buttonsAreaW * (1.0f / 6.0f) - halfButtonPx, h - 16 * density - halfButtonPx,
                buttonsAreaIni + buttonsAreaW * (1.0f / 6.0f) + halfButtonPx, h - 16 * density + halfButtonPx);
        playBtnBounds = new RectF(buttonsAreaIni + buttonsAreaW * (3.0f / 6.0f) - halfButtonPx, h - 16 * density - halfButtonPx,
                buttonsAreaIni + buttonsAreaW * (3.0f / 6.0f) + halfButtonPx, h - 16 * density + halfButtonPx);
        nextBtnBounds = new RectF(buttonsAreaIni + buttonsAreaW * (5.0f / 6.0f) - halfButtonPx, h - 16 * density - halfButtonPx,
                buttonsAreaIni + buttonsAreaW * (5.0f / 6.0f) + halfButtonPx, h - 16 * density + halfButtonPx);
        waveformBounds = new RectF(0, 0, w, waveformSize * density);
        artBounds = new RectF(albumArtMargin * density, (albumArtMargin + waveformSize) * density, (albumArtSize - albumArtMargin) * density, (albumArtSize - albumArtMargin + waveformSize) * density);


        playBtn=new AnimatableShape(PrimitivePaths.triangle(buttonsSize/2.0f*density),buttonsAreaIni + buttonsAreaW * (3.0f / 6.0f),h - 16 * density - buttonsSize*density,1,30);
        playBtn=new AnimatableShape(PrimitivePaths.triangle(50),50,50,1,0);
        temp=playBtn.getPointsCompound().toPath();
    }


    String title = new String();
    String artist = new String();
    Bitmap albumArt;

    protected void parseMusicInformation(MusicInformation mi) {
        title = mi.getTitle();
        artist = mi.getArtist();
        if (mi.getArtByteArray() != null) {
            albumArt = BitmapConversions.decodeSampledBitmapFromResource(mi.getArtByteArray(), Math.round(artBounds.width()), Math.round(artBounds.height()));
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {

        //Log.d(LOG_TAG, "Drawing WaveformView" + w + " | " + h);
        if (wf != null && ap != null && wf.isReady() && wf.getFilename().equals(ap.getSourceString())) {
            setCurrentPosition((float) (ap.getMusicCurrentFrame() / (double) wf.getNumOfFrames()));
            float spacing = getSpacingBetween();
            float width = getBarWidth();
            float perBar = 1 / (float) wf.getDivisions();
            for (int i = 0; i < wf.getDivisions(); i++) {

                if (i * perBar > currentPosition) pt.setColor(remainingColor);
                else if ((i + 1) * perBar < currentPosition) pt.setColor(playedColor);
                else
                    pt.setColor(rampColor(playedColor, remainingColor, (currentPosition - i * perBar) / perBar));

                //Log.v(LOG_TAG, "Drawing"+(i*spacing)+" to "+(i*spacing+width));
                canvas.drawRect(i * spacing, waveformSize * density, i * spacing + width, waveformSize * density * (1 - wf.getRatio(i)), pt);
            }


            if (displayTimeStamp) {

                //TODO Make this prettier.

                float xPadding = 4;
                float density = getContext().getResources().getDisplayMetrics().density;


                pt.setTextSize(timestampSize * density);

                String s = wf.frameNumberToTimeStamp(ap.getMusicCurrentFrame());
                Paint.FontMetrics fm = new Paint.FontMetrics();
                pt.setTextAlign(Paint.Align.CENTER);
                pt.getFontMetrics(fm);

                pt.setColor(timestampBackgroundColor);
                //Log.d(LOG_TAG, "Drawing rect at ");
                //canvas.drawRect(canvasX - pt.measureText(s) / 2.0f, canvasY+fm.ascent, canvasX + pt.measureText(s) / 2.0f, canvasY+fm.descent, pt);
                canvas.drawRect(w - pt.measureText(s) - timestampOffsetX * density - xPadding * density, h - fm.descent + fm.ascent - timestampOffsetY * density, w - timestampOffsetX * density + xPadding * density, h - timestampOffsetY * density, pt);
                pt.setColor(timestampColor);

                //Log.d(LOG_TAG, "Drawing text at " + canvasX + " | " + (canvasY + -(fm.ascent + fm.descent)));
                //canvas.drawText(s, canvasX, canvasY, pt);
                canvas.drawText(s, w - pt.measureText(s) / 2.0f - timestampOffsetX * density, h - fm.descent - timestampOffsetY * density, pt);
            }


        }

        pt.setColor(menuColor);
        canvas.drawRect(0, waveformSize * density, w, h, pt);

        if (true) { //TODO better alignment here.
            pt.setTextAlign(Paint.Align.LEFT);
            pt.setColor(textPrimary);
            pt.setTextSize(24 * density);
            Paint.FontMetrics fm = new Paint.FontMetrics();

            canvas.drawText(title, albumArtSize * density, (waveformSize + 24) * density, pt);

            pt.setColor(textSecondary);
            pt.setTextSize(16 * density);
            fm = new Paint.FontMetrics();

            canvas.drawText(artist, albumArtSize * density, (waveformSize + 24 + 16) * density, pt);

            //TODO Optimize this part. This only needs to be called once per track init.
            if (albumArt != null) {
                canvas.drawBitmap(albumArt, null, artBounds, pt);
                Log.d(LOG_TAG, "trying to draw..");
            }
        }

        pt.setColor(Color.argb(50, 0, 0, 0));
        canvas.drawRect(prevBtnBounds, pt);
        canvas.drawRect(playBtnBounds, pt);
        canvas.drawRect(nextBtnBounds, pt);

        pt.setColor(Color.RED);
        //canvas.drawPath(PrimitivePaths.square(50), pt);
        //canvas.drawPath(new AnimatableShape(PrimitivePaths.square(50),0,0,1,0).getPointsCompound().toPath(),pt);
        canvas.drawPath(playBtn.getPointsCompound().toPath(),pt);

        invalidate(); //TODO is this good practice?


    }

    float iniX, iniY;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_DOWN) {
            iniX = ev.getX();
            iniY = ev.getY();
        } else if (action == MotionEvent.ACTION_MOVE) {

        } else if (action == MotionEvent.ACTION_UP) {
            if (waveformBounds.contains(ev.getX(), ev.getY())) {
                float totalTime = (float) (wf.getNumOfFrames() / (double) ap.getSampleRate());
                ap.seekTo(totalTime * ev.getX() / w);
            } else if (prevBtnBounds.contains(ev.getX(), ev.getY())) {
                qm.playPreviousFile();
            } else if (playBtnBounds.contains(ev.getX(), ev.getY())) {
                if (ap != null) {
                    if (ap.isPlaying()) {
                        ap.pause();
                    } else if (ap.isPaused()) {
                        ap.playAudio();
                    } else {
                        qm.playFile();
                    }
                }
            } else if (nextBtnBounds.contains(ev.getX(), ev.getY())) {
                qm.playPreviousFile();
            }
        }
        return true;
    }


    protected int rampColor(int colorA, int colorB, float ramp) {
        //Log.d(LOG_TAG,"Ramping color..."+colorA+" | "+colorB+" | "+ramp);
        return Color.argb(Math.round(Color.alpha(colorA) * ramp + Color.alpha(colorB) * (1.0f - ramp)),
                Math.round(Color.red(colorA) * ramp + Color.red(colorB) * (1.0f - ramp)),
                Math.round(Color.green(colorA) * ramp + Color.green(colorB) * (1.0f - ramp)),
                Math.round(Color.blue(colorA) * ramp + Color.blue(colorB) * (1.0f - ramp)));

    }

    public void setSpacing(float f) {
        this.spacing = f;
    }

    public void setCurrentPosition(float f) {
        currentPosition = f;
    }

    public void setPlayedColor(int color) {
        playedColor = color;
    }

    public void setRemainingColor(int color) {
        remainingColor = color;
    }

    public void setTimestampColor(int color) {
        timestampColor = color;
    }

    public void setTimestampBackgroundColor(int color) {
        timestampBackgroundColor = color;
    }

    public void setTimestampVisibility(boolean b) {
        this.displayTimeStamp = b;
    }

    public void setTimestampSize(int x) {
        this.timestampSize = x;
    }

    public void setTimestampOffset(float x, float y) {
        this.timestampOffsetX = x;
        this.timestampOffsetY = y;
    }

    protected float getSpacingBetween() {
        if (wf != null) {
            return w / (wf.getDivisions() * (1.0f + spacing) - spacing) * (spacing + 1.0f);
        }
        return 0;
    }

    protected float getBarWidth() {
        if (wf != null) {
            return w / (wf.getDivisions() * (1.0f + spacing) - spacing);
        }
        return 0;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;
        prepareLayout();
    }


    @Override
    public void newSong(MusicInformation mi) {
        parseMusicInformation(mi);
    }

    @Override
    public void report(String s) {
        //TODO something here.
    }
}
