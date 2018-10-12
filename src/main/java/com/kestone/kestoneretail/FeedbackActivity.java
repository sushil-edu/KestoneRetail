package com.kestone.kestoneretail;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.kestone.kestoneretail.Fragments.FeedbackFragment;

public class FeedbackActivity extends AppCompatActivity {

    public static String MyPREFERENCES = "StoreDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        getSupportActionBar().setTitle("Feedback");

        //SharedPrferences data Saving
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString("StoreType", getIntent().getStringExtra("StoreType"));
        editor.putString("StoreName", getIntent().getStringExtra("StoreName"));
        editor.putString("Location", getIntent().getStringExtra("Location"));
        editor.putString("ContactName", getIntent().getStringExtra("ContactName"));
        editor.putString("ContactNumber", getIntent().getStringExtra("ContactNumber"));
        editor.putString("Date", getIntent().getStringExtra("Date"));
        editor.putString("StoreCode", getIntent().getStringExtra("StoreCode"));
        editor.putString("Label", getIntent().getStringExtra("Label"));
        editor.putString("assigned_store_id",getIntent().getStringExtra("assigned_store_id"));
        editor.putString("deviation",getIntent().getStringExtra("deviation"));
        editor.putString("Id",getIntent().getStringExtra("Id"));
        editor.commit();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new FeedbackFragment()).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Feedback");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
