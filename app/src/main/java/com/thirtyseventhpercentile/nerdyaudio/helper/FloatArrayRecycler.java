package com.thirtyseventhpercentile.nerdyaudio.helper;

import java.util.ArrayList;

/**
 * Created by Chan on 3/15/2016.
 */

public class FloatArrayRecycler { //I want to use generic types here but i dunno

    public FloatArrayRecycler() {

    }

    private static final String LOG_TAG = "CS_AFN";

    int maxSize = 100;
    int arraySize;
    ArrayList<float[]> arrays = new ArrayList<>();

    public synchronized void setArraySize(int size) {
        this.arraySize = size;
        arrays.clear();
    }

    public synchronized void recycle(float[] obj) {
        if (obj.length != arraySize) setArraySize(obj.length);

        if (arrays.size() > maxSize) {
            Log2.log(2, this, "FloatArrayRecycler full!");
            return;
        }
        arrays.add(obj);
    }

    public synchronized float[] request(int size) {
        if (size != arraySize) setArraySize(size);


        if (arrays.size() > 0) {
            return arrays.remove(0);
        } else return new float[arraySize];
    }
}
