package com.kestone.kestoneretail.DatabasePackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kestone.kestoneretail.DataHolders.PopUp;

import java.util.ArrayList;
import java.util.List;

public class DBHelpher extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_POPUP = "PopUp";
    private static final String KEY_ID = "id";
    private static final String KEY_UID = "pjp_id";
    private static final String KEY_STORE_ID = "store_id";
    private static final String KEY_DATE = "date";
    private static final String KEY_BL_VALUE = "valueBL";
    private static final String KEY_FU_VALUE = "valueFU";
    private static final String KEY_NA_VALUE = "valueNA";
    private static final String KEY_STOCK_REASON = "stock_reason";
    private static final String KEY_ORDER_REASON = "order_reason";
    private static String DATABASE_NAME;

    public DBHelpher(Context context, String DATABASE_NAME) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
        //3rd argument to be passed is CursorFactory instance
        this.DATABASE_NAME = DATABASE_NAME;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_POPUP + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_UID + " TEXT,"
                + KEY_STORE_ID + " TEXT," + KEY_DATE + " TEXT,"
                + KEY_BL_VALUE + " TEXT," + KEY_NA_VALUE + " TEXT," +
                "" + KEY_FU_VALUE + " TEXT," +
                KEY_STOCK_REASON + " TEXT )";

        db.execSQL( CREATE_TABLE );
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_POPUP );

        // Create tables again
        onCreate( db );
    }

    // code to add the new contact
    public void addInfo(PopUp popUp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put( KEY_UID, popUp.getRefUserID() );
        values.put( KEY_STORE_ID, popUp.getRefStoreID() );
        values.put( KEY_DATE, popUp.getPJPDate() );
        values.put( KEY_BL_VALUE, popUp.getBacklist() );
        values.put( KEY_FU_VALUE, popUp.getFaceUp() );
        values.put( KEY_NA_VALUE, popUp.getNewArrival() );
        values.put( KEY_STOCK_REASON, popUp.getStockReason() );

        db.insert( TABLE_POPUP, null, values );
        //2nd argument is String containing nullColumnHack

        db.close(); // Closing database connection
    }
    public List<PopUp> getAllInfo() {
        List<PopUp> popUpList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_POPUP;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery( selectQuery, null );

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PopUp popUp = new PopUp();
                popUp.setRefUserID( cursor.getString( 1 ) );
                popUp.setRefStoreID( cursor.getString( 2 ) );
                popUp.setPJPDate( cursor.getString( 3 ) );
                popUp.setBacklist( cursor.getString( 4 ) );
                popUp.setFaceUp( cursor.getString( 5 ) );
                popUp.setNewArrival( cursor.getString( 6 ) );
                popUp.setStockReason( cursor.getString( 7 ) );

                // Adding contact to list
                popUpList.add( popUp );
            } while (cursor.moveToNext());
        }

        // return contact list
        return popUpList;
    }

    //Delete all data from database
    public void deleteAll(String TABLE_NAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
        db.close();
    }
}