package com.chancorp.audiofornerds.visuals;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.chancorp.audiofornerds.exceptions.BufferNotPresentException;

/**
 * Created by Chan on 2015-12-18.
 */
public class WaveformVisuals extends BaseRenderer {
    int range=2048;
    int drawEvery=1;
    Paint pt;

    public WaveformVisuals(float density) {
        super(density);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setRange(int samples) {
        this.range = samples;
    }

    public void drawEvery(int i) {
        this.drawEvery = i;
    }

    @Override
    public void draw(Canvas c, int w, int h) {
        if (vb != null && ap != null) {

            long currentFrame = getCurrentFrame();
try{
            short[] pcmL = getLSamples(currentFrame - range / 2 + 1, currentFrame + range / 2);
            short[] pcmR = getRSamples(currentFrame - range / 2 + 1, currentFrame + range / 2);
            deleteBefore(currentFrame - range / 2 + 1);

            int numberOfLinePoints = pcmL.length / drawEvery;
            float[] lines = new float[numberOfLinePoints * 4];

            int pcmIndex;
            for (int i = 0; i < numberOfLinePoints - 1; i++) {
                pcmIndex = i * drawEvery;
                lines[i * 4] = i / (float) numberOfLinePoints * w;
                lines[i * 4 + 1] = (pcmL[pcmIndex] / 32767.0f + 1) * h / 2.0f;
                lines[i * 4 + 2] = (i + 1) / (float) numberOfLinePoints * w;
                lines[i * 4 + 3] = (pcmL[pcmIndex + drawEvery] / 32767.0f + 1) * h / 2.0f;
            }


            pt.setColor(Color.BLACK);


            c.drawLines(lines, pt);


        }catch (BufferNotPresentException e) {
    Log.d(LOG_TAG, "Buffer not present! Requested around " + currentFrame);
}
    }
}}
