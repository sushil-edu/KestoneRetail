package com.kestone.kestoneretail;

import android.app.Activity;
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

import com.kestone.kestoneretail.ApiDetails.ApiListeners.ApiResponse;
import com.kestone.kestoneretail.ApiDetails.ApiUrls.ApiUrl;
import com.kestone.kestoneretail.DataHolders.PjpData;
import com.kestone.kestoneretail.DataHolders.PopUp;
import com.kestone.kestoneretail.DataHolders.UserDetails;
import com.kestone.kestoneretail.DatabasePackage.DBHelpher;
import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.DatabasePackage.Reporting;
import com.kestone.kestoneretail.RecyclerAdapter.PjpRecyclerAdapter;

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

public class DashBoard extends AppCompatActivity implements View.OnClickListener {

    ArrayList<PjpData> pjpDataList = new ArrayList<>();
    LinearLayout deviationLin;
    String sales_pjp_id;
    SharedPreferences sharedPreferences, sharedPreferencesPssReport;
    String formattedDate;
    PjpRecyclerAdapter pjpRecyclerAdapter;
    String date;
    DatabaseHandler db;
    Intent intent;
    DataOutputStream printout;
    LinearLayout layout_report, layout_upload, layout_deviation, layout_profile;
    DBHelpher dbh = new DBHelpher( DashBoard.this, "PopUpDb" );
    JSONObject jsonObject;


//    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dash_board );

        RecyclerView recyclerView = (RecyclerView) findViewById( R.id.recyclerView );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        pjpRecyclerAdapter = new PjpRecyclerAdapter( DashBoard.this, pjpDataList );
        recyclerView.setAdapter( pjpRecyclerAdapter );

//        bottomNavigationView= (BottomNavigationView) findViewById(R.id.navigation);
//        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        layout_report = (LinearLayout) findViewById( R.id.layout_report );
        layout_report.setOnClickListener( this );
        layout_upload = (LinearLayout) findViewById( R.id.layout_upload );
        layout_upload.setOnClickListener( this );
        layout_deviation = (LinearLayout) findViewById( R.id.layout_deviation );
        layout_deviation.setOnClickListener( this );
        layout_profile = (LinearLayout) findViewById( R.id.layout_profile );
        layout_profile.setOnClickListener( this );

        getSupportActionBar().setTitle( "Today's PJP" );

        sharedPreferences = getSharedPreferences( "PJPData", MODE_PRIVATE );
        sharedPreferencesPssReport = getSharedPreferences( "PSSReporting", MODE_PRIVATE );

        if(!sharedPreferencesPssReport.contains( "PssReport")){
            new PssReporting().execute(  );
        }

