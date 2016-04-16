//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.draw;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.thirtyseventhpercentile.nerdyaudio.animation.MixNode;
import com.thirtyseventhpercentile.nerdyaudio.animation.MixedProperties;
import com.thirtyseventhpercentile.nerdyaudio.animation.Mixer;
import com.thirtyseventhpercentile.nerdyaudio.animation.PointsCompound;
import com.thirtyseventhpercentile.nerdyaudio.animation.PropertySet;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;

public class AnimatableShape extends Animatable{

    PointsCompound path;
    MixNode<PointsCompound> shape;

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

    public AnimatableShape(MixNode<PointsCompound> animatedShape, int color, MixNode<PropertySet> basisSet){
        super(basisSet);

        this.shape=animatedShape;
        this.color=color;

    }

    public AnimatableShape(PointsCompound path, int color, MixNode<PropertySet> basisSet){
        super(basisSet);

        this.path =path;
        this.color=color;
    }

    private PointsCompound getPointsCompound(long currentTime){
        PropertySet ps= mixedProperties.getValue(currentTime);

        mat=new Matrix();
        mat.preTranslate(ps.getValue("X"), ps.getValue("Y"));
        mat.preRotate(ps.getValue("Rotation"));
        mat.preScale(ps.getValue("Scale"), ps.getValue("Scale"));

        if (path!=null) return path.transform(mat);
        else if (shape!=null) return shape.getValue(currentTime).transform(mat);
        else {
            Log2.log(4,this,"Path and Shape are all null! wtf?");
            return null;
        }
    }

    public RectF getBounds(float padding,long currentTime){
        return getPointsCompound(currentTime).getBounds(padding);
    }

    @Override
    public void draw(Canvas c, Paint pt, long currentTime){
        PropertySet ps= mixedProperties.getValue(currentTime);

        pt.setColor(color);
        pt.setAlpha(Math.round(255 * ps.getValue("Alpha")));
        //Log2.log(2,this,path);
        c.drawPath(getPointsCompound(currentTime).toPath(),pt);
        pt.setAlpha(255);
    }


}
