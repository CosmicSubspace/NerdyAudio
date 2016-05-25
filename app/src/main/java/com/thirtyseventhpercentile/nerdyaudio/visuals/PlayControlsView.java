//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.visuals;

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

import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.animation.MixNode;
import com.thirtyseventhpercentile.nerdyaudio.animation.PointsCompound;
import com.thirtyseventhpercentile.nerdyaudio.draw.AnimatableRectF;
import com.thirtyseventhpercentile.nerdyaudio.draw.AnimatableShape;
import com.thirtyseventhpercentile.nerdyaudio.animation.EasingEquations;
import com.thirtyseventhpercentile.nerdyaudio.draw.PrimitivePaths;
import com.thirtyseventhpercentile.nerdyaudio.animation.PropertySet;
import com.thirtyseventhpercentile.nerdyaudio.audio.AudioPlayer;
import com.thirtyseventhpercentile.nerdyaudio.audio.Waveform;
import com.thirtyseventhpercentile.nerdyaudio.draw.AnimatableText;
import com.thirtyseventhpercentile.nerdyaudio.draw.AnimatableWaveform;
import com.thirtyseventhpercentile.nerdyaudio.file.MusicInformation;
import com.thirtyseventhpercentile.nerdyaudio.file.QueueManager;
import com.thirtyseventhpercentile.nerdyaudio.helper.BitmapConversions;
import com.thirtyseventhpercentile.nerdyaudio.helper.ColorFiddler;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.QueueListener;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.ProgressStringListener;

//TODO : Enlarge time when scrubbing.

public class PlayControlsView extends View implements ProgressStringListener, QueueListener {
    int w, h;
    Waveform wf;
    Paint pt;
    //RectF artBounds;
    AnimatableRectF artBoundsAnim;
    MixNode<PropertySet> artBoundsNormal;
    MixNode<PropertySet> artBoundsExpanded;

    int menuColor, buttonColor, textPrimary, textSecondary;
    int timestampColor = Color.WHITE, timestampBackgroundColor = Color.BLACK;
    int playedColorEX = Color.BLACK, remainingColorEX = Color.GRAY, playedColor = Color.BLACK, remainingColor = Color.GRAY;
    int timestampSize = 24;
    int buttonPaddings = 16;
    int buttonMargins = 8;//dp
    int albumArtSize = 100;//dp
    int normalBarHeight = 100; //dp
    int expandedBarHeight = 200;
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
    MixNode<PointsCompound> playBtnShape;
    MixNode<PointsCompound> playBtnShapeNormal;
    MixNode<PointsCompound> playBtnShapeDiamond;
    MixNode<PropertySet> buttonFollower;
    MixNode<PropertySet> buttonFollowerProgress;
    MixNode<PropertySet> buttonFollowerUser;
    MixNode<PropertySet> buttonFollowerYExpanded;
    MixNode<PropertySet> buttonFollowerYNotExpanded;

    MixNode<PropertySet> playBtnRestPosition;
    MixNode<PropertySet> playBtnRestCenter;
    MixNode<PropertySet> playBtnRestSide;

    AnimatableText titleAnimatable;
    MixNode<PropertySet> titleNoArt;
    MixNode<PropertySet> titleNormal;
    MixNode<PropertySet> titleExpanded;
    MixNode<PropertySet> titleNotExpanded;

    AnimatableText artistAnimatable;
    MixNode<PropertySet> artistNoArt;
    MixNode<PropertySet> artistNormal;
    MixNode<PropertySet> artistExpanded;
    MixNode<PropertySet> artistNotExpanded;


    MixNode<PropertySet> albumArtColor;
    MixNode<PropertySet> albumArtNormal;
    MixNode<PropertySet> albumArtNone;

    AnimatableShape pauseBtn;
    MixNode<PointsCompound> pausePointsMixed;
    MixNode<PointsCompound> pausePointsSquare;
    MixNode<PointsCompound> pausePointsNormal;
    MixNode<PropertySet> pauseRest;
    MixNode<PropertySet> pauseRestCenter;
    MixNode<PropertySet> pauseRestSide;
    MixNode<PropertySet> pauseVisible;
    MixNode<PropertySet> pauseInvisible;


    AnimatableShape nextBtn;
    MixNode<PropertySet> nextSide;
    MixNode<PropertySet> nextCenter;
    MixNode<PropertySet> nextBounce;


    AnimatableShape prevBtn;
    MixNode<PropertySet> prevSide;
    MixNode<PropertySet> prevCenter;
    MixNode<PropertySet> prevBounce;

