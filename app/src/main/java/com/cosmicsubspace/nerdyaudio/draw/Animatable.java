package com.cosmicsubspace.nerdyaudio.draw;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.cosmicsubspace.nerdyaudio.animation.MixNode;
import com.cosmicsubspace.nerdyaudio.animation.PropertySet;

/**
 * Created by Chan on 2/26/2016.
 */
public abstract class Animatable {
    MixNode<PropertySet> mixedProperties;

    public Animatable(MixNode<PropertySet> basis) {
        this.mixedProperties = basis;
    }

    public MixNode<PropertySet> getMixNode() {
        return this.mixedProperties;
    }

    public abstract void draw(Canvas c, Paint pt, long currentTime);
}
