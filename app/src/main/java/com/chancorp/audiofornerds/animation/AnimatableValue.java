//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.chancorp.audiofornerds.animation;




public class AnimatableValue {

    float value, target;
    boolean animate=false;
    long startTime;
    long endTime;
    int mode;
    public AnimatableValue(float value){
        this.value=value;
    }

    public void animate(float target, float duration, int mode){
        if (animate){//This value is currently being animated, need to commit the value before animating.
            this.value=getValue();
        }
        this.target=target;
        this.startTime=System.currentTimeMillis();
        this.endTime=this.startTime+(int)(duration*1000);
        this.mode=mode;
        this.animate=true;
    }

    public void animateDelayed(float target, float duration, float delay, int mode){
        this.target=target;
        this.startTime=System.currentTimeMillis()+(int)(delay*1000);
        this.endTime=this.startTime+(int)(duration*1000);
        this.mode=mode;
        this.animate=true;
    }

    public void set(float target){
        this.value=target;
        this.animate=false;
    }

    public boolean isAnimating(){
        return animate;
    }

    public float getValue(){
        return getValue(System.currentTimeMillis());
    }



    public float getValue(long currentTime){
        if (!animate) return value;
        if (currentTime>endTime){
            value=target;
            animate=false;
            return value;
        }
        return (float)EasingEquations.ease1D(0,endTime-startTime,currentTime-startTime,value,target,mode);
    }


}
