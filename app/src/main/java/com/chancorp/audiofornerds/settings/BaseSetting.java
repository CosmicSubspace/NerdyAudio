package com.chancorp.audiofornerds.settings;

/**
 * Created by Chan on 2/3/2016.
 */
public abstract class BaseSetting {
    public static final int VISUALIZATION=124;
    public static final int VU=129642;
    public static final int WAVEFORM=984132;
    public static final int SPECTOGRAPH=685321;
    public static final int NONE=1243634;
    public static final int SPECTRUM=12529368;

    public abstract int getType();
}
