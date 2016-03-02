//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.chancorp.audiofornerds.visuals;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.chancorp.audiofornerds.R;
import com.chancorp.audiofornerds.animation.MixedProperties;
import com.chancorp.audiofornerds.draw.AnimatableRectF;
import com.chancorp.audiofornerds.draw.AnimatableShape;
import com.chancorp.audiofornerds.animation.EasingEquations;
import com.chancorp.audiofornerds.animation.PrimitivePaths;
import com.chancorp.audiofornerds.animation.PropertySet;
import com.chancorp.audiofornerds.audio.AudioPlayer;
import com.chancorp.audiofornerds.audio.Waveform;
import com.chancorp.audiofornerds.draw.AnimatableText;
import com.chancorp.audiofornerds.draw.AnimatableWaveform;
import com.chancorp.audiofornerds.file.MusicInformation;
import com.chancorp.audiofornerds.file.QueueManager;
import com.chancorp.audiofornerds.helper.BitmapConversions;
import com.chancorp.audiofornerds.helper.ColorFiddler;
import com.chancorp.audiofornerds.interfaces.NewSongListener;
import com.chancorp.audiofornerds.interfaces.ProgressStringListener;

public class PlayControlsView extends View implements ProgressStringListener, NewSongListener {
    int w, h;
    Waveform wf;
    Paint pt;
    //RectF artBounds;
    AnimatableRectF artBoundsAnim;
    MixedProperties artBoundsNormal;
    MixedProperties artBoundsExpanded;

    int menuColor, buttonColor, textPrimary, textSecondary;
    int  timestampColor = Color.WHITE, timestampBackgroundColor = Color.BLACK;
    int playedColorEX = Color.BLACK, remainingColorEX = Color.GRAY,playedColor = Color.BLACK, remainingColor = Color.GRAY;
    int timestampSize = 24;
    int buttonPaddings =16;
    int buttonMargins=8;//dp
    int albumArtSize = 100;//dp
    int normalBarHeight = 100; //dp
    int expandedBarHeight=200;
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
    MixedProperties buttonFollowerYExpanded;
    MixedProperties buttonFollowerYNotExpanded;

    MixedProperties playBtnRestPosition;
    MixedProperties playBtnRestCenter;
    MixedProperties playBtnRestSide;

    AnimatableText titleAnimatable;
    MixedProperties titleNoArt;
    MixedProperties titleNormal;
    MixedProperties titleExpanded;
    MixedProperties titleNotExpanded;

    AnimatableText artistAnimatable;
    MixedProperties artistNoArt;
    MixedProperties artistNormal;
    MixedProperties artistExpanded;
    MixedProperties artistNotExpanded;


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
    MixedProperties timestampExpanded;
    MixedProperties timestampNotExpanded;

    AnimatableWaveform waveform;
    MixedProperties waveformExpanded;
    MixedProperties waveformNotExpanded;

    MixedProperties barHeight;
    MixedProperties expandedBarHeightMP;
    MixedProperties normalBarHeightMP;

    AnimatableText filePath;
    MixedProperties filePathActive;
    MixedProperties filePathActiveArt;
    MixedProperties filePathActiveNoArt;
    MixedProperties filePathInactive;



    //TODO Performance. Srsly.

    public PlayControlsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
        density = getResources().getDisplayMetrics().density;
        qm = QueueManager.getInstance();
        ap = AudioPlayer.getInstance();

        Resources res=getResources();
        menuColor = getResources().getColor(R.color.colorPrimary);
        buttonColor = Color.WHITE;
        textPrimary = getResources().getColor(R.color.colorLightPrimaryText);
        textSecondary = getResources().getColor(R.color.colorLightSecondaryText);

        playedColor= ColorFiddler.setAlpha(res.getColor(R.color.colorPrimary), 200);
        //Log2.log(2,this,Color.alpha(playedColor),Color.red(playedColor),Color.blue(playedColor),Color.green(playedColor));
        remainingColor= ColorFiddler.setAlpha(res.getColor(R.color.colorPrimary), 100);

