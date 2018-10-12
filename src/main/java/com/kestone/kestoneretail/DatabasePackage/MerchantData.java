package com.kestone.kestoneretail.DatabasePackage;

/**
 * Created by user on 04/05/17.
 */

public class MerchantData {


    public MerchantData(){   }
    public MerchantData(int id, String name){
        this._id = id;
        this._name = name;

    }

    public MerchantData(String name){
        this._name = name;
    }


    int _id;
    String _name;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }
}
