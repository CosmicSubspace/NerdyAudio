//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.thirtyseventhpercentile.nerdyaudio.audio.VisualizationBuffer;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;


public class VisualizationView extends SurfaceView implements SurfaceHolder.Callback {


    public static final String LOG_TAG = "CS_AFN";


    SurfaceHolder sh;
    VisualsRenderThread vr;
    VisualizationBuffer vb;


    public VisualizationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        vr = new VisualsRenderThread();
        vb = VisualizationBuffer.getInstance();
        sh = getHolder();
        sh.addCallback(this);
    }

    public VisualsRenderThread getRenderThread() {
        return vr;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log2.log(2, this, "Surface Created. Starting render.");
        vb.activate();
        vr.setSurfaceHolder(surfaceHolder);
        vr.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int w, int h) {
        vr.setSize(w, h);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log2.log(2, this, "Surface Destroyed. Stopping render.");
        vr.stopRender();
        vb.deactivate();
    }


}
