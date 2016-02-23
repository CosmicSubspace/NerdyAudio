package com.chancorp.audiofornerds.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

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

    int bgColor;
    boolean drawBG;

    public void setBgColor(int color){
        this.bgColor=color;
    }
    public void drawBackground(boolean draw){
        this.drawBG=draw;
    }

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
    RectF bounds;

    public static final int ALIGN_BOTTOM_LEFT_CORNER =5673;
    public static final int ALIGN_CENTER=984526;
    int align= ALIGN_BOTTOM_LEFT_CORNER;
    public void setAlign(int align){
        this.align=align;
    }
    public void draw(Canvas c, Paint pt){
        current= mixedProperties.update(System.currentTimeMillis());
        fm=pt.getFontMetrics();

        pt.setTextSize(textSize);
        if (align== ALIGN_BOTTOM_LEFT_CORNER) pt.setTextAlign(Paint.Align.LEFT);
        else if (align==ALIGN_CENTER) pt.setTextAlign(Paint.Align.CENTER);

        textHeight=fm.descent-fm.ascent;
        textWidth=pt.measureText(text);

        if (drawBG) {
            if (align== ALIGN_BOTTOM_LEFT_CORNER){
                bounds = new RectF(current.getValue("X"), current.getValue("Y"), current.getValue("X") + textWidth, current.getValue("Y") - fm.ascent + fm.descent);
            }else if (align==ALIGN_CENTER){
                bounds = new RectF(current.getValue("X")-textWidth/2, current.getValue("Y")-textHeight/2, current.getValue("X") + textWidth/2, current.getValue("Y") - fm.ascent + fm.descent-textHeight/2);
            }
            pt.setColor(bgColor);
            c.drawRect(bounds,pt);
        }

        pt.setColor(color);
        if (align== ALIGN_BOTTOM_LEFT_CORNER){
            c.drawText(text,current.getValue("X"),current.getValue("Y")-fm.ascent,pt);
        }else if (align==ALIGN_CENTER){
            c.drawText(text,current.getValue("X"),current.getValue("Y")-fm.ascent-textHeight/2,pt);
        }

    }

}
