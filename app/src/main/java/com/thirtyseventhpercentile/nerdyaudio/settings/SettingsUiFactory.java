package com.thirtyseventhpercentile.nerdyaudio.settings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.thirtyseventhpercentile.nerdyaudio.R;
import com.thirtyseventhpercentile.nerdyaudio.helper.Log2;
import com.thirtyseventhpercentile.nerdyaudio.interfaces.NewSettingsUpdateListener;


import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Chan on 6/25/2016.
 */
public class SettingsUiFactory {

    @SuppressWarnings("ResourceType")
    public static ViewGroup generateSettings(List<SettingElement> elements, Context ctxt, final NewSettingsUpdateListener sul){
        LinearLayout root=new LinearLayout(ctxt);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        //LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        //root.setLayoutParams(rootParams);



        for(int i=0;i<elements.size();i++){
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
            title.setText(elements.get(i).getName());
            elementRoot.addView(title);


            if (elements.get(i).getType()==SettingElement.SLIDER){
                final SliderElement sliderElement=(SliderElement) elements.get(i);

                SeekBar slider=new SeekBar(ctxt);
                slider.setId(i*10+2);
                slider.setMax(sliderElement.getSeekbarMax());

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
                        //Log2.log(2,this,"Changing sliderElement.",i);
                        sliderElement.setValue(i);
                        valueDisplay.setText(sliderElement.getNewStringRepr());
                        if (sul!=null) sul.settingsChanged(sliderElement);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                slider.setProgress(sliderElement.getValueProgress());
                elementRoot.addView(slider);
                elementRoot.addView(valueDisplay);
            }else if (elements.get(i).getType()==SettingElement.BOOLEAN){
                final BooleanElement sliderElement=(BooleanElement) elements.get(i);

                Switch toggle=new Switch(ctxt);
                toggle.setId(i*10+2);

                RelativeLayout.LayoutParams switchParams=new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                switchParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                toggle.setLayoutParams(switchParams);

                toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        sliderElement.setValue(isChecked);
                    }
                });

                toggle.setChecked(sliderElement.getValue());

                elementRoot.addView(toggle);

            }else if (elements.get(i).getType()==SettingElement.SPINNER){
                final SpinnerElement spinnerElement=(SpinnerElement) elements.get(i);

                final Spinner spinner=new Spinner(ctxt);

                RelativeLayout.LayoutParams switchParams=new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                switchParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                spinner.setLayoutParams(switchParams);

                spinner.setAdapter(spinnerElement.generateAdapter(ctxt));
                spinner.post(new Runnable() {
                    public void run() {
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                spinnerElement.setIndex(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });

                spinner.setSelection(spinnerElement.getIndex());

                elementRoot.addView(spinner);

            }else{
                Log2.log(3,SettingsUiFactory.class,"Unknown element type!");
            }

            root.addView(elementRoot);
        }

        return root;
    }
}
