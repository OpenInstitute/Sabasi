package com.ujuziweb.mydynamicform.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Rage Munene on 18-Sep-16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    public Long rpt_id_last;

    // All Static variables
    private String              path;
    private SQLiteDatabase      myDataBase;

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "db_opendata";

    // Accounts table name
    private static final String TABLE_ACCOUNTS = "mob_account_setup";
    private static final String KEY_IMEI_ID = "imei_id";
    private static final String KEY_ACC_ID = "acc_id";
    private static final String KEY_ACC_PASS = "acc_pass";


    // Form Reports Table
    private static final String TABLE_FORMPOSTS = "mob_form_posts";
    private static final String KEY_RPT_ID = "post_id";
    private static final String KEY_RPT_IMEI_ID = "imei_id";
    private static final String KEY_RPT_ACC_ID = "acc_id";
    private static final String KEY_RPT_FORM_ID = "form_id";
    private static final String KEY_RPT_FORM_SESSION = "form_session";
    private static final String KEY_RPT_FIELD_ID = "field_id";
    private static final String KEY_RPT_FIELD_DETAIL = "field_detail";
    private static final String KEY_RPT_DATE = "record_date";
    private static final String KEY_RPT_SYNC = "record_sync";
    private static final String KEY_RPT_DURATION = "duration";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        path = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase database) {

        String CREATE_ACCOUNTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ACCOUNTS + " ( " +
                "  `" + KEY_ACC_ID + "` TEXT NOT NULL UNIQUE," +
                "  `" + KEY_ACC_PASS + "` TEXT," +
                "  `" + KEY_IMEI_ID + "` TEXT NOT NULL UNIQUE" +
                ")";

        /*  " `" + KEY_RPT_ID + "` INTEGER PRIMARY KEY autoincrement," +   */
       String CREATE_FORMPOSTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FORMPOSTS + " ( " +
               " `" + KEY_RPT_IMEI_ID + "` TEXT," +
               " `" + KEY_RPT_ACC_ID + "` TEXT," +
               " `" + KEY_RPT_FORM_ID + "` TEXT," +
               " `" + KEY_RPT_FORM_SESSION + "` TEXT," +
               " `" + KEY_RPT_FIELD_ID + "` TEXT," +
               " `" + KEY_RPT_FIELD_DETAIL + "` TEXT," +
               " `" + KEY_RPT_DATE + "` TEXT," +
               " `" + KEY_RPT_SYNC + "` TEXT  DEFAULT 0 NOT NULL, " +
               " `" + KEY_RPT_DURATION + "` TEXT " +
               ")";

        //Log.e("APP_LOG", CREATE_FORMPOSTS_TABLE);
        //database.execSQL(CREATE_ACCOUNTS_TABLE);

        //database.execSQL("DROP TABLE " + TABLE_FORMPOSTS + "");
        database.execSQL(CREATE_FORMPOSTS_TABLE);
        //db.execSQL("DROP TABLE " + TABLE_MEDICAL_REPORTS + "");
        //db.execSQL(CREATE_MEDICAL_REPORTS);
    }





    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        // Create tables again
        onCreate(db);
    }


/**
 * **************************************************************************
 *  USER ACCOUNT TABLE
 ****************************************************************************
**/

    // Adding new account
    public void addAccount(dbAccount account) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ACC_ID, account.getID()); // Account ID
        values.put(KEY_ACC_PASS, account.getPass()); // Account pass
        values.put(KEY_IMEI_ID, account.getIMEI());

        // Inserting Row
        db.insert(TABLE_ACCOUNTS, null, values);
        db.close(); // Closing database connection
    }


    // Getting single account
    dbAccount getAccount(String id) {
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.query(TABLE_ACCOUNTS, new String[]{
                        KEY_ACC_ID, KEY_ACC_PASS, KEY_IMEI_ID}, KEY_ACC_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);


        if (cursor != null)
            cursor.moveToFirst();

        dbAccount account = new dbAccount(cursor.getString(0), cursor.getString(1), cursor.getString(2));
        // return account
        return account;
    }


    // Getting All Accounts
    public List<dbAccount> getAllAccounts() {
        List<dbAccount> accountList = new ArrayList<dbAccount>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                dbAccount account = new dbAccount();
                //account.setID(Integer.parseInt(cursor.getString(0)));
                account.setID(cursor.getString(0));
                account.setPass(cursor.getString(1));
                account.setIMEI(cursor.getString(2));
                // Adding account to list
                accountList.add(account);
            } while (cursor.moveToNext());
        }

        // return account list
        return accountList;
    }


    // Updating single account
    public int updateAccount(dbAccount account) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ACC_PASS, account.getPass());
        values.put(KEY_ACC_ID, account.getID());

        // updating row
        return db.update(TABLE_ACCOUNTS, values, KEY_ACC_ID + " = ?",
                new String[] { String.valueOf(account.getID()) });
    }



    // Deleting single account
    public void deleteAccount(dbAccount account) { //
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCOUNTS, KEY_ACC_ID + " = ?",
                new String[]{String.valueOf(account.getID())});

        db.close();
    }

    // Getting accounts Count
    public int getAccountsCount() {
        String countQuery = " SELECT  count(*) FROM " + TABLE_ACCOUNTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int num_accounts= cursor.getInt(0);
        cursor.close();
        return num_accounts;
    }




