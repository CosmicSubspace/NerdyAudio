package com.thirtyseventhpercentile.nerdyaudio.settings;

import java.io.Serializable;

/**
 * Created by Chan on 7/6/2016.
 */
public class BooleanElement extends SettingElement implements Serializable {
    public final static long serialVersionUID = 10L;


    public BooleanElement(String name, boolean current) {
        super(name, BOOLEAN);
        value = current;
        newValue = current;
    }

    boolean value, newValue;

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean val) {
        this.newValue = val;
    }

    public boolean getNewValue() {
        return newValue;
    }

    @Override
    public void applyValue() {
        this.value = this.newValue;
    }

    @Override
    public void fromElement(SettingElement e) {
        this.newValue = ((BooleanElement) e).getValue();
    }
}
