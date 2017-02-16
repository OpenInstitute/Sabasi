package com.ujuziweb.mydynamicform.database;

/**
 * Created by Rage Munene on 18-Sep-16.
 */
public class dbAccount {

    //private variables
    int _id;
    String _name;
    String _imei_id;
    String _acc_id;
    String _acc_pass;

    // Empty constructor
    public dbAccount(){ }


    // constructor
    public dbAccount(String acc_id, String acc_pass, String imei_id){
        this._acc_id = acc_id;
        this._acc_pass = acc_pass;
        this._imei_id = imei_id;
    }
    // getting ID
    public String getID(){
        return this._acc_id;
    }

    // setting id
    public void setID(String acc_id){
        this._acc_id = acc_id;
    }

    // getting password
    public String getPass(){
        return this._acc_pass;
    }

    // setting name
    public void setPass(String acc_pass){
        this._acc_pass = acc_pass;
    }

    // getting name
    public String getIMEI(){
        return this._imei_id;
    }

    // setting name
    public void setIMEI(String imei_id){
        this._imei_id = imei_id;
    }

}
