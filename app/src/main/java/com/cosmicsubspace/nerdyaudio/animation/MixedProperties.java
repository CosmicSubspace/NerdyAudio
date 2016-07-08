//Licensed under the MIT License.
//Include the license text thingy if you're gonna use this.
//Copyright (c) 2016 Chansol Yang

package com.cosmicsubspace.nerdyaudio.animation;

import com.cosmicsubspace.nerdyaudio.helper.Log2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Mixes many ProperySets together
 *
 * @deprecated use the MixNode class instead, as it provides better performance and is more generic.
 */
@Deprecated
public class MixedProperties {
    public static final String LOG_TAG = "CS_AFN";

    PropertySet basis;
    ArrayList<MixedProperties> set;

    private String name;
    private AnimatableValue influence;
    private PropertySet memoized;
    private long memoizedTime;

    public MixedProperties(String name, PropertySet basis) {
        this.name = name;
        influence = new AnimatableValue(1);
        this.basis = basis;
    }

    public MixedProperties(String name) {
        this.name = name;
        influence = new AnimatableValue(1);
        set = new ArrayList<>();
    }


    public AnimatableValue getInfluence() {
        return this.influence;
    }

    public String getName() {
        return this.name;
    }

    public PropertySet getBasis() {
        if (basis == null) Log2.log(3, this, "getBasis() called to a non-basic instance!");
        return basis;
    }

    public PropertySet update(long time) {
        //TODO perf Improvements : This method causes a lot of memory allocations. (~80% of all app allocations)


        if (basis != null) return basis;

        if (memoizedTime == time && memoized != null) {
            return memoized;
        }


        PropertySet res = new PropertySet();
        float influence;
        int size = -1;

        HashMap<String, Float> influences = new HashMap<>();

        for (MixedProperties mp : this.set) {

            PropertySet ps = mp.update(time);

            if (size > 0 && ps.getNumKeys() != size) {
            } else size = ps.getNumKeys();

            influence = mp.getInfluence().getValue(time);

            for (Object k : ps.getIter()) {
                String key = (String) k;
                if (influences.get(key) == null) influences.put(key, 0.0f);
                influences.put(key, influences.get(key) + influence);
                res.setValue(key, res.getValue(key, 0) + ps.getValue(key) * influence);
            }
        }
        for (Object k : res.getIter()) {
            String key = (String) k;
            if (influences.get(key) == 0)
                Log2.log(3, this, "Influence of " + key + " is ZERO. Expect Animation errors.");
            res.setValue(key, res.getValue(key) / influences.get(key));

        }


        memoized = res;
        memoizedTime = time;

        return res;

    }

    public void addProperty(MixedProperties mp) {
        this.set.add(mp);
    }

}
