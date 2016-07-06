package com.thirtyseventhpercentile.nerdyaudio.helper;

/**
 * Created by Chan on 7/5/2016.
 */
public class IntFloatMapper {
    int min, length;
    float minF, lengthF;

    public IntFloatMapper(int min, int max, float minF, float maxF) {
        this.min = min;
        this.length = max - min;
        this.minF = minF;
        this.lengthF = maxF - minF;
    }

    public int fromFloat(float f) {
        float ratio = (f - minF) / lengthF;
        float mapped = min + length * ratio;
        return Math.round(mapped);
    }

    public float fromInt(int i) {
        float ratio = (i - min) / (float) length;
        return minF + ratio * lengthF;
    }

    public static int fromFloat(int min, int max, float minF, float maxF, float val) {
        return new IntFloatMapper(min, max, minF, maxF).fromFloat(val);
    }

    public static float fromInt(int min, int max, float minF, float maxF, int val) {
        return new IntFloatMapper(min, max, minF, maxF).fromInt(val);
    }
}
