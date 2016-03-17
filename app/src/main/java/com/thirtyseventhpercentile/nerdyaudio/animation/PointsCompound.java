//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.animation;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;

import java.util.ArrayList;


public class PointsCompound {

    float[] pointsArray;
    int[] cutsArray;

    private PointsCompound(ArrayList<Float> pts, ArrayList<Integer> cuts) {
        Log2.log(2,this,pts,cuts);
        pointsArray = new float[pts.size()];
        for (int i = 0; i < pts.size(); i++) {
            pointsArray[i] = pts.get(i);
        }
        cutsArray=new int[cuts.size()];
        for (int i = 0; i < cuts.size(); i++) {
            cutsArray[i] = cuts.get(i);
        }
    }

    private PointsCompound(float[] pts, int[] cuts) {
        pointsArray = pts;
        cutsArray=cuts;
    }

    public PointsCompound transform(Matrix mat) throws IllegalStateException {
        float[] res = new float[pointsArray.length];
        mat.mapPoints(res, pointsArray);
        return new PointsCompound(res,cutsArray);
    }

    public Path toPath() {
        Path res = new Path();
        boolean skip;
        for (int i = 0; i < pointsArray.length / 2; i++) {
            skip = false;

            if (cutsArray!=null) {
                //Log2.log(2,this,cutsArray);
                for (int cut : this.cutsArray) {
                    //Log2.log(2,this,cut);
                    if (cut == i) {
                        //Log2.log(2,this,"cutting",i);
                        res.close();
                        res.moveTo(pointsArray[i * 2], pointsArray[i * 2 + 1]);
                        skip = true;
                    }
                }
            }

            if (!skip) {
                if (i == 0) res.moveTo(pointsArray[i * 2], pointsArray[i * 2 + 1]);
                else res.lineTo(pointsArray[i * 2], pointsArray[i * 2 + 1]);
            }
        }
        res.close();
        return res;
    }

    public RectF getBounds(float padding) {
        float maxX = -10000, minX = 10000, maxY = -10000, minY = 10000;
        for (int i = 0; i < pointsArray.length / 2; i++) {
            //X
            if (pointsArray[i * 2] < minX) minX = pointsArray[i * 2];
            if (pointsArray[i * 2] > maxX) maxX = pointsArray[i * 2];

            //Y
            if (pointsArray[i * 2 + 1] < minY) minY = pointsArray[i * 2 + 1];
            if (pointsArray[i * 2 + 1] > maxY) maxY = pointsArray[i * 2 + 1];
        }

        return new RectF(minX - padding, minY - padding, maxX + padding, maxY + padding);
    }



    public static class Builder {
        ArrayList<Float> points = new ArrayList<>();
        ArrayList<Integer> cuts = new ArrayList<>();

        public void addPoint(float x, float y) {
            points.add(x);
            points.add(y);
        }

        public void cut() {
            cuts.add(points.size() / 2);
        }

        public PointsCompound build() {
            return new PointsCompound(points, cuts);
        }
    }

    public static class IncompatableShapesException extends Exception {
        public IncompatableShapesException(String s) {
            super(s);
        }
    }
}
