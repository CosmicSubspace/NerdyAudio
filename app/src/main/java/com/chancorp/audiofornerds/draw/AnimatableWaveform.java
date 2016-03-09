package com.chancorp.audiofornerds.draw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.chancorp.audiofornerds.animation.MixedProperties;
import com.chancorp.audiofornerds.animation.PropertySet;
import com.chancorp.audiofornerds.audio.AudioPlayer;
import com.chancorp.audiofornerds.audio.Waveform;
import com.chancorp.audiofornerds.helper.ColorFiddler;
import com.chancorp.audiofornerds.helper.Log2;

/**
 * Created by Chan on 2/25/2016.
 */
public class AnimatableWaveform extends Animatable{
    Waveform wf;
    AudioPlayer ap;
    AnimatableText notAvailableText;

    float barSpacing=0.0f;

    /**
     * Required Properties:
     * X  - The Left corner
     * Y  - The Bottom corner
     * XSize
     * YSize
     *
     * Played-R
     * Played-G
     * Played-B
     * Played-A
     *
     * Remaining-R
     * Remaining-G
     * Remaining-B
     * Remaining-A
     *
     */



    public AnimatableWaveform(Waveform wf, AudioPlayer ap, MixedProperties basis, float density) {
        super(basis);
        this.wf = wf;
        this.ap = ap;
        this.notAvailableText=new AnimatableText(new MixedProperties("Final",new PropertySet().setValue("X",0).setValue("Y",0)),Color.BLACK,"Waveorm not yet prepared...",16*density);
        this.notAvailableText.setAlign(AnimatableText.ALIGN_CENTER);
    }


    float currentPosition;
    PropertySet current;
    public void draw(Canvas c, Paint pt) {
        current = mixedProperties.update(System.currentTimeMillis());
        notAvailableText.getMixedProperties().getBasis().setValue("X",current.getValue("X")+current.getValue("XSize")/2.0f).setValue("Y",current.getValue("Y")-current.getValue("YSize")/2.0f);
        if (wf != null && ap != null && wf.isReady() && wf.getFilename().equals(ap.getSourceString())) {
            currentPosition=(float) (ap.getMusicCurrentFrame() / (double) wf.getNumOfFrames());
            float spacing = current.getValue("XSize") / (wf.getDivisions() * (1.0f + barSpacing) - barSpacing) * (barSpacing + 1.0f);
            float width = current.getValue("XSize") / (wf.getDivisions() * (1.0f + barSpacing) - barSpacing);
            float progressPerBar = 1 / (float) wf.getDivisions();
            for (int i = 0; i < wf.getDivisions(); i++) {


                if (i * progressPerBar > currentPosition) pt.setColor(getRemainingColor(current));
                else if ((i + 1) * progressPerBar < currentPosition) pt.setColor(getPlayedColor(current));
                else pt.setColor(ColorFiddler.rampColor(getPlayedColor(current), getRemainingColor(current), (currentPosition - i * progressPerBar) / progressPerBar));

                //Log.v(LOG_TAG, "Drawing"+(i*spacing)+" to "+(i*spacing+width));
                c.drawRect(current.getValue("X")+i * spacing, current.getValue("Y")- wf.getRatio(i)*current.getValue("YSize"), current.getValue("X")+i * spacing + width, current.getValue("Y"), pt);
            }
        }else{

            notAvailableText.draw(c,pt);
        }
    }

    private int getPlayedColor(PropertySet ps){
        //Log2.log(2,this,ps.getValue("Played-A"),ps.getValue("Played-R"),ps.getValue("Played-G"),ps.getValue("Played-B"));
        return Color.argb(Math.round(ps.getValue("Played-A") * 255),
                Math.round(ps.getValue("Played-R")*255),
                Math.round(ps.getValue("Played-G")*255),
                Math.round(ps.getValue("Played-B")*255));
    }
    private int getRemainingColor(PropertySet ps){
        return Color.argb(Math.round(ps.getValue("Remaining-A") * 255),
                Math.round(ps.getValue("Remaining-R")*255),
                Math.round(ps.getValue("Remaining-G")*255),
                Math.round(ps.getValue("Remaining-B")*255));
    }


}
