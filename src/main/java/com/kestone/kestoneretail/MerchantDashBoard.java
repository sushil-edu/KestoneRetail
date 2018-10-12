package com.kestone.kestoneretail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kestone.kestoneretail.ApiDetails.ApiListeners.ApiListener;
import com.kestone.kestoneretail.ApiDetails.ApiListeners.ApiResponse;
import com.kestone.kestoneretail.ApiDetails.ApiUrls.ApiUrl;
import com.kestone.kestoneretail.DataHolders.PjpData;
import com.kestone.kestoneretail.DataHolders.PopUp;
import com.kestone.kestoneretail.DataHolders.UserDetails;
import com.kestone.kestoneretail.DatabasePackage.DBHelpher;
import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.DatabasePackage.Reporting;
import com.kestone.kestoneretail.RecyclerAdapter.PjpMAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MerchantDashBoard extends AppCompatActivity implements ApiResponse, View.OnClickListener {

    ArrayList<PjpData> pjpDataList = new ArrayList<>();
    LinearLayout deviationLin;
    SharedPreferences sharedPreferences, sharedPreferencesPssReport;
    String formattedDate;
    String sales_pjp_id;
    PjpMAdapter pjpMAdapter;
    String date;
    JSONArray jsonArray;
    DataOutputStream printout;
    List<Reporting> reportingList;
    DatabaseHandler db;
    DBHelpher dbh = new DBHelpher( MerchantDashBoard.this, "PopUpDbM" );
    JSONObject jsonObject;
    Intent intent;
    LinearLayout layout_report, layout_upload, layout_profile;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_dash_board);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pjpMAdapter = new PjpMAdapter(MerchantDashBoard.this, pjpDataList);
        recyclerView.setAdapter(pjpMAdapter);
//        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
//        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        layout_report = (LinearLayout)findViewById(R.id.layout_report);
        layout_report.setOnClickListener(this);
        layout_upload= (LinearLayout)findViewById(R.id.layout_upload);
        layout_upload.setOnClickListener(this);
        layout_profile= (LinearLayout)findViewById(R.id.layout_profile);
        layout_profile.setOnClickListener(this);

        getSupportActionBar().setTitle("Today's PJP");

        sharedPreferences = getSharedPreferences("PJPMData", MODE_PRIVATE);
        sharedPreferencesPssReport = getSharedPreferences( "PSSReporting", MODE_PRIVATE );
        if(!sharedPreferencesPssReport.contains( "PssReport")){
            new PssReporting().execute(  );
        }

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        int month = today.month + 1;
        String mo;
        if (month < 10) {
            mo = "0" + month;
        } else mo = month + "";


        int day = today.monthDay;
        String dayStr;

        if (day < 10) {
            dayStr = "0" + day;
        } else
            dayStr = day + "";

        formattedDate = today.year + "-" + mo + "-" + dayStr;