/**
 * **************************************************************************
 *  USER POSTS TABLE
 ****************************************************************************
 **/



    public void dropFormPost() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORMPOSTS);
    }/**/



    public void addFormPost(dbFormPost formPost) {
        SQLiteDatabase db = this.getWritableDatabase();

        onCreate(db);

        final Calendar c = Calendar.getInstance();
        int dY = c.get(Calendar.YEAR);
        int dM = c.get(Calendar.MONTH) + 1;
        int dD =  c.get(Calendar.DAY_OF_MONTH);
        int dH =  c.get(Calendar.HOUR_OF_DAY);
        int dT =  c.get(Calendar.MINUTE);

        String rpt_date = dY + "" + dM + "" + dD;
        String rpt_sess = dY + dM + dD + "_" + dH + dT;

        ContentValues values = new ContentValues();
        values.put(KEY_RPT_ACC_ID, formPost.get_acc_id());
        values.put(KEY_RPT_FORM_ID, formPost.get_form_id());
        //values.put(KEY_RPT_FORM_SESSION, rpt_sess);
        values.put(KEY_RPT_FORM_SESSION, formPost.get_form_session());
        values.put(KEY_RPT_FIELD_ID, formPost.get_field_id());
        values.put(KEY_RPT_FIELD_DETAIL, formPost.get_field_detail());
        values.put(KEY_RPT_DATE, rpt_date);
        values.put(KEY_RPT_IMEI_ID, formPost.get_imei_id());
        values.put(KEY_RPT_DURATION, formPost.get_duration());
        // Inserting Row
        db.insert(TABLE_FORMPOSTS, null, values); //rpt_id_last =
        db.close(); // Closing database connection
    }


    // Getting Form Post Count
    public int getPostsCount() {
        String countQuery = " SELECT  count(*) FROM " + TABLE_FORMPOSTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int num_accounts = cursor.getInt(0);
        cursor.close();
        return num_accounts;
    }


    public int getFormPostCount(){

        String selectQuery = "SELECT  * FROM " + TABLE_FORMPOSTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int ngapi = 0;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ngapi = ngapi + 1;
            } while (cursor.moveToNext());
        }

        // return account list
        return ngapi;
    }


    // Updating sync status
    public int updateFormPostSync(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RPT_SYNC, "1");

        // updating row
        return db.update(TABLE_FORMPOSTS, values, KEY_RPT_SYNC + " = ?",
                new String[] { String.valueOf(id) });
    }

    /**
     * String acc_id, String form_id, String form_session, String field_id, String field_detail
     *  private static final String TABLE_FORMPOSTS = "mob_form_posts";
     private static final String KEY_RPT_IMEI_ID = "imei_id";
     private static final String KEY_RPT_ACC_ID = "acc_id";
     private static final String KEY_RPT_FORM_ID = "form_id";
     private static final String KEY_RPT_FORM_SESSION = "form_session";
     private static final String KEY_RPT_FIELD_ID = "field_id";
     private static final String KEY_RPT_FIELD_DETAIL = "field_detail";
     private static final String KEY_RPT_DATE = "record_date";
     private static final String KEY_RPT_SYNC = "record_sync";
     *
     **/

    public List<dbFormPost> getFormPostAll() {
        List<dbFormPost> formPostList = new ArrayList<dbFormPost>();

        String selectQuery = "SELECT  * FROM " + TABLE_FORMPOSTS + " WHERE " + KEY_RPT_SYNC +" = '0' "; // LIMIT 1

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                dbFormPost formPost = new dbFormPost();
                formPost.set_imei_id(cursor.getString(0));
                formPost.set_acc_id(cursor.getString(1));
                formPost.set_form_id(cursor.getString(2));
                //formPost.set_form_session(cursor.getString(3));
                //formPost.set_field_id(cursor.getString(4));
                formPost.set_field_detail(cursor.getString(5));
                formPost.set_record_date(cursor.getString(6));
                //formPost.set_record_sync(cursor.getString(7));
                formPost.set_duration(cursor.getString(8));

                formPostList.add(formPost);
            } while (cursor.moveToNext());
        }

        // return account list
        return formPostList;
    }


    public String myGlobals(String globalVal){
        String globalResult = null;

        String web_link = "http://www.lanet.opencounty.org/app/";
        //String web_link = "http://10.0.3.2:8080/web2/oi_mobile/";

        if(globalVal.equals("link_ajx_med_report")){
            globalResult = web_link + "mob_posts.php";
        }

        if(globalVal.equals("link_ajx_med_acc_setup")){
            globalResult = web_link + "med_acc_setup.php";
        }

        return globalResult;
    }
}
