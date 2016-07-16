package com.cosmicsubspace.nerdyaudio.visuals;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import com.cosmicsubspace.nerdyaudio.animation.PointsCompound;
import com.cosmicsubspace.nerdyaudio.exceptions.BufferNotPresentException;
import com.cosmicsubspace.nerdyaudio.helper.Log2;
import com.cosmicsubspace.nerdyaudio.helper.SimpleMaths;
import com.cosmicsubspace.nerdyaudio.settings.FloatSliderElement;
import com.cosmicsubspace.nerdyaudio.settings.SettingElement;
import com.cosmicsubspace.nerdyaudio.settings.SliderElement;

import java.util.List;

/**
 * Created by Chan on 3/24/2016.
 */

public class CircleVisuals extends FftRenderer {
    Paint pt;

    /*
        int bars = 100;

        float sensitivity = 1, bassSensitivity = 1;

        */
    SliderElement bars = new SliderElement("Number of vertices", 3, 500, 100);
    FloatSliderElement sensitivity = new FloatSliderElement("Sensitivity", 0, 3, 1, 100);
    FloatSliderElement bassSensitivity = new FloatSliderElement("Bass Sensitivity", 0, 3, 1, 100);
    FloatSliderElement baseRadius = new FloatSliderElement("Base Radius", 30, 300, 100, 1000);

    int repeats = 2;
    float overlap = 0.2f;

    @Override
    public List<SettingElement> getSettings() {
        List<SettingElement> res = super.getSettings();
        res.add(bars);
        res.add(sensitivity);
        res.add(bassSensitivity);
        res.add(baseRadius);
        return res;
    }


    @Override
    public String getKey() {
        return "CircleVisuals";
    }

    public CircleVisuals(Context ctxt) {
        super(ctxt);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);


    }


    @Override
    public void dimensionsChanged(int w, int h) {

    }

    @Override
    public void drawVisuals(Canvas c, int w, int h) {

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
        int bars = this.bars.getValue();


        if (radiuses == null || (radiuses.length != bars)) {
            radiuses = new float[bars];
        }


        for (int i = 0; i < bars; i++) {
            radiuses[i] = 0;
        }
        int currentRadiusIndex;
        float currentCircleRatio, currentFftRatio;
        float influence;
        float offsetPerRepeat = 1.0f / repeats;

        for (int rpt = 0; rpt < repeats; rpt++) {

            for (int i = 0; i < bars + overlap * bars; i++) { //Loop for all bars plus overlap.
                //Loop here and do stuff.
                currentRadiusIndex = (i + (int) (offsetPerRepeat * bars * rpt)) % bars; //Index to put data in
                currentCircleRatio = i / (float) bars; //How far into the circle we are.


                if (currentCircleRatio < 1.0f) { //Ramp up.
                    influence = SimpleMaths.linearMapClamped(currentCircleRatio, 0, overlap, 0, 1.0f);
                } else { //Ramp down.
                    influence = SimpleMaths.linearMapClamped(currentCircleRatio, 1, 1 + overlap, 1.0f, 0);
                }

                radiuses[currentRadiusIndex] += influence * getMagnitudeRatio(currentCircleRatio) * sensitivity.getFloatValue();
            }
        }


        //We smooth the values a little.
        baseR = baseR * 0.5 + (getMagnitudeRange(50, 150, true) * bassSensitivity.getFloatValue() + baseRadius.getFloatValue()) * 0.5;
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
