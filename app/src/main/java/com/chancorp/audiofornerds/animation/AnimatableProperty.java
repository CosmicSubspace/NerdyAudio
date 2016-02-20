package com.chancorp.audiofornerds.animation;

/**
 * Created by Chan on 2/20/2016.
 */
public class AnimatableProperty {

    float value, target;
    boolean animate=false;
    long startTime;
    long endTime;
    int mode;
    public AnimatableProperty(float value){
        this.value=value;
    }

    public void animate(float target, float time, int mode){
        this.target=target;
        this.startTime=System.currentTimeMillis();
        this.endTime=System.currentTimeMillis()+(int)(time*1000);
        this.mode=mode;
        this.animate=true;
    }

    public boolean isAnimating(){
        return animate;
    }

    public float getValue(){
        if (!animate) return value;

        long currentTime=System.currentTimeMillis();
        if (currentTime>endTime){
            value=target;
            animate=false;
            return value;
        }

        return (float)EasingEquations.ease1D(0,endTime-startTime,currentTime-startTime,value,target,mode);
    }


}
