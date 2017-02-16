package com.ujuziweb.mydynamicform.autocompletetext;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.ujuziweb.mydynamicform.R;

import java.util.List;

/**
 * This class is responsible for the {@link AutoCompleteTextView} of address,
 * that the user input the address text, and the widget will be search the data
 * on the database.
 * 
 * */
public class FormAutoCompleteAdapter extends CursorAdapter implements Filterable {
        
        private static final String LOG_TAG = "#FormAutoCompleteAdapter";
        
        private final Context       context;
        
        private final int           layout;
        
        private List<String>        presentationColumns;
        
        public FormAutoCompleteAdapter(
                                       Context context,
                                       int layout,
                                       Cursor cursor,
                                       List<String> presentationColumns) {
                super(context, cursor);
                this.context = context;
                this.layout = layout;
                this.presentationColumns = presentationColumns;
        }
        
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
                String text = "";
                
                for (String column : presentationColumns) {
                        text += column + " : " + cursor.getString(cursor.getColumnIndex(column)) + "\n";
                }
                
                TextView textViewItem = (TextView) view.findViewById(R.id.text_row);
                textViewItem.setText(text);
        }
        
        @Override
        public View newView(Context ctx, Cursor c, ViewGroup parent) {
                final LayoutInflater inflater = LayoutInflater.from(context);
                View v = inflater.inflate(layout, parent, false);
                return v;
        }
        
        /**
         * You need to override this to return the string value when selecting
         * an item from the autocomplete.
         * */
        @Override
        public CharSequence convertToString(Cursor cursor) {
                String text = "";
                
                for (String column : presentationColumns) {
                        text += column + " : " + cursor.getString(cursor.getColumnIndex(column)) + "\n";
                }
                
                return text;
        }
        
}
