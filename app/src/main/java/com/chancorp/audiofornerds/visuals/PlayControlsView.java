//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

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
import com.chancorp.audiofornerds.animation.MixedProperties;
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
    RectF waveformBounds, artBounds;
    int menuColor, buttonColor, textPrimary, textSecondary;
    int playedColor = Color.BLACK, remainingColor = Color.GRAY, timestampColor = Color.WHITE, timestampBackgroundColor = Color.BLACK;
    int timestampSize = 24;
    int buttonPaddings =16;
    int buttonMargins=8;//dp
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
    MixedProperties buttonFollower;
    MixedProperties buttonFollowerProgress;
    MixedProperties buttonFollowerUser;

    MixedProperties playBtnRestPosition;
    MixedProperties playBtnRestCenter;
    MixedProperties playBtnRestSide;

    AnimatableText titleAnimatable;
    MixedProperties titleNoArt;
    MixedProperties titleNormal;

    AnimatableText artistAnimatable;
    MixedProperties artistNoArt;
    MixedProperties artistNormal;

    MixedProperties albumArtColor;
    MixedProperties albumArtNormal;
    MixedProperties albumArtNone;

    AnimatableShape pauseBtn;
    MixedProperties pauseRest;
    MixedProperties pauseRestCenter;
    MixedProperties pauseRestSide;
    MixedProperties pauseVisible;
    MixedProperties pauseInvisible;


    AnimatableShape nextBtn;
    MixedProperties nextSide;
    MixedProperties nextCenter;

    AnimatableShape prevBtn;
    MixedProperties prevSide;
    MixedProperties prevCenter;

    AnimatableText timestampAnim;
    MixedProperties timestampRest;
    MixedProperties timestampFollow;

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

        //We need to disable hardware acceleration for this layer,
        //since we need to fiddle with blending and shader combining
        //for the marquee text effect.
        //It also takes care of a bug with Paths.
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);


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
        //prevBtnBounds = new RectF(buttonsAreaIni + buttonsAreaW * (1.0f / 6.0f) - halfButtonPx, h - 16 * density - halfButtonPx,
        //        buttonsAreaIni + buttonsAreaW * (1.0f / 6.0f) + halfButtonPx, h - 16 * density + halfButtonPx);
        //playBtnBounds = new RectF(buttonsAreaIni + buttonsAreaW * (3.0f / 6.0f) - halfButtonPx, h - 16 * density - halfButtonPx,
        //        buttonsAreaIni + buttonsAreaW * (3.0f / 6.0f) + halfButtonPx, h - 16 * density + halfButtonPx);
        //nextBtnBounds = new RectF(buttonsAreaIni + buttonsAreaW * (5.0f / 6.0f) - halfButtonPx, h - 16 * density - halfButtonPx,
        //        buttonsAreaIni + buttonsAreaW * (5.0f / 6.0f) + halfButtonPx, h - 16 * density + halfButtonPx);
        waveformBounds = new RectF(0, 0, w, waveformSize * density);
        artBounds = new RectF(albumArtMargin * density, (albumArtMargin + waveformSize) * density, (albumArtSize - albumArtMargin) * density, (albumArtSize - albumArtMargin + waveformSize) * density);


        buttonFollower=new MixedProperties("Follower");
        buttonFollowerUser=new MixedProperties("User",new PropertySet().setValue("X", 0).setValue("Y",waveformSize*density).setValue("Scale",0.3f).setValue("Rotation",180).setValue("Alpha",1.0f));
        buttonFollowerProgress=new MixedProperties("Progress",new PropertySet().setValue("X", 0).setValue("Y",waveformSize*density).setValue("Scale",0.3f).setValue("Rotation",180).setValue("Alpha",1.0f));
        buttonFollowerUser.getInfluence().set(0.0f);
        buttonFollower.addProperty(buttonFollowerProgress);
        buttonFollower.addProperty(buttonFollowerUser);

        buttonFollower.getInfluence().set(0);
        playBtnRestPosition =new MixedProperties("Rest");
        playBtnRestSide=new MixedProperties("Side",new PropertySet().setValue("X",buttonsAreaIni + buttonsAreaW * (3.0f / 6.0f)).setValue("Y",h - (buttonsSize/2+buttonMargins)*density).setValue("Scale",1).setValue("Rotation",30).setValue("Alpha",1.0f));
        playBtnRestCenter=new MixedProperties("Center",new PropertySet().setValue("X",w * (3.0f / 6.0f)).setValue("Y",h - (buttonsSize/2+buttonMargins)*density).setValue("Scale",1).setValue("Rotation",30).setValue("Alpha",1.0f));
        playBtnRestCenter.getInfluence().set(0.0f);
        playBtnRestPosition.addProperty(playBtnRestSide);
        playBtnRestPosition.addProperty(playBtnRestCenter);


        playBtn=new AnimatableShape(PrimitivePaths.triangle(buttonsSize/2.0f*density),buttonColor,new MixedProperties("Mix"));
        playBtn.getMixedProperties().addProperty(buttonFollower);
        playBtn.getMixedProperties().addProperty(playBtnRestPosition);


        pauseRest =new MixedProperties("Rest");
        pauseRestSide=new MixedProperties("Side",new PropertySet().setValue("X", buttonsAreaIni + buttonsAreaW * (3.0f / 6.0f)).setValue("Y",h - (buttonsSize/2+buttonMargins)*density).setValue("Scale",1).setValue("Rotation", 0));
        pauseRestCenter=new MixedProperties("Center",new PropertySet().setValue("X", w * (3.0f / 6.0f)).setValue("Y",h - (buttonsSize/2+buttonMargins)*density).setValue("Scale",1).setValue("Rotation", 0));
        pauseRestCenter.getInfluence().set(0.0f);
        pauseRest.addProperty(pauseRestSide);
        pauseRest.addProperty(pauseRestCenter);

        pauseBtn=new AnimatableShape(PrimitivePaths.pause(buttonsSize / 2.0f * density),buttonColor,new MixedProperties("Mix"));

        pauseVisible=new MixedProperties("PauseVisible",new PropertySet().setValue("Alpha", 1.0f));
        pauseVisible.getInfluence().set(0);
        pauseInvisible=new MixedProperties("PauseInvisible",new PropertySet().setValue("Alpha", 0.0f));

        pauseBtn.getMixedProperties().addProperty(pauseInvisible);
        pauseBtn.getMixedProperties().addProperty(pauseVisible);
        pauseBtn.getMixedProperties().addProperty(pauseRest);


        nextBtn=new AnimatableShape(PrimitivePaths.next(buttonsSize / 2.0f * density),buttonColor,new MixedProperties("FinalMix"));
        nextSide=new MixedProperties("Side",new PropertySet().setValue("X",buttonsAreaIni + buttonsAreaW * (5.0f / 6.0f)).setValue("Y",h - (buttonsSize/2+buttonMargins)*density).setValue("Scale",1).setValue("Rotation",0).setValue("Alpha", 1.0f));
        nextCenter=new MixedProperties("Center",new PropertySet().setValue("X",w * (5.0f / 6.0f)).setValue("Y",h - (buttonsSize/2+buttonMargins)*density).setValue("Scale",1).setValue("Rotation",0).setValue("Alpha",1.0f));
        nextCenter.getInfluence().set(0.0f);
        nextBtn.getMixedProperties().addProperty(nextSide);
        nextBtn.getMixedProperties().addProperty(nextCenter);



        prevBtn=new AnimatableShape(PrimitivePaths.next(buttonsSize / 2.0f * density),buttonColor,new MixedProperties("FinalMix"));
        prevSide=new MixedProperties("Side",new PropertySet().setValue("X",buttonsAreaIni + buttonsAreaW * (1.0f / 6.0f)).setValue("Y",h - (buttonsSize/2+buttonMargins)*density).setValue("Scale",1).setValue("Rotation",180).setValue("Alpha",1.0f));
        prevCenter=new MixedProperties("Center",new PropertySet().setValue("X",w * (1.0f / 6.0f)).setValue("Y",h - (buttonsSize/2+buttonMargins)*density).setValue("Scale",1).setValue("Rotation",180).setValue("Alpha",1.0f));
        prevCenter.getInfluence().set(0.0f);
        prevBtn.getMixedProperties().addProperty(prevSide);
        prevBtn.getMixedProperties().addProperty(prevCenter);



        titleAnimatable=new AnimatableText(new MixedProperties("Mix"),textPrimary,"",24*density);
        titleNoArt=new MixedProperties("NoArt",new PropertySet().setValue("X", albumArtMargin * density).setValue("Y", waveformSize * density));
        titleNoArt.getInfluence().set(0);
        titleNormal=new MixedProperties("Normal",new PropertySet().setValue("X",albumArtSize * density).setValue("Y", waveformSize * density));
        titleAnimatable.getMixedProperties().addProperty(titleNoArt);
        titleAnimatable.getMixedProperties().addProperty(titleNormal);
        titleAnimatable.enableMarquee(w - (albumArtSize+albumArtMargin) * density, 16 * density);


        artistNormal=new MixedProperties("Normal",new PropertySet().setValue("X",albumArtSize * density).setValue("Y", (waveformSize+30) * density));
        artistAnimatable=new AnimatableText(new MixedProperties("FinalMix"),textSecondary,"",16*density);
        artistNoArt=new MixedProperties("NoArt",new PropertySet().setValue("X", albumArtMargin * density).setValue("Y",(waveformSize+30) * density));
        artistNoArt.getInfluence().set(0);
        artistAnimatable.getMixedProperties().addProperty(artistNoArt);
        artistAnimatable.getMixedProperties().addProperty(artistNormal);

        albumArtColor=new MixedProperties("AlbumArtMix");
        albumArtNormal=new MixedProperties("Normal",new PropertySet().setValue("alpha",255));
        albumArtNone=new MixedProperties("NoArt",new PropertySet().setValue("alpha", 0));
        albumArtNone.getInfluence().set(0);
        albumArtColor.addProperty(albumArtNone);
        albumArtColor.addProperty(albumArtNormal);


        timestampAnim=new AnimatableText(new MixedProperties("FinalMixed"),textPrimary,"",timestampSize*density);
        timestampRest=new MixedProperties("Rest",new PropertySet().setValue("X", w-30 * density).setValue("Y",30*density));
        timestampFollow=new MixedProperties("Follow",new PropertySet().setValue("X", 0).setValue("Y",20*density));
        timestampFollow.getInfluence().set(0.0f);
        timestampAnim.getMixedProperties().addProperty(timestampRest);
        timestampAnim.getMixedProperties().addProperty(timestampFollow);
        timestampAnim.setBgColor(timestampBackgroundColor);
        timestampAnim.drawBackground(true);
        timestampAnim.setAlign(AnimatableText.ALIGN_CENTER);
        //playBtn=new AnimatableShape(PrimitivePaths.triangle(50),50,50,1,0);


    }


    String title = new String();
    String artist = new String();
    Bitmap albumArt;

    protected void parseMusicInformation(final MusicInformation mi) {
        title = mi.getTitle();
        titleAnimatable.setText(mi.getTitle());
        artist = mi.getArtist();
        artistAnimatable.setText(mi.getArtist());

        if (mi.hasArt()) {
            //TODO: move album art decode into seperate thread.

            new Thread(new Runnable() {
                @Override
                public void run() {
                    albumArt = BitmapConversions.decodeSampledBitmapFromResource(mi.getArtByteArray(), Math.round(artBounds.width()), Math.round(artBounds.height()));
                }
            }).start();



            titleAnimatable.getMixedProperties().getProperty("Normal").getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
            titleAnimatable.getMixedProperties().getProperty("NoArt").getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            titleAnimatable.enableMarquee(w - (albumArtSize+albumArtMargin) * density, 16 * density);

            artistAnimatable.getMixedProperties().getProperty("Normal").getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            artistAnimatable.getMixedProperties().getProperty("NoArt").getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            albumArtColor.getProperty("Normal").getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            albumArtColor.getProperty("NoArt").getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

            playBtnRestCenter.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            playBtnRestSide.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);

            pauseRestCenter.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            pauseRestSide.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);

            prevCenter.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            prevSide.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            nextCenter.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            nextSide.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);

        }else{
            titleAnimatable.getMixedProperties().getProperty("Normal").getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            titleAnimatable.getMixedProperties().getProperty("NoArt").getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            titleAnimatable.enableMarquee(w - albumArtMargin*2 * density,16*density);

            albumArtColor.getProperty("Normal").getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            albumArtColor.getProperty("NoArt").getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            artistAnimatable.getMixedProperties().getProperty("Normal").getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            artistAnimatable.getMixedProperties().getProperty("NoArt").getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);

            playBtnRestCenter.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            playBtnRestSide.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            pauseRestCenter.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            pauseRestSide.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

            prevCenter.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            prevSide.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            nextCenter.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            nextSide.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
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

