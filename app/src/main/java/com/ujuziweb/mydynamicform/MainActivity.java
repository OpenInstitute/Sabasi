package com.ujuziweb.mydynamicform;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ujuziweb.mydynamicform.database.DatabaseHandler;
import com.ujuziweb.mydynamicform.database.dbFormPost;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private String report_result = "";
    private String acc_id;
    private String imei_id;
    private DatabaseHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        setTitle(R.string.app_name);


        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String phoneID = telephonyManager.getDeviceId();
        String simNo = telephonyManager.getSimSerialNumber();

        acc_id = simNo;
        imei_id = phoneID;

        //Toast.makeText(getApplicationContext(), "IMEI / Phone: " + phoneID + " / " + simNo, Toast.LENGTH_LONG).show();

        db = new DatabaseHandler(this);


        ListView listView = (ListView)findViewById(R.id.examples_list);

        String[] items = {"SDG 5 - Gender Equality", "Sync"}; /*"Data Gaps Questionnaire", , "Survey Other" */
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0: {
                        intent = new Intent(MainActivity.this, FormGeneratorExample.class);
                        startActivity(intent);
                        break;
                    }
                    case 1: {
                        syncTask();
                        //intent = new Intent(MainActivity.this, ComplexForm.class);
                        break;
                    }
                    /*case 2: {
                        //intent = new Intent(MainActivity.this, CustomFormActivity.class);
                        break;
                    }
                    default: {
                        throw new Error("Unhandled list item " + position);
                    }*/
                }

                //if (intent != null) {
                    //startActivity(intent);
                //}

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }


    /**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Toast.makeText(this, item.getItemId(), Toast.LENGTH_SHORT).show();

        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_exit:
                finish();
                return true;
            case R.id.action_update:
                Intent promptInstall = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://lanet.opencounty.org/apps/?q=lanetsdg5"));
                startActivity(promptInstall);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void subFormLoader(String btnSource) {
        Toast.makeText(getApplicationContext(), "Sub Source: " + btnSource, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.lbl_caution);
        alertDialogBuilder.setMessage("Sure you want to exit?").setCancelable(false).setPositiveButton(R.string.lbl_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                finish();
            }
        }).setNegativeButton(R.string.lbl_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }




    // @Override

    public void syncTask() {

        JSONObject obj = new JSONObject();

        List<dbFormPost> formPostList = db.getFormPostAll();

        long rpt_id = 1;

        for (dbFormPost cn : formPostList) {

            String rpt_source = cn.get_acc_id() + "__" + cn.get_imei_id();
            String rpt_form = cn.get_form_id();
            String rpt_detail = cn.get_field_detail();
            String rpt_session = cn.get_form_session();
            String rpt_date = cn.get_record_date(); //new SimpleDateFormat("dd/MM/yy  hh:mma").format(cn.get_record_date());
            String rpt_duration = cn.get_duration();

            try {
                obj.put("rpt_source", rpt_source);
                obj.put("rpt_form", rpt_form);
                //obj.put("rpt_data",  report_id);
                //JSONArray rpt_data = new JSONArray(rpt_detail);
                obj.put("rpt_form_detail", rpt_detail);
                obj.put("rpt_form_session", rpt_session);
                obj.put("rpt_form_date", rpt_date);
                obj.put("rpt_duration", rpt_duration);

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }

            String reportValues = obj.toString();
            sendPostRequest(rpt_source, rpt_form, reportValues, rpt_id);


        }
    }


    private void sendPostRequest(String account_key, String category_id, String report_data, final Long report_id) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String param_account_key = params[0];
                String param_category_id = params[1];
                String param_report_data = params[2];

                //System.out.println("*** doInBackground ** paramUsername " + paramUsername + " paramPassword :" + paramPassword);

                HttpClient httpClient = new DefaultHttpClient();

                /* web page URL of the HttpPost argument*/
                /* LOCALHOST: 10.0.2.2 for default AVD and 10.0.3.2 for Genymotion */

                HttpPost httpPost = new HttpPost(db.myGlobals("link_ajx_med_report"));

                // Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
                //uniquely separate by the other end.
                //To achieve that we use BasicNameValuePair
                //Things we need to pass with the POST request

                //BasicNameValuePair account_key_BasicNameValuePair = new BasicNameValuePair("param_account_key", param_account_key);
                //BasicNameValuePair category_id_BasicNameValuePAir = new BasicNameValuePair("param_category_id", param_category_id);
                BasicNameValuePair report_data_BasicNameValuePAir = new BasicNameValuePair("param_report_data", param_report_data);

                // We add the content that we want to pass with the POST request to as name-value pairs
                //Now we put those sending details to an ArrayList with type safe of NameValuePair
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                //nameValuePairList.add(account_key_BasicNameValuePair);
                //nameValuePairList.add(category_id_BasicNameValuePAir);
                nameValuePairList.add(report_data_BasicNameValuePAir);

                try {
                    // UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs.
                    //This is typically useful while sending an HTTP POST request.
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

                    // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
                    httpPost.setEntity(urlEncodedFormEntity);

                    try {
                        // HttpResponse is an interface just like HttpPost.
                        //Therefore we can't initialize them
                        HttpResponse httpResponse = httpClient.execute(httpPost);

                        // According to the JAVA API, InputStream constructor do nothing.
                        //So we can't initialize InputStream although it is not an interface
                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        StringBuilder stringBuilder = new StringBuilder();

                        String bufferedStrChunk = null;

                        while((bufferedStrChunk = bufferedReader.readLine()) != null){
                            stringBuilder.append(bufferedStrChunk);
                        }

                        return stringBuilder.toString();

                    } catch (ClientProtocolException cpe) {
                        System.out.println("First Exception caz of HttpResponse :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe) {
                        System.out.println("Second Exception caz of HttpResponse :" + ioe);
                        ioe.printStackTrace();
                    }

                } catch (UnsupportedEncodingException uee) {
                    System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                Toast.makeText(getApplicationContext(), "Result: "+result, Toast.LENGTH_LONG).show();
                if(result.equals("Success"))
                {
                    //Integer.parseInt(report_id)
                    db.updateFormPostSync("0");

                    /*Intent i = new Intent(getApplicationContext(), Expandable.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();*/
                }
                report_result = result;
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(account_key, category_id, report_data);
    }


}
