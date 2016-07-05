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
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.helper.SimpleMaths;
import com.thirtyseventhpercentile.nerdyaudio.settings.BaseSetting;
import com.thirtyseventhpercentile.nerdyaudio.settings.CircleVisualSettings;
import com.thirtyseventhpercentile.nerdyaudio.settings.SettingsUiFactory;

/**
 * Created by Chan on 3/24/2016.
 */

//TODO : osu-like.

public class CircleVisuals extends FftRenderer {
    Paint pt;

    CircleVisualSettings newSettings = null;

    int bars = 100;
    float overlap = 0.2f;
    float sensitivity = 1, bassSensitivity = 1;
    int repeats = 2;

    public float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public float getBassSensitivity() {
        return bassSensitivity;
    }

    public void setBassSensitivity(float bassSensitivity) {
        this.bassSensitivity = bassSensitivity;
    }

    public int getBars() {
        return bars;
    }

    public void setBars(int bars) {
        this.bars = bars;
    }


    private void syncChanges() {
        if (newSettings != null) {
            setFFTSize(newSettings.getFftSize());
            Log2.log(2, this, "Spectrum: size changing" + fftSize);

            try {
                setFrequencyRange(newSettings.getStartFreq(), newSettings.getEndFreq());
            } catch (InvalidParameterException e) {
                Log2.log(3, this, "SpectrogramVisuals>syncChanges() wut?");
            }
            setLogScale(newSettings.getLogScale());

            setSensitivity(newSettings.getSensitivity());
            setBassSensitivity(newSettings.getBassSensitivity());
            setBars(newSettings.getBars());

            radiuses = new float[newSettings.getBars()];

            newSettings = null;
        }
    }

    @Override
    public SettingsUiFactory.SettingElement[] getSettingUI() {
        return new SettingsUiFactory.SettingElement[0];
    }

    public CircleVisuals(float density) {
        super(density);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);

        //I have a feeling that this would cause some nasty shit in the future.
        updated(sbs.getSetting(BaseSetting.CIRCLE));

    }

    @Override
    public void updated(BaseSetting setting) {
        if (setting instanceof CircleVisualSettings) {
            newSettings = (CircleVisualSettings) setting;
        }
    }

    @Override
    public void dimensionsChanged(int w, int h) {

    }

    @Override
    public void drawVisuals(Canvas c, int w, int h) {
        syncChanges();
        long currentFrame = getCurrentFrame();
        try {
            updateFFT(currentFrame);

            pt.setColor(Color.BLACK);
            c.drawPath(getPath(), pt);
        } catch (BufferNotPresentException e) {
            Log2.log(1, this, "Buffer not present! Requested around " + currentFrame);
        }
    }

    double baseR = 0, addR, x, y;
    float[] radiuses;

    private Path getPath() {
        PointsCompound.Builder builder = new PointsCompound.Builder();
        assert radiuses.length == bars;
        //TODO here


        for (int i = 0; i < bars; i++) {
            radiuses[i] = 0;
        }
        int currentRadiusIndex;
        float currentCircleRatio, currentFftRatio;
        float influence;
        float offsetPerRepeat = 1.0f / repeats;

        for (int rpt = 0; rpt < repeats; rpt++) {

            for (int i = 0; i < bars + overlap*bars ; i++) { //Loop for all bars plus overlap.
                //Loop here and do stuff.
                currentRadiusIndex = (i+(int)(offsetPerRepeat*bars*rpt))%bars; //Index to put data in
                currentCircleRatio=i/(float)bars; //How far into the circle we are.


                if (currentCircleRatio<1.0f) { //Ramp up.
                    influence = SimpleMaths.linearMapClamped(currentCircleRatio, 0, overlap, 0, 1.0f);
                } else { //Ramp down.
                    influence = SimpleMaths.linearMapClamped(currentCircleRatio, 1, 1 + overlap, 1.0f, 0);
                }

                radiuses[currentRadiusIndex] += influence * getMagnitudeRatio(currentCircleRatio) * sensitivity;
            }
        }


        //We smooth the values a little.
        baseR = baseR * 0.5 + SimpleMaths.linearMapClamped(getMagnitudeRange(50, 150, true) * bassSensitivity, 0, 300, 100, 250) * 0.5;
        for (int i = 0; i < bars; i++) {
            //addR=getMagnitudeRatio(i/(float)bars)*sensitivity;
            addR = radiuses[i];
            x = (baseR + addR) * Math.cos(i / (double) bars * 2 * Math.PI);
            y = (baseR + addR) * Math.sin(i / (double) bars * 2 * Math.PI);
            builder.addPoint((float) x, (float) y);
        }
        Matrix translationMatrix = new Matrix();
        translationMatrix.preTranslate(w / 2, h / 2);
        return builder.build().transform(translationMatrix).toPath();
    }


}
