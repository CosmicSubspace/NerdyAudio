//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.animation;

import android.util.Log;

import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;

import java.util.HashMap;



public class PropertySet implements Mixable {
    private static final String LOG_TAG = "CS_AFN";

    @Override
    public Mixer getMixer() {
        return new PropertySetMixer();
    }

    public static class PropertySetMixer implements Mixer<PropertySet> {
        PropertySet res = new PropertySet();
        PropertySet additive = new PropertySet();
        HashMap<String, Float> influences = new HashMap<>();

        @Override
        public void addMix(PropertySet thing, float influence) {

            for (Object k : thing.getIter()) {
                String key = (String) k;
                if (influences.get(key) == null) influences.put(key, 0.0f);

                if (thing.mode == OVERWRITE) {
                    influences.put(key, influences.get(key) + influence);
                    res.setValue(key, res.getValue(key, 0) + thing.getValue(key) * influence);
                } else if (thing.mode == ADDITIVE) {
                    additive.setValue(key,additive.getValue(key,0)+thing.getValue(key)*influence);
                }else{
                    Log2.log(4,this,"MODE ERROR");
                }
            }
        }

        @Override
        public PropertySet mix() {
            for (Object k : res.getIter()) {
                String key = (String) k;
                if (influences.get(key) < 0.0001f)
                    Log.w(LOG_TAG, "Influence sum of " + key + " is near zero. Expect Animation errors.");
                res.setValue(key, res.getValue(key) / influences.get(key) + additive.getValue(key,0));
            }
            return res;
        }
    }

    public static final int ADDITIVE = 1;
    public static final int OVERWRITE = 0;


    private HashMap<String, Float> properties = new HashMap<String, Float>();

    private int mode = OVERWRITE;

    public Float getValue(String key) {
        //Log2.log(2,this,properties);
        return properties.get(key);
    }

    public float getValue(String key, float defaultValue) {
        //Log2.log(2,this,properties);
        if (properties.get(key) == null) return defaultValue;
        return properties.get(key);
    }

    public PropertySet setValue(String key, float value) {
        properties.put(key, value);
        return this;
    }


    public Iterable getIter() {
        return properties.keySet();
    }


    public PropertySet() {
    }

    public void setMode(int mode){
        this.mode=mode;
    }

    /*
    public void setInfluence(AnimatableValue influence){
        this.influence=influence;
    }*/


    public int getNumKeys() {
        return properties.size();
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (String key : properties.keySet()) {
            res.append("(" + key + ", " + properties.get(key) + ")");
        }

        return res.toString();
    }

}
