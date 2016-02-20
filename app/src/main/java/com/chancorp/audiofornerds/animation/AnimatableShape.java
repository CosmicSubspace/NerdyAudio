package com.chancorp.audiofornerds.animation;

import android.graphics.Matrix;
import android.graphics.Path;

import com.chancorp.audiofornerds.helper.Log2;

/**
 * Created by Chan on 2/20/2016.
 */
public class AnimatableShape {
    protected AnimatableProperty x,y,scale,rotation;
    PointsCompound path;
    Matrix mat;

    public AnimatableShape(PointsCompound path, float x, float y, float scale, float rotation){
        this.path =path;
        this.x=new AnimatableProperty(x);
        this.y=new AnimatableProperty(y);
        this.scale=new AnimatableProperty(scale);
        this.rotation=new AnimatableProperty(rotation);
    }

    private boolean isAnimating(){
        return x.isAnimating()||y.isAnimating()||scale.isAnimating()||rotation.isAnimating();
    }

    Path tempPath=new Path();
    public PointsCompound getPointsCompound(){
        //TODO optimizations here.
        //return PrimitivePaths.triangle(100);

        mat=new Matrix();
        mat.preTranslate(x.getValue(), y.getValue());
        mat.preRotate(rotation.getValue());
        mat.preScale(scale.getValue(), scale.getValue());
        float[] val=new float[9];
        mat.getValues(val);
        Log2.log(2,this,val[0],val[1],val[2],val[3],val[4],val[5],val[6],val[7],val[8]);
        return path.transform(mat);
        //return path;
        //return PrimitivePaths.triangle(100);


        /*
        if (isAnimating()){
            mat=new Matrix();
            mat.preRotate(rotation.getValue());
            mat.preScale(scale.getValue(),scale.getValue());
            mat.preTranslate(x.getValue(),y.getValue());
            oldPath.transform(mat, newPath);
            return newPath;
        }else{
            return newPath;
        }*/


    }

    public void animate(float x, float y, float scale, float rotation, float time, int type){
        this.x.animate(x,time,type);
        this.y.animate(y,time,type);
        this.scale.animate(scale,time,type);
        this.rotation.animate(rotation,time,type);
    }
}