    AnimatableText timestampAnim;
    MixNode<PropertySet> timestampRest;
    MixNode<PropertySet> timestampFollow;
    MixNode<PropertySet> timestampExpanded;
    MixNode<PropertySet> timestampNotExpanded;

    AnimatableWaveform waveform;
    MixNode<PropertySet> waveformExpanded;
    MixNode<PropertySet> waveformNotExpanded;
    MixNode<PropertySet> waveformCenterActive;
    MixNode<PropertySet> waveformCenterInactive;

    MixNode<PropertySet> barHeight;
    MixNode<PropertySet> expandedBarHeightMP;
    MixNode<PropertySet> normalBarHeightMP;

    AnimatableText filePath;
    MixNode<PropertySet> filePathActive;
    MixNode<PropertySet> filePathActiveArt;
    MixNode<PropertySet> filePathActiveNoArt;
    MixNode<PropertySet> filePathInactive;

    static PlayControlsView inst;
    public static PlayControlsView getInstance(){
        return inst;
    }

    //TODO Performance. Srsly.

    public PlayControlsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
        density = getResources().getDisplayMetrics().density;
        qm = QueueManager.getInstance();
        ap = AudioPlayer.getInstance();

        Resources res = getResources();
        menuColor = getResources().getColor(R.color.colorPrimary);
        buttonColor = Color.WHITE;
        textPrimary = getResources().getColor(R.color.colorLightPrimaryText);
        textSecondary = getResources().getColor(R.color.colorLightSecondaryText);

        playedColor = ColorFiddler.setAlpha(res.getColor(R.color.colorPrimary), 200);
        //Log2.log(2,this,Color.alpha(playedColor),Color.red(playedColor),Color.blue(playedColor),Color.green(playedColor));
        remainingColor = ColorFiddler.setAlpha(res.getColor(R.color.colorPrimary), 100);

