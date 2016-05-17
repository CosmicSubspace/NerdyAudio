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

    private float tempInfluence; //This assumes only one mixing operation takes place at once.

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


    private void getBasisNodes(List<MixNode<T>> list, float influenceMultiplier, long time){
        if (basis!=null){
            tempInfluence=influenceMultiplier*this.getInfluence().getValue(time);
            list.add(this);
            return;
        }
        for(MixNode<T> m: child){
            m.getBasisNodes(list,influenceMultiplier*this.getInfluence().getValue(time),time);
        }
    }

    List<MixNode<T>> basisNodes=new ArrayList<>();
    public T getValue(long time){

        if (basis!=null) return basis;


        basisNodes.clear();
        getBasisNodes(basisNodes,1.0f,time);

        Mixer<T> mixer=null;

        T valTemp;

        for(MixNode<T> node:basisNodes){

            if (mixer==null) {
                mixer=(Mixer<T>) node.getBasis().getMixer(); //All nodes in this loop are guarenteed to be a basis node.
            }

            try {
                mixer.addMix(node.getBasis(), node.tempInfluence);
                //Log2.log(2,this,valTemp,node.getInfluence().getValue(time));
            }catch(UnMixableException e){
                ErrorLogger.log(e);
            }catch(NullPointerException e){
                ErrorLogger.log(e);
                Log2.log(4,this,"NPE",this.name,mixer,node,node.getBasis());
            }
        }
        //Log2.log(2,this,"Returning",mixer,mixer.mix());
        return mixer.mix();

        /*
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
        */


    }
    public void addNode(MixNode<T> mp){
        this.child.add(mp);
    }
}
