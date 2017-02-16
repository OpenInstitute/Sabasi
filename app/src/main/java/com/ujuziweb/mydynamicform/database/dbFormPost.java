package com.ujuziweb.mydynamicform.database;

/**
 * Created by Rage Munene on 18-Sep-16.
 */


public class dbFormPost {

    //private variables
    String _imei_id;
    String _acc_id;
    String _form_id;
    String _form_session;
    String _field_id;
    String _field_detail;
    String _record_date;
    String _record_sync;
    String _duration;

    // Empty constructor
    public dbFormPost(){ }


    // constructor
    public dbFormPost(String acc_id, String form_id, String form_session, String field_id, String field_detail, String imei_id, String duration){
        this._acc_id = acc_id;
        this._form_id = form_id;
        this._form_session = form_session;
        this._field_id = field_id;
        this._field_detail = field_detail;
        this._imei_id = imei_id;
        this._duration = duration;
    }

    public String get_imei_id(){ return this._imei_id; }
    public void set_imei_id(String imei_id){ this._imei_id = imei_id; }

    public String get_acc_id(){ return this._acc_id; }
    public void set_acc_id(String acc_id){ this._acc_id = acc_id; }

    public String get_form_id(){ return this._form_id; }
    public void set_form_id(String form_id){ this._form_id = form_id; }

    public String get_form_session(){ return this._form_session; }
    public void set_form_session(String form_session){ this._form_session = form_session; }

    public String get_field_id(){ return this._field_id; }
    public void set_field_id(String field_id){ this._field_id = field_id; }

    public String get_field_detail(){ return this._field_detail; }
    public void set_field_detail(String field_detail){ this._field_detail = field_detail; }

    public String get_record_date(){ return this._record_date; }
    public void set_record_date(String record_date){ this._record_date = record_date; }

    public String get_record_sync(){ return this._record_sync; }
    public void set_record_sync(String record_sync){ this._record_sync = record_sync; }

    public String get_duration(){ return this._duration; }
    public void set_duration(String duration){ this._duration = duration; }

}
