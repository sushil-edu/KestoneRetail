package com.kestone.kestoneretail.DatabasePackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_NAME;
    private static final String TABLE_REPORTING = "Report";
    private static final String KEY_ID = "id";
    private static final String KEY_PJP_ID = "pjp_id";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_BOOKNAME = "bookname";
    private static final String KEY_STOCK = "stock";
    private static final String KEY_SALES = "sales";
    private static final String KEY_ORDERS = "orders";
    private static final String KEY_BOOK_ID = "book_id";
    private static final String KEY_STORE_ID = "store_id";
    private static final String KEY_DATE = "date";
    private static final String KEY_DISTRIBUTOR = "distributor";

    public DatabaseHandler(Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
        this.DATABASE_NAME = DATABASE_NAME;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_REPORTS_TABLE = "CREATE TABLE " + TABLE_REPORTING + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PJP_ID + " TEXT,"
                + KEY_CATEGORY + " TEXT," + KEY_AUTHOR + " TEXT," + KEY_BOOKNAME + " TEXT,"
                + KEY_STOCK + " TEXT," + KEY_SALES + " TEXT," + KEY_ORDERS + " TEXT,"
                + KEY_BOOK_ID + " TEXT," + KEY_STORE_ID + " TEXT," + KEY_DATE + " TEXT,"
                + KEY_DISTRIBUTOR + " TEXT" +")";

        db.execSQL(CREATE_REPORTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTING);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    public void addContact(Reporting reporting) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PJP_ID, reporting.getPjpId()); // Contact Name
        values.put(KEY_CATEGORY, reporting.getCategory()); // Contact Phone
        values.put(KEY_AUTHOR, reporting.getAuthor());
        values.put(KEY_BOOKNAME, reporting.getBookname());
        values.put(KEY_STOCK, reporting.getStock());
        values.put(KEY_SALES, reporting.getSales());
        values.put(KEY_ORDERS, reporting.getOrder());
        values.put(KEY_BOOK_ID, reporting.getBookId());
        values.put(KEY_STORE_ID, reporting.getStoreId());
        values.put(KEY_DATE, reporting.getDate());
        values.put(KEY_DISTRIBUTOR, reporting.getDistributor());

        // Inserting Row
        db.insert(TABLE_REPORTING, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    public List<Reporting> getContact(int id) {

        List<Reporting> reportingList = new ArrayList<Reporting>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_REPORTING + " WHERE "+KEY_PJP_ID+" ='"+ id+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Reporting reporting = new Reporting();
                reporting.setId(Integer.parseInt(cursor.getString(0)));
                reporting.setPjpId(cursor.getString(1));
                reporting.setCategory(cursor.getString(2));
                reporting.setAuthor(cursor.getString(3));
                reporting.setBookname(cursor.getString(4));
                reporting.setStock(cursor.getString(5));
                reporting.setSales(cursor.getString(6));
                reporting.setOrder(cursor.getString(7));
                reporting.setBookId(cursor.getString(8));
                reporting.setStoreId(cursor.getString(9));
                reporting.setDate(cursor.getString(10));
                reporting.setDistributor(cursor.getString(11));
                // Adding contact to list
                reportingList.add(reporting);
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return reportingList;
    }

    // code to get all contacts in a list view
    public List<Reporting> getAllContacts() {
        List<Reporting> reportingList = new ArrayList<Reporting>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_REPORTING ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Reporting reporting = new Reporting();
                reporting.setId(Integer.parseInt(cursor.getString(0)));
                reporting.setPjpId(cursor.getString(1));
                reporting.setCategory(cursor.getString(2));
                reporting.setAuthor(cursor.getString(3));
                reporting.setBookname(cursor.getString(4));
                reporting.setStock(cursor.getString(5));
                reporting.setSales(cursor.getString(6));
                reporting.setOrder(cursor.getString(7));
                reporting.setBookId(cursor.getString(8));
                reporting.setStoreId(cursor.getString(9));
                reporting.setDate(cursor.getString(10));
                reporting.setDistributor(cursor.getString(11));
                // Adding contact to list
                reportingList.add(reporting);
            } while (cursor.moveToNext());
        }

        db.close();
        // return contact list
        return reportingList;
    }

    public List<Reporting> getAllBooks(String authorName, Integer pjpId){
        List<Reporting> reportingList = new ArrayList<Reporting>();

        String selectQuery = "SELECT  * FROM " + TABLE_REPORTING + " WHERE "+KEY_AUTHOR+" ='"+ authorName+"' AND "+KEY_PJP_ID+" ="+pjpId;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Reporting reporting = new Reporting();
                reporting.setId(Integer.parseInt(cursor.getString(0)));
                reporting.setPjpId(cursor.getString(1));
                reporting.setCategory(cursor.getString(2));
                reporting.setAuthor(cursor.getString(3));
                reporting.setBookname(cursor.getString(4));
                reporting.setStock(cursor.getString(5));
                reporting.setSales(cursor.getString(6));
                reporting.setOrder(cursor.getString(7));
                reporting.setBookId(cursor.getString(8));
                reporting.setStoreId(cursor.getString(9));
                reporting.setDate(cursor.getString(10));
                reporting.setDistributor(cursor.getString(11));
                // Adding contact to list
                reportingList.add(reporting);
            } while (cursor.moveToNext());
        }

        return reportingList;
    }

    public List<Reporting> getAllAuthors(int pjpID) {



        List<Reporting> reportingList = new ArrayList<>();


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " + KEY_AUTHOR + ", COUNT(author) FROM " + TABLE_REPORTING+" WHERE "+ KEY_PJP_ID
                +"=" + pjpID+" GROUP BY author", null);

        if (c != null) {

            if (c.moveToFirst()) {
                do {
                    Reporting reporting = new Reporting();
                    reporting.setAuthor(c.getString(0));
                    reporting.setCount(c.getInt(1));

                    // Adding contact to list
                    reportingList.add(reporting);
                } while (c.moveToNext());
            }
        }
        //  return c;

        db.close();
        return reportingList;
    }

    public List<Reporting> getBooks(int pjpID) {
        List<Reporting> reportingList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " + KEY_PJP_ID+","+ KEY_CATEGORY+","+KEY_AUTHOR+","+KEY_BOOKNAME+","+KEY_STOCK+","+KEY_SALES+","+KEY_ORDERS+","+KEY_BOOK_ID
                + " FROM " + TABLE_REPORTING+" WHERE "+ KEY_PJP_ID
                +"=" + pjpID, null);

        if (c != null) {

            if (c.moveToFirst()) {
                do {
                    Reporting reporting = new Reporting();
                    reporting.setId(Integer.parseInt(c.getString(0)));
                    reporting.setPjpId(c.getString(1));
                    reporting.setCategory(c.getString(2));
                    reporting.setAuthor(c.getString(3));
                    reporting.setBookname(c.getString(4));
                    reporting.setStock(c.getString(5));
                    reporting.setSales(c.getString(6));
                    reporting.setOrder(c.getString(7));
                    reporting.setBookId(c.getString(8));
                    reporting.setStoreId(c.getString(9));
                    reporting.setDate(c.getString(10));
                    reporting.setDistributor(c.getString(11));

                    // Adding contact to list
                    reportingList.add(reporting);
                } while (c.moveToNext());
            }
        }

        return reportingList;
    }

    // code to update the single contact
    public int updateContact(Reporting reporting) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PJP_ID, reporting.getPjpId());
        values.put(KEY_CATEGORY, reporting.getCategory());
        values.put(KEY_AUTHOR, reporting.getAuthor());
        values.put(KEY_BOOKNAME, reporting.getBookname());
        values.put(KEY_STOCK, reporting.getStock());
        values.put(KEY_SALES, reporting.getSales());
        values.put(KEY_ORDERS, reporting.getOrder());
        values.put(KEY_BOOK_ID, reporting.getBookId());
        values.put(KEY_STORE_ID, reporting.getStoreId());
        values.put(KEY_DATE, reporting.getDate());
        values.put(KEY_DISTRIBUTOR, reporting.getDistributor());

        // updating row
        return db.update(TABLE_REPORTING, values, KEY_ID + " = ?",
                new String[]{String.valueOf(reporting.getId())});
    }

    // Deleting single contact
    public void deleteContact(Reporting reporting) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REPORTING, KEY_ID + " = ?",
                new String[]{String.valueOf(reporting.getId())});
        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_REPORTING;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    //Delete all data from database
    public void deleteAll(String DATABASE_NAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + DATABASE_NAME);
        db.close();
    }

    public void deleteWithId(int pjpId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REPORTING, KEY_PJP_ID + " = ?",
                new String[]{String.valueOf(pjpId)});
        db.close();
    }

    public void updateDistributor(String s,String storeId,String pjpDate){


        SQLiteDatabase db = this.getWritableDatabase();

        String updateQuery = "UPDATE "+TABLE_REPORTING +" SET " + KEY_DISTRIBUTOR+ " = '"+s+"'"+" WHERE "+KEY_STORE_ID+ " = '"+storeId +"' AND "+ KEY_DATE +" = '"+pjpDate+"'";

        db.execSQL(updateQuery);
        db.close();

        Log.d("Distributor Data", updateQuery);


    }

}