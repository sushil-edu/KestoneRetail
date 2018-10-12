package com.kestone.kestoneretail;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.DatabasePackage.Reporting;
import com.kestone.kestoneretail.RecyclerAdapter.OrderRecyclerAdapter;
import com.kestone.kestoneretail.RecyclerAdapter.StockOrderAdapter;

import java.util.List;

public class StockDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences("StoreDetails",MODE_PRIVATE);


        DatabaseHandler db = new DatabaseHandler(StockDetails.this,"StockDb");
        List<Reporting> reportList = db.getAllBooks(getIntent().getStringExtra("Author"),Integer.parseInt(sharedPreferences.getString("Id","")));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new StockOrderAdapter(StockDetails.this,reportList));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