        playedColorEX= ColorFiddler.setAlpha(res.getColor(R.color.colorAccentFallbackDark), 255);
        remainingColorEX= ColorFiddler.setAlpha(res.getColor(R.color.colorAccent),255);
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
        barHeight=new MixedProperties("BarHeight");
        expandedBarHeightMP=new MixedProperties("Expanded",new PropertySet().setValue("Height",expandedBarHeight*density));
        expandedBarHeightMP.getInfluence().set(0.0f);
        normalBarHeightMP=new MixedProperties("Expanded",new PropertySet().setValue("Height",normalBarHeight*density));
        barHeight.addProperty(expandedBarHeightMP);
        barHeight.addProperty(normalBarHeightMP);

        float buttonsAreaW = w - albumArtSize * density;
        float buttonsAreaIni = albumArtSize * density;
        float halfButtonPx = buttonsSize * density / 2.0f;
        //prevBtnBounds = new RectF(buttonsAreaIni + buttonsAreaW * (1.0f / 6.0f) - halfButtonPx, h - 16 * density - halfButtonPx,
        //        buttonsAreaIni + buttonsAreaW * (1.0f / 6.0f) + halfButtonPx, h - 16 * density + halfButtonPx);
        //playBtnBounds = new RectF(buttonsAreaIni + buttonsAreaW * (3.0f / 6.0f) - halfButtonPx, h - 16 * density - halfButtonPx,
        //        buttonsAreaIni + buttonsAreaW * (3.0f / 6.0f) + halfButtonPx, h - 16 * density + halfButtonPx);
        //nextBtnBounds = new RectF(buttonsAreaIni + buttonsAreaW * (5.0f / 6.0f) - halfButtonPx, h - 16 * density - halfButtonPx,
        //        buttonsAreaIni + buttonsAreaW * (5.0f / 6.0f) + halfButtonPx, h - 16 * density + halfButtonPx);
        //artBounds = new RectF(albumArtMargin * density, h-(-albumArtMargin + (normalBarHeight)) * density, (albumArtSize - albumArtMargin) * density, h-(-albumArtSize + albumArtMargin + normalBarHeight) * density);
        artBoundsAnim=new AnimatableRectF(new MixedProperties("Final"));
        artBoundsNormal=new MixedProperties("Normal",new PropertySet().setValue("X-", albumArtMargin * density)
                .setValue("Y-",h-(-albumArtMargin + (normalBarHeight)) * density)
                .setValue("X+",(albumArtSize - albumArtMargin) * density)
                .setValue("Y+",h-(-albumArtSize + albumArtMargin + normalBarHeight) * density));
        artBoundsExpanded=new MixedProperties("Expanded",new PropertySet().setValue("X-", albumArtMargin * density)
                .setValue("Y-",h-(-albumArtMargin + (expandedBarHeight)) * density)
                .setValue("X+",(albumArtSize - albumArtMargin) * density)
                .setValue("Y+",h-(-albumArtSize + albumArtMargin + expandedBarHeight) * density));
        artBoundsExpanded.getInfluence().set(0.0f);
        artBoundsAnim.getMixedProperties().addProperty(artBoundsNormal);
        artBoundsAnim.getMixedProperties().addProperty(artBoundsExpanded);




