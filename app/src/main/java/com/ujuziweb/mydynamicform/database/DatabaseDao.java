package com.ujuziweb.mydynamicform.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseDao {
        private final static String  LOG_TAG  = "DATABASE_DAO";
        public static SQLiteDatabase database = null;
        
        public static SQLiteDatabase getDatabase(
                                                 Context context,
                                                 String databaseName) {
                DataBaseHelper myDbHelper = null;
                
                if (databaseName == null) {
                        myDbHelper = null;
                }
                else {
                        if (myDbHelper == null) myDbHelper = new DataBaseHelper(context, databaseName);
                        
                        if (myDbHelper.openDataBase()) {
                                database = myDbHelper.getReadableDatabase();
                        }
                        else {
                                database = null;
                        }
                }
                
                if (database == null) {
                        Log.e(LOG_TAG, "db is null?  No useful work will be done by this task.");
                }
                
                return database;
        }
        
        public static Cursor generateCursor(
                                            Context context,
                                            String databaseName,
                                            
                                            boolean distinct,
                                            String tableName,
                                            String[] selectColumns,
                                            String selection,
                                            String[] selectionArgs,
                                            String groupBy,
                                            String having,
                                            String orderBy,
                                            String limit) {
                if (database == null) {
                        getDatabase(context, databaseName);
                }
                
                Cursor cursor = database.query(distinct, /* distinct? */
                                tableName, /* Table */
                                selectColumns, /* Columns */
                                selection, /* Selection */
                                selectionArgs, /* Selection Args */
                                groupBy, /* Group By */
                                having, /* Having */
                                orderBy, /* Order By */
                                limit); /* Limit */
                return cursor;
        }
        
        /**
         * 
         * The id column must be called '_id'
         * 
         * */
        public static String getById(
                                     Context context,
                                     String databaseName,
                                     long id,
                                     String tableName,
                                     String outputColumn) {
                String output = null;
                Cursor cursor = generateCursor(context, databaseName, false, tableName, new String[] { "*" }, "_id = " + id, null, null, null, null, null);
                
                if (cursor.moveToFirst()) {
                        output = cursor.getString(cursor.getColumnIndex(outputColumn));
                }
                
                return output;
        }
}
