package com.thirtyseventhpercentile.nerdyaudio.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.thirtyseventhpercentile.nerdyaudio.animation.MixedProperties;
import com.thirtyseventhpercentile.nerdyaudio.animation.PropertySet;

/**
 * Created by Chan on 2/26/2016.
 */
public class AnimatableRectF extends Animatable{
    /**
     * Required Properties:
     * Y+
     * Y-
     * X+
     * X-
     *
     */
    public AnimatableRectF(MixedProperties properties){
        super(properties);
    }
    PropertySet current;
    public RectF getRectF(long time){
        current=mixedProperties.update(time);
        return new RectF(current.getValue("X-"),current.getValue("Y-"),current.getValue("X+"),current.getValue("Y+"));
    }
    @Override
    public void draw(Canvas c, Paint pt, long currentTime){
        throw new UnsupportedOperationException();
    }
}