        playedColorEX = ColorFiddler.setAlpha(res.getColor(R.color.colorAccentFallbackDark), 255);
        remainingColorEX = ColorFiddler.setAlpha(res.getColor(R.color.colorAccent), 255);
        //We need to disable hardware acceleration for this layer,
        //since we need to fiddle with blending and shader combining
        //for the marquee text effect.
        //It also takes care of a bug with Paths.
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);


        prepareLayout();

        inst=this;
    }

    public void setWaveform(Waveform w) {
        this.wf = w;
        invalidate();
    }

    protected void prepareLayout() {
        barHeight = new MixNode<PropertySet>("BarHeight");
        expandedBarHeightMP = new MixNode<PropertySet>("Expanded", new PropertySet().setValue("Height", expandedBarHeight * density));
        expandedBarHeightMP.getInfluence().set(0.0f);
        normalBarHeightMP = new MixNode<PropertySet>("Expanded", new PropertySet().setValue("Height", normalBarHeight * density));
        barHeight.addNode(expandedBarHeightMP);
        barHeight.addNode(normalBarHeightMP);

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
        artBoundsAnim = new AnimatableRectF(new MixNode<PropertySet>("Final"));
        artBoundsNormal = new MixNode<PropertySet>("Normal", new PropertySet().setValue("X-", albumArtMargin * density)
                .setValue("Y-", h - (-albumArtMargin + (normalBarHeight)) * density)
                .setValue("X+", (albumArtSize - albumArtMargin) * density)
                .setValue("Y+", h - (-albumArtSize + albumArtMargin + normalBarHeight) * density));
        artBoundsExpanded = new MixNode<PropertySet>("Expanded", new PropertySet().setValue("X-", albumArtMargin * density)
                .setValue("Y-", h - (-albumArtMargin + (expandedBarHeight)) * density)
                .setValue("X+", (albumArtSize - albumArtMargin) * density)
                .setValue("Y+", h - (-albumArtSize + albumArtMargin + expandedBarHeight) * density));
        artBoundsExpanded.getInfluence().set(0.0f);
        artBoundsAnim.getMixNode().addNode(artBoundsNormal);
        artBoundsAnim.getMixNode().addNode(artBoundsExpanded);


        buttonFollower = new MixNode<PropertySet>("Follower");
        buttonFollowerUser = new MixNode<PropertySet>("User", new PropertySet().setValue("X", 0).setValue("Scale", 0.3f).setValue("Rotation", 0).setValue("Alpha", 1.0f));
        buttonFollowerProgress = new MixNode<PropertySet>("Progress", new PropertySet().setValue("X", 0).setValue("Scale", 0.3f).setValue("Rotation", 0).setValue("Alpha", 1.0f));
        buttonFollowerUser.getInfluence().set(0.0f);
        buttonFollowerYExpanded = new MixNode<PropertySet>("YExpanded", new PropertySet().setValue("Y", h - (expandedBarHeight - albumArtSize - 20) * density));
        buttonFollowerYExpanded.getInfluence().set(0.0f);
        buttonFollowerYNotExpanded = new MixNode<PropertySet>("YNotExpanded", new PropertySet().setValue("Y", h - (normalBarHeight) * density));
        buttonFollower.addNode(buttonFollowerProgress);
        buttonFollower.addNode(buttonFollowerUser);
        buttonFollower.addNode(buttonFollowerYExpanded);
        buttonFollower.addNode(buttonFollowerYNotExpanded);
        buttonFollower.getInfluence().set(0);

        playBtnRestPosition = new MixNode<PropertySet>("Rest");
        playBtnRestSide = new MixNode<PropertySet>("Side", new PropertySet().setValue("X", buttonsAreaIni + buttonsAreaW * (3.0f / 6.0f)).setValue("Y", h - (buttonsSize / 2 + buttonMargins) * density).setValue("Scale", 1).setValue("Rotation", -90).setValue("Alpha", 1.0f));
        playBtnRestCenter = new MixNode<PropertySet>("Center", new PropertySet().setValue("X", w * (3.0f / 6.0f)).setValue("Y", h - (buttonsSize / 2 + buttonMargins) * density).setValue("Scale", 1).setValue("Rotation", -90).setValue("Alpha", 1.0f));
        playBtnRestCenter.getInfluence().set(0.0f);
        playBtnRestPosition.addNode(playBtnRestSide);
        playBtnRestPosition.addNode(playBtnRestCenter);

        playBtnShape=new MixNode<PointsCompound>("Play Btn Shape");
        playBtnShapeNormal=new MixNode<PointsCompound>("Play Btn Normal",PrimitivePaths.play(buttonsSize / 2.0f * density));
        playBtnShapeDiamond=new MixNode<PointsCompound>("Play Btn Normal",PrimitivePaths.playDiamond(buttonsSize / 2.0f * density));
        playBtnShapeDiamond.getInfluence().set(0.0f);
        playBtnShape.getInfluence().set(1.0f);
        playBtnShape.addNode(playBtnShapeNormal);
        playBtnShape.addNode(playBtnShapeDiamond);

        playBtn = new AnimatableShape(playBtnShape, buttonColor, new MixNode<PropertySet>("Mix"));
        playBtn.getMixNode().addNode(buttonFollower);
        playBtn.getMixNode().addNode(playBtnRestPosition);


        pauseRest = new MixNode<PropertySet>("Rest");
        pauseRestSide = new MixNode<PropertySet>("Side", new PropertySet().setValue("X", buttonsAreaIni + buttonsAreaW * (3.0f / 6.0f)).setValue("Y", h - (buttonsSize / 2 + buttonMargins) * density));
        pauseRestCenter = new MixNode<PropertySet>("Center", new PropertySet().setValue("X", w * (3.0f / 6.0f)).setValue("Y", h - (buttonsSize / 2 + buttonMargins) * density));
        pauseRestCenter.getInfluence().set(0.0f);
        pauseRest.addNode(pauseRestSide);
        pauseRest.addNode(pauseRestCenter);


        pausePointsMixed=new MixNode<PointsCompound>("Pause Shape");
        pausePointsNormal=new MixNode<PointsCompound>("Pause Normal",PrimitivePaths.pause(buttonsSize / 2.0f * density));
        pausePointsSquare=new MixNode<PointsCompound>("Pause Square",PrimitivePaths.pauseSquare(buttonsSize / 2.0f * density));
        pausePointsNormal.getInfluence().set(0.0f);
        pausePointsMixed.addNode(pausePointsSquare);
        pausePointsMixed.addNode(pausePointsNormal);

        pauseBtn = new AnimatableShape(pausePointsMixed, buttonColor, new MixNode<PropertySet>("Mix"));

        pauseVisible = new MixNode<PropertySet>("PauseVisible", new PropertySet().setValue("Alpha", 1.0f).setValue("Scale", 1).setValue("Rotation", 0));
        pauseVisible.getInfluence().set(0);
        pauseInvisible = new MixNode<PropertySet>("PauseInvisible", new PropertySet().setValue("Alpha", 0.0f).setValue("Scale", 0).setValue("Rotation", 180));

        pauseBtn.getMixNode().addNode(pauseInvisible);
        pauseBtn.getMixNode().addNode(pauseVisible);
        pauseBtn.getMixNode().addNode(pauseRest);



        nextBtn = new AnimatableShape(PrimitivePaths.next(buttonsSize / 2.0f * density), buttonColor, new MixNode<PropertySet>("FinalMix"));
        nextSide = new MixNode<PropertySet>("Side", new PropertySet().setValue("X", buttonsAreaIni + buttonsAreaW * (5.0f / 6.0f)).setValue("Y", h - (buttonsSize / 2 + buttonMargins) * density).setValue("Scale", 1).setValue("Rotation", 0).setValue("Alpha", 1.0f));
        nextCenter = new MixNode<PropertySet>("Center", new PropertySet().setValue("X", w * (5.0f / 6.0f)).setValue("Y", h - (buttonsSize / 2 + buttonMargins) * density).setValue("Scale", 1).setValue("Rotation", 0).setValue("Alpha", 1.0f));
        nextCenter.getInfluence().set(0.0f);
        nextBtn.getMixNode().addNode(nextSide);
        nextBtn.getMixNode().addNode(nextCenter);
        nextBounce=new MixNode<PropertySet>("Next Bounce",new PropertySet().setValue("X",32*density));
        nextBounce.getBasis().setMode(PropertySet.ADDITIVE);
        nextBounce.getInfluence().set(0.0f);
        nextBtn.getMixNode().addNode(nextBounce);




        prevBtn = new AnimatableShape(PrimitivePaths.next(buttonsSize / 2.0f * density), buttonColor, new MixNode<PropertySet>("FinalMix"));
        prevSide = new MixNode<PropertySet>("Side", new PropertySet().setValue("X", buttonsAreaIni + buttonsAreaW * (1.0f / 6.0f)).setValue("Y", h - (buttonsSize / 2 + buttonMargins) * density).setValue("Scale", 1).setValue("Rotation", 180).setValue("Alpha", 1.0f));
        prevCenter = new MixNode<PropertySet>("Center", new PropertySet().setValue("X", w * (1.0f / 6.0f)).setValue("Y", h - (buttonsSize / 2 + buttonMargins) * density).setValue("Scale", 1).setValue("Rotation", 180).setValue("Alpha", 1.0f));
        prevCenter.getInfluence().set(0.0f);
        prevBtn.getMixNode().addNode(prevSide);
        prevBtn.getMixNode().addNode(prevCenter);
        prevBounce=new MixNode<PropertySet>("Prev Bounce",new PropertySet().setValue("X",-32*density));
        prevBounce.getBasis().setMode(PropertySet.ADDITIVE);
        prevBounce.getInfluence().set(0.0f);
        prevBtn.getMixNode().addNode(prevBounce);


        titleAnimatable = new AnimatableText(new MixNode<PropertySet>("Mix"), textPrimary, "", 24 * density);
        titleNoArt = new MixNode<PropertySet>("NoArt", new PropertySet().setValue("X", albumArtMargin * density));
        titleNoArt.getInfluence().set(0);
        titleNormal = new MixNode<PropertySet>("Normal", new PropertySet().setValue("X", albumArtSize * density));
        titleExpanded = new MixNode<PropertySet>("Expanded", new PropertySet().setValue("Y", h - (expandedBarHeight - 6) * density));
        titleExpanded.getInfluence().set(0.0f);
        titleNotExpanded = new MixNode<PropertySet>("NotExpanded", new PropertySet().setValue("Y", h - (normalBarHeight - 6) * density));
        titleAnimatable.getMixNode().addNode(titleNoArt);
        titleAnimatable.getMixNode().addNode(titleNormal);
        titleAnimatable.getMixNode().addNode(titleExpanded);
        titleAnimatable.getMixNode().addNode(titleNotExpanded);
        titleAnimatable.enableMarquee(w - (albumArtSize + albumArtMargin) * density, 16 * density);


        artistNormal = new MixNode<PropertySet>("Normal", new PropertySet().setValue("X", albumArtSize * density));
        artistAnimatable = new AnimatableText(new MixNode<PropertySet>("FinalMix"), textSecondary, "", 16 * density);
        artistNoArt = new MixNode<PropertySet>("NoArt", new PropertySet().setValue("X", albumArtMargin * density));
        artistNoArt.getInfluence().set(0);
        artistExpanded = new MixNode<PropertySet>("Expanded", new PropertySet().setValue("Y", h - (expandedBarHeight - 36) * density));
        artistExpanded.getInfluence().set(0.0f);
        artistNotExpanded = new MixNode<PropertySet>("NotExpanded", new PropertySet().setValue("Y", h - (normalBarHeight - 36) * density));

        artistAnimatable.getMixNode().addNode(artistNoArt);
        artistAnimatable.getMixNode().addNode(artistNormal);
        artistAnimatable.getMixNode().addNode(artistExpanded);
        artistAnimatable.getMixNode().addNode(artistNotExpanded);

        filePath = new AnimatableText(new MixNode<PropertySet>("Mix"), textSecondary, "", 12 * density);
        filePath.enableMarquee(w - (albumArtSize + albumArtMargin) * density, 16 * density);
        filePathActive = new MixNode<PropertySet>("Active");
        filePathActiveArt = new MixNode<PropertySet>("Art", new PropertySet().setValue("X", albumArtSize * density).setValue("Y", h - (expandedBarHeight - 60) * density));
        filePathActiveNoArt = new MixNode<PropertySet>("NoArt", new PropertySet().setValue("X", albumArtMargin * density).setValue("Y", h - (expandedBarHeight - 60) * density));
        filePathActiveNoArt.getInfluence().set(0.0f);
        filePathActive.addNode(filePathActiveArt);
        filePathActive.addNode(filePathActiveNoArt);
        filePathInactive = new MixNode<PropertySet>("Inactive", new PropertySet().setValue("X", w).setValue("Y", h - (expandedBarHeight - 60) * density));
        filePathActive.getInfluence().set(0.0f);
        filePath.getMixNode().addNode(filePathActive);
        filePath.getMixNode().addNode(filePathInactive);

        albumArtColor = new MixNode<PropertySet>("AlbumArtMix");
        albumArtNormal = new MixNode<PropertySet>("Normal", new PropertySet().setValue("alpha", 255));
        albumArtNone = new MixNode<PropertySet>("NoArt", new PropertySet().setValue("alpha", 0));
        albumArtNone.getInfluence().set(0);
        albumArtColor.addNode(albumArtNone);
        albumArtColor.addNode(albumArtNormal);


        timestampAnim = new AnimatableText(new MixNode<PropertySet>("FinalMixed"), textPrimary, "", timestampSize * density);
        timestampRest = new MixNode<PropertySet>("Rest", new PropertySet().setValue("X", w - 30 * density));
        timestampFollow = new MixNode<PropertySet>("Follow", new PropertySet().setValue("X", 0));
        timestampNotExpanded = new MixNode<PropertySet>("NotExpanded", new PropertySet().setValue("Y", h - (normalBarHeight + 30) * density));
        timestampExpanded = new MixNode<PropertySet>("Expanded", new PropertySet().setValue("Y", h - (expandedBarHeight - albumArtSize - 40 + 45) * density));
        timestampExpanded.getInfluence().set(0.0f);
        timestampFollow.getInfluence().set(0.0f);
        timestampAnim.getMixNode().addNode(timestampRest);
        timestampAnim.getMixNode().addNode(timestampFollow);
        timestampAnim.getMixNode().addNode(timestampNotExpanded);
        timestampAnim.getMixNode().addNode(timestampExpanded);
        timestampAnim.setBgColor(timestampBackgroundColor);
        timestampAnim.drawBackground(true);
        timestampAnim.setAlign(AnimatableText.ALIGN_CENTER);
        //playBtn=new AnimatableShape(PrimitivePaths.triangle(50),50,50,1,0);

        waveform = new AnimatableWaveform(wf, ap, new MixNode<PropertySet>("Final"), density);
        waveformNotExpanded = new MixNode<PropertySet>("NotExpanded",
                new PropertySet().setValue("X", 0).setValue("Y", h - normalBarHeight * density)
                        .setValue("XSize", w).setValue("YSize", 50 * density)
                        .setValue("YBalance",1.0f)
                        .setValue("Played-R", ColorFiddler.rF(playedColor))
                        .setValue("Played-G", ColorFiddler.gF(playedColor))
                        .setValue("Played-B", ColorFiddler.bF(playedColor))
                        .setValue("Played-A", ColorFiddler.aF(playedColor))
                        .setValue("Remaining-R", ColorFiddler.rF(remainingColor))
                        .setValue("Remaining-G", ColorFiddler.gF(remainingColor))
                        .setValue("Remaining-B", ColorFiddler.bF(remainingColor))
                        .setValue("Remaining-A", ColorFiddler.aF(remainingColor))
                        .setValue("CenterBarHeight",2*density));
        waveformExpanded = new MixNode<PropertySet>("Expanded",
                new PropertySet().setValue("X", 0).setValue("Y", h - (expandedBarHeight - albumArtSize - 20) * density)
                        .setValue("XSize", w).setValue("YSize", 40 * density)
                        .setValue("YBalance",0.5f)
                        .setValue("Played-R", ColorFiddler.rF(playedColorEX))
                        .setValue("Played-G", ColorFiddler.gF(playedColorEX))
                        .setValue("Played-B", ColorFiddler.bF(playedColorEX))
                        .setValue("Played-A", ColorFiddler.aF(playedColorEX))
                        .setValue("Remaining-R", ColorFiddler.rF(remainingColorEX))
                        .setValue("Remaining-G", ColorFiddler.gF(remainingColorEX))
                        .setValue("Remaining-B", ColorFiddler.bF(remainingColorEX))
                        .setValue("Remaining-A", ColorFiddler.aF(remainingColorEX))
                        .setValue("CenterBarHeight",2*density));
        waveformCenterActive=new MixNode<PropertySet>("Waveform Center Active",new PropertySet().setValue("CenterBarWidth",1.0f));
        waveformCenterInactive=new MixNode<PropertySet>("Waveform Center Inactive",new PropertySet().setValue("CenterBarWidth",0.0f));
        waveformCenterActive.getInfluence().set(0.0f);
        waveformExpanded.getInfluence().set(0.0f);
        waveform.getMixNode().addNode(waveformExpanded);
        waveform.getMixNode().addNode(waveformNotExpanded);
        waveform.getMixNode().addNode(waveformCenterInactive);
        waveform.getMixNode().addNode(waveformCenterActive);


    }

    private void toggleExpand() {
        if (expanded) {
            expand(false);
        } else {
            expand(true);
        }
    }

    boolean expanded = false;

    public void expand(boolean expand) {
        int ease=EasingEquations.QUINTIC;
        if (this.expanded == expand) return;
        this.expanded = expand;
        if (expand) {
            expandedBarHeightMP.getInfluence().animate(1, 1, ease);
            normalBarHeightMP.getInfluence().animate(0, 1, ease);

            titleExpanded.getInfluence().animate(1, 1, ease);
            titleNotExpanded.getInfluence().animate(0, 1, ease);

            artistExpanded.getInfluence().animate(1, 1, ease);
            artistNotExpanded.getInfluence().animate(0, 1, ease);

            artBoundsExpanded.getInfluence().animate(1, 1, ease);
            artBoundsNormal.getInfluence().animate(0, 1, ease);

            waveformExpanded.getInfluence().animate(1, 1, ease);
            waveformNotExpanded.getInfluence().animate(0, 1, ease);

            timestampExpanded.getInfluence().animate(1, 1, ease);
            timestampNotExpanded.getInfluence().animate(0, 1, ease);

            buttonFollowerYExpanded.getInfluence().animate(1, 1, ease);
            buttonFollowerYNotExpanded.getInfluence().animate(0, 1, ease);

            filePathActive.getInfluence().animate(1, 1, ease);
            filePathInactive.getInfluence().animate(0, 1, ease);
            buttonsCenter(true,ease);
        } else {
            expandedBarHeightMP.getInfluence().animate(0, 1, ease);
            normalBarHeightMP.getInfluence().animate(1, 1, ease);

            titleExpanded.getInfluence().animate(0, 1, ease);
            titleNotExpanded.getInfluence().animate(1, 1, ease);

            artistExpanded.getInfluence().animate(0, 1, ease);
            artistNotExpanded.getInfluence().animate(1, 1, ease);

            artBoundsExpanded.getInfluence().animate(0, 1, ease);
            artBoundsNormal.getInfluence().animate(1, 1, ease);

            waveformExpanded.getInfluence().animate(0, 1, ease);
            waveformNotExpanded.getInfluence().animate(1, 1, ease);

            timestampExpanded.getInfluence().animate(0, 1, ease);
            timestampNotExpanded.getInfluence().animate(1, 1, ease);

            buttonFollowerYExpanded.getInfluence().animate(0, 1, ease);
            buttonFollowerYNotExpanded.getInfluence().animate(1, 1, ease);

            filePathActive.getInfluence().animate(0, 1, ease);
            filePathInactive.getInfluence().animate(1, 1, ease);
            if (currentMusic != null) if (currentMusic.hasArt()) buttonsCenter(false,ease);
        }
    }

    private void buttonsCenter(boolean center){
        buttonsCenter(center,EasingEquations.DEFAULT_EASE);
    }

    private void buttonsCenter(boolean center, int ease) {
        if (!center) {
            playBtnRestCenter.getInfluence().animate(0, 1, ease);
            playBtnRestSide.getInfluence().animate(1, 1, ease);

            pauseRestCenter.getInfluence().animate(0, 1, ease);
            pauseRestSide.getInfluence().animate(1, 1, ease);

            prevCenter.getInfluence().animate(0, 1, ease);
            prevSide.getInfluence().animate(1, 1, ease);
            nextCenter.getInfluence().animate(0, 1, ease);
            nextSide.getInfluence().animate(1, 1, ease);

        } else {
            playBtnRestCenter.getInfluence().animate(1, 1, ease);
            playBtnRestSide.getInfluence().animate(0, 1, ease);
            pauseRestCenter.getInfluence().animate(1, 1, ease);
            pauseRestSide.getInfluence().animate(0, 1, ease);

            prevCenter.getInfluence().animate(1, 1, ease);
            prevSide.getInfluence().animate(0, 1, ease);
            nextCenter.getInfluence().animate(1, 1, ease);
            nextSide.getInfluence().animate(0, 1, ease);
        }
    }


    String title;
    String artist;
    Bitmap albumArt;
    MusicInformation currentMusic;

    protected void parseMusicInformation(final MusicInformation mi) {
        currentMusic = mi;
        title = mi.getTitle();
        titleAnimatable.setText(mi.getTitle());
        artist = mi.getArtist();
        artistAnimatable.setText(mi.getArtist());
        filePath.setText(mi.getFilepath());

        if (mi.hasArt()) {
            artistNormal.getInfluence().set(0);
            artistNoArt.getInfluence().set(1);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //TODO this soometimes cause weird race condition glithches.
                    RectF bounds = artBoundsAnim.getRectF(currentFrameTime);
                    albumArt = BitmapConversions.decodeSampledBitmapFromResource(mi.getArtByteArray(), Math.round(bounds.width()), Math.round(bounds.height()));
                    albumArtNormal.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
                    albumArtNone.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
                }
            }).start();


            titleNormal.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
            titleNoArt.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
            titleAnimatable.enableMarquee(w - (albumArtSize + albumArtMargin) * density, 16 * density);
            filePath.enableMarquee(w - (albumArtSize + albumArtMargin) * density, 16 * density);
            filePathActiveArt.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
            filePathActiveNoArt.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
            artistNormal.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
            artistNoArt.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);

            if (!expanded) buttonsCenter(false);

        } else {
            titleNormal.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
            titleNoArt.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
            titleAnimatable.enableMarquee(w - albumArtMargin * 2 * density, 16 * density);
            filePath.enableMarquee(w - albumArtMargin * 2 * density, 16 * density);
            filePathActiveArt.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
            filePathActiveNoArt.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
            albumArtNormal.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
            albumArtNone.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
            artistNormal.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
            artistNoArt.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);

            if (!expanded) buttonsCenter(true);
        }
    }

    long currentFrameTime;
    @Override
    protected void onDraw(Canvas canvas) {

        //Log2.log(2,this,buttonFollower.getInfluence().getValue(),playBtnRestPosition.getInfluence().getValue());

        currentFrameTime=System.currentTimeMillis();

        pt.setColor(menuColor);
        canvas.drawRect(0, h - barHeight.getValue(currentFrameTime).getValue("Height"), w, h, pt);


        titleAnimatable.draw(canvas, pt,currentFrameTime);

        artistAnimatable.draw(canvas, pt,currentFrameTime);

        filePath.draw(canvas, pt,currentFrameTime);
        if (albumArt != null) {
            pt.setAlpha(Math.round(albumArtColor.getValue(currentFrameTime).getValue("alpha")));
            canvas.drawBitmap(albumArt, null, artBoundsAnim.getRectF(currentFrameTime), pt);
            //Log.d(LOG_TAG, "trying to draw..");
            pt.setAlpha(255);
        }


        waveform.draw(canvas, pt,currentFrameTime);

        if (wf != null && ap != null && wf.isReady() && wf.getFilename().equals(ap.getSourceString())) {
            setCurrentPosition((float) (ap.getMusicCurrentFrame() / (double) wf.getNumOfFrames()));

            if (!dragMode) {
                timestampAnim.setText(wf.frameNumberToTimeStamp(ap.getMusicCurrentFrame()));
            }

            timestampAnim.draw(canvas, pt,currentFrameTime);

            buttonFollowerProgress.getBasis().setValue("X", w * currentPosition);
        }




        pt.setColor(Color.argb(50, 0, 0, 0));

        pt.setColor(buttonColor);

        playBtn.draw(canvas, pt,currentFrameTime);

        pauseBtn.draw(canvas, pt,currentFrameTime);

        prevBtn.draw(canvas, pt,currentFrameTime);
        nextBtn.draw(canvas, pt,currentFrameTime);

        invalidate();

    }


    private void animatePlay() {

        playBtnShapeDiamond.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
        playBtnShapeNormal.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

        playBtnRestPosition.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
        buttonFollower.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);

        pauseVisible.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
        pauseInvisible.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);

        pausePointsSquare.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
        pausePointsNormal.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);

        waveformCenterActive.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
        waveformCenterInactive.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
    }

    private void animateStop() {
        playBtnShapeDiamond.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
        playBtnShapeNormal.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);

        playBtnRestPosition.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
        buttonFollower.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);

        pauseVisible.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
        pauseInvisible.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);

        pausePointsSquare.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
        pausePointsNormal.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);

        waveformCenterActive.getInfluence().animate(0,1,EasingEquations.DEFAULT_EASE);
        waveformCenterInactive.getInfluence().animate(1,1,EasingEquations.DEFAULT_EASE);
    }

    private void animateNext(){
        nextBounce.getInfluence().set(1.0f);
        nextBounce.getInfluence().animate(0.0f,1,EasingEquations.DEFAULT_EASE);
    }
    private void animatePrev(){
        prevBounce.getInfluence().set(1.0f);
        prevBounce.getInfluence().animate(0.0f,1,EasingEquations.DEFAULT_EASE);
    }

    float iniX, iniY;
    boolean dragMode = false;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_DOWN) {
            iniX = ev.getX();
            iniY = ev.getY();

            //Play Button Drag
            if (buttonFollower.getInfluence().getValue() > 0.99f) { //if in playing mode and button is in follow state
                if (playBtn.getBounds(buttonPaddings * density,currentFrameTime).contains(ev.getX(), ev.getY())) {
                    dragMode = true;
                    buttonFollowerUser.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
                    buttonFollowerProgress.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);

                    timestampFollow.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);
                    timestampRest.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);

                    buttonFollowerUser.getBasis().setValue("X", ev.getX());
                    timestampFollow.getBasis().setValue("X", ev.getX());
                    return true;
                }
            }

            //Other buttons
            if (prevBtn.getBounds(buttonPaddings * density,currentFrameTime).contains(ev.getX(), ev.getY())) {
                return true;
            } else if (playBtn.getBounds(buttonPaddings * density,currentFrameTime).contains(ev.getX(), ev.getY())) {
                return true;
            } else if (pauseBtn.getBounds(buttonPaddings * density,currentFrameTime).contains(ev.getX(), ev.getY())) {
                return true;
            } else if (nextBtn.getBounds(buttonPaddings * density,currentFrameTime).contains(ev.getX(), ev.getY())) {
                return true;
            } else if (artBoundsAnim.getRectF(currentFrameTime).contains(ev.getX(), ev.getY())) {
                return true;
            }


        } else if (action == MotionEvent.ACTION_MOVE) {
            //Dragging in progress
            if (dragMode) {
                buttonFollowerUser.getBasis().setValue("X", ev.getX());
                timestampFollow.getBasis().setValue("X", ev.getX());
                timestampAnim.setText(wf.frameNumberToTimeStamp((long) (wf.getNumOfFrames() * (ev.getX() / (double) w))));
            }
            return true;
        } else if (action == MotionEvent.ACTION_UP) {
            performClick();
            //buttons get priority.
            if (dragMode) {
                buttonFollowerUser.getInfluence().animate(0, 0.5f, EasingEquations.DEFAULT_EASE);
                buttonFollowerProgress.getInfluence().animate(1, 0.5f, EasingEquations.DEFAULT_EASE);
                float totalTime = (float) (wf.getNumOfFrames() / (double) ap.getSampleRate());
                ap.seekTo(totalTime * ev.getX() / w);

                timestampFollow.getInfluence().animate(0, 1, EasingEquations.DEFAULT_EASE);
                timestampRest.getInfluence().animate(1, 1, EasingEquations.DEFAULT_EASE);

                dragMode = false;

                return true;
            }

            //Button doing stuff.
            if (prevBtn.getBounds(buttonPaddings * density,currentFrameTime).contains(ev.getX(), ev.getY())) {
                qm.playPreviousFile();
                return true;
            } else if (playBtn.getBounds(buttonPaddings * density,currentFrameTime).contains(ev.getX(), ev.getY())) {
                qm.playCurrent();
                return true;
            } else if (pauseBtn.getBounds(buttonPaddings * density,currentFrameTime).contains(ev.getX(), ev.getY())) {
                qm.pause();
                return true;
            } else if (nextBtn.getBounds(buttonPaddings * density,currentFrameTime).contains(ev.getX(), ev.getY())) {
                qm.playNextFile();
                return true;
            } else if (artBoundsAnim.getRectF(currentFrameTime).contains(ev.getX(), ev.getY())) {
                toggleExpand();

                return true;
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
    public void playbackStarted() {
        animatePlay();
    }

    @Override
    public void playbackStopped() {
        animateStop();
    }

    @Override
    public void nextSong() {
        animateNext();
    }

    @Override
    public void previousSong() {
        animatePrev();
    }

    @Override
    public void report(String s) {
        //TODO something here.
    }
}
