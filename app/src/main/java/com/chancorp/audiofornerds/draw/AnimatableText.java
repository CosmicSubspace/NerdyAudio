package com.chancorp.audiofornerds.draw;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.chancorp.audiofornerds.animation.MixedProperties;
import com.chancorp.audiofornerds.animation.PropertySet;

/**
 * Created by Chan on 2/21/2016.
 */
public class AnimatableText {
    /*
    Required Properties:
    x
    y

     */
    public AnimatableText(MixedProperties basisSet, int color, String text,float size){
        mixedProperties =basisSet;
        this.color=color;
        this.text=text;
        this.textSize=size;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public MixedProperties getMixedProperties(){
        return mixedProperties;
    }



    String text;
    int color;

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    float textSize;
    MixedProperties mixedProperties;



    Paint.FontMetrics fm;

    float textHeight,textWidth;

    PropertySet current;
    public void draw(Canvas c, Paint pt){
        current= mixedProperties.update(System.currentTimeMillis());
        fm=pt.getFontMetrics();
        pt.setColor(color);
        pt.setTextSize(textSize);
        pt.setTextAlign(Paint.Align.LEFT);

        textHeight=fm.descent-fm.ascent;
        textWidth=pt.measureText(text);

        c.drawText(text,current.getValue("X"),current.getValue("Y")-fm.ascent,pt);
    }
}
