package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.thirtyseventhpercentile.nerdyaudio.animation.PointsCompound;
import com.thirtyseventhpercentile.nerdyaudio.exceptions.BufferNotPresentException;
import com.thirtyseventhpercentile.nerdyaudio.exceptions.InvalidParameterException;
import com.thirtyseventhpercentile.nerdyaudio.helper.SimpleMaths;
import com.thirtyseventhpercentile.nerdyaudio.settings.BaseSetting;
import com.thirtyseventhpercentile.nerdyaudio.settings.CircleVisualSettings;
import com.thirtyseventhpercentile.nerdyaudio.settings.SpectrogramVisualSettings;

/**
 * Created by Chan on 3/24/2016.
 */
public class CircleVisuals extends FftRenderer {
    Paint pt;

    CircleVisualSettings newSettings=null;

    int bars=100;
    float maxRad;

    public float getMaxRad() {
        return maxRad;
    }

    public void setMaxRad(float maxRad) {
        this.maxRad = maxRad;
    }

    public int getBars() {
        return bars;
    }

    public void setBars(int bars) {
        this.bars = bars;
    }





    private void syncChanges(){
        if (newSettings!=null){
            setFFTSize(newSettings.getFftSize());
            Log.i(LOG_TAG, "Spectrum: size changing" + fftSize);

            try {
                setFrequencyRange(newSettings.getStartFreq(), newSettings.getEndFreq());
            }catch(InvalidParameterException e){
                Log.w(LOG_TAG,"SpectrogramVisuals>syncChanges() wut?");
            }
            setLogScale(newSettings.getLogScale());

            setMaxRad(newSettings.getBarHeight());
            setBars(newSettings.getBars());

            newSettings=null;
        }
    }

    public CircleVisuals(float density){
        super(density);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);

        //I have a feeling that this would cause some nasty shit in the future.
        updated(sbs.getSetting(BaseSetting.CIRCLE));

    }

    @Override
    public void updated(BaseSetting setting) {
        if (setting instanceof CircleVisualSettings){
            newSettings=(CircleVisualSettings)setting;
        }
    }

    @Override
    public void drawVisuals(Canvas c, int w, int h) {
        syncChanges();
        long currentFrame = getCurrentFrame();
        try {
            updateFFT(currentFrame);

            pt.setColor(Color.BLACK);
            c.drawPath(getPath(), pt);
        }catch (BufferNotPresentException e) {
            Log.d(LOG_TAG, "Buffer not present! Requested around " + currentFrame);
        }

    }
    double baseR=100,addR,x,y;
    private Path getPath(){
        PointsCompound.Builder builder=new PointsCompound.Builder();

        //We smooth the values a little.
        baseR=baseR*0.4+ SimpleMaths.linearMap(getMagnitude(100),0,100,100,200)*0.6;
        for (int i = 0; i < bars; i++) {
            addR=getMagnitudeRatio(i/(float)bars);
            x=(baseR+addR)*Math.cos(i/(double)bars*2*Math.PI);
            y=(baseR+addR)*Math.sin(i/(double)bars*2*Math.PI);
            builder.addPoint((float)x,(float)y);
        }
        Matrix translationMatrix=new Matrix();
        translationMatrix.preTranslate(w/2,h/2);
        return builder.build().transform(translationMatrix).toPath();

    }

}
