package com.kestone.kestoneretail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.DatabasePackage.Reporting;
import com.kestone.kestoneretail.RecyclerAdapter.OrderRecyclerAdapter;

import java.util.List;

public class OrderDetails extends AppCompatActivity {
    List<Reporting> reportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences("StoreDetails",MODE_PRIVATE);



        if (getIntent().getStringExtra("Label").equals("Merchant")) {

            DatabaseHandler db = new DatabaseHandler(OrderDetails.this, "StockDb");
             reportList = db.getAllBooks(getIntent().getStringExtra("Author"),Integer.parseInt(sharedPreferences.getString("Id","")));
        }else if (getIntent().getStringExtra("Label").equals("Sales")) {

            DatabaseHandler db = new DatabaseHandler(OrderDetails.this, "ReportingDb");
            reportList = db.getAllBooks(getIntent().getStringExtra("Author"),Integer.parseInt(sharedPreferences.getString("Id","")));
        }

        Log.d("reportList", reportList.toString());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new OrderRecyclerAdapter(OrderDetails.this, reportList, getIntent().getStringExtra("Label")));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();

        SharedPreferences sharedpreferences = getSharedPreferences("StoreDetails", Context.MODE_PRIVATE);
        Intent intent;


        if (getIntent().getStringExtra("Label").equals("Merchant")) {

            intent = new Intent(OrderDetails.this,MerchantStock.class);
            intent.putExtra("StoreType",sharedpreferences.getString("StoreType",""));
            intent.putExtra("StoreName",sharedpreferences.getString("StoreName",""));
            intent.putExtra("Location",sharedpreferences.getString("Location",""));
            intent.putExtra("ContactName",sharedpreferences.getString("ContactName",""));
            intent.putExtra("ContactNumber",sharedpreferences.getString("ContactNumber",""));
            intent.putExtra("Date",sharedpreferences.getString("Date",""));
            intent.putExtra("StoreCode",sharedpreferences.getString("StoreCode",""));
            intent.putExtra("Label",sharedpreferences.getString("Label",""));
            intent.putExtra("Id",sharedpreferences.getString("Id",""));
            intent.putExtra("assigned_store_id",sharedpreferences.getString("assigned_store_id",""));
            startActivity(intent);
            finish();
        }else if (getIntent().getStringExtra("Label").equals("Sales")) {

            intent = new Intent(OrderDetails.this,FillDetailsActivity.class);
            intent.putExtra("StoreType",sharedpreferences.getString("StoreType",""));
            intent.putExtra("StoreName",sharedpreferences.getString("StoreName",""));
            intent.putExtra("Location",sharedpreferences.getString("Location",""));
            intent.putExtra("ContactName",sharedpreferences.getString("ContactName",""));
            intent.putExtra("ContactNumber",sharedpreferences.getString("ContactNumber",""));
            intent.putExtra("Date",sharedpreferences.getString("Date",""));
            intent.putExtra("StoreCode",sharedpreferences.getString("StoreCode",""));
            intent.putExtra("Label",sharedpreferences.getString("Label",""));
            intent.putExtra("Id",sharedpreferences.getString("Id",""));
            intent.putExtra("assigned_store_id",sharedpreferences.getString("assigned_store_id",""));
            startActivity(intent);
            finish();
        }



    }
}
