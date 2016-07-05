package com.thirtyseventhpercentile.nerdyaudio.interfaces;

import com.thirtyseventhpercentile.nerdyaudio.visuals.BaseRenderer;
import com.thirtyseventhpercentile.nerdyaudio.visuals.LoudnessGraphVisuals;

/**
 * Created by Chan on 7/2/2016.
 */
public interface NewSettingsUpdateListener {
    void settingsChanged(int index);
}
