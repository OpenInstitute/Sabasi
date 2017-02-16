package com.ujuziweb.mydynamicform;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ujuziweb.mydynamicform.database.DatabaseHandler;
import com.ujuziweb.mydynamicform.database.dbFormPost;

public class FormGeneratorExample extends FormActivity {

        //private DataBaseHelper db;
        private long time_start;
        private long time_end;
        private String acc_id;
        private String imei_id;
        private DatabaseHandler db;
        //private MainActivity mainActivity;

        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                //setTitle(R.string.app_name);
                // Create LinearLayout
                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.HORIZONTAL);

                // Create TextView
                TextView product = new TextView(this);
                product.setText(R.string.app_name);
                ll.addView(product);


                //acc_id = "722333";
                //imei_id = "ABC12356XYZ";

                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String phoneID = telephonyManager.getDeviceId();
                String simNo = telephonyManager.getSimSerialNumber();

                acc_id = simNo;
                imei_id = phoneID;

                db = new DatabaseHandler(this);
                //db.addAccount(new dbAccount("722333", "123456", imei_id ));
                //db.addAccount(new dbAccount("722444", "123456", imei_id));

                //db.dropFormPost();
                //db.addFormPost(new dbFormPost("722333", "survey_a", "sess", "field_id", "json", "722333"));

                //int accountCount = db.getAccountsCount();
                //int postsCount = db.getPostsCount();
                //int formPostCount = db.getFormPostCount();

                //Toast.makeText(getApplicationContext(), "num accounts " + accountCount + " ", Toast.LENGTH_LONG).show();

                time_start = System.currentTimeMillis();

                generateForm(FormActivity.parseFileToString(this, "sdg_gender.json")); //data_gaps_page2.json, "bauru.json"
                //survey_a.json == HAS_CHECKBOX
        }
        
        @Override
        public void saveTask(String resultJson) {
                //Log.e("JSON Recebido para! ", resultJson);
                //Toast.makeText(getApplicationContext(), "EXAMPLE Result: " + resultJson, Toast.LENGTH_LONG).show();
                //debugAlert(resultJson);

                time_end = (System.currentTimeMillis() - time_start) / 1000;
                String timeEnd = String.valueOf(time_end);
                /* Save Report to DB */
                db.addFormPost(new dbFormPost(acc_id, "sdg_gender", "a", "survey_a", resultJson, imei_id, timeEnd));
                //db.addFormPost(new dbFormPost(acc_id, "data_gaps_a", "a", "survey_a", resultJson, imei_id));


                //Long record_id = db.rpt_id_last;
                //int report_count = db.getFormPostCount();
                //debugAlert("posted records:" + report_count + "  #Last Id" + record_id);


                Toast.makeText(getApplicationContext(), "Form Details Saved." , Toast.LENGTH_LONG).show();

               // mainActivity.syncTask();
                //moveTaskToBack(true);
                //finish();
        }




        @Override
        public void deleteTask() {}
        
        /**
         * ===============================================================
         * 
         * Only tests functions (deprecated)
         * 
         * ===============================================================
         * */
        public void finishResultOk() {
                String result = save().toString();
                Intent data = new Intent();
                
                data.putExtra("type", "save");
                data.putExtra("data", result);
                
                setResult(RESULT_OK, data);
                finish();
        }
        
        public void finishResultDelete() {
                String result = save().toString();
                Intent data = new Intent();
                
                data.putExtra("type", "delete");
                data.putExtra("data", result);
                
                setResult(RESULT_OK, data);
                finish();
        }
        
        public void finishResultCancel() {
                setResult(RESULT_CANCELED, new Intent());
                finish();
        }
        
        protected void onActivityResultExample(
                                               int requestCode,
                                               int resultCode,
                                               Intent data) {
                String result = (String) data.getExtras().getSerializable("data");
                String type = (String) data.getExtras().getSerializable("type");
                
                if (type.equals("save")) {
                        // new Form().setPreenchimentoDoForm(result)
                        // dao.save() e o ID do form? vai criar um novo?
                }
                else if (type.equals("delete")) {
                        // dao.remove(result); // e o ID ?
                }
                
                super.onActivityResult(requestCode, resultCode, data);
        }


        public void debugAlert(String alertText){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Debug");
                alertDialogBuilder.setMessage(alertText).setCancelable(false).setNegativeButton(R.string.lbl_close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                        }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
        }


}