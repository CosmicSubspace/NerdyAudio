//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.interfaces;

import com.thirtyseventhpercentile.nerdyaudio.file.MusicInformation;

public interface QueueListener {
    //A new song has begun playing.
    void newSong(MusicInformation mi);

    //Playback has stopped/started.
    void playbackStarted();
    void playbackStopped();

    //Next/Prev song. newSong() will be called after these callbacks.
    void nextSong();
    void previousSong();
}
