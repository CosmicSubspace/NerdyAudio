package com.thirtyseventhpercentile.nerdyaudio.animation;

/**
 * Created by Chan on 4/9/2016.
 */
public class TestMixable implements Mixable {

    static class TestMixer implements Mixer<TestMixable>{
        float sum=0;
        float num=0;
        @Override
        public void addMix(TestMixable thing, float influence) throws UnMixableException {
            //Log2.log(2,this,thing,influence);
            num+=influence;
            sum+=thing.value*influence;
        }

        @Override
        public TestMixable mix() {
            return new TestMixable(sum/num);
        }

        @Override
        public String toString(){
            return "TestMixer: "+sum+" | "+num;
        }
    }

    public TestMixable(float val){
        this.value=val;
    }
    public float value;
    @Override
    public Mixer getMixer() {
        return new TestMixer();
    }
    @Override
    public String toString(){
        return "TestMixable: "+value;
    }
}
