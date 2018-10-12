package com.kestone.kestoneretail.DataHolders;

/**
 * Created by Tiwaris on 4/21/2017.
 */

public class UserDetails {
    public static String UID, UName, UEmail, URegion;

    public static String getURegion() {
        return URegion;
    }

    public static void setURegion(String URegion) {
        UserDetails.URegion = URegion;
    }

    public static String getUID() {
        return UID;
    }

    public static void setUID(String UID) {
        UserDetails.UID = UID;
    }

    public static String getUName() {
        return UName;
    }

    public static void setUName(String UName) {
        UserDetails.UName = UName;
    }

    public static String getUEmail() {
        return UEmail;
    }

    public static void setUEmail(String UEmail) {
        UserDetails.UEmail = UEmail;
    }
}
