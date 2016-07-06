package com.thirtyseventhpercentile.nerdyaudio.file;

import com.thirtyseventhpercentile.nerdyaudio.interfaces.MusicListDisplayable;

import java.util.ArrayList;

/**
 * Created by Chan on 5/24/2016.
 */
public class MusicGroup implements MusicListDisplayable {
    ArrayList<MusicInformation> elements;

    boolean expanded = false;
    String identity;

    public MusicGroup(String identity, ArrayList<MusicInformation> elements) {
        this.elements = elements;
        this.identity = identity;
    }

    public MusicGroup(String identity) {
        this.elements = new ArrayList<>();
        this.identity = identity;
    }

    public void toggleExpand() {
        expanded = !expanded;
    }

    public ArrayList<MusicInformation> getElements() {
        return elements;
    }

    public String getIdentity() {
        return identity;
    }

    public void addMusic(MusicInformation mi) {
        elements.add(mi);
    }

    @Override
    public boolean expanded() {
        return expanded;
    }

    @Override
    public boolean isAGroup() {
        return true;
    }

    @Override
    public String getTitle() {
        return identity;
    }

    @Override
    public String getSubTitle() {
        return "(" + elements.size() + " songs.)";
    }
}
