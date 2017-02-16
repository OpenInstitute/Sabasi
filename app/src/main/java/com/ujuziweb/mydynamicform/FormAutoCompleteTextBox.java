package com.ujuziweb.mydynamicform;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.ujuziweb.mydynamicform.autocompletetext.AsyncFetchRemoteFile;
import com.ujuziweb.mydynamicform.autocompletetext.AutoCompleteListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;

public class FormAutoCompleteTextBox extends FormWidget {
        protected AutoCompleteTextView        _input;
        private Context                       context;
        
        private final String                  LOG_TAG      = getClass().getSimpleName();
        
        private String                        databaseURL  = null;
        private String                        databaseName = null;
        private String                        databaseDir  = null;
        private List<String>                  whereColumns = null;
        private String                        outputColumn = null;
        private List<String>                  presentationColumns;
        private String                        tableName    = null;
        
        private String                        value        = null;
        
        private AutoCompleteListener          mAutoCompleteListener;
        private AsyncFetchRemoteFile          asyncGetDb;
        
        private ProgressDialog                dialog;
        private HashMap<String, List<String>> optionsMap;
        
        public FormAutoCompleteTextBox(
                                       Context context,
                                       String property,
                                       JSONObject options) {
                super(context, property);
                
                this.optionsMap = parseJsonMapToHashMap(options);
                initializeFields();
                
                if (databaseURL != null && databaseName != null && whereColumns != null && tableName != null) {
                        
                        databaseDir = context.getDatabasePath(databaseName).getAbsolutePath();
                        Log.d(LOG_TAG, databaseDir);
                        
                        /* Grab handle to your AutoCompleteTextView. */
                        _input = new AutoCompleteTextView(context);
                        _input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        
                        /*
                         * Create a new listener, pass in context,
                         * AutoCompleteTextView, and a database name.
                         */
                        //mAutoCompleteListener = new AutoCompleteListener(_input, context, this, databaseName, false, tableName, selectColumns, whereColumns, outputColumnName, presentationColumns, whereColumns, null, null, null, null, limit)
                        mAutoCompleteListener = new AutoCompleteListener(_input, context, this, databaseName, false, tableName, whereColumns, outputColumn, presentationColumns, null, null, null, null, 30);
                        /*
                         * Download a remotely-served database to back our
                         * AutoCompleteTextView.
                         */
                        /*
                         * In a real app, you probably don't want to fetch the
                         * database EVERY time the app starts up.
                         */
                        Log.i(LOG_TAG, "Data connection detected.");
                        
                        /*
                         * Create ProgressDialog to keep user notified of what's
                         * going on.
                         */
                        dialog = ProgressDialog.show(context, "", "Fetching database, please wait.", true);
                        /*
                         * If the user cancels, then the db will not be
                         * downloaded.
                         */
                        dialog.setCancelable(true);
                        
                        /*
                         * In the background, go fetch the database. You'll
                         * probably want your own database in a real app.
                         */
                        asyncGetDb = new AsyncFetchRemoteFile(dialog);
                        asyncGetDb.execute(databaseURL + databaseName, databaseDir);
                        
                        _layout.addView(_input);
                }
        }
        
        /**
         * Initialize the following database properties: databaseName, dbUrl,
         * whereColumns, outputColumns, presentationColumns, tableName.
         */
        public void initializeFields() {
                databaseName = getFirstObject(optionsMap.get("db_name"));
                databaseURL = getFirstObject(optionsMap.get("db_url"));

                whereColumns = optionsMap.get("inputColumns");
                outputColumn = getFirstObject(optionsMap.get("outputColumn"));
                presentationColumns = optionsMap.get("presentationColumns");
                tableName = getFirstObject(optionsMap.get("tableName"));
        }
        
        /**
         * Gets the first object of the array.
         * 
         * @param list
         * @return the first string property from array.
         */
        public String getFirstObject(List<String> list) {
                String property = null;
                
                if (list != null && !list.isEmpty()) {
                        property = list.get(0);
                }
                
                return property;
        }
        
        @Override
        public String getValue() {
                return value;
        }
        
        @Override
        public void setValue(String value) {
                _input.setText(value);
                this.value = value;
        }
        
        @Override
        public void setHint(String value) {
                _input.setHint(value);
        }
        
        public List<String> getInputColumns() {
                return whereColumns;
        }
        
        public void setInputColumns(List<String> inputColumns) {
                this.whereColumns = inputColumns;
        }
}
