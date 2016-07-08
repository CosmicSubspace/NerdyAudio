package com.cosmicsubspace.nerdyaudio.animation;

/**
 * Created by Chan on 2016-04-06.
 */
public interface Mixer<T> {
    void addMix(T thing, float influence) throws UnMixableException;

    T mix();
}
