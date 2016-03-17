//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.animation;

import java.util.HashMap;

public class PropertySet {

    private HashMap<String,Float> properties=new HashMap<String,Float>();

    public Float getValue(String key){
        //Log2.log(2,this,properties);
        return properties.get(key);
    }
    public float getValue(String key, float defaultValue){
        //Log2.log(2,this,properties);
        if (properties.get(key)==null) return defaultValue;
        return properties.get(key);
    }

    public PropertySet setValue(String key,float value) {
        properties.put(key,value);
        return this;
    }


    public Iterable getIter(){
        return properties.keySet();
    }



    public PropertySet(){
    }

    /*
    public void setInfluence(AnimatableValue influence){
        this.influence=influence;
    }*/



    public int getNumKeys(){
        return properties.size();
    }

    @Override
    public String toString(){
        StringBuilder res=new StringBuilder();
        for(String key:properties.keySet()){
            res.append("("+key+", "+properties.get(key)+")");
        }

        return res.toString();
    }

}
