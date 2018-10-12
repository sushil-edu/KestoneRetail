package com.kestone.kestoneretail;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.kestoneretail.ApiDetails.ApiUrls.ApiUrl;
import com.kestone.kestoneretail.DataHolders.HolderFlag;
import com.kestone.kestoneretail.DataHolders.PopUp;
import com.kestone.kestoneretail.DataHolders.UserDetails;
import com.kestone.kestoneretail.DatabasePackage.DBHelpher;
import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.DatabasePackage.Reporting;
import com.kestone.kestoneretail.Interface.OnItemClick;
import com.kestone.kestoneretail.RecyclerAdapter.MyAdapterMerchant;

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
import java.util.HashSet;
import java.util.List;

public class MerchantActivity extends AppCompatActivity implements View.OnClickListener, OnItemClick {

    //    ArrayList<RowItem> rowItems;
    public static final int REQUEST_CODE = 1;
    public static String Label;
    View view1, view2, view3;
    //    private SharedPreferences sharedPreferences1;
//
//    RecyclerView recyclerView;
//    MyAdapter adapter;
//    String [] list = {"Attendance", "Reporting Stock take", "Order punching","Total SKU in Store BL","New arrival in store","Feedback"};
//    Integer [] icon_list = {R.drawable.attendence, R.drawable.merchandising,R.drawable.merchandising,
//            R.drawable.merchandising,R.drawable.merchandising,R.drawable.feedback};
    RecyclerView recyclerView;
    MyAdapterMerchant adapter;
    DataOutputStream printout;
    String backList = "", newArrival = "", faceUp = "", facelist = "", storeID = null, storeCode = null, pjpID = null;
    DBHelpher dbh = new DBHelpher( MerchantActivity.this, "PopUpDbM" );
    String[] list = {
            "Attendance",
            "Pre Deployment",
            "Post Deployment",
            "Stock Report",
            "New Arrival For The Month",
            "Total SKU",
            "Face Up",
            "Checkout",
            "Feedback"};
    //            "Attendance", "Reporting Stock take", "Order punching","Total SKU in Store BL","New arrival in store","Feedback"};
    Integer[] icon_list = {R.drawable.attendence, R.drawable.merchandising, R.drawable.merchandising, R.drawable.merchandising,
            R.drawable.merchandising, R.drawable.merchandising, R.drawable.merchandising, R.drawable.attendence, R.drawable.feedback};
    ArrayList<RowItem> rowItems;
    ArrayList<HolderFlag> listFlag = new ArrayList<>();
    HashSet<Integer> hs = new HashSet<>();
    JSONObject jsonObject;
    private LinearLayout attendanceLin, stockLin, postLin, merchantLin, reportLin, feedbackLin;
    private SharedPreferences sharedPreferences1, preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_merchant );

        getSupportActionBar().setTitle( "Dashboard" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        SharedPreferences sharedPreferences = getSharedPreferences( "PjpDataMerchant", MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString( "PJPId", getIntent().getStringExtra( "Id" ) );

        Log.d( "PjpStoredId", getIntent().getStringExtra( "Id" ) + "" );

        sharedPreferences1 = getSharedPreferences( "GenreData", MODE_PRIVATE );

        preferences = getSharedPreferences( "PSSReporting", MODE_PRIVATE );
        try {
//            jsonArray = new JSONArray(  preferences.getString( "PssReport" ,""));
            jsonObject = new JSONArray( preferences.getString( "PssReport", "" ) ).getJSONObject( 0 );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        initializeLayout();


//        ArrayList<PopUp> lst =new ArrayList<>(  );
//        lst.addAll( dbh.getAllInfo());
//        for(int i =0;i<lst.size();i++){
//            Log.e("Data ", lst.get( i ).getRefUserID()+"-"+lst.get( i ).getPJPDate()+
//                    "-"+lst.get( i ).getBacklist()+"-"+lst.get( i ).getFaceUp()+"-"+lst.get( i ).getNewArrival()
//                    +"-"+lst.get( i ).getStoreFaceList());
//        }
    }

    private void initializeLayout() {
        rowItems = new ArrayList<>();
        for (int i = 0; i < icon_list.length; i++) {
            RowItem item = new RowItem( list[i], icon_list[i] );
            rowItems.add( item );
            HolderFlag hf = new HolderFlag();
            hf.setTitle( list[i] );
            hf.setFlag( false );
            listFlag.add( hf );
        }
        recyclerView = (RecyclerView) findViewById( R.id.recyclerView );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        storeID = getIntent().getStringExtra( "assigned_store_id" );
        storeCode = getIntent().getStringExtra( "StoreCode" );
        pjpID = getIntent().getStringExtra( "Id" );
        adapter = new MyAdapterMerchant( MerchantActivity.this, rowItems, listFlag, pjpID, storeID, storeCode, this );
        recyclerView.setAdapter( adapter );


        attendanceLin = (LinearLayout) findViewById( R.id.attendanceLin );
        attendanceLin.setOnClickListener( this );
        stockLin = (LinearLayout) findViewById( R.id.stockLin );
        stockLin.setOnClickListener( this );
        postLin = (LinearLayout) findViewById( R.id.postLin );
        postLin.setOnClickListener( this );
        merchantLin = (LinearLayout) findViewById( R.id.merchantLin );
        merchantLin.setOnClickListener( this );
        reportLin = (LinearLayout) findViewById( R.id.reportLin );
        reportLin.setOnClickListener( this );
        view1 = findViewById( R.id.view1 );
        view2 = findViewById( R.id.view2 );
        view3 = findViewById( R.id.view3 );
        feedbackLin = (LinearLayout) findViewById( R.id.feedbackLin );
        feedbackLin.setOnClickListener( this );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.sales_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {

            SharedPreferences sharedPreferences1 = getSharedPreferences( "SignInCredentials", MODE_PRIVATE );
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString( "Name", "" );
            editor.putString( "Password", "" );
            editor.apply();

            Intent intent = new Intent( MerchantActivity.this, SignInActivity.class );
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity( intent );
            finish();
            return true;
        } else if (id == R.id.action_syncData) {
            new GenreFetch().execute();
        } else onBackPressed();
        return super.onOptionsItemSelected( item );
    }


    @Override
    public void onClick(View v) {

        Intent intent;
        switch (v.getId()) {

            case R.id.attendanceLin:
                intent = new Intent( MerchantActivity.this, MerchandiseAttendence.class );
                intent.putExtra( "StoreType", getIntent().getStringExtra( "StoreType" ) );
                intent.putExtra( "StoreName", getIntent().getStringExtra( "StoreName" ) );
                intent.putExtra( "Location", getIntent().getStringExtra( "Location" ) );
                intent.putExtra( "ContactName", getIntent().getStringExtra( "ContactName" ) );
                intent.putExtra( "ContactNumber", getIntent().getStringExtra( "ContactNumber" ) );
                intent.putExtra( "Date", getIntent().getStringExtra( "Date" ) );
                intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
                intent.putExtra( "Label", Label );
                intent.putExtra( "Id", getIntent().getStringExtra( "Id" ) );
                intent.putExtra( "assigned_store_id", getIntent().getStringExtra( "assigned_store_id" ) );
                intent.putExtra( "latitude", getIntent().getStringExtra( "latitude" ) );
                intent.putExtra( "longitude", getIntent().getStringExtra( "longitude" ) );
                startActivity( intent );

                break;
            case R.id.stockLin:
                Label = "Merchant";
                intent = new Intent( MerchantActivity.this, MerchantReport.class );
                intent.putExtra( "StoreType", getIntent().getStringExtra( "StoreType" ) );
                intent.putExtra( "StoreName", getIntent().getStringExtra( "StoreName" ) );
                intent.putExtra( "Location", getIntent().getStringExtra( "Location" ) );
                intent.putExtra( "ContactName", getIntent().getStringExtra( "ContactName" ) );
                intent.putExtra( "ContactNumber", getIntent().getStringExtra( "ContactNumber" ) );
                intent.putExtra( "Date", getIntent().getStringExtra( "Date" ) );
                intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
                intent.putExtra( "assigned_store_id", getIntent().getStringExtra( "assigned_store_id" ) );
                intent.putExtra( "Label", Label );
                intent.putExtra( "Id", getIntent().getStringExtra( "Id" ) );
                startActivity( intent );

                break;

            case R.id.postLin:
                Label = "Merchant";
                intent = new Intent( MerchantActivity.this, PostReport.class );
                intent.putExtra( "StoreType", getIntent().getStringExtra( "StoreType" ) );
                intent.putExtra( "StoreName", getIntent().getStringExtra( "StoreName" ) );
                intent.putExtra( "Location", getIntent().getStringExtra( "Location" ) );
                intent.putExtra( "ContactName", getIntent().getStringExtra( "ContactName" ) );
                intent.putExtra( "ContactNumber", getIntent().getStringExtra( "ContactNumber" ) );
                intent.putExtra( "Date", getIntent().getStringExtra( "Date" ) );
                intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
                intent.putExtra( "assigned_store_id", getIntent().getStringExtra( "assigned_store_id" ) );
                intent.putExtra( "Label", Label );
                intent.putExtra( "Id", getIntent().getStringExtra( "Id" ) );
                startActivity( intent );

                break;

            case R.id.merchantLin:
                Label = "Merchant";
                intent = new Intent( MerchantActivity.this, MerchantStock.class );
                intent.putExtra( "StoreType", getIntent().getStringExtra( "StoreType" ) );
                intent.putExtra( "StoreName", getIntent().getStringExtra( "StoreName" ) );
                intent.putExtra( "Location", getIntent().getStringExtra( "Location" ) );
                intent.putExtra( "ContactName", getIntent().getStringExtra( "ContactName" ) );
                intent.putExtra( "ContactNumber", getIntent().getStringExtra( "ContactNumber" ) );
                intent.putExtra( "Date", getIntent().getStringExtra( "Date" ) );
                intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
                intent.putExtra( "assigned_store_id", getIntent().getStringExtra( "assigned_store_id" ) );
                intent.putExtra( "Label", Label );
                intent.putExtra( "Id", getIntent().getStringExtra( "Id" ) );
                startActivity( intent );

                break;

            case R.id.reportLin:
                Label = "Report";
                intent = new Intent( MerchantActivity.this, ReportActivity.class );
                intent.putExtra( "StoreType", getIntent().getStringExtra( "StoreType" ) );
                intent.putExtra( "StoreName", getIntent().getStringExtra( "StoreName" ) );
                intent.putExtra( "Location", getIntent().getStringExtra( "Location" ) );
                intent.putExtra( "ContactName", getIntent().getStringExtra( "ContactName" ) );
                intent.putExtra( "ContactNumber", getIntent().getStringExtra( "ContactNumber" ) );
                intent.putExtra( "Date", getIntent().getStringExtra( "Date" ) );
                intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
                intent.putExtra( "assigned_store_id", getIntent().getStringExtra( "assigned_store_id" ) );
                intent.putExtra( "Label", Label );
                intent.putExtra( "Id", getIntent().getStringExtra( "Id" ) );
                startActivity( intent );

                break;

            case R.id.feedbackLin:

                Label = "Feedback";
                intent = new Intent( MerchantActivity.this, FeedbackActivity.class );
                intent.putExtra( "StoreType", getIntent().getStringExtra( "StoreType" ) );
                intent.putExtra( "StoreName", getIntent().getStringExtra( "StoreName" ) );
                intent.putExtra( "Location", getIntent().getStringExtra( "Location" ) );
                intent.putExtra( "ContactName", getIntent().getStringExtra( "ContactName" ) );
                intent.putExtra( "ContactNumber", getIntent().getStringExtra( "ContactNumber" ) );
                intent.putExtra( "Date", getIntent().getStringExtra( "Date" ) );
                intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
                intent.putExtra( "Id", getIntent().getStringExtra( "Id" ) );
                intent.putExtra( "Label", Label );
                intent.putExtra( "assigned_store_id", getIntent().getStringExtra( "assigned_store_id" ) );
                intent.putExtra( "deviation", getIntent().getStringExtra( "deviation" ) );
                startActivity( intent );

                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter.notifyDataSetChanged();

        DatabaseHandler db1 = new DatabaseHandler( MerchantActivity.this, "Predeploy" );
        List<Reporting> reportingList = db1.getAllContacts();
        if (reportingList.size() > 0) {
//            listFlag.get( 1 ).setFlag( true );
            view1.setBackgroundColor( getResources().getColor( R.color.green ) );
        } else {
//            listFlag.get( 1 ).setFlag( false );
            view1.setBackgroundColor( getResources().getColor( R.color.orange ) );}


        DatabaseHandler db2 = new DatabaseHandler( MerchantActivity.this, "Postdeploy" );
        List<Reporting> contactList2 = db2.getAllContacts();
        if (contactList2.size() > 0) {
//            listFlag.get( 2 ).setFlag( true );
            view2.setBackgroundColor( getResources().getColor( R.color.green ) );
        } else {
//            listFlag.get( 2 ).setFlag( false );
            view2.setBackgroundColor( getResources().getColor( R.color.orange ) );
        }

        DatabaseHandler db3 = new DatabaseHandler( MerchantActivity.this, "StockDb" );
        List<Reporting> contactList3 = db3.getAllContacts();
        if (contactList3.size() > 0) {
            for(int i =0;i<contactList3.size();i++) {
                if (storeID.equals( contactList3.get( i ).getStoreId() ) && pjpID.equals( contactList3.get( i ).getPjpId() )) {
                    listFlag.get( 3 ).setFlag( true );
                }else {
                    listFlag.get( 3 ).setFlag( false );
                }
                break;
            }
            view3.setBackgroundColor( getResources().getColor( R.color.green ) );
        } else {
            listFlag.get( 3 ).setFlag( false );
            view3.setBackgroundColor( getResources().getColor( R.color.orange ) );
        }

        //get adiitional info

        ArrayList<PopUp> lst = new ArrayList<>();
        lst.addAll( dbh.getAllInfo() );
        Log.e( "count ", "" + lst.size() );
        if (lst.size() > 0) {
            for (int i = 0; i < lst.size(); i++) {
                if (storeID.equals( lst.get( i ).getRefStoreID() )) {
                    if(Integer.parseInt(  lst.get( i ).getNewArrival())>0){
                        listFlag.get( 4 ).setFlag( true );
                    }else {
                        listFlag.get( 4 ).setFlag( false );
                    }

                    if(Integer.parseInt(  lst.get( i ).getBacklist())>0){
                        listFlag.get( 5 ).setFlag( true );
                    }else {
                        listFlag.get( 5 ).setFlag( false );
                    }

                    if(Integer.parseInt(  lst.get( i ).getFaceUp())>0){
                        listFlag.get( 6 ).setFlag( true );
                    }else {
                        listFlag.get( 6 ).setFlag( false );
                    }
                }else{
                    listFlag.get( 4 ).setFlag( false );
                    listFlag.get( 5 ).setFlag( false );
                    listFlag.get( 6 ).setFlag( false );
                }

            }
        }

    }

    @Override
    public void onClick(String value, int pos) {
        Log.e( "Adapter ", "" + pos );
        hs.add( pos );
        Intent intent;
        if (value.equalsIgnoreCase( "Attendance" )) {
            Label = "Attendance";
            intent = new Intent( MerchantActivity.this, MerchandiseAttendence.class );
            intent.putExtra( "StoreType", getIntent().getStringExtra( "StoreType" ) );
            intent.putExtra( "StoreName", getIntent().getStringExtra( "StoreName" ) );
            intent.putExtra( "Location", getIntent().getStringExtra( "Location" ) );
            intent.putExtra( "ContactName", getIntent().getStringExtra( "ContactName" ) );
            intent.putExtra( "ContactNumber", getIntent().getStringExtra( "ContactNumber" ) );
            intent.putExtra( "Date", getIntent().getStringExtra( "Date" ) );
            intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
            intent.putExtra( "Label", Label );
            intent.putExtra( "Id", getIntent().getStringExtra( "Id" ) );
            intent.putExtra( "assigned_store_id", getIntent().getStringExtra( "assigned_store_id" ) );
            intent.putExtra( "latitude", getIntent().getStringExtra( "latitude" ) );
            intent.putExtra( "longitude", getIntent().getStringExtra( "longitude" ) );
            startActivityForResult( intent, 100 );
        } else if (value.equalsIgnoreCase( "Checkout" )) {
            Label = "Attendance";
            intent = new Intent( MerchantActivity.this, MerchandiseAttendence.class );
            intent.putExtra( "StoreType", getIntent().getStringExtra( "StoreType" ) );
            intent.putExtra( "StoreName", getIntent().getStringExtra( "StoreName" ) );
            intent.putExtra( "Location", getIntent().getStringExtra( "Location" ) );
            intent.putExtra( "ContactName", getIntent().getStringExtra( "ContactName" ) );
            intent.putExtra( "ContactNumber", getIntent().getStringExtra( "ContactNumber" ) );
            intent.putExtra( "Date", getIntent().getStringExtra( "Date" ) );
            intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
            intent.putExtra( "Label", Label );
            intent.putExtra( "Id", getIntent().getStringExtra( "Id" ) );
            intent.putExtra( "assigned_store_id", getIntent().getStringExtra( "assigned_store_id" ) );
            intent.putExtra( "latitude", getIntent().getStringExtra( "latitude" ) );
            intent.putExtra( "longitude", getIntent().getStringExtra( "longitude" ) );
            startActivityForResult( intent, 100 );

        } else if (value.equalsIgnoreCase( "Feedback" )) {
            Label = "Feedback";
            listFlag.get( pos ).setFlag( true );
            intent = new Intent( MerchantActivity.this, FeedbackActivity.class );
            intent.putExtra( "StoreType", getIntent().getStringExtra( "StoreType" ) );
            intent.putExtra( "StoreName", getIntent().getStringExtra( "StoreName" ) );
            intent.putExtra( "Location", getIntent().getStringExtra( "Location" ) );
            intent.putExtra( "ContactName", getIntent().getStringExtra( "ContactName" ) );
            intent.putExtra( "ContactNumber", getIntent().getStringExtra( "ContactNumber" ) );
            intent.putExtra( "Date", getIntent().getStringExtra( "Date" ) );
            intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
            intent.putExtra( "Id", getIntent().getStringExtra( "Id" ) );
            intent.putExtra( "Label", Label );
            intent.putExtra( "assigned_store_id", getIntent().getStringExtra( "assigned_store_id" ) );
            intent.putExtra( "deviation", getIntent().getStringExtra( "deviation" ) );
            startActivity( intent );
        } else if (value.equalsIgnoreCase( "Pre Deployment" )) {
            Label = "Merchant";
            listFlag.get( pos ).setFlag( true );
            intent = new Intent( MerchantActivity.this, MerchantReport.class );
            intent.putExtra( "StoreType", getIntent().getStringExtra( "StoreType" ) );
            intent.putExtra( "StoreName", getIntent().getStringExtra( "StoreName" ) );
            intent.putExtra( "Location", getIntent().getStringExtra( "Location" ) );
            intent.putExtra( "ContactName", getIntent().getStringExtra( "ContactName" ) );
            intent.putExtra( "ContactNumber", getIntent().getStringExtra( "ContactNumber" ) );
            intent.putExtra( "Date", getIntent().getStringExtra( "Date" ) );
            intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
            intent.putExtra( "assigned_store_id", getIntent().getStringExtra( "assigned_store_id" ) );
            intent.putExtra( "Label", Label );
            intent.putExtra( "Id", getIntent().getStringExtra( "Id" ) );
            startActivity( intent );

        } else if (value.equalsIgnoreCase( "Post Deployment" )) {
            Label = "Merchant";
            listFlag.get( pos ).setFlag( true );
            intent = new Intent( MerchantActivity.this, PostReport.class );
            intent.putExtra( "StoreType", getIntent().getStringExtra( "StoreType" ) );
            intent.putExtra( "StoreName", getIntent().getStringExtra( "StoreName" ) );
            intent.putExtra( "Location", getIntent().getStringExtra( "Location" ) );
            intent.putExtra( "ContactName", getIntent().getStringExtra( "ContactName" ) );
            intent.putExtra( "ContactNumber", getIntent().getStringExtra( "ContactNumber" ) );
            intent.putExtra( "Date", getIntent().getStringExtra( "Date" ) );
            intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
            intent.putExtra( "assigned_store_id", getIntent().getStringExtra( "assigned_store_id" ) );
            intent.putExtra( "Label", Label );
            intent.putExtra( "Id", getIntent().getStringExtra( "Id" ) );
            startActivity( intent );

        } else if (value.equalsIgnoreCase( "Stock Report" )) {
            Label = "Merchant";
//            listFlag.get( pos ).setFlag( true );
            intent = new Intent( MerchantActivity.this, MerchantStock.class );
            intent.putExtra( "StoreType", getIntent().getStringExtra( "StoreType" ) );
            intent.putExtra( "StoreName", getIntent().getStringExtra( "StoreName" ) );
            intent.putExtra( "Location", getIntent().getStringExtra( "Location" ) );
            intent.putExtra( "ContactName", getIntent().getStringExtra( "ContactName" ) );
            intent.putExtra( "ContactNumber", getIntent().getStringExtra( "ContactNumber" ) );
            intent.putExtra( "Date", getIntent().getStringExtra( "Date" ) );
            intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
            intent.putExtra( "assigned_store_id", getIntent().getStringExtra( "assigned_store_id" ) );
            intent.putExtra( "Label", Label );
            intent.putExtra( "Id", getIntent().getStringExtra( "Id" ) );
            startActivity( intent );
        } else {

//            Log.e( "cat name ", getIntent().getStringExtra( "StoreCategory" ) );
            try {
                showDialog( value, getIntent().getStringExtra( "StoreCategory" ),
                        getIntent().getStringExtra( "Date" ), getIntent().getStringExtra( "assigned_store_id" ), pos );
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void showDialog(final String title, final String storeType, final String date, final String storeId, final int pos) throws JSONException {
        final Dialog dialog = new Dialog( this );
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
//        dialog.setCancelable( false );
        dialog.setContentView( R.layout.dialog );

        TextView text = (TextView) dialog.findViewById( R.id.text_msg );
        text.setText( title );
        TextView hint = (TextView) dialog.findViewById( R.id.tv_hint );
        final EditText value = (EditText) dialog.findViewById( R.id.et_value );
        hint.setVisibility( View.GONE );
        if (storeType.equalsIgnoreCase( "A" )) {
            if (title.contains( "Total SKU" )) {
                if (Integer.parseInt( jsonObject.getString( "BackList_A_MAXCount" ) ) == Integer.parseInt( jsonObject.getString( "BackList_A_MINCount" ) ))
                    hint.setText( "*Store Category A (BL count > " + jsonObject.getString( "BackList_A_MAXCount" ) + ")" );
                else
                    hint.setText( "*Store Category A (BL count > " + jsonObject.getString( "BackList_A_MINCount" ) + " - " +
                            jsonObject.getString( "BackList_A_MAXCount" ) );

                hint.setVisibility( View.VISIBLE );
                value.setText( "" + backList );
            } else if (title.contains( "Arrival" )) {
                hint.setVisibility( View.VISIBLE );
                if (Integer.parseInt( jsonObject.getString( "NewPro_A_MAXCount" ) ) == Integer.parseInt( jsonObject.getString( "NewPro_A_MINCount" ) ))
                    hint.setText( "*Store Category A (New Arrivals count > " + jsonObject.getString( "NewPro_A_MAXCount" ) + ")" );
                else
                    hint.setText( "*Store Category A (New Arrivals count > " + jsonObject.getString( "NewPro_A_MINCount" ) + " - " +
                            jsonObject.getString( "NewPro_A_MAXCount" ) );

                value.setText( "" + newArrival );

            } else if (title.contains( "Face Up" )) {
                hint.setVisibility( View.VISIBLE );
                if (Integer.parseInt( jsonObject.getString( "FaceUp_A_MINCount" ) ) == Integer.parseInt( jsonObject.getString( "FaceUp_A_MAXCount" ) ))
                    hint.setText( "*Store Category A (Face Up count > " + jsonObject.getString( "FaceUp_A_MAXCount" ) + ")" );
                else
                    hint.setText( "*Store Category A (Face Up count " + jsonObject.getString( "FaceUp_A_MINCount" ) + " - " +
                            jsonObject.getString( "FaceUp_A_MAXCount" ) );

                value.setText( "" + faceUp );
            }

        } else if (storeType.equalsIgnoreCase( "B" )) {
            if (title.contains( "Total SKU" )) {
                hint.setVisibility( View.VISIBLE );
                hint.setText( "*Store Category B (BL count " + jsonObject.getString( "BackList_B_MINCount" ) +
                        " - " + jsonObject.getString( "BackList_B_MAXCount" ) + ")" );

                value.setText( "" + backList );
            } else if (title.contains( "Arrival" )) {
                hint.setVisibility( View.VISIBLE );
                hint.setText( "*Store Category B (New Arrivals count " + jsonObject.getString( "NewPro_B_MINCount" ) +
                        " - " + jsonObject.getString( "NewPro_B_MAXCount" ) + ")" );

                value.setText( "" + newArrival );
            } else if (title.contains( "Face Up" )) {
                hint.setVisibility( View.VISIBLE );
                hint.setText( "*Store Category B (Face Up count " + jsonObject.getString( "FaceUp_B_MINCount" ) +
                        " - " + jsonObject.getString( "FaceUp_B_MAXCount" ) + ")" );

                value.setText( "" + faceUp );
            }
        } else if (storeType.equalsIgnoreCase( "C" )) {
            if (title.contains( "Total SKU" )) {
                hint.setVisibility( View.VISIBLE );
                hint.setText( "*Store Category C (BL count " + jsonObject.getString( "BackList_C_MINCount" ) +
                        " - " + jsonObject.getString( "BackList_C_MAXCount" ) + ")" );

                value.setText( "" + backList );
            } else if (title.contains( "Arrival" )) {
                hint.setVisibility( View.VISIBLE );
                hint.setText( "*Store Category C (New Arrivals count " + jsonObject.getString( "NewPro_C_MINCount" ) +
                        " - " + jsonObject.getString( "NewPro_C_MAXCount" ) + ")" );

                value.setText( "" + newArrival );
            } else if (title.contains( "Face Up" )) {
                hint.setVisibility( View.VISIBLE );
                hint.setText( "*Store Category C (Face Up count " + jsonObject.getString( "FaceUp_C_MINCount" ) +
                        " - " + jsonObject.getString( "FaceUp_C_MAXCount" ) + ")" );

                value.setText( "" + faceUp );
            }
        } else {
            hint.setVisibility( View.GONE );
        }


        Button dialogButton = (Button) dialog.findViewById( R.id.btn_save );
        dialogButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopUp popUp = new PopUp();

                if (title.contains( "Total SKU" )) {
                    backList = value.getText().toString();
                    listFlag.get( pos ).setFlag( true );
                } else if (title.contains( "Arrival" )) {
                    newArrival = value.getText().toString();
                    listFlag.get( pos ).setFlag( true );
                } else if (title.contains( "Face Up" )) {
                    faceUp = value.getText().toString();
                    listFlag.get( pos ).setFlag( true );
                }
                adapter.notifyDataSetChanged();
                popUp.setRefUserID( UserDetails.getUName() );
                popUp.setRefStoreID( storeId );
                popUp.setPJPDate( date );
                if (backList != "" && newArrival != "" && faceUp != "") {
                    popUp.setBacklist( backList );
                    popUp.setNewArrival( newArrival );
                    popUp.setFaceUp( faceUp );
                    popUp.setStockReason( "0" );
                    dbh.addInfo( popUp );
                }
                dialog.dismiss();
            }
        } );

        dialog.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if(listFlag.get( 0 ).isFlag() && listFlag.get( 2 ).isFlag() && listFlag.get( 3 ).isFlag()
//                && listFlag.get( 4 ).isFlag() && listFlag.get( 5 ).isFlag() && listFlag.get( 6 ).isFlag()
//                &&listFlag.get( 7 ).isFlag()&& listFlag.get( 8 ).isFlag() && listFlag.get( 9 ).isFlag()) {
//            super.onBackPressed();
//        } else if (hs.size() == 0) {
//            super.onBackPressed();
//        } else {
//            Toast.makeText( this, "Complete all the activity.", Toast.LENGTH_SHORT ).show();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult( requestCode, resultCode, data );
            Log.e( "Rq code ", requestCode + " Res Code " + resultCode + " OK " + Activity.RESULT_OK );

            if (requestCode == 100) {
                if (resultCode == 100) {
                    listFlag.get( 0 ).setFlag( true );
                } else {
                    listFlag.get( 0 ).setFlag( false );
                }

                if (resultCode == 700) {
                    listFlag.get( 7 ).setFlag( true );
                    listFlag.get( 0 ).setFlag( true );
                } else {
                    listFlag.get( 7 ).setFlag( false );
                }
            }
        } catch (Exception ex) {
//            Toast.makeText(MerchantActivity.this, ex.toString(),
//                    Toast.LENGTH_SHORT).show();
        }

    }

    public class GenreFetch extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Progress.showProgress( MerchantActivity.this );
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.Category;

                URL url = new URL( Url );
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e( "Get", "Get" );
                Log.e( "URL", Url );
                htp.setRequestMethod( "GET" );
                htp.setRequestProperty( "Content-Type", "application/json" );
                htp.setDoInput( true );
                htp.setUseCaches( false );
                htp.connect();


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
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString( "GenreType", s );
            editor.apply();

            new BookFetch().execute();
        }
    }

    class BookFetch extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress.showProgress(MainActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.Book;

                URL url = new URL( Url );
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e( "Get", "Get" );
                Log.e( "URL", Url );
                htp.setRequestMethod( "GET" );
                htp.setRequestProperty( "Content-Type", "application/json" );
                htp.setDoInput( true );
                htp.setUseCaches( false );
                htp.connect();


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

            //Progress.closeProgress();

            Log.d( "getALlBooks Reponse", s );
            SharedPreferences sharedPreferences1 = getSharedPreferences( "BookData", MODE_PRIVATE );
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString( "BookType", s );
            editor.apply();

            new AuthorFetch().execute();

        }
    }

    class AuthorFetch extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress.showProgress(FillDetailsActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.Author;

                URL url = new URL( Url );
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e( "Get", "Get" );
                Log.e( "URL", Url );
                htp.setRequestMethod( "GET" );
                htp.setRequestProperty( "Content-Type", "application/json" );
                htp.setDoInput( true );
                htp.setUseCaches( false );
                htp.connect();


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


            SharedPreferences sharedPreferences1 = getSharedPreferences( "AuthoData", MODE_PRIVATE );
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString( "AuthorType", s );
            editor.apply();

            new ReasonFetch().execute();

        }
    }

    class ReasonFetch extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress.showProgress(FillDetailsActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.Reason;

                URL url = new URL( Url );
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e( "Get", "Get" );
                Log.e( "URL", Url );
                htp.setRequestMethod( "GET" );
                htp.setRequestProperty( "Content-Type", "application/json" );
                htp.setDoInput( true );
                htp.setUseCaches( false );
                htp.connect();


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

            SharedPreferences sharedPreferences1 = getSharedPreferences( "ReasonData", MODE_PRIVATE );
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString( "ReasonType", s );
            editor.apply();

            new PosmFetch().execute();

        }
    }

    class PosmFetch extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress.showProgress(FillDetailsActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.Posm;

                URL url = new URL( Url );
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e( "Get", "Get" );
                Log.e( "URL", Url );
                htp.setRequestMethod( "GET" );
                htp.setRequestProperty( "Content-Type", "application/json" );
                htp.setDoInput( true );
                htp.setUseCaches( false );
                htp.connect();


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

            Log.d( "Author", s );


            SharedPreferences sharedPreferences1 = getSharedPreferences( "PosmData", MODE_PRIVATE );
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString( "PosmType", s );
            editor.apply();

            //Progress.closeProgress();

            new DistributorFetch().execute();

        }
    }

    class DistributorFetch extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress.showProgress(FillDetailsActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.Distributors;

                URL url = new URL( Url );
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e( "Get", "Get" );
                Log.e( "URL", Url );
                htp.setRequestMethod( "GET" );
                htp.setRequestProperty( "Content-Type", "application/json" );
                htp.setDoInput( true );
                htp.setUseCaches( false );
                htp.connect();


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

            Log.d( "Distributor", s );

            Progress.closeProgress();

            SharedPreferences sharedPreferences1 = getSharedPreferences( "Distributor", MODE_PRIVATE );
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString( "DistributorList", s );
            editor.apply();

        }
    }
}

