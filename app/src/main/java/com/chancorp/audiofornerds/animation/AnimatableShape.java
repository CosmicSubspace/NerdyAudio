package com.chancorp.audiofornerds.animation;

import android.graphics.Matrix;
import android.graphics.Path;

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

    public AnimatableShape(PointsCompound path, float x, float y, float scale, float rotation){
        animator=new Animator(new PropertySet("Basis").setValue("X",x).setValue("Y",y).setValue("Scale",scale).setValue("Rotation",rotation));

        this.path =path;
    }



    public PointsCompound getPointsCompound(){
        //TODO optimizations here.

        PropertySet ps=animator.update(System.currentTimeMillis());

        mat=new Matrix();
        mat.preTranslate(ps.getValue("X"), ps.getValue("Y"));
        mat.preRotate(ps.getValue("Rotation"));
        mat.preScale(ps.getValue("Scale"), ps.getValue("Scale"));

        return path.transform(mat);

    }

    public Animator getAnimator(){
        return animator;
    }


}
