package com.chancorp.audiofornerds.animation;

import android.graphics.Path;

/**
 * Created by Chan on 2/20/2016.
 */
public class PrimitivePaths {
    static final float sqrt3=1.7320508075688772f;
    static final float sqrt2=1.4142135623730951f;
    public static PointsCompound triangle(float radius){
        PointsCompound.Builder builder=new PointsCompound.Builder();
        builder.addPoint(0, radius);
        builder.addPoint(radius * sqrt3 / 2.0f, -radius / 2.0f);
        builder.addPoint(-radius * sqrt3 / 2.0f, -radius / 2.0f);
        return builder.build();
    }
    public static PointsCompound square(float radius){
        PointsCompound.Builder builder=new PointsCompound.Builder();
        builder.addPoint(radius/sqrt2,radius/sqrt2);
        builder.addPoint(radius / sqrt2, -radius / sqrt2);
        builder.addPoint(-radius / sqrt2, -radius / sqrt2);
        builder.addPoint(-radius / sqrt2, radius / sqrt2);
        return builder.build();
    }
    public static PointsCompound pause(float radius){
        PointsCompound.Builder builder=new PointsCompound.Builder();
        builder.addPoint(radius/sqrt2,radius/sqrt2);
        builder.addPoint(0.33f*radius / sqrt2, radius / sqrt2);
        builder.addPoint(0.33f*radius / sqrt2, -radius / sqrt2);
        builder.addPoint(radius / sqrt2, -radius / sqrt2);
        builder.cut();
        builder.addPoint(-radius/sqrt2,radius/sqrt2);
        builder.addPoint(-0.33f*radius / sqrt2, radius / sqrt2);
        builder.addPoint(-0.33f*radius / sqrt2, -radius / sqrt2);
        builder.addPoint(-radius / sqrt2, -radius / sqrt2);
        return builder.build();
    }

    public static PointsCompound next(float radius){
        PointsCompound.Builder builder=new PointsCompound.Builder();
        builder.addPoint(radius*-0.5f,radius*0.70711f);
        builder.addPoint(radius*0.4f,radius*0.08875f);
        builder.addPoint(radius*0.4f,radius*0.70711f);
        builder.addPoint(radius*0.70711f,radius*0.70711f);
        builder.addPoint(radius*0.70711f,radius*-0.70711f);
        builder.addPoint(radius*0.4f,radius*-0.70711f);
        builder.addPoint(radius*0.4f,radius*-0.08875f);
        builder.addPoint(radius*-0.5f,radius*-0.70711f);
        return builder.build();
    }
}
