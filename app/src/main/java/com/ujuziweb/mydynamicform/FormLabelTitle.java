package com.ujuziweb.mydynamicform;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class FormLabelTitle extends FormWidget {
        private HashMap<String, String> optionsMap = null;
        
        private Integer                 topMargin  = null;
        private Integer                 fontSize   = null;
        
        public FormLabelTitle(
                              Context context,
                              String property,
                              JSONObject options) {
                super(context, property);
                
                initializeOptions(options);
                
                initializeTopMargin();
                initiliazeFontSize();
                
                LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                titleLayoutParams.topMargin = topMargin;
                
                _label.setLayoutParams(titleLayoutParams);
                _label.setGravity(Gravity.LEFT);
                _label.setTextSize(fontSize);
                _label.setEms(10);
        }
        
        public void initializeTopMargin() {
                topMargin = parseToint(optionsMap.get("top_padding"));
                
                if (topMargin == null) {
                        topMargin = 12;
                }
        }
        
        public void initiliazeFontSize() {
                fontSize = parseToint(optionsMap.get("font_size"));
                
                if (fontSize == null) {
                        fontSize = 18;
                }
        }
        
        public void initializeOptions(JSONObject options) {
                HashMap<String, String> propertiesMap = new HashMap<String, String>();
                
                if (options != null) {
                        String value;
                        String key;
                        
                        JSONArray propertyNames = options.names();
                        
                        try {
                                for (int i = 0; i < options.length(); i++) {
                                        key = propertyNames.getString(i);
                                        value = options.getString(key);
                                        propertiesMap.put(key, value);
                                }
                        }
                        catch (JSONException e) {
                                Log.e("FORM_LABEL_TITLE", e.getMessage());
                        }
                }
                
                this.optionsMap = propertiesMap;
        }
        
        private Integer parseToint(String property) {
                Integer propertyToInt = null;
                
                if (property != null) {
                        
                        try {
                                propertyToInt = Integer.parseInt(property);
                        }
                        catch (Exception e) {
                                Log.e("FORM_LABEL_TITLE", e.getMessage());
                        }
                }
                
                return propertyToInt;
        }
}
