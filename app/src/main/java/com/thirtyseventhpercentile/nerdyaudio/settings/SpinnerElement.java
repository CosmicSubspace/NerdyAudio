package com.thirtyseventhpercentile.nerdyaudio.settings;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Chan on 7/6/2016.
 */
public class SpinnerElement<V> extends SettingElement implements Serializable{

    public final static long serialVersionUID=10L;

    transient List<String> spinnerChoices;
    transient List<V> mappedValues;
    public SpinnerElement(String name, List<String> spinnerChoices, List<V> mappedValues, int current) {
        super(name, SPINNER);

        assert spinnerChoices.size()==mappedValues.size();

        this.spinnerChoices=spinnerChoices;
        this.mappedValues=mappedValues;

        this.index=current;
        this.newIndex=current;
    }

    int index, newIndex;

    public V getValue(){
        return mappedValues.get(index);
    }
    public int getIndex(){return index;}
    public void setIndex(int idx){
        this.newIndex=idx;
    }

    public ArrayAdapter<String> generateAdapter(Context c){
        ArrayAdapter<String> res = new ArrayAdapter<String>(c,
                android.R.layout.simple_spinner_item,
                spinnerChoices.toArray(new String[spinnerChoices.size()]));
        res.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return res;
    }


    @Override
    public void applyValue() {
        this.index=this.newIndex;
    }

    @Override
    public void fromElement(SettingElement e) {
        this.newIndex=((SpinnerElement) e).getIndex();
    }
}
