package com.chancorp.audiofornerds.animation;

import com.chancorp.audiofornerds.exceptions.InvalidParameterException;
import com.chancorp.audiofornerds.helper.Log2;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Chan on 2/21/2016.
 */
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
