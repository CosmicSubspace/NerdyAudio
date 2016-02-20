package com.chancorp.audiofornerds.animation;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

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
            res.lineTo(pointsArray[i * 2], pointsArray[i * 2 + 1]);
        }
        res.close();
        return res;
    }

    public RectF getBounds(float padding){
        float maxX=-10000, minX=10000,maxY=-10000,minY=10000;
        for (int i = 0; i < pointsArray.length/2; i++) {
            //X
            if (pointsArray[i * 2]<minX) minX=pointsArray[i * 2];
            if (pointsArray[i * 2]>maxX) maxX=pointsArray[i * 2];

            //Y
            if (pointsArray[i * 2 + 1]<minY) minY=pointsArray[i * 2 + 1];
            if (pointsArray[i * 2 + 1]>maxY) maxY=pointsArray[i * 2 + 1];
        }

        return new RectF(minX-padding,minY-padding,maxX+padding,maxY+padding);
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
