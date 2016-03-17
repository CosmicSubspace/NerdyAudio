//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class VisualizationView extends SurfaceView implements SurfaceHolder.Callback{


    public static final String LOG_TAG="CS_AFN";


    SurfaceHolder sh;
    VisualsRenderThread vr;


    public VisualizationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        vr=new VisualsRenderThread();
        sh=getHolder();
        sh.addCallback(this);
    }
    public VisualsRenderThread getRenderThread(){
        return vr;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i(LOG_TAG, "Surface Created. Starting render.");
        vr.setSurfaceHolder(surfaceHolder);
        vr.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int w, int h) {
        vr.setSize(w,h);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.i(LOG_TAG,"Surface Destroyed. Stopping render.");
        vr.stopRender();
    }


}
