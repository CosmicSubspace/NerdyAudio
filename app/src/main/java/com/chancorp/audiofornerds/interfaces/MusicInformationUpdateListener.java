package com.chancorp.audiofornerds.interfaces;

/**
 * Created by Chan on 2/28/2016.
 */
public interface MusicInformationUpdateListener {
    void musicInformationUpdated(int index); //index of -1 is a flag that the whole thing needs to be updated.
}