//        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MerchantDashBoard.this, MerchantDeviationActivity.class);
//                intent.putExtra("Type", "Merchant");
//                intent.putExtra("sales_pjp_id", sales_pjp_id);
//                startActivity(intent);
//            }
//        });
//
//        deviationLin = (LinearLayout) findViewById(R.id.deviationLin);
//        deviationLin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MerchantDashBoard.this, MerchantDeviationActivity.class);
//                intent.putExtra("Type", "Merchant");
//                intent.putExtra("sales_pjp_id", sales_pjp_id);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        layout_report.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        layout_upload.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        layout_profile.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        SharedPreferences sharedPreference1 = getSharedPreferences("PJPMData", MODE_PRIVATE);
        String response = sharedPreference1.getString("PJPMREsponse", "");

        if (response.length() > 0) {


            try {

                if (pjpDataList.size() > 0)
                    pjpDataList.clear();


                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    if (jsonObject1.getString("PJPDate").equals(formattedDate) &&
                            UserDetails.getUName().equals(jsonObject1.getString("EmailID"))) {

                        PjpData pjpData = new PjpData();

//                            pjpData.setId(jsonObject1.getString("id"));
//                            pjpData.setStore_code(jsonObject1.getString("store_code"));
//                            pjpData.setStore_name(jsonObject1.getString("store_name"));
//                            pjpData.setStore_formate(jsonObject1.getString("store_formate"));
//                            pjpData.setContact_person(jsonObject1.getString("contact_person"));
//                            pjpData.setContat_no(jsonObject1.getString("contat_no"));
//                            pjpData.setFull_address(jsonObject1.getString("full_address"));
//                            pjpData.setLocation(jsonObject1.getString("location"));
//                            pjpData.setPincode(jsonObject1.getString("pincode"));
//                            pjpData.setState(jsonObject1.getString("state"));
//                            pjpData.setCity_name(jsonObject1.getString("city_name"));
//                            pjpData.setStore_longitude(jsonObject1.getString("store_longitude"));
//                            pjpData.setStore_latitude(jsonObject1.getString("store_latitude"));
//                            pjpData.setPjp_date(jsonObject1.getString("pjp_date"));
//                            pjpData.setPre_diployment(jsonObject1.getString("pre_diployment"));
//                            pjpData.setPost_diployment(jsonObject1.getString("post_diployment"));
//                            pjpData.setMerchant_stock(jsonObject1.getString("stock_completed"));
//                            sales_pjp_id = jsonObject1.getString("merch_pjp_id");
//                            pjpData.setDeviation(jsonObject1.getString("deviation"));
//                            pjpData.setAssigned_store_id(jsonObject1.getString("assigned_store_id"));
//                            pjpData.setCheckout(jsonObject1.getString("Checkout"));
//                            pjpData.setCheckin(jsonObject1.getString("Checkin"));
                        pjpData.setStoreID(jsonObject1.getString("StoreID"));
                        pjpData.setStoreName(jsonObject1.getString("StoreName"));
                        pjpData.setStoreCode(jsonObject1.getString("StoreCode"));
                        pjpData.setPJPDate(jsonObject1.getString("PJPDate"));
                        pjpData.setStoreCompleteness(jsonObject1.getString("StoreCompleteness"));
                        pjpData.setIsDeviation(jsonObject1.getString("IsDeviation"));
                        pjpData.setEmailID(jsonObject1.getString("EmailID"));
                        pjpData.setPJPID(jsonObject1.getString("PJPID"));
                        pjpData.setPreDeployment(jsonObject1.getString("PreDeployment"));
                        pjpData.setPostDeloyment(jsonObject1.getString("PostDeloyment"));
                        pjpData.setAttendence(jsonObject1.getString("Attendence"));
                        pjpData.setStore_longitude(jsonObject1.getString("StoreLongitude"));
                        pjpData.setStore_latitude(jsonObject1.getString("StoreLatitude"));
                        pjpData.setStore_category( jsonObject1.getString( "StoreCategory" ) );

                        sales_pjp_id = jsonObject1.getString("PJPID");

                        Log.d("Pjp", i + "cat "+jsonObject1.getString( "StoreCategory" ));

                        pjpDataList.add(pjpData);
                    }

                }
                pjpMAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_signout) {
//
//            SharedPreferences sharedPreferences1 = getSharedPreferences("SignInCredentials", MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences1.edit();
//            editor.putString("Name", "");
//            editor.putString("Password", "");
//            editor.apply();
//
//            Intent intent = new Intent(MerchantDashBoard.this, SignInActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//            return true;
//        } else
        if (id == R.id.action_syncPJP) {

            JSONObject jsonObject = new JSONObject();
            try {
                Time today = new Time(Time.getCurrentTimezone());
                today.setToNow();
                Log.d("Date", today.monthDay + "-" + today.month + "-" + today.year);

                int month = today.month + 1;
                String mo;
                if (month < 10) {
                    mo = "0" + month;
                } else mo = month + "";

                int day = today.monthDay;
                String dayStr;
                if (day < 10) {
                    dayStr = "0" + day;
                } else dayStr = day + "";


                date = today.year + "-" + mo + "-" + dayStr;

                jsonObject.put("EmailID", getIntent().getStringExtra("email"));
                jsonObject.put("PJPDate", date);

                if (new ConnectionStatus(MerchantDashBoard.this).isNetworkAvailable()) {

                    Progress.showProgress(MerchantDashBoard.this);
                    new ApiListener(MerchantDashBoard.this, ApiUrl.MerchandiserPJP, jsonObject).execute();
                } else {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Progress.closeProgress();
            }
        }
// else if (id == R.id.report) {
//
//            Intent intent;
//            intent = new Intent(MerchantDashBoard.this, ReportActivity.class);
//            intent.putExtra("Date", formattedDate);
//            startActivity(intent);
//
//        } else if (id == R.id.upload) {
//
//            db = new DatabaseHandler(MerchantDashBoard.this, "StockDb");
//            reportingList = db.getAllContacts();
//
//            if (reportingList.size() > 0) {
//
//                jsonArray = new JSONArray();
//
//                Progress.showProgress(MerchantDashBoard.this);
//
//                for (int i = 0; i < reportingList.size(); i++) {
//                    Reporting reporting = reportingList.get(i);
//                    JSONObject jsonObject = new JSONObject();
//
//                    try {
//
//                        jsonObject.put("RefUserID", UserDetails.getUName());
//                        jsonObject.put("RefStoreID", reporting.getStoreId());
//                        jsonObject.put("RefBookID", reporting.getBookId());
//                        jsonObject.put("StockQty", reporting.getStock());
//                        jsonObject.put("SalesQty", "0");
//                        jsonObject.put("OrderQty", "0");
//                        jsonObject.put("PJPDate", reporting.getDate());
//                        jsonObject.put("RefDistributorId", "0");
//
//                        jsonArray.put(jsonObject);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Progress.closeProgress();
//                    }
//
//                    if (new ConnectionStatus(MerchantDashBoard.this).isNetworkAvailable()) {
//                        new FinalMerchantUpload().execute();
//                    } else {
//                        Progress.closeProgress();
//                        Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            } else Toast.makeText(this, "No Data to Upload", Toast.LENGTH_SHORT).show();
//
//        }
        else onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onApiResponse(String response) {
        Log.d("PJP response", response);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PJPMREsponse", response);
        editor.commit();

        Progress.closeProgress();
        try {
            if (pjpDataList.size() > 0) {
                pjpDataList.clear();
            }

            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                if (jsonObject1.getString("PJPDate").equals(formattedDate)) {

                    PjpData pjpData = new PjpData();

//                        pjpData.setId(jsonObject1.getString("id"));
//                        pjpData.setStore_code(jsonObject1.getString("store_code"));
//                        pjpData.setStore_name(jsonObject1.getString("store_name"));
//                        pjpData.setStore_formate(jsonObject1.getString("store_formate"));
//                        pjpData.setContact_person(jsonObject1.getString("contact_person"));
//                        pjpData.setContat_no(jsonObject1.getString("contat_no"));
//                        pjpData.setFull_address(jsonObject1.getString("full_address"));
//                        pjpData.setLocation(jsonObject1.getString("location"));
//                        pjpData.setPincode(jsonObject1.getString("pincode"));
//                        pjpData.setState(jsonObject1.getString("state"));
//                        pjpData.setCity_name(jsonObject1.getString("city_name"));
//                        pjpData.setStore_longitude(jsonObject1.getString("store_longitude"));
//                        pjpData.setStore_latitude(jsonObject1.getString("store_latitude"));
//                        pjpData.setPjp_date(jsonObject1.getString("pjp_date"));
//                        pjpData.setPre_diployment(jsonObject1.getString("pre_diployment"));
//                        pjpData.setPost_diployment(jsonObject1.getString("post_diployment"));
//                        sales_pjp_id = jsonObject1.getString("merch_pjp_id");
//                        pjpData.setDeviation(jsonObject1.getString("deviation"));
//                        pjpData.setMerchant_stock(jsonObject1.getString("stock_completed"));
//                        pjpData.setAssigned_store_id(jsonObject1.getString("assigned_store_id"));
//                        pjpData.setCheckout(jsonObject1.getString("Checkout"));
//                        pjpData.setCheckin(jsonObject1.getString("Checkin"));
                    pjpData.setStoreID(jsonObject1.getString("StoreID"));
                    pjpData.setStoreName(jsonObject1.getString("StoreName"));
                    pjpData.setStoreCode(jsonObject1.getString("StoreCode"));
                    pjpData.setPJPDate(jsonObject1.getString("PJPDate"));
                    pjpData.setStoreCompleteness(jsonObject1.getString("StoreCompleteness"));
                    pjpData.setIsDeviation(jsonObject1.getString("IsDeviation"));
                    pjpData.setEmailID(jsonObject1.getString("EmailID"));
                    pjpData.setPJPID(jsonObject1.getString("PJPID"));
                    pjpData.setPreDeployment(jsonObject1.getString("PreDeployment"));
                    pjpData.setPostDeloyment(jsonObject1.getString("PostDeloyment"));
                    pjpData.setAttendence(jsonObject1.getString("Attendence"));
                    pjpData.setStore_longitude(jsonObject1.getString("StoreLongitude"));
                    pjpData.setStore_latitude(jsonObject1.getString("StoreLatitude"));
                    pjpData.setStore_category(jsonObject1.getString("StoreCategory"));

                    sales_pjp_id = jsonObject1.getString("PJPID");

                    Log.d("Pjp", i + "");

                    pjpDataList.add(pjpData);
                }


                pjpMAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_report:
                layout_report.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                layout_upload.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                layout_profile.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                intent = new Intent(MerchantDashBoard.this, ReportActivity.class);
                intent.putExtra("Date", formattedDate);
                startActivity(intent);

                break;

            case R.id.layout_upload:

                layout_report.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                layout_upload.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                layout_profile.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                ArrayList<PopUp> lst = new ArrayList<>();
                lst.addAll( dbh.getAllInfo() );
                Log.e( "count ", "" + lst.size() );
                if (lst.size() > 0) {
                    JSONArray aryPopup = new JSONArray();
                    for (int i = 0; i < lst.size(); i++) {
                        jsonObject = new JSONObject();
                        try {
                            jsonObject.put( "RefUserID", lst.get( i ).getRefUserID() );
                            jsonObject.put( "RefStoreID", lst.get( i ).getRefStoreID() );
                            jsonObject.put( "Backlist", lst.get( i ).getBacklist() );
                            jsonObject.put( "NewArrival", lst.get( i ).getNewArrival() );
                            jsonObject.put( "FaceUp", lst.get( i ).getFaceUp() );
                            jsonObject.put( "StoreFaceList", lst.get( i ).getStoreFaceList() );
                            jsonObject.put( "PJPDate", lst.get( i ).getPJPDate() );

                            Log.e( "Info ", jsonObject.toString() );
                            aryPopup.put( jsonObject );
                            new AddtionalInfo( aryPopup ).execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }


                db = new DatabaseHandler(MerchantDashBoard.this, "StockDb");
                reportingList = db.getAllContacts();

                if (reportingList.size() > 0) {

                    jsonArray = new JSONArray();

                    Progress.showProgress(MerchantDashBoard.this);

                    for (int i = 0; i < reportingList.size(); i++) {
                        Reporting reporting = reportingList.get(i);
                        JSONObject jsonObject = new JSONObject();

                        try {

                            jsonObject.put("RefUserID", UserDetails.getUName());
                            jsonObject.put("RefStoreID", reporting.getStoreId());
                            jsonObject.put("RefBookID", reporting.getBookId());
                            jsonObject.put("StockQty", reporting.getStock());
                            jsonObject.put("SalesQty", "0");
                            jsonObject.put("OrderQty", "0");
                            jsonObject.put("PJPDate", reporting.getDate());
                            jsonObject.put("RefDistributorId", "0");

                            jsonArray.put(jsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Progress.closeProgress();
                        }

                        if (new ConnectionStatus(MerchantDashBoard.this).isNetworkAvailable()) {
                            new FinalMerchantUpload().execute();
                        } else {
                            Progress.closeProgress();
                            Toast.makeText(MerchantDashBoard.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
//                else
//                    Toast.makeText(MerchantDashBoard.this, "No Data to Upload", Toast.LENGTH_SHORT).show();

                break;

            case R.id.layout_deviation:
                layout_report.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                layout_upload.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                layout_profile.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                Intent intent = new Intent(MerchantDashBoard.this, MerchantDeviationActivity.class);
                intent.putExtra("Type", "Merchant");
                intent.putExtra("sales_pjp_id", sales_pjp_id);
                startActivity(intent);
                break;
            case R.id.layout_profile:
                layout_report.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                layout_upload.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                layout_profile.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                showAlert();

//                intent = new Intent(MerchantDashBoard.this, ProfileActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);

                break;
        }

    }

    class FinalMerchantUpload extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.Retail;

                URL url = new URL(Url);
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e("Post", "Post");
                Log.e("URL", Url);
                Log.e("Params", jsonArray.toString());
                htp.setDoOutput(true);
                // is output buffer writter
                htp.setRequestMethod("POST");
                htp.setRequestProperty("Content-Type", "application/json");
                htp.setRequestProperty("Accept", "application/json");
                printout = new DataOutputStream(htp.getOutputStream());
                printout.writeBytes(jsonArray.toString());
                printout.flush();
                printout.close();


                InputStream inputStream = htp.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String Line;
                while ((Line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(Line);
                }
                return stringBuilder.toString();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return "null";
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsonObject = new JSONObject(s);

                if (jsonObject.getString("retval").equalsIgnoreCase("Registration Successfull.")) {

                    Toast.makeText(MerchantDashBoard.this, "Successfully Submitted", Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < reportingList.size(); i++) {
                        Reporting contact = reportingList.get(i);
                        db.deleteContact(contact);
                    }

                    Progress.closeProgress();

                    Toast.makeText(MerchantDashBoard.this, "Data Uploaded", Toast.LENGTH_SHORT).show();

                    layout_report.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    layout_upload.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    layout_profile.setBackgroundColor(getResources().getColor(R.color.colorPrimary));


                } else {
                    Toast.makeText(MerchantDashBoard.this, "There was some Problem", Toast.LENGTH_SHORT).show();
                    Progress.closeProgress();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Progress.closeProgress();
            }


            Log.d("Collection Response", s);
        }
    }
    class AddtionalInfo extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;
        JSONArray ary;

        public AddtionalInfo(JSONArray lst) {
            this.ary = lst;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress.showProgress(FillDetailsActivity.this);

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.AdditionalInfo;

                URL url = new URL( Url );
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e( "Post", "Post" );
                Log.e( "URL", Url );
                Log.e( "Params", ary.toString() );
                htp.setRequestMethod( "POST" );
                htp.setRequestProperty( "Content-Type", "application/json" );
                htp.setDoInput( true );
                htp.setDoOutput( true );
                htp.setUseCaches( false );
                htp.connect();
                printout = new DataOutputStream( htp.getOutputStream() );
                printout.writeBytes( ary.toString() );
                printout.flush();
                printout.close();


                InputStream inputStream = htp.getInputStream();
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );
                String Line;
                while ((Line = bufferedReader.readLine()) != null) {
                    stringBuilder.append( Line );
                }
                return stringBuilder.toString();
            } catch (Exception e) {
                Log.e( "Error", e.getMessage() );
            }
            return "null";
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d( "Additional info ", s );
            try {
                JSONObject object = new JSONObject( s );
                if (object.getString( "retval" ).contains( "Registration Successfull." )) {
                    dbh.deleteAll( DBHelpher.TABLE_POPUP );
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            Progress.closeProgress();
//
//
//            SharedPreferences sharedPreferences1 = getSharedPreferences( "Distributor", MODE_PRIVATE );
//            SharedPreferences.Editor editor = sharedPreferences1.edit();
//            editor.putString( "DistributorList", s );
//            editor.apply();

        }
    }

    class PssReporting extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.PssReporting;

                URL url = new URL(Url);
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e("Get", "Get");
                Log.e("URL", Url);
                htp.setRequestMethod("GET");
                htp.setRequestProperty("Content-Type", "application/json");
                htp.setDoInput(true);
                htp.setUseCaches(false);
                htp.connect();


                InputStream inputStream = htp.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String Line;
                while ((Line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(Line);
                }
                return stringBuilder.toString();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return "null";
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("Response ", s);
            SharedPreferences.Editor editor = sharedPreferencesPssReport.edit();
            editor.putString("PssReport", s);
            editor.apply();

        }
    }

    public void showAlert(){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MerchantDashBoard.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MerchantDashBoard.this);
        }
        builder.setTitle("")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences1 = getSharedPreferences("SignInCredentials", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences1.edit();
                        editor.putString("Name", "");
                        editor.putString("Password", "");
                        editor.apply();

                        Intent in = new Intent(MerchantDashBoard.this, SignInActivity.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(in);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
