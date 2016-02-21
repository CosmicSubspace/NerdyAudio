package com.chancorp.audiofornerds.draw;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.chancorp.audiofornerds.animation.Animator;
import com.chancorp.audiofornerds.animation.PointsCompound;
import com.chancorp.audiofornerds.animation.PropertySet;
import com.chancorp.audiofornerds.helper.Log2;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Chan on 2/20/2016.
 */
public class AnimatableShape {

    PointsCompound path;

    Animator animator;
    Matrix mat;

    int color;

    /*
    Required Properties:
    x
    y
    scale
    rotation
    alpha
     */

    public AnimatableShape(PointsCompound path, int color, PropertySet basisSet){
        animator=new Animator(basisSet);

        this.path =path;
        this.color=color;
    }

    private PointsCompound getPointsCompound(){
        PropertySet ps=animator.update(System.currentTimeMillis());

        mat=new Matrix();
        mat.preTranslate(ps.getValue("X"), ps.getValue("Y"));
        mat.preRotate(ps.getValue("Rotation"));
        mat.preScale(ps.getValue("Scale"), ps.getValue("Scale"));

        return path.transform(mat);
    }

    public RectF getBounds(float padding){
        return getPointsCompound().getBounds(padding);
    }

    public void draw(Canvas c, Paint pt){
        PropertySet ps=animator.update(System.currentTimeMillis());

        mat=new Matrix();
        mat.preTranslate(ps.getValue("X"), ps.getValue("Y"));
        mat.preRotate(ps.getValue("Rotation"));
        mat.preScale(ps.getValue("Scale"), ps.getValue("Scale"));

        pt.setColor(color);
        pt.setAlpha(Math.round(255 * ps.getValue("Alpha")));
        //Log2.log(2,this,path);
        c.drawPath(path.transform(mat).toPath(),pt);
        pt.setAlpha(255);
    }

    public Animator getAnimator(){
        return animator;
    }


}
