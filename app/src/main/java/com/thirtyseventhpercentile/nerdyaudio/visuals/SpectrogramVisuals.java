//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.thirtyseventhpercentile.nerdyaudio.exceptions.BufferNotPresentException;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.settings.FloatSliderElement;
import com.thirtyseventhpercentile.nerdyaudio.settings.SettingElement;
import com.thirtyseventhpercentile.nerdyaudio.settings.SliderElement;
import com.thirtyseventhpercentile.nerdyaudio.settings.SpectrogramVisualSettings;

import java.nio.IntBuffer;
import java.util.List;


public class SpectrogramVisuals extends FftRenderer{
    Paint pt;

    Bitmap graph;
    int canvasX, canvasY;
    IntBuffer graphBuffer;
    /*
    int scrollPxPerRedraw=1;
    float contrast=2.0f;
    */

    SliderElement scrollPxPerRedraw=new SliderElement("Scroll Per Frame",1,10,3);
    FloatSliderElement contrast=new FloatSliderElement("Contrast",0,5,2,100);

    public void newGraph(int w, int h){
        canvasX=w;
        canvasY=h;
        graphBuffer=IntBuffer.allocate(w*h);
        graph=Bitmap.createBitmap(canvasX,canvasY, Bitmap.Config.ARGB_8888);
    }

    @Override
    public List<SettingElement> getSettings() {
        List<SettingElement> res=super.getSettings();
        res.add(scrollPxPerRedraw);
        res.add(contrast);
        return res;
    }


    @Override
    public String getKey() {
        return "SpectrogramVisuals";
    }

    public SpectrogramVisuals(Context ctxt) {
        super(ctxt);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void drawVisuals(Canvas c, int w, int h) {

        int scrollPxPerRedraw=this.scrollPxPerRedraw.getValue();
        float contrast=this.contrast.getFloatValue();

            if (w!=canvasX||h!=canvasY) newGraph(w, h);
            long currentFrame = getCurrentFrame();
            try {
                updateFFT(currentFrame);

                graphBuffer.position(canvasX *scrollPxPerRedraw);
                graphBuffer.compact();
                graphBuffer.position(graphBuffer.limit()-canvasX*scrollPxPerRedraw-1);
                int newColors[]=new int[canvasX];
                for (int i=0;i<canvasX;i++){

                    newColors[i]=magnitudeToColor(getMagnitudeRatio(i/(float)canvasX),contrast);
                }
                for (int i=0;i<scrollPxPerRedraw;i++) graphBuffer.put(newColors);
                graphBuffer.rewind();
                graph.copyPixelsFromBuffer(graphBuffer);


                c.drawBitmap(graph,0,0,pt);


            } catch (BufferNotPresentException e) {
                Log2.log(1,this, "Buffer not present! Requested around " + currentFrame);
            }


    }


    private int magnitudeToColor(double mag, float contrast){
        int inten=(int)(mag*contrast);
        if (inten>255) inten=255;
        return Color.argb(255,inten,inten,inten);
    }


    @Override
    public void dimensionsChanged(int w, int h) {

    }
}


