package com.chancorp.audiofornerds.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.chancorp.audiofornerds.animation.Animator;
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
    public AnimatableText(PropertySet basisSet, int color, String text,float size){
        animator=new Animator(basisSet);
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

    public Animator getAnimator(){
        return animator;
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
    Animator animator;



    Paint.FontMetrics fm;

    float textHeight,textWidth;

    PropertySet current;
    public void draw(Canvas c, Paint pt){
        current=animator.update(System.currentTimeMillis());
        fm=pt.getFontMetrics();
        pt.setColor(color);
        pt.setTextSize(textSize);
        pt.setTextAlign(Paint.Align.LEFT);

        textHeight=fm.descent-fm.ascent;
        textWidth=pt.measureText(text);

        c.drawText(text,current.getValue("X"),current.getValue("Y")-fm.ascent,pt);
    }
}
