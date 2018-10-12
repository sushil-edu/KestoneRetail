package com.kestone.kestoneretail;


import android.app.ProgressDialog;
import android.content.Context;

public class Progress {
    static ProgressDialog progress;

    static public void showProgress(Context context) {
        progress = new ProgressDialog(context);
        progress.setMessage("Please Wait");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        progress.show();
    }

    static public void closeProgress() {
        progress.dismiss();
    }

}
