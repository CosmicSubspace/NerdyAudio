package com.thirtyseventhpercentile.nerdyaudio.helper;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Chan on 3/15/2016.
 */
public class ShortArrayRecycler { //I want to use generic types here but

    private ShortArrayRecycler(){

    }
    private static ShortArrayRecycler inst=new ShortArrayRecycler();
    public static ShortArrayRecycler getInstance(){
        return inst;
    }
    private static final String LOG_TAG="CS_AFN";

    int maxSize=100;
    int arraySize;
    ArrayList<short[]> arrays=new ArrayList<>();

    public synchronized void setArraySize(int size){
        this.arraySize=size;
        arrays.clear();
    }
    public synchronized void recycle(short[] obj){
        if (obj.length!=arraySize) setArraySize(obj.length);

        if (arrays.size()>maxSize) {
            Log.i(LOG_TAG, "ShortArrayRecycler full!");
            return;
        }
        arrays.add(obj);
    }
    public synchronized short[] request(int size){
        if (size!=arraySize) setArraySize(size);


        if (arrays.size()>0){
            return arrays.remove(0);
        }
        else return new short[arraySize];
    }
}
