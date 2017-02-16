package com.ujuziweb.mydynamicform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class FormCheckBox extends FormWidget {
        
        protected int                                _priority;
        protected CheckBox                           _checkbox;
        
        protected HashMap<String, ArrayList<String>> _toggles;
        
        public FormCheckBox(Context context, String property) {
                super(context, property);
                
                _checkbox = new CheckBox(context);
                _checkbox.setText(this.getDisplayText());
                
                _layout.addView(_checkbox);
        }
        
        @Override
        public String getValue() {
                String value = "";
                
                if (isVisible()) {
                        value = String.valueOf(_checkbox.isChecked() ? "1" : "0");
                }
                
                return value;
        }
        
        public void setValue(String value) {
                _checkbox.setChecked(value.equals("1"));
        }
        
        class ChangeHandler implements CompoundButton.OnCheckedChangeListener {
                protected FormWidget _widget;
                
                public ChangeHandler(FormWidget widget) {
                        _widget = widget;
                }
                
                public void onCheckedChanged(
                                             CompoundButton buttonView,
                                             boolean isChecked) {
                        if (_handler != null) {
                                _handler.toggle(_widget);
                        }
                }
        }
        
        // -----------------------------------------------
        //
        // toggles
        //
        // -----------------------------------------------
        
        /**
         * sets the list of toggles for this widgets the structure of the data
         * looks like this: HashMap<value of property for visibility,
         * ArrayList<list of properties to toggle on>>
         */
        public void setToggles(HashMap<String, ArrayList<String>> toggles) {
                _toggles = toggles;
        }
        
        /**
         * return list of widgets to toggle on
         * 
         * @param value
         * @return
         */
        public ArrayList<String> getToggledOn() {
                if (_toggles == null) return new ArrayList<String>();
                
                if (_toggles.get(getValue()) != null) {
                        return _toggles.get(getValue());
                }
                else {
                        return new ArrayList<String>();
                }
        }
        
        /**
         * return list of widgets to toggle off
         * 
         * @param value
         * @return
         */
        public ArrayList<String> getToggledOff() {
                ArrayList<String> result = new ArrayList<String>();
                if (_toggles == null) return result;
                
                Set<String> set = _toggles.keySet();
                
                for (String key : set) {
                        if (!key.equals(getValue())) {
                                ArrayList<String> list = _toggles.get(key);
                                if (list == null) return new ArrayList<String>();
                                for (int i = 0; i < list.size(); i++) {
                                        result.add(list.get(i));
                                }
                        }
                }
                
                return result;
        }
        
        /**
         * sets a handler for value changes
         * 
         * @param handler
         * 
         *                public void setToggleHandler(
         *                FormActivity.FormWidgetToggleHandler handler) {
         *                _handler = handler; }
         **/
        public void setToggleHandler(
                                     FormActivity.FormWidgetToggleHandler handler) {
                _checkbox.setOnCheckedChangeListener(new ChangeHandler(this));
        }
        
        @Override
        public void clear() {
                _checkbox.setChecked(false);
        }
        
}
