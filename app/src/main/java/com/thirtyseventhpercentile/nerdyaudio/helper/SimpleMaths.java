package com.thirtyseventhpercentile.nerdyaudio.helper;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Chan on 3/24/2016.
 */
public class SimpleMaths {
    public static float constrain(float value, float min, float max){
        if (value<min) return min;
        else if (value>max) return max;
        else return value;
    }
    public static float linearMapClamped(float value, float fromStart, float fromEnd, float toStart, float toEnd){
        Log2.log(2,null,value,fromStart,fromEnd,toStart,toEnd);
        value=constrain(value,fromStart,fromEnd);
        Log2.log(2,null,(value-fromStart)/(fromEnd-fromStart)*(toEnd-toStart)+toStart);
        return (value-fromStart)/(fromEnd-fromStart)*(toEnd-toStart)+toStart;
    }
    public static float linearMapUnclamped(float value, float fromStart, float fromEnd, float toStart, float toEnd){
        return (value-fromStart)/(fromEnd-fromStart)*(toEnd-toStart)+fromStart;
    }
    public static RectF fit(RectF original, RectF bounds){
        float ratio=original.width()/original.height();
        float boundRatio=bounds.width()/bounds.height();
        if (boundRatio>ratio){ //then fit to height
            float height=bounds.height();
            float width=bounds.height()*ratio;
            return new RectF(bounds.centerX()-width/2,bounds.centerY()-height/2,bounds.centerX()+width/2,bounds.centerY()+height/2);
        }else{
            float height=bounds.width()/ratio;
            float width=bounds.width();
            return new RectF(bounds.centerX()-width/2,bounds.centerY()-height/2,bounds.centerX()+width/2,bounds.centerY()+height/2);
        }
    }
}
