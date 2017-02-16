package com.ujuziweb.mydynamicform;

import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class FormEditText extends FormWidget {
        protected EditText _input;
        
        public FormEditText(Context context, String property) {
                super(context, property);
                
                _input = new EditText(context);
                _input.setLayoutParams(FormActivity.defaultLayoutParams);
                _input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                
                _layout.addView(_input);
        }
        
        @Override
        public String getValue() {
                String value = "";
                
                if (isVisible()) {
                       value = _input.getText().toString();
                }
                
                return value;
        }
        
        @Override
        public void setValue(String value) {
                _input.setText(value);
        }
        
        @Override
        public void setHint(String value) {
                _input.setHint(value);
        }
        
        @Override
        public void clear() {
                _input.setText("");
        }
        
}
