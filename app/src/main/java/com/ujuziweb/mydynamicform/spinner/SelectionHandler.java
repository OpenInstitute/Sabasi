package com.ujuziweb.mydynamicform.spinner;

import com.ujuziweb.mydynamicform.FormSpinner;
import android.view.View;
import android.widget.AdapterView;

public class SelectionHandler implements AdapterView.OnItemSelectedListener {
        protected FormSpinner _widget;
        
        public SelectionHandler(FormSpinner widget) {
                _widget = widget;
        }
        
        public void onItemSelected(
                                   AdapterView<?> arg0,
                                   View arg1,
                                   int arg2,
                                   long arg3) {
                _widget.applyModifications();
        }
        
        public void onNothingSelected(AdapterView<?> arg0) {}
}