        Time today = new Time( Time.getCurrentTimezone() );
        today.setToNow();
        Log.d( "Date", today.monthDay + "-" + today.month + "-" + today.year );

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
//                Intent intent = new Intent(DashBoard.this, DeviationActivity.class);
//                intent.putExtra("sales_pjp_id", sales_pjp_id);
//                intent.putExtra("Type", "Sales");
//                startActivity(intent);
//
//            }
//        });

//        deviationLin = (LinearLayout) findViewById(R.id.deviationLin);
//        deviationLin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(DashBoard.this, DeviationActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        layout_report.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );
        layout_upload.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );
        layout_deviation.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );
        layout_profile.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );

        SharedPreferences sharedPreferences1 = getSharedPreferences( "PJPData", MODE_PRIVATE );
        String response = sharedPreferences1.getString( "PJPREsponse", "" );
        Log.e( "Response ", response );

        if (response.length() > 0) {

            try {
                JSONArray jsonArray = new JSONArray( response );
                if (pjpDataList.size() > 0)
                    pjpDataList.clear();


                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject1 = jsonArray.getJSONObject( i );
                    String user_email = jsonObject1.getString( "EmailID" );

                    if (user_email.equals( UserDetails.getUName() )) {

                        if (jsonObject1.getString( "PJPDate" ).equals( formattedDate )) {

                            PjpData pjpData = new PjpData();

//                                pjpData.setId(jsonObject1.getString("id"));
//                                pjpData.setStore_code(jsonObject1.getString("store_code"));
//                                pjpData.setStore_name(jsonObject1.getString("store_name"));
//                                pjpData.setStore_formate(jsonObject1.getString("store_formate"));
//                                pjpData.setContact_person(jsonObject1.getString("contact_person"));
//                                pjpData.setContat_no(jsonObject1.getString("contat_no"));
//                                pjpData.setFull_address(jsonObject1.getString("full_address"));
//                                pjpData.setLocation(jsonObject1.getString("location"));
//                                pjpData.setPincode(jsonObject1.getString("pincode"));
//                                pjpData.setState(jsonObject1.getString("state"));
//                                pjpData.setCity_name(jsonObject1.getString("city_name"));
//                                pjpData.setStore_longitude(jsonObject1.getString("store_longitude"));
//                                pjpData.setStore_latitude(jsonObject1.getString("store_latitude"));
//                                pjpData.setPjp_date(jsonObject1.getString("pjp_date"));
//                                pjpData.setDeviation(jsonObject1.getString("deviation"));
//                                pjpData.setAssigned_store_id(jsonObject1.getString("assigned_store_id"));
//                                pjpData.setOrder_completed(jsonObject1.getString("order_completed"));
//                                pjpData.setSales_completed(jsonObject1.getString("sales_completed"));
//                                pjpData.setStock_completed(jsonObject1.getString("stock_completed"));
//                                pjpData.setCollection_completed(jsonObject1.getString("collection_completed"));
//                                pjpData.setCheckout(jsonObject1.getString("Checkout"));
//                                pjpData.setCheckin(jsonObject1.getString("Checkin"));

                            pjpData.setStoreID( jsonObject1.getString( "StoreID" ) );
                            pjpData.setStoreName( jsonObject1.getString( "StoreName" ) );
                            pjpData.setStoreCode( jsonObject1.getString( "StoreCode" ) );
                            pjpData.setPJPDate( jsonObject1.getString( "PJPDate" ) );
                            pjpData.setStoreCompleteness( jsonObject1.getString( "StoreCompleteness" ) );
                            pjpData.setIsDeviation( jsonObject1.getString( "IsDeviation" ) );
                            pjpData.setEmailID( jsonObject1.getString( "EmailID" ) );
                            pjpData.setPJPID( jsonObject1.getString( "PJPID" ) );
                            pjpData.setAttendence( jsonObject1.getString( "Attendence" ) );
                            pjpData.setStore_longitude( jsonObject1.getString( "StoreLongitude" ) );
                            pjpData.setStore_latitude( jsonObject1.getString( "StoreLatitude" ) );
                            pjpData.setStore_category( jsonObject1.getString( "StoreCategory" ) );

                            sales_pjp_id = jsonObject1.getString( "PJPID" );

                            pjpDataList.add( pjpData );

                            Log.d( "Pjp", i + "" );

                        }


                    }


                    pjpRecyclerAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {

            //syncBtn.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.dashboard, menu );
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
//            Intent intent = new Intent(DashBoard.this, SignInActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//            return true;
//        }else
        if (id == R.id.action_syncPJP) {
            JSONObject jsonObject = new JSONObject();
            try {

                Time today = new Time( Time.getCurrentTimezone() );
                today.setToNow();
                Log.d( "Date", today.monthDay + "-" + today.month + "-" + today.year );

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

                jsonObject.put( "EmailID", getIntent().getStringExtra( "email" ) );
                jsonObject.put( "PJPDate", date );

                if (new ConnectionStatus( DashBoard.this ).isNetworkAvailable()) {
                    Progress.showProgress( DashBoard.this );
                    new FetchPjp( DashBoard.this, ApiUrl.SalesPJP, jsonObject ).executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
                    new PssReporting().execute(  );
                } else {
                    Toast.makeText( this, "No Internet Connection", Toast.LENGTH_SHORT ).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Progress.closeProgress();
            }
        }
//        else if (id == R.id.report) {
//
//            Intent intent;
//            intent = new Intent(DashBoard.this, ReportActivity.class);
//            intent.putExtra("Date", formattedDate);
//            startActivity(intent);
//
//        } else if (id == R.id.upload) {
//            db = new DatabaseHandler(DashBoard.this, "ReportingDb");
//            List<Reporting> reportingList = db.getAllContacts();
//            if (reportingList.size() > 0) {
//
//                JSONArray jsonArray = new JSONArray();
//                Progress.showProgress(DashBoard.this);
//
//                for (int i = 0; i < reportingList.size(); i++) {
//
//                    Reporting reporting = reportingList.get(i);
//                    JSONObject jsonObject = new JSONObject();
//
//                    try {
//
//                        jsonObject.put("RefUserID", UserDetails.getUName());
//                        jsonObject.put("RefStoreID", reporting.getStoreId());
//                        jsonObject.put("RefBookID", reporting.getBookId());
//                        jsonObject.put("StockQty", reporting.getStock());
//                        jsonObject.put("SalesQty", reporting.getSales());
//                        jsonObject.put("OrderQty", reporting.getOrder());
//                        jsonObject.put("PJPDate", reporting.getDate());
//                        jsonObject.put("RefDistributorId", reporting.getDistributor());
//
//                        jsonArray.put(jsonObject);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Progress.closeProgress();
//                    }
//                }
//
//                if (new ConnectionStatus(DashBoard.this).isNetworkAvailable()) {
//                    new DataPost(DashBoard.this, ApiUrl.Retail, jsonArray, "array").execute();
//                }else {
//                    Progress.closeProgress();
//                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
//                }
//
//                Log.d("Json Array", jsonArray.toString());
//            } else Toast.makeText(this, "Nothing to upload", Toast.LENGTH_SHORT).show();
//        }
        else onBackPressed();
        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_report:

                layout_report.setBackgroundColor( getResources().getColor( R.color.colorPrimaryDark ) );
                layout_upload.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );
                layout_deviation.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );
                layout_profile.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );

                intent = new Intent( DashBoard.this, ReportActivity.class );
                intent.putExtra( "Date", formattedDate );
                startActivity( intent );
                break;

            case R.id.layout_upload:
                layout_report.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );
                layout_upload.setBackgroundColor( getResources().getColor( R.color.colorPrimaryDark ) );
                layout_deviation.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );
                layout_profile.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );


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
                            jsonObject.put( "stockreason", lst.get( i ).getStockReason() );
                            jsonObject.put( "orderreason", lst.get( i ).getOrderReason() );

                            Log.e( "Info ", jsonObject.toString() );
                            aryPopup.put( jsonObject );
                            new AddtionalInfo( aryPopup ).execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }


                db = new DatabaseHandler(DashBoard.this, "ReportingDb");
                List<Reporting> reportingList = db.getAllContacts();
                if (reportingList.size() > 0) {

                    JSONArray jsonArray = new JSONArray();

                    for (int i = 0; i < reportingList.size(); i++) {

                        Reporting reporting = reportingList.get(i);
                        JSONObject jsonObject = new JSONObject();

                        try {

                            jsonObject.put("RefUserID", UserDetails.getUName());
                            jsonObject.put("RefStoreID", reporting.getStoreId());
                            jsonObject.put("RefBookID", reporting.getBookId());
                            jsonObject.put("StockQty", reporting.getStock());
                            jsonObject.put("SalesQty", reporting.getSales());
                            jsonObject.put("OrderQty", reporting.getOrder());
                            jsonObject.put("PJPDate", reporting.getDate());
                            jsonObject.put("RefDistributorId", reporting.getDistributor());

                            jsonArray.put(jsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Progress.closeProgress();
                        }
                    }

                    if (new ConnectionStatus(DashBoard.this).isNetworkAvailable()) {
                        new DataPost(DashBoard.this, ApiUrl.Retail, jsonArray, "array").execute();
                        Progress.showProgress(DashBoard.this);
                    } else {
                        Progress.closeProgress();
                        Toast.makeText(DashBoard.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                    Log.d("Json Array", jsonArray.toString());
                }
//                else
//                    Toast.makeText(DashBoard.this, "Nothing to upload", Toast.LENGTH_SHORT).show();
                break;

            case R.id.layout_deviation:

                layout_report.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );
                layout_upload.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );
                layout_deviation.setBackgroundColor( getResources().getColor( R.color.colorPrimaryDark ) );
                layout_profile.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );

                intent = new Intent( DashBoard.this, DeviationActivity.class );
                intent.putExtra( "sales_pjp_id", sales_pjp_id );
                intent.putExtra( "Type", "Sales" );
                startActivity( intent );
                break;
            case R.id.layout_profile:
                layout_report.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );
                layout_upload.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );
                layout_deviation.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );
                layout_profile.setBackgroundColor( getResources().getColor( R.color.colorPrimaryDark ) );