        buttonFollower=new MixedProperties("Follower");
        buttonFollowerUser=new MixedProperties("User",new PropertySet().setValue("X", 0).setValue("Scale",0.3f).setValue("Rotation",180).setValue("Alpha",1.0f));
        buttonFollowerProgress=new MixedProperties("Progress",new PropertySet().setValue("X", 0).setValue("Scale",0.3f).setValue("Rotation",180).setValue("Alpha",1.0f));
        buttonFollowerUser.getInfluence().set(0.0f);
        buttonFollowerYExpanded=new MixedProperties("YExpanded",new PropertySet().setValue("Y", h - (expandedBarHeight - albumArtSize - 40) * density));
        buttonFollowerYExpanded.getInfluence().set(0.0f);
        buttonFollowerYNotExpanded = new MixedProperties("YNotExpanded",new PropertySet().setValue("Y", h - (normalBarHeight) * density));
        buttonFollower.addProperty(buttonFollowerProgress);
        buttonFollower.addProperty(buttonFollowerUser);
        buttonFollower.addProperty(buttonFollowerYExpanded);
        buttonFollower.addProperty(buttonFollowerYNotExpanded);
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
        titleNoArt=new MixedProperties("NoArt",new PropertySet().setValue("X", albumArtMargin * density));
        titleNoArt.getInfluence().set(0);
        titleNormal=new MixedProperties("Normal",new PropertySet().setValue("X", albumArtSize * density));
        titleExpanded=new MixedProperties("Expanded",new PropertySet().setValue("Y", h-(expandedBarHeight-6) * density));
        titleExpanded.getInfluence().set(0.0f);
        titleNotExpanded=new MixedProperties("NotExpanded",new PropertySet().setValue("Y", h-(normalBarHeight-6) * density));
        titleAnimatable.getMixedProperties().addProperty(titleNoArt);
        titleAnimatable.getMixedProperties().addProperty(titleNormal);
        titleAnimatable.getMixedProperties().addProperty(titleExpanded);
        titleAnimatable.getMixedProperties().addProperty(titleNotExpanded);
        titleAnimatable.enableMarquee(w - (albumArtSize + albumArtMargin) * density, 16 * density);


        artistNormal=new MixedProperties("Normal",new PropertySet().setValue("X",albumArtSize * density));
        artistAnimatable=new AnimatableText(new MixedProperties("FinalMix"),textSecondary,"",16*density);
        artistNoArt=new MixedProperties("NoArt",new PropertySet().setValue("X", albumArtMargin * density));
        artistNoArt.getInfluence().set(0);
        artistExpanded=new MixedProperties("Expanded",new PropertySet().setValue("Y",h-(expandedBarHeight -36) * density));
        artistExpanded.getInfluence().set(0.0f);
        artistNotExpanded=new MixedProperties("NotExpanded",new PropertySet().setValue("Y", h - (normalBarHeight - 36) * density));

        artistAnimatable.getMixedProperties().addProperty(artistNoArt);
        artistAnimatable.getMixedProperties().addProperty(artistNormal);
        artistAnimatable.getMixedProperties().addProperty(artistExpanded);
        artistAnimatable.getMixedProperties().addProperty(artistNotExpanded);

        filePath=new AnimatableText(new MixedProperties("Mix"),textSecondary,"",12*density);
        filePath.enableMarquee(w - (albumArtSize + albumArtMargin) * density, 16 * density);
        filePathActive=new MixedProperties("Active");
        filePathActiveArt =new MixedProperties("Art",new PropertySet().setValue("X", albumArtSize * density).setValue("Y",h-(expandedBarHeight -60) * density));
        filePathActiveNoArt =new MixedProperties("NoArt",new PropertySet().setValue("X", albumArtMargin * density).setValue("Y",h-(expandedBarHeight -60) * density));
        filePathActiveNoArt.getInfluence().set(0.0f);
        filePathActive.addProperty(filePathActiveArt);
        filePathActive.addProperty(filePathActiveNoArt);
        filePathInactive=new MixedProperties("Inactive",new PropertySet().setValue("X", w).setValue("Y",h-(expandedBarHeight -60) * density));
        filePathActive.getInfluence().set(0.0f);
        filePath.getMixedProperties().addProperty(filePathActive);
        filePath.getMixedProperties().addProperty(filePathInactive);

        albumArtColor=new MixedProperties("AlbumArtMix");
        albumArtNormal=new MixedProperties("Normal",new PropertySet().setValue("alpha",255));
        albumArtNone=new MixedProperties("NoArt",new PropertySet().setValue("alpha", 0));
        albumArtNone.getInfluence().set(0);
        albumArtColor.addProperty(albumArtNone);
        albumArtColor.addProperty(albumArtNormal);


