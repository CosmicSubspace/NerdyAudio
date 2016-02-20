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
    public static final int COPY_ALL=541;
    public static final int COPY_POSITION=687;


    ArrayList<PropertySet> set=new ArrayList<>();
    float x,y,scale,rotation;

    PointsCompound path;
    Matrix mat;


    public AnimatableShape(PointsCompound path, float x, float y, float scale, float rotation){
        this.path =path;
        this.set.add(new PropertySet(x,y,scale,rotation,1,"Basis"));
        this.x=x;
        this.y=y;
        this.scale=scale;
        this.rotation=rotation;
    }

    private void update(){
        long currentTime=System.currentTimeMillis();

        float influenceSum=0;
        float tX=0,tY=0,tS=0,tR=0;
        float influence;

        for (PropertySet ps : this.set) {
            influence=ps.getInfluence().getValue(currentTime);
            influenceSum+=influence;
            tX+=ps.getX()*influence;
            tY+=ps.getY()*influence;
            tS+=ps.getScale()*influence;
            tR+=ps.getRotation()*influence;
            Log2.log(2,this,ps.getX(),ps.getY(),ps.getName(),ps.getInfluence().getValue(currentTime));
        }

        x=tX/influenceSum;
        y=tY/influenceSum;
        scale=tS/influenceSum;
        rotation=tR/influenceSum;
        Log2.log(2,this,tX,tY,x,y,influenceSum);



    }

    public PointsCompound getPointsCompound(){
        //TODO optimizations here.
        //return PrimitivePaths.triangle(100);
        update();

        mat=new Matrix();
        mat.preTranslate(x, y);
        mat.preRotate(rotation);
        mat.preScale(scale, scale);
        //float[] val=new float[9];
        //mat.getValues(val);
        //Log2.log(2,this,val[0],val[1],val[2],val[3],val[4],val[5],val[6],val[7],val[8]);
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

    public void addPropertySet(PropertySet ps){
        this.set.add(ps);
    }
    public PropertySet getPropertySet(String name){
        for (PropertySet ps:this.set){
            if (ps.getName().equals(name)) {
                return ps;
            }
        }
        return null;
    }
}
