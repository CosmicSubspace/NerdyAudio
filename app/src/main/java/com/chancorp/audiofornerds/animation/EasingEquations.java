//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.chancorp.audiofornerds.animation;


public class EasingEquations {




    public final static int LINEAR=0;
    public final static int QUADRATIC=1;
    public final static int QUADRATIC_IN=2;
    public final static int QUADRATIC_OUT=3;
    public final static int SINE=4;
    public final static int SINE_OUT=5;
    public final static int SINE_IN=6;
    public final static int EXPONENTIAL=7;
    public final static int EXPONENTIAL_OUT=8;
    public final static int EXPONENTIAL_IN=9;
    public final static int CIRCULAR=10;
    public final static int CIRCULAR_OUT=11;
    public final static int CIRCULAR_IN=12;
    public final static int CUBIC=13;
    public final static int CUBIC_OUT=14;
    public final static int CUBIC_IN=15;
    public final static int QUARTIC=16;
    public final static int QUARTIC_OUT=17;
    public final static int QUARTIC_IN=18;
    public final static int QUINTIC=19;
    public final static int QUINTIC_IN=20;
    public final static int QUINTIC_OUT=21;


    public static int DEFAULT_EASE=QUINTIC_OUT;
    public void setDefaultEase(int ease){
        DEFAULT_EASE=ease;
    }


    public static double ease1D(double start, double end, double current, double startVal, double endVal, int mode){
        if (current>end) return endVal;
        if (current<start) return startVal;

        double t=current-start;
        double d=end-start;
        double b=startVal;
        double c=endVal-startVal;

        if (mode==LINEAR) return c*t/d + b;
        else if (mode==QUADRATIC){
            t /= d/2;
            if (t < 1) return c/2*t*t + b;
            t--;
            return -c/2 * (t*(t-2) - 1) + b;
        }
        else if (mode==QUADRATIC_IN){
            t /= d;
            return c*t*t + b;
        }
        else if (mode==QUADRATIC_OUT){
            t /= d;
            return -c * t*(t-2) + b;
        }
        else if (mode==SINE){
            return -c/2 * (Math.cos(Math.PI*t/d) - 1) + b;
        }
        else if (mode==SINE_OUT){
            return c * Math.sin(t/d * (Math.PI/2)) + b;
        }
        else if (mode==SINE_IN){
            return -c * Math.cos(t/d * (Math.PI/2)) + c + b;
        }
        else if (mode==EXPONENTIAL){
            t /= d/2;
            if (t < 1) return c/2 * Math.pow( 2, 10 * (t - 1) ) + b;
            t--;
            return c/2 * ( -Math.pow( 2, -10 * t) + 2 ) + b;
        }
        else if (mode==EXPONENTIAL_OUT){
            return c * ( -Math.pow( 2, -10 * t/d ) + 1 ) + b;
        }
        else if (mode==EXPONENTIAL_IN){
            return c * Math.pow( 2, 10 * (t/d - 1) ) + b;
        }
        else if (mode==CIRCULAR){
            t /= d/2;
            if (t < 1) return -c/2 * (Math.sqrt(1 - t*t) - 1) + b;
            t -= 2;
            return c/2 * (Math.sqrt(1 - t*t) + 1) + b;
        }
        else if (mode==CIRCULAR_OUT){
            t /= d;
            t--;
            return c * Math.sqrt(1 - t*t) + b;
        }
        else if (mode==CIRCULAR_IN){
            t /= d;
            return -c * (Math.sqrt(1 - t*t) - 1) + b;
        }
        else if (mode==CUBIC){
            t /= d/2;
            if (t < 1) return c/2*t*t*t + b;
            t -= 2;
            return c/2*(t*t*t + 2) + b;
        }
        else if (mode==CUBIC_OUT){
            t /= d;
            t--;
            return c*(t*t*t + 1) + b;
        }
        else if (mode==CUBIC_IN){
            t /= d;
            return c*t*t*t + b;
        }
        else if (mode==QUARTIC){
            t /= d/2;
            if (t < 1) return c/2*t*t*t*t + b;
            t -= 2;
            return -c/2 * (t*t*t*t - 2) + b;

        }
        else if (mode==QUARTIC_OUT){
            t /= d;
            t--;
            return -c * (t*t*t*t - 1) + b;
        }
        else if (mode==QUARTIC_IN){
            t /= d;
            return c*t*t*t*t + b;
        }
        else if (mode==QUINTIC){
            t /= d/2;
            if (t < 1) return c/2*t*t*t*t*t + b;
            t -= 2;
            return c/2*(t*t*t*t*t + 2) + b;
        }
        else if (mode==QUINTIC_IN){
            t /= d;
            return c*t*t*t*t*t + b;
        }
        else if (mode==QUINTIC_OUT){
            t /= d;
            t--;
            return c*(t*t*t*t*t + 1) + b;
        }
        else return startVal;
    }
}
