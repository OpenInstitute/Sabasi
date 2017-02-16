package com.ujuziweb.mydynamicform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class FormSpinner extends FormWidget {
        private static String                        LOG_TAG             = "FORM_SPINNER";
        
        protected JSONObject                         _options;
        protected Spinner                            _spinner;
        protected HashMap<String, String>            _propmap;
        protected ArrayAdapter<String>               _adapter;
        
        protected List<FormWidget>                   lastModifiedWidgets = new ArrayList<FormWidget>();
        
        protected HashMap<String, ArrayList<String>> _widgetsToHide;
        
        public FormSpinner(Context context, String property, JSONObject options) {
                super(context, property);
                
                _options = options;
                
                _spinner = new Spinner(context);
                _spinner.setLayoutParams(FormActivity.defaultLayoutParams);
                
                String value;
                String key;
                JSONArray propertyNames = options.names();
                
                _propmap = new HashMap<String, String>();
                _adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
                _adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                _spinner.setAdapter(_adapter);
                
                try {
                        for (int i = 0; i < options.length(); i++) {
                                key = propertyNames.getString(i);
                                value = options.getString(key);
                                
                                _propmap.put(key, value);
                        }
                }
                catch (JSONException e) {
                        Log.e(LOG_TAG, e.getMessage());
                }
                
                _propmap = sortByKeys(_propmap); // sort the hashmap entries
                
                for (Entry<String, String> entry : _propmap.entrySet()) {
                        String combo_value = entry.getValue();
                        _adapter.add(combo_value);
                }
                
                _layout.addView(_spinner);
        }
        
        @Override
        public String getValue() {
                int spinnerPosition = _spinner.getSelectedItemPosition();
                String value = "";
                
                if (isVisible()) {
                        value = _adapter.getItem(spinnerPosition);
                }
                
                return value;
        }
        
        public String getPosition() {
                int spinnerPosition = _spinner.getSelectedItemPosition();
                return "" + spinnerPosition;
        }
        
        @Override
        public void setValue(String value) {
                try {
                        String name;
                        JSONArray names = _options.names();
                        for (int i = 0; i < names.length(); i++) {
                                name = names.getString(i);
                                String item = _options.getString(name);
                                
                                if (name.equals(value) || item.equals(value)) {
                                        _spinner.setSelection(_adapter.getPosition(item));
                                }
                        }
                }
                catch (JSONException e) {
                        Log.e(LOG_TAG, e.getMessage());
                }
        }
        
        public void applyModifications() {
                resetLastModifications();
                List<String> widgetsToHide = getWidgetsToBeInvisible();
                
                if (!widgetsToHide.isEmpty()) {
                        hideWidgets(widgetsToHide);
                }
        }
        
        public void resetLastModifications() {
                
                for (FormWidget widget : lastModifiedWidgets) {
                        showWidget(widget);
                }
                
                lastModifiedWidgets.clear();
        }
        
        /**
         * Iterate over an array, hide and add the widget into the
         * lastModifiedWidgets.
         * 
         * @param widgetsToHide
         */
        public void hideWidgets(List<String> widgetsToHide) {
                String widgetId = "";
                int i;
                
                for (i = 0; i < widgetsToHide.size(); i++) {
                        widgetId = widgetsToHide.get(i);
                        
                        if (FormActivity._map.get(widgetId) != null) {
                                FormWidget widget = FormActivity._map.get(widgetId);
                                
                                if (widget.isVisible()) {
                                        Log.i(_displayText + " GONE: ", widget._displayText);
                                        widget.setVisibility(View.GONE);
                                        lastModifiedWidgets.add(widget);
                                }
                                else {
                                        Log.i(_displayText + "JÁ ESTÁ GONE: ", widget._displayText);
                                }
                        }
                }
        }
        
        /**
         * returns a list of widgets that will be invisible.
         * 
         * @param value
         * @return
         */
        public ArrayList<String> getWidgetsToBeInvisible() {
                ArrayList<String> widgetsToHide = new ArrayList<String>();
                
                if (_widgetsToHide != null) {
                        String value = getPosition();
                        
                        if (_widgetsToHide.get(value) != null) {
                                widgetsToHide = _widgetsToHide.get(value);
                        }
                }
                
                return widgetsToHide;
        }
        
        public void setWidgetsToHide(
                                     HashMap<String, ArrayList<String>> _widgetsToHide) {
                this._widgetsToHide = _widgetsToHide;
        }
        
        @Override
        public void clear() {
                _spinner.setSelection(0);
        }
        
}
