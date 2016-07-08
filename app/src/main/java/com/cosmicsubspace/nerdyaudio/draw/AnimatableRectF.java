package com.cosmicsubspace.nerdyaudio.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.cosmicsubspace.nerdyaudio.animation.MixNode;
import com.cosmicsubspace.nerdyaudio.animation.PropertySet;

/**
 * Created by Chan on 2/26/2016.
 */
public class AnimatableRectF extends Animatable {
    /**
     * Required Properties:
     * Y+
     * Y-
     * X+
     * X-
     */
    public AnimatableRectF(MixNode<PropertySet> properties) {
        super(properties);
    }

    PropertySet current;

    public RectF getRectF(long time) {
        current = mixedProperties.getValue(time);
        return new RectF(current.getValue("X-"), current.getValue("Y-"), current.getValue("X+"), current.getValue("Y+"));
    }

    @Override
    public void draw(Canvas c, Paint pt, long currentTime) {
        throw new UnsupportedOperationException();
    }
}
