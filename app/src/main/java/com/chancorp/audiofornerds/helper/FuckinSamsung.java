package com.chancorp.audiofornerds.helper;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;

/**
 * Created by Chan on 2/18/2016.
 */
public class FuckinSamsung { //http://stackoverflow.com/questions/13596245/typeface-italic-not-working-on-galaxy-note-2
    public static CharSequence forceItalic(CharSequence text) {
        final StyleSpan style = new StyleSpan(Typeface.ITALIC);
        final SpannableString str = new SpannableString(text);
        str.setSpan(style, 0, text.length(), 0);
        return str;
    }
}
