package com.chancorp.audiofornerds.visuals;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.animation.Animator;
import com.chancorp.audiofornerds.draw.AnimatableShape;
import com.chancorp.audiofornerds.animation.EasingEquations;
import com.chancorp.audiofornerds.animation.PrimitivePaths;
import com.chancorp.audiofornerds.animation.PropertySet;
import com.chancorp.audiofornerds.audio.AudioPlayer;
import com.chancorp.audiofornerds.audio.Waveform;
import com.chancorp.audiofornerds.draw.AnimatableText;
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
    int buttonPaddings =16;
    int albumArtSize = 100;//dp
    int waveformSize = 50; //dp
    int buttonsSize = 36;//dp
    int albumArtMargin = 12;//dp
    float spacing = 0.3f;
    float currentPosition = 0.5f;
    float timestampOffsetX = 30, timestampOffsetY = 100;
    AudioPlayer ap;
    QueueManager qm;
    boolean displayTimeStamp = true;
    public static final String LOG_TAG = "CS_AFN";
    float density;

    AnimatableShape playBtn;
    PropertySet buttonFollower;

    AnimatableText titleAnimatable;
    PropertySet titleNoArt;

    Animator albumArtColor;
    PropertySet albumArtNone;

    AnimatableShape pauseBtn;
    PropertySet pauseVisible;
    PropertySet pauseInvisible;

    //TODO : Animate _EVERYTHING_



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

        buttonFollower=new PropertySet("Follower").setValue("X",0).setValue("Y",waveformSize*density).setValue("Scale",0.3f).setValue("Rotation",180).setValue("Alpha",1.0f);
        buttonFollower.getInfluence().set(0);
        playBtn=new AnimatableShape(PrimitivePaths.triangle(buttonsSize/2.0f*density),buttonColor,new PropertySet("Basis").setValue("X",buttonsAreaIni + buttonsAreaW * (3.0f / 6.0f)).setValue("Y",h - 16 * density - buttonsSize*density).setValue("Scale",1).setValue("Rotation",30).setValue("Alpha",1.0f));
        playBtn.getAnimator().addPropertySet(buttonFollower);

        pauseBtn=new AnimatableShape(PrimitivePaths.pause(buttonsSize / 2.0f * density),buttonColor,new PropertySet("Basis").setValue("X",buttonsAreaIni + buttonsAreaW * (3.0f / 6.0f)).setValue("Y",h - 16 * density - buttonsSize*density).setValue("Scale",1).setValue("Rotation",0));

        pauseVisible=new PropertySet("PauseVisible").setValue("Alpha",1.0f);
        pauseVisible.getInfluence().set(0);
        pauseInvisible=new PropertySet("PauseInvisible").setValue("Alpha",0.0f);

        pauseBtn.getAnimator().addPropertySet(pauseInvisible);
        pauseBtn.getAnimator().addPropertySet(pauseVisible);


        titleAnimatable=new AnimatableText(new PropertySet("Basis").setValue("X",albumArtSize * density).setValue("Y", waveformSize * density),textPrimary,"",24*density);
        titleNoArt=new PropertySet("NoArt").setValue("X", 0).setValue("Y",waveformSize * density);
        titleNoArt.getInfluence().set(0);
        titleAnimatable.getAnimator().addPropertySet(titleNoArt);

        albumArtColor=new Animator(new PropertySet("Basis").setValue("alpha",255));
        albumArtNone=new PropertySet("NoArt").setValue("alpha",0);
        albumArtNone.getInfluence().set(0);
        albumArtColor.addPropertySet(albumArtNone);


        timestampOffsetY=110;
        timestampOffsetX=30;

        //playBtn=new AnimatableShape(PrimitivePaths.triangle(50),50,50,1,0);


    }


    String title = new String();
    String artist = new String();
    Bitmap albumArt;

    protected void parseMusicInformation(MusicInformation mi) {
        title = mi.getTitle();
        titleAnimatable.setText(mi.getTitle());
        artist = mi.getArtist();
        if (mi.getArtByteArray() != null) {
            albumArt = BitmapConversions.decodeSampledBitmapFromResource(mi.getArtByteArray(), Math.round(artBounds.width()), Math.round(artBounds.height()));
            titleAnimatable.getAnimator().getPropertySet("Basis").getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            titleAnimatable.getAnimator().getPropertySet("NoArt").getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            albumArtColor.getPropertySet("Basis").getInfluence().animate(1,1,EasingEquations.LINEAR);
            albumArtColor.getPropertySet("NoArt").getInfluence().animate(0,1,EasingEquations.LINEAR);
        }else{
            titleAnimatable.getAnimator().getPropertySet("Basis").getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            titleAnimatable.getAnimator().getPropertySet("NoArt").getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            albumArtColor.getPropertySet("Basis").getInfluence().animate(0,1,EasingEquations.LINEAR);
            albumArtColor.getPropertySet("NoArt").getInfluence().animate(1,1,EasingEquations.LINEAR);
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

            buttonFollower.setValue("X",w*currentPosition);
        }

        pt.setColor(menuColor);
        canvas.drawRect(0, waveformSize * density, w, h, pt);

        if (true) { //TODO better alignment here.
            pt.setTextAlign(Paint.Align.LEFT);
            pt.setColor(textPrimary);
            pt.setTextSize(24 * density);

            //canvas.drawText(title, albumArtSize * density, (waveformSize + 24) * density, pt);

            titleAnimatable.draw(canvas,pt);

            pt.setColor(textSecondary);
            pt.setTextSize(16 * density);


            canvas.drawText(artist, albumArtSize * density, (waveformSize + 24 + 16) * density, pt);

            //TODO Optimize this part. This only needs to be called once per track init.
            if (albumArt != null) {
                pt.setAlpha(Math.round(albumArtColor.update(System.currentTimeMillis()).getValue("alpha")));
                canvas.drawBitmap(albumArt, null, artBounds, pt);
                Log.d(LOG_TAG, "trying to draw..");
                pt.setAlpha(255);
            }
        }



        pt.setColor(Color.argb(50, 0, 0, 0));
        canvas.drawRect(prevBtnBounds, pt);
        canvas.drawRect(playBtnBounds, pt);
        canvas.drawRect(nextBtnBounds, pt);

        //debug draws
        pt.setColor(Color.argb(30, 255, 0, 0));
        canvas.drawRect(playBtn.getBounds(buttonPaddings * density), pt);
        canvas.drawRect(pauseBtn.getBounds(buttonPaddings * density), pt);

        pt.setColor(buttonColor);
        //canvas.drawPath(PrimitivePaths.square(50), pt);
        //canvas.drawPath(new AnimatableShape(PrimitivePaths.square(50),0,0,1,0).getPointsCompound().toPath(),pt);

        playBtn.draw(canvas,pt);

        pauseBtn.draw(canvas,pt);



        invalidate(); //TODO is this good practice?


    }


    private void animatePlay(){
        playBtn.getAnimator().getPropertySet("Basis").getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
        playBtn.getAnimator().getPropertySet("Follower").getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);

        pauseBtn.getAnimator().getPropertySet("PauseVisible").getInfluence().animate(1,1,EasingEquations.LINEAR);
        pauseBtn.getAnimator().getPropertySet("PauseInvisible").getInfluence().animate(0,1,EasingEquations.LINEAR);
    }
    private void animateStop(){
        playBtn.getAnimator().getPropertySet("Basis").getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
        playBtn.getAnimator().getPropertySet("Follower").getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

        pauseBtn.getAnimator().getPropertySet("PauseVisible").getInfluence().animate(0,1,EasingEquations.LINEAR);
        pauseBtn.getAnimator().getPropertySet("PauseInvisible").getInfluence().animate(1,1,EasingEquations.LINEAR);
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
            //buttons get priority.
            if (prevBtnBounds.contains(ev.getX(), ev.getY())) {
                qm.playPreviousFile();
            } else if (playBtn.getBounds(buttonPaddings *density).contains(ev.getX(), ev.getY())) {
                if (ap != null) {
                    if (ap.isPaused()) {
                        ap.playAudio();
                        animatePlay();
                    } else {
                        qm.playFile();
                        animatePlay();
                    }
                }
            } else if (pauseBtn.getBounds(buttonPaddings*density).contains(ev.getX(),ev.getY())){
                if (ap != null) {
                    if (ap.isPlaying()) {
                        ap.pause();
                        animateStop();
                    }
                }
            }
            else if (nextBtnBounds.contains(ev.getX(), ev.getY())) {
                qm.playNextFile();
            }else if (waveformBounds.contains(ev.getX(), ev.getY())) {
                float totalTime = (float) (wf.getNumOfFrames() / (double) ap.getSampleRate());
                ap.seekTo(totalTime * ev.getX() / w);
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
