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
    public static float linearMap(float value, float fromStart, float fromEnd, float toStart, float toEnd){
        Log2.log(2,null,value,fromStart,fromEnd,toStart,toEnd);
        value=constrain(value,fromStart,fromEnd);
        Log2.log(2,null,(value-fromStart)/(fromEnd-fromStart)*(toEnd-toStart)+toStart);
        return (value-fromStart)/(fromEnd-fromStart)*(toEnd-toStart)+toStart;
    }
    public static float linearMapUnconstrained(float value, float fromStart, float fromEnd, float toStart, float toEnd){
        return (value-fromStart)/(fromEnd-fromStart)*(toEnd-toStart)+fromStart;
    }

}
