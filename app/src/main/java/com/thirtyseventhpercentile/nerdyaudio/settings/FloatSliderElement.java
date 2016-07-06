package com.thirtyseventhpercentile.nerdyaudio.settings;

import com.thirtyseventhpercentile.nerdyaudio.helper.IntFloatMapper;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Chan on 7/5/2016.
 */
//Extension of the SliderElement to support float values.
public class FloatSliderElement extends SliderElement implements Serializable {
    public final static long serialVersionUID=10L;

    transient IntFloatMapper ifm;

    public FloatSliderElement(String name, float min, float max, float current, int divisions){
        super(name,0,divisions,0);
        ifm=new IntFloatMapper(0,divisions,min,max);
        setValue(ifm.fromFloat(current));
    }

    public float getFloatValue(){
        return ifm.fromInt(getValue());
    }
    public float getNewFloatValue(){
        return ifm.fromInt(getNewValue());
    }

    @Override
    public String getStringRepr(){
        return String.format(java.util.Locale.US, "%.2f", getFloatValue());
    }
    @Override
    public String getNewStringRepr(){
        return String.format(java.util.Locale.US, "%.2f", getNewFloatValue());
    }
/*
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        ifm=new IntFloatMapper(0,divisions,min,max);
    }
    */
}
