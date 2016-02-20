package com.chancorp.audiofornerds.animation;

/**
 * Created by Chan on 2/21/2016.
 */
public class PropertySet {
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setName(String name) {
        this.name = name;
    }

    private float x,y,scale,rotation;
    private String name;
    private AnimatableProperty influence;
    public PropertySet(float x, float y, float scale, float rotation,float influence,String name){
        this.x=x;
        this.y=y;
        this.scale=scale;
        this.rotation=rotation;
        this.name=name;
        this.influence=new AnimatableProperty(influence);
    }
    public void setInfluence(AnimatableProperty influence){
        this.influence=influence;
    }
    public AnimatableProperty getInfluence(){
        return this.influence;
    }
    public String getName(){
        return this.name;
    }
}
