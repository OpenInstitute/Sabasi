/**
 * 
 * Based on DatabaseAutoCompleteLibrary by Joseph Rios
 * 
 * https://github.com/alotau/DatabaseAutoCompleteLibrary
 * 
 * 
 * */

package com.ujuziweb.mydynamicform.autocompletetext;

import java.util.List;

import com.ujuziweb.mydynamicform.database.DatabaseDao;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncAutoCompleteQuery extends AsyncTask<String, Integer, Cursor> {
        
        private final String         LOG_TAG  = getClass().getSimpleName();
        
        private AutoCompleteListener caller;
        private Context              context;
        
        /* Query Properties */
        private String               databaseName;
        boolean                      distinct;
        private String               tableName;
        private String[]             selectColumns;
        private List<String>         whereColumns;
        private String               selection;
        private String               matchingText;
        private String[]             selectionArgs;
        private String               groupBy;
        private String               having;
        private String               orderBy;
        private int                  limit    = 30;
        private String               limitStr = "30";
        
        public AsyncAutoCompleteQuery(
                                      AutoCompleteListener caller,
                                      Context context,
                                      String databaseName,
                                      boolean distinct,
                                      String tableName,
                                      String[] columns,
                                      List<String> whereColumns,
                                      String matchingText,
                                      String[] selectionArgs,
                                      String groupBy,
                                      String having,
                                      String orderBy,
                                      int limit) {
                this.caller = caller;
                this.context = context;
                
                this.databaseName = databaseName;
                this.distinct = distinct;
                this.tableName = tableName;
                this.selectColumns = columns;
                this.whereColumns = whereColumns;
                this.matchingText = matchingText;
                this.selectionArgs = selectionArgs;
                this.groupBy = groupBy;
                this.having = having;
                this.orderBy = orderBy;
                this.limit = limit;
                this.limitStr = String.valueOf(limit);
                
                this.execute();
        }
        
        @Override
        protected Cursor doInBackground(String... params) {
                //String[] selectColumns = columnNames.toArray(new String[columnNames.size()]);
                selection = generateLikeClause();
                
                Cursor cursor = DatabaseDao.generateCursor(context, databaseName, distinct, tableName, params, selection, params, groupBy, having, orderBy, limitStr);
                
                return cursor;
        }
        
        /**
         * Where / Like clause.
         */
        public String generateLikeClause() {
                String selection = "";
                
                for (int i = 0; i < whereColumns.size(); i++) {
                        selection += whereColumns.get(i) + " LIKE '%" + matchingText + "%'";
                        
                        if (i != whereColumns.size() - 1) {
                                selection += " OR ";
                        }
                }
                
                return selection;
        }
        
        @Override
        protected void onCancelled() {
                super.onCancelled();
        }
        
        @Override
        protected void onPostExecute(Cursor cursor) {
                
                Log.i(LOG_TAG, "Quantidade de registros: " + cursor.getCount());
                
                if (cursor != null) {
                        caller.onCompletion(cursor);
                        //myDbHelper.close();
                }
                else {
                        Log.d("AsyncQuery", "db was null.");
                        caller.onCompletion(null);
                }
        }
}
