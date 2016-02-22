package com.chancorp.audiofornerds.animation;

import android.util.Log;

import com.chancorp.audiofornerds.exceptions.PropertySetException;
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
    public static final String LOG_TAG="CS_AFN";


    ArrayList<PropertySet> set=new ArrayList<>();

    public Animator(PropertySet basis){
        addPropertySet(basis);
    }

    public PropertySet update(long time){
        PropertySet res=new PropertySet("results");


        //float influenceSum=0;
        float influence;
        int size=-1;

        HashMap<String,Float> influences=new HashMap<>();

        for (PropertySet ps : this.set) {
            if (size>0 && ps.getNumKeys()!=size){
                //throw new PropertySetException("Property Set Size Mismatch!");
                //Log.w(LOG_TAG, "Property Set Size Mismatch!");
            }else size=ps.getNumKeys();

            //Log2.log(2,this,ps.toString());

            influence=ps.getInfluence().getValue(time);

            //influenceSum+=influence;
            for (Object k:ps.getIter()){
                String key=(String)k;

                if (influences.get(key)==null) influences.put(key,0.0f);

                influences.put(key,influences.get(key)+influence);
                //Log2.log(2,this,key,k,ps.getName());
                res.setValue(key, res.getValue(key,0)+ps.getValue(key)*influence);

            }

        }
        for (Object k:res.getIter()){
            String key=(String)k;
            if (influences.get(key)==0) Log.w(LOG_TAG,"Influence of "+key+" is ZERO. Expect Animation errors.");
            res.setValue(key, res.getValue(key)/influences.get(key));

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
