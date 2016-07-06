package com.thirtyseventhpercentile.nerdyaudio.settings;

import java.io.Serializable;

/**
 * Created by Chan on 7/5/2016.
 */
public abstract class SettingElement implements Serializable{
    public final static long serialVersionUID=10L;

    public static transient int SLIDER=1;
    public static transient int SPINNER=2;
    public static transient int BOOLEAN=3;

    public int type;
    public String name;
    public SettingElement(String name, int type){
        this.name=name;
        this.type=type;
    }
    public int getType(){return type;}
    public String getName() {
        return name;
    }

    public abstract void applyValue();
    public abstract void fromElement(SettingElement e);
}