/*
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
*/
            if (!dragMode) {
                timestampAnim.setText(wf.frameNumberToTimeStamp(ap.getMusicCurrentFrame()));
            }


            timestampAnim.draw(canvas,pt);

            buttonFollowerProgress.getBasis().setValue("X", w * currentPosition);
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


            //canvas.drawText(artist, albumArtSize * density, (waveformSize + 24 + 16) * density, pt);
            artistAnimatable.draw(canvas,pt);

            //TODO Optimize this part. This only needs to be called once per track init.
            if (albumArt != null) {
                pt.setAlpha(Math.round(albumArtColor.update(System.currentTimeMillis()).getValue("alpha")));
                canvas.drawBitmap(albumArt, null, artBounds, pt);
                Log.d(LOG_TAG, "trying to draw..");
                pt.setAlpha(255);
            }
        }


        pt.setColor(Color.argb(50, 0, 0, 0));
        //canvas.drawRect(prevBtnBounds, pt);
        //canvas.drawRect(playBtnBounds, pt);
        //canvas.drawRect(nextBtnBounds, pt);

        //debug draws
        /*
        pt.setColor(Color.argb(30, 255, 0, 0));
        canvas.drawRect(playBtn.getBounds(buttonPaddings * density), pt);
        canvas.drawRect(pauseBtn.getBounds(buttonPaddings * density), pt);
        canvas.drawRect(prevBtn.getBounds(buttonPaddings * density), pt);
        canvas.drawRect(nextBtn.getBounds(buttonPaddings * density), pt);
        */
        pt.setColor(buttonColor);
        //canvas.drawPath(PrimitivePaths.square(50), pt);
        //canvas.drawPath(new AnimatableShape(PrimitivePaths.square(50),0,0,1,0).getPointsCompound().toPath(),pt);

        playBtn.draw(canvas,pt);

        pauseBtn.draw(canvas,pt);

        prevBtn.draw(canvas,pt);
        nextBtn.draw(canvas,pt);


        invalidate(); //TODO is this good practice?

    }


    private void animatePlay(){
        //TODO replace getProperty with direct references
        playBtnRestPosition.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
        playBtn.getMixedProperties().getProperty("Follower").getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
        //We et influence to 1000 so it will override both Basis and Centered.

        pauseBtn.getMixedProperties().getProperty("PauseVisible").getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
        pauseBtn.getMixedProperties().getProperty("PauseInvisible").getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
    }
    private void animateStop(){
        playBtnRestPosition.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
        playBtn.getMixedProperties().getProperty("Follower").getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

        pauseBtn.getMixedProperties().getProperty("PauseVisible").getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
        pauseBtn.getMixedProperties().getProperty("PauseInvisible").getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
    }

    float iniX, iniY;
    boolean dragMode=false;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_DOWN) {
            iniX = ev.getX();
            iniY = ev.getY();
            if (buttonFollower.getInfluence().getValue()>0.99f){ //if in playing mode and button is in follow state
                if (playBtn.getBounds(buttonPaddings*density).contains(ev.getX(),ev.getY())){
                    dragMode=true;
                    buttonFollowerUser.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
                    buttonFollowerProgress.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

                    timestampFollow.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
                    timestampRest.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

                    buttonFollowerUser.getBasis().setValue("X", ev.getX());
                    timestampFollow.getBasis().setValue("X", ev.getX());
                }
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (dragMode) {
                buttonFollowerUser.getBasis().setValue("X",ev.getX());
                timestampFollow.getBasis().setValue("X", ev.getX());
                timestampAnim.setText(wf.frameNumberToTimeStamp((long) (wf.getNumOfFrames() * (ev.getX() / (double) w))));
            }
        } else if (action == MotionEvent.ACTION_UP) {
            //buttons get priority.
            if (dragMode) { //TODO : Drag
                buttonFollowerUser.getInfluence().animate(0,0.5f,EasingEquations.DEFAULT_EASE);
                buttonFollowerProgress.getInfluence().animate(1, 0.5f, EasingEquations.DEFAULT_EASE);
                float totalTime = (float) (wf.getNumOfFrames() / (double) ap.getSampleRate());
                ap.seekTo(totalTime * ev.getX() / w);

                timestampFollow.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
                timestampRest.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);

                dragMode=false;
            }else if (prevBtn.getBounds(buttonPaddings * density).contains(ev.getX(), ev.getY())) {

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
            else if (nextBtn.getBounds(buttonPaddings * density).contains(ev.getX(), ev.getY())) {
                qm.playNextFile();
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
