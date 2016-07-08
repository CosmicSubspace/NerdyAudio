//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.cosmicsubspace.nerdyaudio.draw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;

import com.cosmicsubspace.nerdyaudio.animation.EasingEquations;
import com.cosmicsubspace.nerdyaudio.animation.MixNode;
import com.cosmicsubspace.nerdyaudio.animation.PropertySet;


public class AnimatableText extends Animatable {
    /*
    Required Properties:
    x
    y
     */
    public AnimatableText(MixNode<PropertySet> basisSet, int color, String text, float size) {
        super(basisSet);
        setColor(color);
        this.text = text;
        this.textSize = size;
    }

    //Getter/setter
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        timeOffset = System.currentTimeMillis();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        this.transpColor = Color.argb(Color.red(color), Color.green(color), Color.blue(color), 0);
    }


    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public void setBgColor(int color) {
        this.bgColor = color;
    }

    public void drawBackground(boolean draw) {
        this.drawBG = draw;
    }

    public void enableMarquee(float maxWidth, float fadeSize) {
        this.marquee = true;
        this.maxWidth = maxWidth;
        this.fadeSize = fadeSize;
    }

    public void disableMarquee() {
        this.marquee = false;
    }

    public void setVisibility(boolean isVisible) {
        this.visible = isVisible;
    }

    //Instance Variables
    String text;
    int color, transpColor;
    int bgColor;
    boolean drawBG = false, marquee = false, visible = true;

    float textSize, fadeSize, maxWidth;


    //Temporary variables
    Paint.FontMetrics fm;
    float textHeight, textWidth;
    PropertySet current;
    RectF bounds;
    float marqueeProgress = 0;

    public static final int ALIGN_BOTTOM_LEFT_CORNER = 5673;
    public static final int ALIGN_CENTER = 984526;
    int align = ALIGN_BOTTOM_LEFT_CORNER;

    public void setAlign(int align) {
        this.align = align;
    }

    public Shader getMask(float startX, float endX, float gradSize, int color) {
        LinearGradient val = new LinearGradient(endX - gradSize, 0, endX, 0, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);
        LinearGradient sat = new LinearGradient(startX, 0, startX + gradSize, 0, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        ComposeShader merged = new ComposeShader(val, sat, PorterDuff.Mode.ADD);
        LinearGradient fill = new LinearGradient(0, 0, 0, 0, color, color, Shader.TileMode.CLAMP);
        return new ComposeShader(fill, merged, PorterDuff.Mode.DST_OUT);
    }

    //timekeeping values / constants
    float marqueeDuration = 10;
    float waitDuration = 3;
    float rewindDuration = 2;
    float evalTime;
    long timeOffset = 0;

    public void draw(Canvas c, Paint pt, long currentTime) {
        if (!visible) return;
        //TODO : Perf Improvements.
        current = mixedProperties.getValue(currentTime);


        pt.setTextSize(textSize);
        if (align == ALIGN_BOTTOM_LEFT_CORNER) pt.setTextAlign(Paint.Align.LEFT);
        else if (align == ALIGN_CENTER) pt.setTextAlign(Paint.Align.CENTER);


        fm = pt.getFontMetrics();


        textHeight = fm.descent - fm.ascent;
        textWidth = pt.measureText(text);


        //Log2.log(2,this,current.getValue("X"), current.getValue("Y"));
        if (drawBG) {
            if (align == ALIGN_BOTTOM_LEFT_CORNER) {
                bounds = new RectF(current.getValue("X"), current.getValue("Y"), current.getValue("X") + textWidth, current.getValue("Y") - fm.ascent + fm.descent);
            } else if (align == ALIGN_CENTER) {
                bounds = new RectF(current.getValue("X") - textWidth / 2, current.getValue("Y") - textHeight / 2, current.getValue("X") + textWidth / 2, current.getValue("Y") - fm.ascent + fm.descent - textHeight / 2);
            }
            pt.setColor(bgColor);
            c.drawRect(bounds, pt);
        }

        pt.setColor(color);

        if (marquee && textWidth > maxWidth) {
            if (align == ALIGN_BOTTOM_LEFT_CORNER) {
                pt.setShader(getMask(current.getValue("X"), current.getValue("X") + maxWidth, fadeSize, color));
            } else if (align == ALIGN_CENTER) {
                pt.setShader(getMask(current.getValue("X") - textWidth / 2, current.getValue("X") - textWidth / 2 + maxWidth, fadeSize, color));
            }

        }

        if (marquee && textWidth > maxWidth) {
            evalTime = ((currentTime - timeOffset) % ((long) ((marqueeDuration + rewindDuration + waitDuration) * 1000))) / 1000.0f;
            //Log2.log(2,this,evalTime,(marqueeDuration+rewindDuration+waitDuration)*1000,marqueeDuration+rewindDuration+waitDuration);
            if (evalTime < waitDuration) {
                marqueeProgress = 0;
            } else if (evalTime < (waitDuration + marqueeDuration)) {
                marqueeProgress = (float) EasingEquations.ease1D(0, marqueeDuration, evalTime - waitDuration, 0, 1, EasingEquations.QUADRATIC);
            } else
                marqueeProgress = (float) EasingEquations.ease1D(0, rewindDuration, evalTime - waitDuration - marqueeDuration, 1, 0, EasingEquations.QUINTIC);
        }

        //pt.setShader(new RadialGradient(0, 0, 1, color, color, Shader.TileMode.CLAMP));
        if (align == ALIGN_BOTTOM_LEFT_CORNER) {
            if (marquee && textWidth > maxWidth)
                c.drawText(text, current.getValue("X") - marqueeProgress * (textWidth - maxWidth + fadeSize * 2) + fadeSize, current.getValue("Y") - fm.ascent, pt);
            else c.drawText(text, current.getValue("X"), current.getValue("Y") - fm.ascent, pt);
        } else if (align == ALIGN_CENTER) {
            if (marquee && textWidth > maxWidth)
                c.drawText(text, current.getValue("X") - marqueeProgress * (textWidth - maxWidth + fadeSize * 2) + fadeSize, current.getValue("Y") - fm.ascent - textHeight / 2, pt);
            else
                c.drawText(text, current.getValue("X"), current.getValue("Y") - fm.ascent - textHeight / 2, pt);
        }

        pt.setShader(null);

    }

}
