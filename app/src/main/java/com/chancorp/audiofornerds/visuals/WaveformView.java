package com.chancorp.audiofornerds.visuals;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.chancorp.audiofornerds.audio.AudioPlayer;
import com.chancorp.audiofornerds.audio.Waveform;

/**
 * Created by Chan on 2015-12-10.
 */
public class WaveformView extends View {
    int w, h;
    Waveform wf;
    Paint pt;
    int playedColor=Color.BLACK, remainingColor=Color.GRAY, timestampColor= Color.WHITE, timestampBackgroundColor=Color.BLACK;
    int timestampSize=24;
    float spacing=0.3f;
    float currentPosition =0.5f;
    float timestampOffsetX=0,timestampOffsetY=0;
    AudioPlayer connected;
    boolean displayTimeStamp=false;
    public static final String LOG_TAG = "CS_AFN";

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pt = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setWaveform(Waveform w) {
        this.wf = w;
        invalidate();
    }

    public void connectToAudioPlayer(AudioPlayer ap){
        this.connected=ap;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {

        //Log.d(LOG_TAG, "Drawing WaveformView" + w + " | " + h);
        if (wf != null && connected !=null && wf.isReady()&&wf.getFilename().equals(connected.getSourceString())) {
            setCurrentPosition((float)(connected.getCurrentFrame()/(double)wf.getNumOfFrames()));
            float spacing = getSpacingBetween();
            float width=getBarWidth();
            float perBar=1/(float)wf.getDivisions();
            for (int i = 0; i < wf.getDivisions(); i++) {

                if (i*perBar>currentPosition) pt.setColor(remainingColor);
                else if ((i+1)*perBar<currentPosition) pt.setColor(playedColor);
                else pt.setColor(rampColor(playedColor,remainingColor,(currentPosition-i*perBar)/perBar));

                //Log.v(LOG_TAG, "Drawing"+(i*spacing)+" to "+(i*spacing+width));
                canvas.drawRect(i*spacing , h, i*spacing+width, h*(1-wf.getRatio(i)), pt);
            }


            if (displayTimeStamp){

                //TODO you know this.

                float x=100,y=100, xPadding=4;
                float density=getContext().getResources().getDisplayMetrics().density;


                pt.setTextSize(timestampSize*density);

                String s=wf.frameNumberToTimeStamp(connected.getCurrentFrame());
                Paint.FontMetrics fm = new Paint.FontMetrics();
                pt.setTextAlign(Paint.Align.CENTER);
                pt.getFontMetrics(fm);

                pt.setColor(timestampBackgroundColor);
                //Log.d(LOG_TAG, "Drawing rect at ");
                //canvas.drawRect(canvasX - pt.measureText(s) / 2.0f, canvasY+fm.ascent, canvasX + pt.measureText(s) / 2.0f, canvasY+fm.descent, pt);
                canvas.drawRect(w - pt.measureText(s)-timestampOffsetX*density-xPadding*density, h-fm.descent+fm.ascent-timestampOffsetY*density, w-timestampOffsetX*density+xPadding*density, h-timestampOffsetY*density, pt);
                pt.setColor(timestampColor);

                //Log.d(LOG_TAG, "Drawing text at " + canvasX + " | " + (canvasY + -(fm.ascent + fm.descent)));
                //canvas.drawText(s, canvasX, canvasY, pt);
                canvas.drawText(s, w - pt.measureText(s)/2.0f-timestampOffsetX*density, h-fm.descent-timestampOffsetY*density, pt);


            }


        }
        invalidate(); //TODO is this good practice?


    }
    protected int rampColor(int colorA, int colorB, float ramp){
        //Log.d(LOG_TAG,"Ramping color..."+colorA+" | "+colorB+" | "+ramp);
        return Color.argb(Math.round(Color.alpha(colorA) * ramp + Color.alpha(colorB) * (1.0f - ramp)),
                Math.round(Color.red(colorA) * ramp + Color.red(colorB) * (1.0f - ramp)),
                Math.round(Color.green(colorA) * ramp + Color.green(colorB) * (1.0f - ramp)),
                Math.round(Color.blue(colorA) * ramp + Color.blue(colorB) * (1.0f - ramp)));

    }
    public void setSpacing(float f){
        this.spacing=f;
    }
    public void setCurrentPosition(float f){
        currentPosition=f;
    }
    public void setPlayedColor(int color){
        playedColor=color;
    }
    public void setRemainingColor(int color){
        remainingColor=color;
    }
    public void setTimestampColor(int color){
        timestampColor=color;
    }
    public void setTimestampBackgroundColor(int color){
        timestampBackgroundColor=color;
    }
    public void setTimestampVisibility(boolean b){
        this.displayTimeStamp=b;
    }
    public void setTimestampSize(int x){
        this.timestampSize=x;
    }
    public void setTimestampOffset(float x,float y){this.timestampOffsetX=x;this.timestampOffsetY=y;}
    protected float getSpacingBetween(){
        if (wf!=null){
            return w/(wf.getDivisions()*(1.0f+spacing)-spacing)*(spacing+1.0f);
        }
        return 0;
    }
    protected float getBarWidth(){
        if (wf!=null){
            return w/(wf.getDivisions()*(1.0f+spacing)-spacing);
        }
        return 0;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;
    }

}