        timestampAnim=new AnimatableText(new MixedProperties("FinalMixed"),textPrimary,"",timestampSize*density);
        timestampRest=new MixedProperties("Rest",new PropertySet().setValue("X", w - 30 * density));
        timestampFollow=new MixedProperties("Follow",new PropertySet().setValue("X", 0));
        timestampNotExpanded=new MixedProperties("NotExpanded",new PropertySet().setValue("Y",h - (normalBarHeight+30) * density));
        timestampExpanded=new MixedProperties("Expanded",new PropertySet().setValue("Y",h - (expandedBarHeight-albumArtSize-40+30) * density));
        timestampExpanded.getInfluence().set(0.0f);
        timestampFollow.getInfluence().set(0.0f);
        timestampAnim.getMixedProperties().addProperty(timestampRest);
        timestampAnim.getMixedProperties().addProperty(timestampFollow);
        timestampAnim.getMixedProperties().addProperty(timestampNotExpanded);
        timestampAnim.getMixedProperties().addProperty(timestampExpanded);
        timestampAnim.setBgColor(timestampBackgroundColor);
        timestampAnim.drawBackground(true);
        timestampAnim.setAlign(AnimatableText.ALIGN_CENTER);
        //playBtn=new AnimatableShape(PrimitivePaths.triangle(50),50,50,1,0);

