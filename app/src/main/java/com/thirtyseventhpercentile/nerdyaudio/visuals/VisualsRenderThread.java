//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.Settings;
import android.util.Log;
import android.view.SurfaceHolder;

import com.thirtyseventhpercentile.nerdyaudio.helper.ErrorLogger;

public class VisualsRenderThread extends Thread{
    public static final String LOG_TAG="CS_AFN";

    Paint pt;

    int w,h;
    long lastDrawn=0, currentDrawn=1;
    float fps=0;
    //float maxFPS=60.0f;

    BaseRenderer renderer;

    SurfaceHolder sf;

    boolean active=true;

    Canvas c;
    public VisualsRenderThread(){
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
    }
    public void setSurfaceHolder(SurfaceHolder sf){ //is this valid?
        this.sf=sf;
    }
    public void stopRender(){
        active=false;
    }

    public void setRenderer(BaseRenderer r){
        if (this.renderer!=null) this.renderer.release();
        this.renderer=r;
    }
    public void setSize(int w,int h){
        this.w=w;
        this.h=h;
    }
    /*
    public void setMaxFPS(float maxFPS){
        this.maxFPS=maxFPS;
        this.minDelay=(int)(1000.0f/maxFPS);
    }*/
    public BaseRenderer getRenderer(){
        return renderer;
    }

    long lastTime=0, currentTime;
    int framesDrawn=0;
    @Override
    public void run(){
        while (active) {
            framesDrawn++;
            currentTime= System.currentTimeMillis();
            if (lastTime+1000<currentTime){ //1 sec has elapsed. Update FPS.
                fps=framesDrawn;
                lastTime=currentTime;
                framesDrawn=0;
            }

            c = sf.lockCanvas();

            if (c==null) {
                Log.i(LOG_TAG,"VisualsRenderThread: lockCanvas() returned null. breaking loop.");
                break;
            }

            c.drawColor(Color.WHITE);

            if (renderer!=null) renderer.draw(c,w,h);

            /*
            //Log.d(LOG_TAG,"DB: "+lastDrawn+" | "+minDelay+" | "+System.currentTimeMillis());
            while (lastDrawn+minDelay>System.currentTimeMillis()){
                try {
                    //Log.d(LOG_TAG,"Sleeping(FPS too high)");
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    ErrorLogger.log(e);
                }
            }
            */


            pt.setTextSize(36.0f);

            pt.setStyle(Paint.Style.STROKE);
            pt.setStrokeWidth(8);
            pt.setColor(Color.BLACK);
            c.drawText("FPS: " + (int) fps, 10, 40, pt);

            pt.setStyle(Paint.Style.FILL);
            pt.setColor(Color.WHITE);
            c.drawText("FPS: "+(int)fps, 10, 40, pt);


            sf.unlockCanvasAndPost(c);
        }

    }
}
