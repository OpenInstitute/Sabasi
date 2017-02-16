package com.ujuziweb.mydynamicform;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public abstract class FormWidget {
        private static final String                    LOG_TAG = "#FORMWIDGET";
        protected View                                 _view;
        protected String                               _property;
        protected String                               _displayText;
        protected String                               _value;
        protected int                                  _priority;
        public LinearLayout                            _layout;
        protected FormActivity.FormWidgetToggleHandler _handler;
        protected TextView                             _label;
        private String                                 id;
        
        public FormWidget(Context context, String name) {
                _layout = new LinearLayout(context);
                _layout.setLayoutParams(FormActivity.defaultLayoutParams);
                _layout.setOrientation(LinearLayout.VERTICAL);
                
                _property = name;
                _displayText = name.replace("_", " ");
                _displayText = toTitleCase(_displayText);
                
                _label = new TextView(context);
                _label.setText(getDisplayText());
                _label.setTextColor(Color.BLACK);
                _layout.addView(_label);
                
        }
        
        // -----------------------------------------------
        //
        // view
        //
        // -----------------------------------------------
        /**
         * return LinearLayout containing this widget's view elements
         */
        public View getView() {
                return _layout;
        }
        
        /**
         * 
         * Returns whether widget is visible or not.
         * 
         * @return boolean isVisible
         */
        public boolean isVisible() {
                boolean isVisible = false;
                int visibility = _layout.getVisibility();
                
                if (visibility != View.GONE && visibility != View.INVISIBLE) {
                        isVisible = true;
                }
                
                return isVisible;
        }
        
        /**
         * toggles the visibility of this widget
         * 
         * @param value
         */
        public void setVisibility(int value) {
                _layout.setVisibility(value);
        }
        
        // -----------------------------------------------
        //
        // set / get value
        //
        // -----------------------------------------------
        
        /**
         * returns value of this widget as String
         */
        public String getValue() {
                return "" /*_value*/;
        }
        
        /**
         * sets value of this widget, method should be overridden in sub-class
         * 
         * @param value
         */
        public void setValue(String value) {
                /*this._value = value;*/    // -- override
        }

        public String getId() {
                return id;
        }
        
        public void setId(String id) {
                this.id = id;
        }
        
        // -----------------------------------------------
        //
        // modifiers
        //
        // -----------------------------------------------
        
        /**
         * sets the hint for the widget, method should be overriden in sub-class
         */
        public void setHint(String value) {
                // -- override
        }
        
        /**
         * sets an object that contains keys for special properties on an object
         * 
         * @param modifiers
         */
        public void setModifiers(JSONObject modifiers) {
                // -- override
        }
        
        // -----------------------------------------------
        //
        // set / get priority
        //
        // -----------------------------------------------
        
        /**
         * sets the visual priority of this widget essentially this means it's
         * physical location in the form
         */
        public void setPriority(int value) {
                _priority = value;
        }
        
        /**
         * returns visual priority
         * 
         * @return
         */
        public int getPriority() {
                return _priority;
        }
        
        // -----------------------------------------------
        //
        // property name mods
        //
        // -----------------------------------------------
        
        /**
         * returns the un-modified name of the property this widget represents
         */
        public String getPropertyName() {
                return _property;
        }
        
        /**
         * returns a title case version of this property
         * 
         * @return
         */
        public String getDisplayText() {
                return _displayText;
        }
        
        /**
         * takes a property name and modifies
         * 
         * @param s
         * @return
         */
        public String toTitleCase(String s) {
                char[] chars = s.trim().toCharArray(); //s.trim().toLowerCase().toCharArray();
                boolean found = false;
                
                for (int i = 0; i < chars.length; i++) {
                        if (!found && Character.isLetter(chars[i])) {
                                chars[i] = Character.toUpperCase(chars[i]);
                                found = true;
                        }
                        else if (Character.isWhitespace(chars[i])) {
                                found = false;
                        }
                }
                
                return String.valueOf(chars);
        }
        
        public <K extends Comparable, V extends Comparable> LinkedHashMap<K, V> sortByKeys(
                                                                                           HashMap<K, V> map) {
                List<K> keys = new LinkedList<K>(map.keySet());
                
                Collections.sort(keys, (Comparator<? super K>) new Comparator<String>() {
                        @Override
                        public int compare(String first, String second) {
                                Collator collator = Collator.getInstance(Locale.getDefault());
                                //Collator collator = Collator.getInstance(new Locale("tr", "TR"));
                                return collator.compare(first, second);
                        }
                });
                
                LinkedHashMap<K, V> sortedMap = new LinkedHashMap<K, V>();
                
                for (K key : keys) {
                        sortedMap.put(key, map.get(key));
                }
                
                return sortedMap;
        }
        
        /**
         * 
         * Converts a json map to Java HashMap
         * 
         * @param jsonMap
         *                the object that has the map in it.
         * @return the jsonMap converted as Java HashMap.
         */
        public static HashMap<String, List<String>> parseJsonMapToHashMap(
                                                                          JSONObject jsonMap) {
                HashMap<String, List<String>> propertiesMap = new HashMap<String, List<String>>();
                
                if (jsonMap != null) {
                        List<String> value;
                        String key;
                        
                        JSONArray propertyNames = jsonMap.names();
                        
                        try {
                                for (int i = 0; i < jsonMap.length(); i++) {
                                        key = propertyNames.getString(i);
                                        value = jsonArrayToJavaArray(jsonMap.getJSONArray(key));
                                        propertiesMap.put(key, value);
                                }
                        }
                        catch (JSONException e) {
                                Log.e(LOG_TAG, e.getMessage());
                        }
                }
                
                return propertiesMap;
        }
        
        public static List<String> jsonArrayToJavaArray(JSONArray jsonArray) throws JSONException {
                List<String> list = new ArrayList<String>();
                
                if (jsonArray != null) {
                        int len = jsonArray.length();
                        for (int i = 0; i < len; i++) {
                                list.add(jsonArray.get(i).toString());
                        }
                }
                
                return list;
        }
        
        public void showWidget(FormWidget widget) {
                widget.setVisibility(View.VISIBLE);
        }
        
        public void hideWidget(FormWidget widget) {
                widget.setVisibility(View.GONE);
        }
        
        /**
         * Clears the field to the original value, like blank field or blank
         * option on spinner.
         */
        public void clear() {}
}
