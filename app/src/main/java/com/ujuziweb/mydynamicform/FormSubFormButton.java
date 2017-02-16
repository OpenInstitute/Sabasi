package com.ujuziweb.mydynamicform;

import android.content.Context;
import android.view.View;
import android.widget.Button;

/**
 * Created by Rage Munene on 18-Sep-16.
 */
public class FormSubFormButton extends FormWidget {
    protected Button _button;

    public FormSubFormButton(final Context context, String property, String btn_id, final String btn_source) {
        super(context, property);

        _button = new Button(context);
        _button.setText(property);
        //_button.setImeOptions(EditorInfo.IME_ACTION_DONE);

        _layout.addView(_button);

        _button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showConfirmSaveDialog();
                //Toast.makeText(context, "Clicked ", Toast.LENGTH_LONG).show();
                //FormActivity formActivity = new FormActivity();
                //formActivity.subFormLoader(btn_source);
                //Intent intent;
                //intent = new Intent(MainActivity.class, FormSubFormShow.class);

            }
        });
    }

    @Override
    public String getValue() {
        String value = "";

        if (isVisible()) {
            _button.getText().toString();
        }

        return value;
    }

    @Override
    public void setValue(String value) {
        _button.setText(value);
    }

    @Override
    public void setHint(String value) {
        _button.setHint(value);
    }

    @Override
    public void clear() {
        _button.setText("");
    }

}
