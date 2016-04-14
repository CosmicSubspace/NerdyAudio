package com.thirtyseventhpercentile.nerdyaudio.animation;

import com.thirtyseventhpercentile.nerdyaudio.helper.ErrorLogger;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ErrorManager;


public class MixNode<T extends Mixable> {

    T basis;
    ArrayList<MixNode<T>> child;

    private String name;
    private AnimatableValue influence;

    public MixNode(String name, T basis){
        this.name=name;
        this.basis=basis;
        this.influence=new AnimatableValue(1);
    }

    public MixNode(String name){
        this.name=name;
        this.basis=null;
        this.influence=new AnimatableValue(1);
        child=new ArrayList<>();
    }


    public AnimatableValue getInfluence(){
        return this.influence;
    }

    public String getName(){
        return this.name;
    }

    public T getBasis(){
        return this.basis;
    }


    private List<T> getBasisNodes(float influenceMultiplier){
        List<T> res=null;
        for(MixNode<T> m: child){
            if (res!=null) res.addAll(m.getBasisNodes(influenceMultiplier));
            else res=m.getBasisNodes(influenceMultiplier);
        }
        return null;
    }

    public T getValue(long time){
        //TODO flatten tree before mixing.
        if (basis!=null) return basis;

        else{
            Mixer<T> mixer=null;
            T valTemp;
            for(MixNode<T> node:child){
                valTemp= node.getValue(time);
                if (mixer==null) {
                    mixer=(Mixer<T>) valTemp.getMixer();
                    //Log2.log(2,this,"Getting Mixer...");
                }
                try {
                    mixer.addMix(valTemp,node.getInfluence().getValue(time));
                    //Log2.log(2,this,valTemp,node.getInfluence().getValue(time));
                }catch(UnMixableException e){
                    ErrorLogger.log(e);
                }catch(NullPointerException e){
                    Log2.log(4,this,"NPE",this.name);
                }
            }
            //Log2.log(2,this,"Returning",mixer,mixer.mix());
            return mixer.mix();
        }



    }
    public void addNode(MixNode<T> mp){
        this.child.add(mp);
    }
}