        waveform=new AnimatableWaveform(wf,ap,new MixedProperties("Final"));
        waveformNotExpanded=new MixedProperties("NotExpanded",
                new PropertySet().setValue("X", 0).setValue("Y", h - normalBarHeight * density)
                .setValue("XSize", w).setValue("YSize", 50 * density)
                .setValue("Played-R", ColorFiddler.rF(playedColor))
                .setValue("Played-G", ColorFiddler.gF(playedColor))
                .setValue("Played-B", ColorFiddler.bF(playedColor))
                .setValue("Played-A", ColorFiddler.aF(playedColor))
                .setValue("Remaining-R", ColorFiddler.rF(remainingColor))
                .setValue("Remaining-G", ColorFiddler.gF(remainingColor))
                .setValue("Remaining-B", ColorFiddler.bF(remainingColor))
                .setValue("Remaining-A", ColorFiddler.aF(remainingColor)));
        waveformExpanded=new MixedProperties("Expanded",
                new PropertySet().setValue("X", 0).setValue("Y", h - (expandedBarHeight-albumArtSize-40) * density)
                        .setValue("XSize", w).setValue("YSize", 40 * density)
                        .setValue("Played-R", ColorFiddler.rF(playedColorEX))
                        .setValue("Played-G", ColorFiddler.gF(playedColorEX))
                        .setValue("Played-B", ColorFiddler.bF(playedColorEX))
                        .setValue("Played-A", ColorFiddler.aF(playedColorEX))
                        .setValue("Remaining-R", ColorFiddler.rF(remainingColorEX))
                        .setValue("Remaining-G", ColorFiddler.gF(remainingColorEX))
                        .setValue("Remaining-B", ColorFiddler.bF(remainingColorEX))
                        .setValue("Remaining-A", ColorFiddler.aF(remainingColorEX)));
        waveformExpanded.getInfluence().set(0.0f);
        waveform.getMixedProperties().addProperty(waveformExpanded);
        waveform.getMixedProperties().addProperty(waveformNotExpanded);


    }

    private void toggleExpand(){
        if (expanded){
            expand(false);
        }
        else {
            expand(true);
        }
    }

    boolean expanded=false;
    public void expand(boolean expand){
        if (this.expanded==expand) return;
        this.expanded=expand;
        if (expand){
            expandedBarHeightMP.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            normalBarHeightMP.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

            titleExpanded.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            titleNotExpanded.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

           artistExpanded.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            artistNotExpanded.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

            artBoundsExpanded.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            artBoundsNormal.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

            waveformExpanded.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            waveformNotExpanded.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

            timestampExpanded.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            timestampNotExpanded.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

            buttonFollowerYExpanded.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            buttonFollowerYNotExpanded.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

            filePathActive.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
            filePathInactive.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
            buttonsCenter(true);
        }else{
            expandedBarHeightMP.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            normalBarHeightMP.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);

            titleExpanded.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            titleNotExpanded.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);

            artistExpanded.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            artistNotExpanded.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);

            artBoundsExpanded.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            artBoundsNormal.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);

            waveformExpanded.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            waveformNotExpanded.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);

            timestampExpanded.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            timestampNotExpanded.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);

            buttonFollowerYExpanded.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            buttonFollowerYNotExpanded.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);

            filePathActive.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
            filePathInactive.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
            if (currentMusic!=null) if (currentMusic.hasArt()) buttonsCenter(false);
        }
    }

    private void buttonsCenter(boolean center){
        if (!center) {
            playBtnRestCenter.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
            playBtnRestSide.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);

            pauseRestCenter.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
            pauseRestSide.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);

            prevCenter.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
            prevSide.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
            nextCenter.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
            nextSide.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);

        }else{
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



    String title;
    String artist;
    Bitmap albumArt;
    MusicInformation currentMusic;
    protected void parseMusicInformation(final MusicInformation mi) {
        currentMusic=mi;
        title = mi.getTitle();
        titleAnimatable.setText(mi.getTitle());
        artist = mi.getArtist();
        artistAnimatable.setText(mi.getArtist());
        filePath.setText(mi.getFilepath());

        if (mi.hasArt()) {


            new Thread(new Runnable() {
                @Override
                public void run() {
                    RectF bounds=artBoundsAnim.getRectF();
                    albumArt = BitmapConversions.decodeSampledBitmapFromResource(mi.getArtByteArray(), Math.round(bounds.width()), Math.round(bounds.height()));
                }
            }).start();



            titleNormal.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
            titleNoArt.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            titleAnimatable.enableMarquee(w - (albumArtSize + albumArtMargin) * density, 16 * density);
            filePath.enableMarquee(w - (albumArtSize + albumArtMargin) * density, 16 * density);
            filePathActiveArt.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
            filePathActiveNoArt.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            artistNormal.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
            artistNoArt.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            albumArtNormal.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            albumArtNone.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

            if (!expanded) buttonsCenter(false);

        }else{
            titleNormal.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            titleNoArt.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
            titleAnimatable.enableMarquee(w - albumArtMargin * 2 * density, 16 * density);
            filePath.enableMarquee(w - albumArtMargin * 2 * density, 16 * density);
            filePathActiveArt.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
            filePathActiveNoArt.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
            albumArtNormal.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            albumArtNone.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
            artistNormal.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
            artistNoArt.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);

            if (!expanded) buttonsCenter(true);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {

        pt.setColor(menuColor);
        canvas.drawRect(0, h-barHeight.update(System.currentTimeMillis()).getValue("Height"), w, h, pt);

        //Log.d(LOG_TAG, "Drawing WaveformView" + w + " | " + h);
        if (wf != null && ap != null && wf.isReady() && wf.getFilename().equals(ap.getSourceString())) {
            setCurrentPosition((float) (ap.getMusicCurrentFrame() / (double) wf.getNumOfFrames()));
            /*
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
                canvas.drawRect(i * spacing, normalBarHeight * density, i * spacing + width, normalBarHeight * density * (1 - wf.getRatio(i)), pt);
            }*/

/*
            if (displayTimeStamp) {



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
            waveform.draw(canvas,pt);

            if (!dragMode) {
                timestampAnim.setText(wf.frameNumberToTimeStamp(ap.getMusicCurrentFrame()));
            }


            timestampAnim.draw(canvas,pt);

            buttonFollowerProgress.getBasis().setValue("X", w * currentPosition);
        }




            //canvas.drawText(title, albumArtSize * density, (normalBarHeight + 24) * density, pt);

            titleAnimatable.draw(canvas,pt);



            //canvas.drawText(artist, albumArtSize * density, (normalBarHeight + 24 + 16) * density, pt);
            artistAnimatable.draw(canvas,pt);

            filePath.draw(canvas,pt);
            if (albumArt != null) {
                pt.setAlpha(Math.round(albumArtColor.update(System.currentTimeMillis()).getValue("alpha")));
                canvas.drawBitmap(albumArt, null, artBoundsAnim.getRectF(), pt);
                //Log.d(LOG_TAG, "trying to draw..");
                pt.setAlpha(255);
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


        invalidate();

    }


    private void animatePlay(){
        //TODO replace getProperty with direct references
        playBtnRestPosition.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
        buttonFollower.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
        //We et influence to 1000 so it will override both Basis and Centered.

        pauseVisible.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
        pauseInvisible.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
    }
    private void animateStop(){
        playBtnRestPosition.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
        buttonFollower.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

        pauseVisible.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
        pauseInvisible.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
    }

    float iniX, iniY;
    boolean dragMode=false;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_DOWN) {
            iniX = ev.getX();
            iniY = ev.getY();

            //Play Button Drag
            if (buttonFollower.getInfluence().getValue()>0.99f){ //if in playing mode and button is in follow state
                if (playBtn.getBounds(buttonPaddings*density).contains(ev.getX(),ev.getY())){
                    dragMode=true;
                    buttonFollowerUser.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
                    buttonFollowerProgress.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

                    timestampFollow.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
                    timestampRest.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

                    buttonFollowerUser.getBasis().setValue("X", ev.getX());
                    timestampFollow.getBasis().setValue("X", ev.getX());
                    return true;
                }
            }

            //Other buttons
            if (prevBtn.getBounds(buttonPaddings * density).contains(ev.getX(), ev.getY())) {
                return true;
            } else if (playBtn.getBounds(buttonPaddings *density).contains(ev.getX(), ev.getY())) {
                return true;
            } else if (pauseBtn.getBounds(buttonPaddings*density).contains(ev.getX(),ev.getY())){
                return true;
            }
            else if (nextBtn.getBounds(buttonPaddings * density).contains(ev.getX(), ev.getY())) {
                return true;
            }else if(artBoundsAnim.getRectF().contains(ev.getX(), ev.getY())){
                return true;
            }


        } else if (action == MotionEvent.ACTION_MOVE) {
            //Dragging in progress
            if (dragMode) {
                buttonFollowerUser.getBasis().setValue("X",ev.getX());
                timestampFollow.getBasis().setValue("X", ev.getX());
                timestampAnim.setText(wf.frameNumberToTimeStamp((long) (wf.getNumOfFrames() * (ev.getX() / (double) w))));
            }
            return true;
        } else if (action == MotionEvent.ACTION_UP) {
            //buttons get priority.
            if (dragMode) {
                buttonFollowerUser.getInfluence().animate(0,0.5f,EasingEquations.DEFAULT_EASE);
                buttonFollowerProgress.getInfluence().animate(1, 0.5f, EasingEquations.DEFAULT_EASE);
                float totalTime = (float) (wf.getNumOfFrames() / (double) ap.getSampleRate());
                ap.seekTo(totalTime * ev.getX() / w);

                timestampFollow.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
                timestampRest.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);

                dragMode=false;

                return true;
            }

            //Button doing stuff.
            if (prevBtn.getBounds(buttonPaddings * density).contains(ev.getX(), ev.getY())) {
                qm.playPreviousFile();
                return true;
            } else if (playBtn.getBounds(buttonPaddings *density).contains(ev.getX(), ev.getY())) {
                if (ap != null) {
                    if (ap.isPaused()) {
                        ap.playAudio();
                        animatePlay();
                    } else {
                        qm.play();
                        animatePlay();
                    }
                }
                return true;
            } else if (pauseBtn.getBounds(buttonPaddings*density).contains(ev.getX(),ev.getY())){
                if (ap != null) {
                    if (ap.isPlaying()) {
                        ap.pause();
                        animateStop();
                    }
                }
                return true;
            }
            else if (nextBtn.getBounds(buttonPaddings * density).contains(ev.getX(), ev.getY())) {
                qm.playNextFile();
                return true;
            }else if(artBoundsAnim.getRectF().contains(ev.getX(),ev.getY())){
                toggleExpand();
            }



        }
        return false;
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
