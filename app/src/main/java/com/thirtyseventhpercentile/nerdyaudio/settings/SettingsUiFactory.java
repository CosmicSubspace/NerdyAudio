package com.thirtyseventhpercentile.nerdyaudio.settings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.NewSettingsUpdateListener;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.SettingsUpdateListener;

import org.w3c.dom.Text;

import java.io.Serializable;

/**
 * Created by Chan on 6/25/2016.
 */
public class SettingsUiFactory {
    public static class SettingElement{
        public static int SLIDER=1;
        public static int SPINNER=2;
        public static int TOGGLE=3;

        private int type;
        private String name;

        public SettingElement(String name, int type){
            this.name=name;
            this.type=type;
        }
        public int getType(){return type;}
        public String getName() {
            return name;
        }
    }
    public static class SliderElement extends SettingElement implements Serializable{
        private int min, max;

        public SliderElement(String name, int min, int max, int current){
            super(name,SLIDER);
            this.min=min;
            this.max=max;
            this.value=current;
            this.newValue=current;
        }

        public int getSeekbarMax(){
            return max-min;
        }


        private int value;
        private int newValue;
        public void setValue(int val){
            newValue=val+min;
        }

        public int getValue(){
            return value;
        }
        public void applyValue(){
            value=newValue;
        }
    }


    @SuppressWarnings("ResourceType")
    public static ViewGroup generateSettings(final SettingElement[] elements, Context ctxt, final NewSettingsUpdateListener sul){
        LinearLayout root=new LinearLayout(ctxt);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        //LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        //root.setLayoutParams(rootParams);



        for(int i=0;i<elements.length;i++){
            Log2.log(2,SettingsUiFactory.class,"Element",i);
            RelativeLayout elementRoot=new RelativeLayout(ctxt);
            LinearLayout.LayoutParams elementRootParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            elementRoot.setLayoutParams(elementRootParams);

            TextView title= new TextView(ctxt);
            title.setId(i*10+1);
            RelativeLayout.LayoutParams titleParams=new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            titleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
            titleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
            title.setLayoutParams(titleParams);
            title.setText(elements[i].getName());
            elementRoot.addView(title);

            final int elementNum=i;
            if (elements[i].type==SettingElement.SLIDER){
                final SliderElement element=(SliderElement) elements[i];

                SeekBar slider=new SeekBar(ctxt);
                slider.setId(i*10+2);
                slider.setMax(element.getSeekbarMax());

                final TextView valueDisplay=new TextView(ctxt);
                valueDisplay.setId(i*10+3);


                RelativeLayout.LayoutParams sliderParams=new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                sliderParams.addRule(RelativeLayout.BELOW,title.getId());
                sliderParams.addRule(RelativeLayout.LEFT_OF,valueDisplay.getId());
                slider.setLayoutParams(sliderParams);

                RelativeLayout.LayoutParams valueParams=new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                valueParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                valueParams.addRule(RelativeLayout.ALIGN_BOTTOM,slider.getId());
                valueDisplay.setLayoutParams(valueParams);


                slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        Log2.log(2,this,"Changing element.",i);
                        element.setValue(i);
                        valueDisplay.setText(""+i);
                        if (sul!=null) sul.settingsChanged(elementNum);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });


                elementRoot.addView(slider);
                elementRoot.addView(valueDisplay);
            }






            root.addView(elementRoot);
        }

        return root;
    }/*
    private static View generateSlider(Context c){
        RelativeLayout root=new RelativeLayout()
    }
    private static View generateSpinner(){

    }
    private static View generateToggle(){

    }
    private static View generateDivider(){

    }*/
}
