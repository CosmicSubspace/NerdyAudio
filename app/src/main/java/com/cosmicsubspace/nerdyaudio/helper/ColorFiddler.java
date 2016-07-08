package com.cosmicsubspace.nerdyaudio.helper;

import android.graphics.Color;

/**
 * Created by Chan on 2/26/2016.
 */
public class ColorFiddler {
    public static int setAlpha(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static int rampColor(int colorA, int colorB, float ramp) {
        //Log2.log(1,this,"Ramping color..."+colorA+" | "+colorB+" | "+ramp);
        return Color.argb(Math.round(Color.alpha(colorA) * ramp + Color.alpha(colorB) * (1.0f - ramp)),
                Math.round(Color.red(colorA) * ramp + Color.red(colorB) * (1.0f - ramp)),
                Math.round(Color.green(colorA) * ramp + Color.green(colorB) * (1.0f - ramp)),
                Math.round(Color.blue(colorA) * ramp + Color.blue(colorB) * (1.0f - ramp)));

    }

    public static float intToFloat(int color) {
        return color / 255.0f;
    }

    public static float rF(int color) {
        return intToFloat(Color.red(color));
    }

    public static float bF(int color) {
        return intToFloat(Color.blue(color));
    }

    public static float gF(int color) {
        return intToFloat(Color.green(color));
    }

    public static float aF(int color) {
        return intToFloat(Color.alpha(color));
    }
}
