package com.kestone.kestoneretail;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.kestone.kestoneretail.ApiDetails.ApiListeners.ApiListener;
import com.kestone.kestoneretail.ApiDetails.ApiListeners.ApiResponse;
import com.kestone.kestoneretail.ApiDetails.ApiUrls.ApiUrl;
import com.kestone.kestoneretail.DataHolders.ReportData;
import com.kestone.kestoneretail.DataHolders.UserDetails;
import com.kestone.kestoneretail.RecyclerAdapter.ReportAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity implements ApiResponse {

    private ArrayList<ReportData> reportList = new ArrayList();
    private ReportAdapter reportAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        getSupportActionBar().setTitle("Daily Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(ReportActivity.this,reportList);
        recyclerView.setAdapter(reportAdapter);

        Progress.showProgress(ReportActivity.this);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("PJPDate", getIntent().getStringExtra("Date"));
            jsonObject.put("EmailID", UserDetails.getUName());

            Log.d("params", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            Progress.closeProgress();
        }


        new ApiListener(ReportActivity.this, ApiUrl.DailyReport, jsonObject).execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onApiResponse(String response) {
        Progress.closeProgress();
        Log.d("response", response);
        if (response.length() > 0) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {

                        ReportData reportData = new ReportData();

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        reportData.setBook(jsonObject.getString("Book"));
                        reportData.setOrderQty(jsonObject.getString("OrderQty"));
                        reportData.setSalesQty(jsonObject.getString("SalesQty"));
                        reportData.setStockQty(jsonObject.getString("StockQty"));
                        reportData.setStore(jsonObject.getString("Store"));
                        reportList.add(reportData);

                    }
                    reportAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else Toast.makeText(this, "No Data for today", Toast.LENGTH_SHORT).show();
    }
}
