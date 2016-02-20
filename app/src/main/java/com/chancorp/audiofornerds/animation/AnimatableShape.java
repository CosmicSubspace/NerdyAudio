package com.chancorp.audiofornerds.animation;

import android.graphics.Matrix;
import android.graphics.Path;

/**
 * Created by Chan on 2/20/2016.
 */
public class AnimatableShape {
    protected AnimatableProperty x,y,scale,rotation;
    Path oldPath;
    Path newPath;
    Matrix mat;

    public AnimatableShape(Path path, float x, float y, float scale, float rotation){
        this.newPath =path;
        this.x=new AnimatableProperty(x);
        this.y=new AnimatableProperty(y);
        this.scale=new AnimatableProperty(scale);
        this.rotation=new AnimatableProperty(rotation);
    }
    private boolean isAnimating(){
        return x.isAnimating()||y.isAnimating()||scale.isAnimating()||rotation.isAnimating();
    }


    public Path getPath(){
        if (isAnimating()){
            mat=new Matrix();
            mat.preRotate(rotation.getValue());
            mat.preScale(scale.getValue(),scale.getValue());
            mat.preTranslate(x.getValue(),y.getValue());
            oldPath.transform(mat, newPath);
            return newPath;
        }else{
            return newPath;
        }
    }

    public void animate(float x, float y, float scale, float rotation, float time, int type){
        oldPath=newPath;
        this.x.animate(x,time,type);
        this.y.animate(y,time,type);
        this.scale.animate(scale,time,type);
        this.rotation.animate(rotation,time,type);
    }
}
