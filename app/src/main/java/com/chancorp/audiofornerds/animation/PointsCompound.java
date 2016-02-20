package com.chancorp.audiofornerds.animation;

import android.graphics.Matrix;
import android.graphics.Path;

import java.util.ArrayList;

/**
 * Created by Chan on 2/21/2016.
 */
public class PointsCompound {

    float[] pointsArray;

    private PointsCompound(ArrayList<Float> pts){
        pointsArray=new float[pts.size()];
        for (int i = 0; i < pts.size(); i++) {
            pointsArray[i]=pts.get(i);
        }
    }
    private PointsCompound(float[] pts){
        pointsArray=pts;
    }

    public PointsCompound transform(Matrix mat) throws IllegalStateException{
        float[] res=new float[pointsArray.length];
        mat.mapPoints(res,pointsArray);
        return new PointsCompound(res);
    }

    public Path toPath(){
        Path res=new Path();
        for (int i = 0; i < pointsArray.length/2; i++) {
            if (i==0) res.moveTo(pointsArray[i*2],pointsArray[i*2+1]);
            res.lineTo(pointsArray[i*2],pointsArray[i*2+1]);
        }
        res.close();
        return res;
    }

    public static class Builder{
        ArrayList<Float> points=new ArrayList<>();
        public void addPoint(float x, float y){
            points.add(x);
            points.add(y);
        }
        public PointsCompound build(){
            return new PointsCompound(points);
        }
    }
}
