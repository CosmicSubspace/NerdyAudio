package com.chancorp.audiofornerds.interfaces;

import com.chancorp.audiofornerds.file.MusicInformation;

import java.util.ArrayList;

/**
 * Created by Chan on 2015-12-17.
 */
public interface FileListReturnListener {
    void onReturn(ArrayList<MusicInformation> files);
}
