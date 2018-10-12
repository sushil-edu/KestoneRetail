package com.kestone.kestoneretail.DataHolders;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagerAttendance {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "DashBoardInfo";

    // All Shared Preferences Keys
    private static final String IS_CHECK_IN = "IsCheckedIn";
    private static final String IS_CHECK_OUT = "IsCheckedOut";
    private static final String IS_STOCK = "stock";
    private static final String IS_ORDER = "order";
    private static final String IS_BL= "bl";
    private static final String IS_NEW_ARRIVAL= "NewArrival";
    private static final String IS_FL= "fl";
    private static final String IS_FACE_UP= "faceUp";
    private static final String IS_FEEDBACK= "feedBack";

    // Constructor
    public SessionManagerAttendance(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void createSession(String name, String email){
        // Storing login value as TRUE
        editor.putBoolean(IS_CHECK_IN, true);
        editor.putBoolean(IS_CHECK_OUT, true);
        editor.putBoolean(IS_STOCK, true);

        // commit changes
        editor.commit();
    }
}
