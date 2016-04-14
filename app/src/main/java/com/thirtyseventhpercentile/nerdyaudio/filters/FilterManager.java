//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.thirtyseventhpercentile.nerdyaudio.filters;

import java.util.ArrayList;
import java.util.Collections;

public class FilterManager {

    public interface FilterListChangeListener{
        void filterListChanged();
    }


    ArrayList<BaseFilter> filters;
    ArrayList<FilterListChangeListener> flcls=new ArrayList<>();
    static FilterManager inst;


    public static float[] shortToFloat(short[] data){
        float[] res=new float[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i]=data[i]/32768.0f;
        }
        return res;
    }
    public static short[] floatToShort(float[] data){
        short[] res=new short[data.length];
        short current;
        for (int i = 0; i < data.length; i++) {
            if (data[i]>1.0f) res[i]=32767;
            else if (data[i]<-1.0f) res[i]=-32768;
            else res[i]=(short)(data[i]*32767);
        }
        return res;
    }


    public static FilterManager getInstance(){
        if (inst==null) inst=new FilterManager();
        return inst;
    }
    protected FilterManager(){
        filters=new ArrayList<>();
        filters.add(new StaticFilter(this,StaticFilter.VISUALS));
    }
    public void addFilter(BaseFilter filter){
        filters.add(filter);
    }
    public ArrayList<BaseFilter> getFilters(){
        return filters;
    }

    public short[] filterAll(short[] data){
        float[] res=shortToFloat(data); //TODO We need some crazy optimizations here.
        for (int i = 0; i < filters.size(); i++) {
            filters.get(i).filter(res);
        }
        return floatToShort(res);
    }

    public void addFilterListChangeListener(FilterListChangeListener flcl){
        flcls.add(flcl);
    }

    public void removeFilterListChangeListener(FilterListChangeListener flcl){
        flcls.remove(flcl);
    }

    private void notifyListeners(){
        for(FilterListChangeListener flcl:flcls){
            flcl.filterListChanged();
        }
    }

    public void deleteFilter(BaseFilter filter){
        filters.remove(filter);
        notifyListeners();
    }
    public void moveUp(BaseFilter filter){
        int idx=filters.indexOf(filter);
        try {
            Collections.swap(filters, idx, idx - 1);
        } catch(IndexOutOfBoundsException e){
            //Just don't do anything, mmkay?
        }
        notifyListeners();
    }
    public void moveDown(BaseFilter filter){
        int idx=filters.indexOf(filter);
        try {
            Collections.swap(filters, idx, idx + 1);
        } catch(IndexOutOfBoundsException e){
            //Just don't do anything, mmkay?
        }
        notifyListeners();
    }
}
