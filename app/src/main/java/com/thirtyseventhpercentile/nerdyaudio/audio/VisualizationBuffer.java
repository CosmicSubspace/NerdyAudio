//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.audio;

import android.util.Log;

import com.thirtyseventhpercentile.nerdyaudio.exceptions.BufferNotPresentException;
import com.thirtyseventhpercentile.nerdyaudio.helper.ErrorLogger;
import com.thirtyseventhpercentile.nerdyaudio.helper.FloatArrayRecycler;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.FloatFeedListener;

import java.util.ArrayList;


public class VisualizationBuffer implements FloatFeedListener {
    public static final int RIGHT_CHANNEL=53214;
    public static final int LEFT_CHANNEL=65965;
    public static final String LOG_TAG = "CS_AFN";
    ArrayList<float[]> bufferR, bufferL;
    long firstBufferStartingFrame = 0;
    long lastFrameNumber=0;
    int numChannels = 2;

    boolean active=false;

    long maximumBufferSize=48000*10*2; //10 seconds.... is this too long? or too short?


    FloatArrayRecycler far=new FloatArrayRecycler();


    static VisualizationBuffer inst;

    public static VisualizationBuffer getInstance(){
        if (inst==null) inst=new VisualizationBuffer();
        return inst;
    }

    protected VisualizationBuffer() { // int bufferSize
        bufferR = new ArrayList<float[]>();
        bufferL = new ArrayList<float[]>();
        //this.bufferSize = bufferSize;
    }

    public synchronized void reset(){
        bufferR.clear();
        bufferL.clear();
        firstBufferStartingFrame=0;
        lastFrameNumber=0;
    }

    public void setNumChannels(int n){
        this.numChannels = n;
    }
    public void setMaximumBufferSize(long s){
        this.maximumBufferSize=s;
    }


    @Override
    public synchronized void feed(float[] buff) {

        if (!active){
            firstBufferStartingFrame+=buff.length/2;
            lastFrameNumber+=buff.length/2;
            return;
        }

        float[] right = far.request(buff.length / 2);

        for (int i = 0; i < right.length; i++) {
            right[i] = buff[i *2+1];
        }
        bufferR.add(right);
        float[] left = far.request(buff.length/2);
        for (int i = 0; i < left.length; i++) {
            left[i] = buff[i *2];
        }
        bufferL.add(left);
        lastFrameNumber+=left.length;

        deleteBefore(lastFrameNumber-maximumBufferSize);
    }

    public synchronized float[] getFrames(long startFrame, long endFrame, int channel) throws BufferNotPresentException {
        if (!active) Log2.log(4,this,"Frames requested while not active!");

        Log.v(LOG_TAG, "Buffer Information: current buffers number: " + bufferR.size() + " | Start Num: " + firstBufferStartingFrame + " | End Num:" + lastFrameNumber);
        Log.v(LOG_TAG, "Requested: " + startFrame + " | End Num:" + endFrame);

        checkRange(startFrame);
        checkRange(endFrame);

        assert bufferL.size()==bufferR.size();

        float[] res = new float[(int) (endFrame - startFrame + 1)];
        long currentIndex = startFrame;
        int currentBuffer = 0;
        long currentBufferStartingFrame = getNthBufferInitialFrameNumber(0);
        for (int i = 0; i < res.length; i++) {
            for (; ; ) {
                try {
                    if (channel==RIGHT_CHANNEL) res[i] = bufferR.get(currentBuffer)[(int) (currentIndex - currentBufferStartingFrame)];
                    else if (channel==LEFT_CHANNEL) res[i] = bufferL.get(currentBuffer)[(int) (currentIndex - currentBufferStartingFrame)];
                    else return null;
                    break;
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.v(LOG_TAG, "AIOOB");
                    currentBuffer++;
                    currentBufferStartingFrame = getNthBufferInitialFrameNumber(currentBuffer);
                } catch (IndexOutOfBoundsException e){ //TODO this exception is caught at random points. Fix that.
                    Log.e(LOG_TAG,"Index Out Of Bounds Exception in VisualizationBuffer.");
                    Log.e(LOG_TAG, "Buffer Information: current buffers number: " + bufferR.size() + " | Start Num: " + firstBufferStartingFrame + " | End Num:" + lastFrameNumber);
                    Log.e(LOG_TAG, "Requested: " + startFrame + " | End Num:" + endFrame);
                    ErrorLogger.log(e);
                    break;
                }
            }

            currentIndex++;
        }
        return res;
    }



    private long getNthBufferInitialFrameNumber(int index) {

        long res = firstBufferStartingFrame;
        for (int i = 0; i < index; i++) {
            res += bufferR.get(i).length; //Should add one, but java's zero-index.
        }
        //Log.d(LOG_TAG,""+index+" Buffer start frame num: "+res);
        return res;
    }


    private void checkRange(long num) throws BufferNotPresentException {
        if (num > lastFrameNumber)
            throw new BufferNotPresentException("Buffer out of bounds!\nPresent buffers: " + firstBufferStartingFrame + " ~ " + lastFrameNumber + "\nRequested: " + num);
        else if (num < firstBufferStartingFrame)
            throw new BufferNotPresentException("Buffer out of bounds!\nPresent buffers: " + firstBufferStartingFrame + " ~ " + lastFrameNumber + "\nRequested: " + num);
    }
/*
    private long getLastFrameNumber() {
        long res = firstBufferStartingFrame;
        for (int i = 0; i < bufferR.size(); i++) {
            res += bufferR.get(i).length; //Should add one, but java's zero-index.
        }
        return res;
    }
*/
    public synchronized void deleteBefore(long frameNumber) {
        Log.v(LOG_TAG, "Deletion request for frames less than " + frameNumber);

        if (bufferR.size()<1) return;

        while ((getNthBufferInitialFrameNumber(0) + bufferR.get(0).length ) <= frameNumber) {
            Log.v(LOG_TAG, "Deleting buffer");
            Log.v(LOG_TAG, "First Buffer Starting Frame: " + firstBufferStartingFrame);
            firstBufferStartingFrame += bufferR.get(0).length;
            //lastFrameNumber-=bufferR.get(0).length;
            //Log.i(LOG_TAG, "(Changed) Buffer Information: current buffers number: " + buffers.size() + " | Start Num: " + firstBufferStartingFrame + " | End Num:" + getLastFrameNumber());
            far.recycle(bufferR.remove(0));
            far.recycle(bufferL.remove(0));

            if (bufferR.size()<1) return;
            //deleteBefore(frameNumber); //If there is a delete, do it again recursively until there are no buffers to delete.
        }
    }

    public synchronized void clearBuffer(){
        //deleteBefore(Long.MAX_VALUE);

        while (bufferR.size()>0){
            firstBufferStartingFrame += bufferR.get(0).length;
            far.recycle(bufferR.remove(0));
            far.recycle(bufferL.remove(0));
        }
    }

    public synchronized void deactivate(){
        clearBuffer();
        this.active=false;
    }
    public synchronized void activate(){
        this.active=true;
    }
}

