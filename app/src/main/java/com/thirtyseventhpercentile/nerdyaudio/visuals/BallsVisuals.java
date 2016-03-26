package com.thirtyseventhpercentile.nerdyaudio.visuals;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.thirtyseventhpercentile.nerdyaudio.exceptions.BufferNotPresentException;
import com.thirtyseventhpercentile.nerdyaudio.exceptions.InvalidParameterException;
import com.thirtyseventhpercentile.nerdyaudio.helper.ColorFiddler;
import com.thirtyseventhpercentile.nerdyaudio.helper.SimpleMaths;
import com.thirtyseventhpercentile.nerdyaudio.settings.BallsVisualSettings;
import com.thirtyseventhpercentile.nerdyaudio.settings.BaseSetting;

import java.util.ArrayList;

/**
 * Created by Chan on 3/24/2016.
 */
public class BallsVisuals extends FftRenderer {
    Paint pt;

    BallsVisualSettings newSettings = null;
    ArrayList<Ball> balls = new ArrayList<>();

    private void syncChanges() {
        if (newSettings != null) {
            setFFTSize(newSettings.getFftSize());
            Log.i(LOG_TAG, "Spectrum: size changing" + fftSize);

            try {
                setFrequencyRange(newSettings.getStartFreq(), newSettings.getEndFreq());
            } catch (InvalidParameterException e) {
                Log.w(LOG_TAG, "SpectrogramVisuals>syncChanges() wut?");
            }
            setLogScale(newSettings.getLogScale());

            newSettings = null;
            initializeSimulation();
        }
    }

    public BallsVisuals(float density) {
        super(density);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);

        //I have a feeling that this would cause some nasty shit in the future.
        updated(sbs.getSetting(BaseSetting.BALLS));

    }

    @Override
    public void updated(BaseSetting setting) {
        if (setting instanceof BallsVisualSettings) {
            newSettings = (BallsVisualSettings) setting;
        }
    }

    @Override
    public void dimensionsChanged(int w, int h) {

    }

    private void initializeSimulation() {
        Log.i(LOG_TAG,"Sim Init...");
        balls.clear();
        for (int i = 0; i < 4; i++) {
            balls.add(new Ball(i * 100, 100, 0, 0,ColorFiddler.rampColor(Color.RED,Color.BLUE,i/5.0f)));
        }
    }

    @Override
    public void drawVisuals(Canvas c, int w, int h) {
        syncChanges();
        long currentFrame = getCurrentFrame();
        try {
            updateFFT(currentFrame);

            //Bass
            balls.get(0).r= 0.5f*balls.get(0).r+0.5f*SimpleMaths.linearMapClamped(getMagnitudeRange(50, 150, true), 0, 300, 50, 150);
            //Low
            balls.get(1).r= 0.5f*balls.get(1).r+0.5f*SimpleMaths.linearMapClamped(getMagnitudeRange(150, 450, true), 0, 150, 50, 150);
            //Mid
            balls.get(2).r= 0.5f*balls.get(2).r+0.5f*SimpleMaths.linearMapClamped(getMagnitudeRange(450, 1000, true), 0, 100, 50, 150);
            //High
            balls.get(3).r= 0.5f*balls.get(3).r+0.5f*SimpleMaths.linearMapClamped(getMagnitudeRange(1000, 10000, true), 0, 50, 50, 150);


            Log.i(LOG_TAG, "Sim Drawing...");

            for (int i = 0; i < 10; i++) { //Sim Iterations
                for (Ball ball : balls) {
                    ball.update(balls, 10);
                    ball.collision(balls);
                    ball.wallIn(0,0,w,h);
                }
            }

            for (Ball ball : balls) {
                ball.draw(c,pt);
                ball.attract(w/2, h/2, 1);
                ball.damp(0.98f);
            }
/*
            for (int i = 0; i < 30; i++) {
                c.drawRect(30+i*10,0,40+i*10,getMagnitudeRange(50+i*20,70+i*20,true),pt);
            }*/

        } catch (BufferNotPresentException e) {
            Log.d(LOG_TAG, "Buffer not present! Requested around " + currentFrame);
        }catch (NullPointerException e) {
            Log.d(LOG_TAG, "NPE @ BallsVisuals " + currentFrame);
        }
    }


    static class Ball {
        float x, y;
        float vx, vy;
        float r;
        int color;

        public Ball(float x, float y, float vx, float vy,int color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.r = 30;
            this.color=color;
        }

        protected float distance(Ball b) {
            return (float) Math.sqrt(Math.pow(b.x - this.x, 2) + Math.pow(b.y - this.y, 2));
        }

        protected float intersect(Ball b) {
            return distance(b) - b.r - this.r;
        }

        public void collision(ArrayList<Ball> balls) {
            for (Ball ball : balls) {
                if (ball.equals(this)) continue;
                if (intersect(ball) < 0) {
                    float dx = this.x - ball.x;
                    float dy = this.y - ball.y;
                    float mag = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                    float dxn = dx / mag;
                    float dyn = dy / mag;

                    vx += dxn * -intersect(ball) / 30;
                    vy += dyn * -intersect(ball) / 30;
                }
            }
        }

        public void draw(Canvas c, Paint pt) {
            pt.setColor(color);
            c.drawCircle(x, y, r, pt);
        }

        public void update(ArrayList<Ball> balls, int division) {
            x += vx / division;
            y += vy / division;
        }

        public void attract(float x, float y, float power) {
            float dx = this.x - x;
            float dy = this.y - y;
            float mag = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
            float dxn = dx / mag;
            float dyn = dy / mag;
            vx -= dxn * power;
            vy -= dyn * power;
        }

        public void damp(float power) {
            vx *= power;
            vy *= power;
        }
        public void wallIn(float minX,float minY,float maxX,float maxY){
            if (x-r<minX){
                vx=-vx;
                x=-x+2*r; //(x-r)=-(x-r)
            }else if (x+r>maxX){
                vx=-vx;
                x=maxX*2-x-2*r;//(x-r)=2maxX-(x-r)
            }
            if (y-r<minY){
                vy=-vy;
                y=-y+2*r;
            }else if (y+r>maxY){
                vy=-vy;
                y=maxY*2-y-2*r;
            }
        }
    }

}
