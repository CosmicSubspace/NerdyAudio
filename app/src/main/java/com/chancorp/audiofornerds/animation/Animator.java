package com.chancorp.audiofornerds.animation;

import android.util.Log;

import com.chancorp.audiofornerds.helper.ErrorLogger;
import com.chancorp.audiofornerds.helper.Log2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

/**
 * Created by Chan on 2/21/2016.
 */
public class Animator {



    ArrayList<PropertySet> set=new ArrayList<>();

    public Animator(PropertySet basis){
        addPropertySet(basis);
    }

    public PropertySet update(long time){
        PropertySet res=new PropertySet("results");


        float influenceSum=0;
        float influence;

        for (PropertySet ps : this.set) {
            Log2.log(2,this,ps.toString());
            influence=ps.getInfluence().getValue(time);
            influenceSum+=influence;
            for (Object k:ps.getIter()){
                String key=(String)k;
                Log2.log(2,this,key,k,ps.getName());
                res.setValue(key, res.getValue(key,0)+ps.getValue(key)*influence);
            }
        }
        for (Object k:res.getIter()){
            String key=(String)k;
            res.setValue(key, res.getValue(key)/influenceSum);
        }

        return res;

    }
    public void addPropertySet(PropertySet ps){
        this.set.add(ps);
    }
    public PropertySet getPropertySet(String name){
        for (PropertySet ps:this.set){
            if (ps.getName().equals(name)) {
                return ps;
            }
        }
        return null;
    }
}