//                intent = new Intent(DashBoard.this, ProfileActivity.class);
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);

               showAlert();
                break;
        }
    }
    //Bottom navigation menu listener
//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//            switch (item.getItemId()) {
//                case R.id.navigation_report:
//                    intent = new Intent(DashBoard.this, ReportActivity.class);
//                    intent.putExtra("Date", formattedDate);
//                    startActivity(intent);
//
//                    return true;
//                case R.id.navigation_upload:
//                    db = new DatabaseHandler(DashBoard.this, "ReportingDb");
//                    List<Reporting> reportingList = db.getAllContacts();
//                    if (reportingList.size() > 0) {
//
//                        JSONArray jsonArray = new JSONArray();
//                        Progress.showProgress(DashBoard.this);
//
//                        for (int i = 0; i < reportingList.size(); i++) {
//
//                            Reporting reporting = reportingList.get(i);
//                            JSONObject jsonObject = new JSONObject();
//
//                            try {
//
//                                jsonObject.put("RefUserID", UserDetails.getUName());
//                                jsonObject.put("RefStoreID", reporting.getStoreId());
//                                jsonObject.put("RefBookID", reporting.getBookId());
//                                jsonObject.put("StockQty", reporting.getStock());
//                                jsonObject.put("SalesQty", reporting.getSales());
//                                jsonObject.put("OrderQty", reporting.getOrder());
//                                jsonObject.put("PJPDate", reporting.getDate());
//                                jsonObject.put("RefDistributorId", reporting.getDistributor());
//
//                                jsonArray.put(jsonObject);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                Progress.closeProgress();
//                            }
//                        }
//
//                        if (new ConnectionStatus(DashBoard.this).isNetworkAvailable()) {
//                            new DataPost(DashBoard.this, ApiUrl.Retail, jsonArray, "array").execute();
//                        }else {
//                            Progress.closeProgress();
//                            Toast.makeText(DashBoard.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
//                        }
//
//                        Log.d("Json Array", jsonArray.toString());
//                    } else Toast.makeText(DashBoard.this, "Nothing to upload", Toast.LENGTH_SHORT).show();
//                    return true;
//                case R.id.navigation_deviation:
//                    intent = new Intent(DashBoard.this, DeviationActivity.class);
//                    startActivity(intent);
//                    return true;
//                case R.id.navigation_profile:
//                    Toast.makeText(DashBoard.this, "Profile", Toast.LENGTH_SHORT).show();
//                    return true;
//            }
//            return false;
//        }
//    };

    public class FetchPjp extends AsyncTask<String, String, String> {
        StringBuilder stringBuilder = new StringBuilder();
        String Url, Type = "";
        JSONObject jsonObject;
        DataOutputStream printout;
        Activity activity;
        private ApiResponse apiResponse;

        public FetchPjp(Activity activity, String Url, JSONObject jsonObject) {
//            this.apiResponse=(ApiResponse)activity;
            this.Url = Url;
            this.jsonObject = jsonObject;
            this.activity = activity;
            this.Type = "";
        }


        @Override
        protected String doInBackground(String... params) {
            try {
                //Log.e("Inside","Do in Background");
                URL url = new URL( ApiUrl.SalesPJP );
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e( "Post", "Post" );
                Log.e( "URL", Url );
                Log.e( "Params", jsonObject.toString() );
                htp.setRequestMethod( "POST" );
                htp.setRequestProperty( "Content-Type", "application/json" );
                htp.setDoInput( true );
                htp.setDoOutput( true );
                htp.setUseCaches( false );
                htp.connect();
                printout = new DataOutputStream( htp.getOutputStream() );
                printout.writeBytes( jsonObject.toString() );
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
            Log.d( "PJP response", s );

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString( "PJPREsponse", s );
            editor.commit();

            Progress.closeProgress();
            try {

                if (pjpDataList.size() > 0)
                    pjpDataList.clear();

                JSONArray jsonArray = new JSONArray( s );
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject1 = jsonArray.getJSONObject( i );

                    if (jsonObject1.getString( "PJPDate" ).equals( formattedDate )) {


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
//                            pjpData.setDeviation(jsonObject1.getString("deviation"));
//                            pjpData.setAssigned_store_id(jsonObject1.getString("assigned_store_id"));
//                            pjpData.setOrder_completed(jsonObject1.getString("order_completed"));
//                            pjpData.setSales_completed(jsonObject1.getString("sales_completed"));
//                            pjpData.setStock_completed(jsonObject1.getString("stock_completed"));
//                            pjpData.setCollection_completed(jsonObject1.getString("collection_completed"));
//                            pjpData.setCheckout(jsonObject1.getString("Checkout"));
//                            pjpData.setCheckin(jsonObject1.getString("Checkin"));
                        pjpData.setStoreID( jsonObject1.getString( "StoreID" ) );
                        pjpData.setStoreName( jsonObject1.getString( "StoreName" ) );
                        pjpData.setStoreCode( jsonObject1.getString( "StoreCode" ) );
                        pjpData.setPJPDate( jsonObject1.getString( "PJPDate" ) );
                        pjpData.setStoreCompleteness( jsonObject1.getString( "StoreCompleteness" ) );
                        pjpData.setIsDeviation( jsonObject1.getString( "IsDeviation" ) );
                        pjpData.setEmailID( jsonObject1.getString( "EmailID" ) );
                        pjpData.setPJPID( jsonObject1.getString( "PJPID" ) );
                        pjpData.setAttendence( jsonObject1.getString( "Attendence" ) );
                        pjpData.setStore_longitude( jsonObject1.getString( "StoreLongitude" ) );
                        pjpData.setStore_latitude( jsonObject1.getString( "StoreLatitude" ) );
                        pjpData.setStore_category( jsonObject1.getString( "StoreCategory" ) );

                        sales_pjp_id = jsonObject1.getString( "PJPID" );

                        pjpDataList.add( pjpData );

                        Log.d( "Pjp", i + "" );


                    }
                    pjpRecyclerAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class DataPost extends AsyncTask<String, String, String> {
        StringBuilder stringBuilder = new StringBuilder();
        String Url, Type = "";
        JSONObject jsonObject;
        JSONArray jsonArray;
        DataOutputStream printout;
        Activity activity;
        String dataType;
        private ApiResponse apiResponse;


        public DataPost(Activity activity, String Url, JSONArray jsonArray, String dataType) {
            this.Url = Url;
            this.jsonArray = jsonArray;
            this.activity = activity;
            this.Type = "";
            this.dataType = dataType;
            Log.e("Post data ", jsonArray.toString());
        }


        @Override
        protected String doInBackground(String... params) {
            try {
                //Log.e("Inside","Do in Background");
                URL url = new URL( Url );
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();
                if (dataType.equals( "array" )) {
                    Log.e( "Post", "Post" );
                    Log.e( "URL", Url );
                    Log.e( "ParamsUpload", jsonArray.toString() );
                    htp.setDoOutput( true );
                    // is output buffer writter
                    htp.setRequestMethod( "POST" );
                    htp.setRequestProperty( "Content-Type", "application/json" );
                    htp.setRequestProperty( "Accept", "application/json" );
                    printout = new DataOutputStream( htp.getOutputStream() );
                    printout.writeBytes( jsonArray.toString() );
                    printout.flush();
                    printout.close();
                }
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
            Progress.closeProgress();
            Log.e( "Response", s );

            try {
                JSONObject jsonObject = new JSONObject( s );
                if (jsonObject.getString( "retval" ).equals( "Registration Successfull." )) {

                    Toast.makeText( DashBoard.this, "Successfully Submitted", Toast.LENGTH_SHORT ).show();

                    db.deleteAll( "Report" );
                    layout_report.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );
                    layout_upload.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );
                    layout_deviation.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );
                    layout_profile.setBackgroundColor( getResources().getColor( R.color.colorPrimary ) );



                } else {
                    Toast.makeText( DashBoard.this, "There was some Problem", Toast.LENGTH_SHORT ).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
                    Toast.makeText( DashBoard.this, "Successfully Submitted", Toast.LENGTH_SHORT ).show();
                    dbh.deleteAll( DBHelpher.TABLE_POPUP );
                }
            } catch (JSONException e) {
                Log.e("Error ", e.getMessage().toString());
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
            builder = new AlertDialog.Builder(DashBoard.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(DashBoard.this);
        }
        builder.setTitle("")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences1 = getSharedPreferences( "SignInCredentials", MODE_PRIVATE );
                        SharedPreferences.Editor editor = sharedPreferences1.edit();
                        editor.putString( "Name", "" );
                        editor.putString( "Password", "" );
                        editor.apply();

                        sharedPreferencesPssReport = getSharedPreferences( "PSSReporting", MODE_PRIVATE );
                        SharedPreferences.Editor editor2 = sharedPreferencesPssReport.edit();
                        editor2.clear();
                        editor2.commit();

                        Intent intent = new Intent( DashBoard.this, SignInActivity.class );
                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        startActivity( intent );
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
