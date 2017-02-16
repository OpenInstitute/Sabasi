package com.ujuziweb.mydynamicform;

import android.content.Context;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class FormNumericEditText extends FormWidget {
        protected EditText _input;
        protected int      _priority;
        
        public FormNumericEditText(Context context, String property) {
                super(context, property);
                
                _input = new EditText(context);
                _input.setInputType(InputType.TYPE_CLASS_PHONE);
                _input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                _input.setLayoutParams(FormActivity.defaultLayoutParams);
                
                _layout.addView(_input);
        }
        
        public String getValue() {
                String value = "";
                
                if (isVisible()) {
                        value = _input.getText().toString();
                }
                
                return value;
        }
        
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
