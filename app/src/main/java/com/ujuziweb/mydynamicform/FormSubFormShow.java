package com.ujuziweb.mydynamicform;

import android.os.Bundle;
import android.widget.Toast;

import com.ujuziweb.mydynamicform.database.DatabaseHandler;
import com.ujuziweb.mydynamicform.database.dbFormPost;

/**
 * Created by Rage Munene on 18-Sep-16.
 */
public class FormSubFormShow extends FormActivity {

    private long time_start;
    private long time_end;
    private String acc_id;
    private String imei_id;
    private DatabaseHandler db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.app_name);
        acc_id = "";
        imei_id = "";

        time_start = System.currentTimeMillis();

        db = new DatabaseHandler(this);

        generateForm(FormActivity.parseFileToString(this, "data_gaps_page1.json"));

    }


    @Override
    public void saveTask(String resultJson) {
        //Log.e("JSON Recebido para! ", resultJson);
        //Toast.makeText(getApplicationContext(), "EXAMPLE Result: " + resultJson, Toast.LENGTH_LONG).show();
        //debugAlert(resultJson);


        time_end = (System.currentTimeMillis() - time_start) / 1000;
        String timeEnd = String.valueOf(time_end);

                /* Save Report to DB */
        db.addFormPost(new dbFormPost(acc_id, "data_gaps_b", "b", "survey_b", resultJson, imei_id, timeEnd));
        //Long record_id = db.rpt_id_last;
        //int report_count = db.getFormPostCount();
        //debugAlert("posted records:" + report_count + "  #Last Id" + record_id);
        Toast.makeText(getApplicationContext(), "Form Details Saved.", Toast.LENGTH_LONG).show();

        finish();
    }
}
