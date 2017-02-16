/**
 * 
 * Based on DatabaseAutoCompleteLibrary by Joseph Rios
 * 
 * https://github.com/alotau/DatabaseAutoCompleteLibrary
 * 
 * 
 * */

package com.ujuziweb.mydynamicform.autocompletetext;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;

import com.ujuziweb.mydynamicform.FormAutoCompleteTextBox;
import com.ujuziweb.mydynamicform.R;
import com.ujuziweb.mydynamicform.database.DatabaseDao;

import java.util.List;

public class AutoCompleteListener implements TextWatcher {
        private final String            LOG_TAG          = getClass().getSimpleName();
        
        private AutoCompleteTextView    autoCompleteView = null;
        private FormAutoCompleteAdapter adapter          = null;
        
        private Context                 context          = null;
        private boolean                 querying, selected;
        
        private AsyncAutoCompleteQuery  asyncQuery;
        
        /* Query Properties */
        private String                  databaseName;
        boolean                         distinct;
        private String                  tableName;
        private String[]                selectColumns    = new String[] { "*" };
        private List<String>            whereColumns;
        private String                  outputColumnName;
        private List<String>            presentationColumns;
        private String                  currentText;
        private String[]                selectionArgs;
        private String                  groupBy;
        private String                  having;
        private String                  orderBy;
        private int                     limit            = 30;
        
        private FormAutoCompleteTextBox parentWidget;
        
        public AutoCompleteListener(
                                    AutoCompleteTextView autoCompleteView,
                                    Context context,
                                    FormAutoCompleteTextBox parentWidget,
                                    
                                    String databaseName,
                                    boolean distinct,
                                    String tableName,
                                    List<String> whereColumns,
                                    String outputColumnName,
                                    List<String> presentationColumns,
                                    String[] selectionArgs,
                                    String groupBy,
                                    String having,
                                    String orderBy,
                                    int limit) {
                this.autoCompleteView = autoCompleteView;
                this.autoCompleteView.addTextChangedListener(this);
                
                this.context = context;
                this.parentWidget = parentWidget;
                this.databaseName = databaseName;
                this.distinct = distinct;
                this.tableName = tableName;
                this.whereColumns = whereColumns;
                this.outputColumnName = outputColumnName;
                this.presentationColumns = presentationColumns;
                this.selectionArgs = selectionArgs;
                this.groupBy = groupBy;
                this.having = having;
                this.orderBy = orderBy;
                this.limit = limit;
        }
        
        public interface AsyncAutoCompleteInterface {}
        
        public void onCompletion(Cursor cursor) {
                
                if (cursor == null || selected) {
                        querying = false;
                        return;
                }
                
                /*
                 * Update the adapter with the query results. There's a chance
                 * the adapter is null, so be careful.
                 */
                if (cursor != null) {
                        /* Otherwise, update the Adapter with new data. */
                        adapter = new FormAutoCompleteAdapter(context, R.layout.autocomplete_row, cursor, presentationColumns);
                        autoCompleteView.setAdapter(adapter);
                        
                        setClickListener();
                        
                        /* Let the View know to update. */
                        adapter.notifyDataSetChanged();
                }
                
        }
        
        /**
         * When a row is clicked this function is called.
         */
        public void setClickListener() {
                autoCompleteView.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(
                                                AdapterView<?> adapter,
                                                View view,
                                                int position,
                                                long objectId) {
                                try {
                                        String output = DatabaseDao.getById(context, databaseName, objectId, tableName, outputColumnName);
                                        Log.i(LOG_TAG, "Output: " + output + "  Posição: " + position + "  Id: " + objectId);
                                        
                                        //TODO: verificar como pegar as informações...
                                        //dao.getById(objectId);
                                        //String result = "";
                                        //parentWidget.setValue(result);
                                        
                                }
                                catch (Exception ex) {
                                        Log.e(LOG_TAG, "Exception onItemClick: " + ex);
                                }
                        }
                });
        }
        
        @Override
        public void afterTextChanged(Editable s) {
                /*
                 * Looks like we might be ready to edit text again, clear
                 * selected flag.
                 */
                selected = false;
        }
        
        @Override
        public void beforeTextChanged(
                                      CharSequence s,
                                      int start,
                                      int count,
                                      int after) {}
        
        @Override
        public void onTextChanged(
                                  CharSequence s,
                                  int start,
                                  int before,
                                  int count) {
                /*
                 * Two things to check: 1. Have we already launched an AsyncTask
                 * to get values? 2. Have they typed at least a few characters?
                 * If so, let's get the matching strings from db.
                 */
                if (s.length() == 0) {
                        /*
                         * This is here in case the user selected one of the
                         * autocomplete options, then deleted the entire name to
                         * start over.
                         */
                        selected = false;
                }
                else if (s.length() > 2 && !selected) {
                        querying = true;
                        currentText = autoCompleteView.getText().toString();
                        currentText = currentText.replace("'", "''");
                        
                        asyncQuery = new AsyncAutoCompleteQuery(this, context, databaseName, distinct, tableName, selectColumns, whereColumns, currentText, selectionArgs, groupBy, having, orderBy, limit);
                }
                
        }
}
