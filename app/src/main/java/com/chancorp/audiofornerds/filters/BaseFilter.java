package com.chancorp.audiofornerds.filters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Chan on 2015-12-18.
 */
abstract public class BaseFilter {
    String name;
    abstract public void filter(float[] data);
    abstract public String getName();
    abstract public View getView(LayoutInflater inflater, ViewGroup container);
}
