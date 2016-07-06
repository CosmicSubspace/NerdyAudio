package com.thirtyseventhpercentile.nerdyaudio.settings;

import java.io.Serializable;

/**
 * Created by Chan on 7/5/2016.
 */
public class SliderElement extends SettingElement implements Serializable {
    public final static long serialVersionUID = 10L;
    public transient int min, max;

    public SliderElement(String name, int min, int max, int current) {
        super(name, SLIDER);
        this.min = min;
        this.max = max;
        this.value = current;
        this.newValue = current;
    }

    public int getSeekbarMax() {
        return max - min;
    }


    public int value;
    public int newValue;

    public void setValue(int val) {
        newValue = val + min;
    }

    public int getNewValue() {
        return newValue;
    }

    public int getValue() {
        return value;
    }

    public void applyValue() {
        value = newValue;
    }

    public int getValueProgress() {
        return value - min;
    }

    @Override
    public void fromElement(SettingElement e) {
        this.newValue = ((SliderElement) e).getValue();
    }

    public String getStringRepr() {
        return "" + value;
    }

    public String getNewStringRepr() {
        return "" + newValue;
    }
}
