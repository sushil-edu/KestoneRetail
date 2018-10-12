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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.kestone.kestoneretail.RecyclerAdapter.MyAdapter;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnItemClick {

    public static final int REQUEST_CODE = 1;
    public static String Label;
    View view1, view2, view3;
    RecyclerView recyclerView;
    MyAdapter adapter;
    DataOutputStream printout;
    String backList = "", newArrival = "", faceUp = "", storeID = null, storeCode = null, pjpID = null;
    DBHelpher dbh = new DBHelpher( MainActivity.this, "PopUpDb" );
    HashSet<Integer> hs = new HashSet<>();
    HashSet<Integer> hs2 = new HashSet<>();
    SharedPreferences preferences;
    String orderReason, stockReason, isDeviation;
    //    SharedPreferences sharedPrefAdd;
//    SharedPreferences.Editor editorAdd;
    int flag = 0;
    String selectedReason=null;
    String[] list = {
            "Attendance",
            "Stock and Order",
            "New Arrival For The Month",
            "Total SKU",
            "Face Up",
            "Checkout",
            "Feedback"};
    Integer[] icon_list = {R.drawable.attendence, R.drawable.merchandising,
            R.drawable.merchandising, R.drawable.merchandising, R.drawable.merchandising,
            R.drawable.attendence, R.drawable.feedback};
    ArrayList<RowItem> rowItems;
    ArrayList<HolderFlag> listFlag = new ArrayList<>();
    JSONArray jsonArray;
    JSONObject jsonObject;
    Intent intent;
    private LinearLayout attendanceLin, stockLin, feedbackLin, reportLin;
    private SharedPreferences sharedPreferences1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        getSupportActionBar().setTitle( "Dashboard" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        initializeLayout();

        SharedPreferences sharedPreferences = getSharedPreferences( "PjpData", MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString( "PJPId", getIntent().getStringExtra( "Id" ) );


        sharedPreferences1 = getSharedPreferences( "GenreData", MODE_PRIVATE );
        preferences = getSharedPreferences( "PSSReporting", MODE_PRIVATE );
        try {
//            jsonArray = new JSONArray(  preferences.getString( "PssReport" ,""));
            jsonObject = new JSONArray( preferences.getString( "PssReport", "" ) ).getJSONObject( 0 );
            Log.e( "Rs ", String.valueOf( jsonObject ) );

        } catch (JSONException e) {
            e.printStackTrace();
        }


        rowItems = new ArrayList<RowItem>();
        listFlag.clear();
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
        isDeviation = getIntent().getStringExtra( "deviation" );
        adapter = new MyAdapter( MainActivity.this, rowItems, listFlag, pjpID, storeID, storeCode, this, isDeviation );
        recyclerView.setAdapter( adapter );
        adapter.notifyDataSetChanged();
//        sharedPrefAdd = getSharedPreferences( "AdditionalInfo", MODE_PRIVATE );
//        editorAdd = sharedPrefAdd.edit();

        SharedPreferences sharedRead = getSharedPreferences( "AdditionalInfo", MODE_PRIVATE );
        String chekin = sharedRead.getString( "checkin", "" );
        String chekout = sharedRead.getString( "checkout", "" );
        if (chekin.equalsIgnoreCase( "true" )) {
            listFlag.get( 0 ).setFlag( true );
        } else {
            listFlag.get( 0 ).setFlag( false );
        }
        if (chekout.equalsIgnoreCase( "true" )) {
            listFlag.get( 5 ).setFlag( true );
        } else {
            listFlag.get( 5 ).setFlag( false );
        }

        ArrayList<PopUp> lst = new ArrayList<>();
        lst.addAll( dbh.getAllInfo() );
        for (int i = 0; i < lst.size(); i++) {
            Log.e( "Data ", lst.get( i ).getRefUserID() + "-" + lst.get( i ).getPJPDate() +
                    "-" + lst.get( i ).getBacklist() + "-" + lst.get( i ).getFaceUp() + "-" + lst.get( i ).getNewArrival() );
        }


    }

    private void initializeLayout() {

        attendanceLin = (LinearLayout) findViewById( R.id.attendanceLin );
        attendanceLin.setOnClickListener( this );
        stockLin = (LinearLayout) findViewById( R.id.stockLin );
        stockLin.setOnClickListener( this );
        feedbackLin = (LinearLayout) findViewById( R.id.feedbackLin );
        feedbackLin.setOnClickListener( this );
        reportLin = (LinearLayout) findViewById( R.id.reportLin );
        reportLin.setOnClickListener( this );
        view1 = findViewById( R.id.view1 );

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

            Intent intent = new Intent( MainActivity.this, SignInActivity.class );
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
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.attendanceLin:
                Label = "Attendance";
                intent = new Intent( MainActivity.this, Attendance.class );
                intent.putExtra( "StoreType", getIntent().getStringExtra( "StoreType" ) );
                intent.putExtra( "StoreName", getIntent().getStringExtra( "StoreName" ) );
                intent.putExtra( "Location", getIntent().getStringExtra( "Location" ) );
                intent.putExtra( "ContactName", getIntent().getStringExtra( "ContactName" ) );
                intent.putExtra( "ContactNumber", getIntent().getStringExtra( "ContactNumber" ) );
                intent.putExtra( "Date", getIntent().getStringExtra( "Date" ) );
                intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
                intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
                intent.putExtra( "Id", getIntent().getStringExtra( "Id" ) );
                intent.putExtra( "Label", Label );
                intent.putExtra( "assigned_store_id", getIntent().getStringExtra( "assigned_store_id" ) );
                intent.putExtra( "deviation", getIntent().getStringExtra( "deviation" ) );
                intent.putExtra( "latitude", getIntent().getStringExtra( "latitude" ) );
                intent.putExtra( "longitude", getIntent().getStringExtra( "longitude" ) );

                // startActivity( intent );

                break;

            case R.id.stockLin:
                Label = "Report";
                intent = new Intent( MainActivity.this, FillDetailsActivity.class );
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

            case R.id.feedbackLin:

                Label = "Feedback";
                intent = new Intent( MainActivity.this, FeedbackActivity.class );
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


            case R.id.reportLin:

                Label = "Report";
                intent = new Intent( MainActivity.this, ReportActivity.class );
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

        DatabaseHandler db = new DatabaseHandler( MainActivity.this, "ReportingDb" );

        Log.e( "Pjp id ", pjpID + " S name " + storeID + " s code " + storeCode );

        // Reading all contacts
        List<Reporting> contactList = db.getAllContacts();
        if (!contactList.isEmpty()) {
            for (int i = 0; i < contactList.size(); i++) {
                Log.e( "Stock report ", "pjp id " + contactList.get( i ).getPjpId() +
                        " sid  " + contactList.get( i ).getStoreId() + " S value" + contactList.get( i ).getStock() + " O value " + contactList.get( i ).getOrder() );

                if (pjpID.equals( contactList.get( i ).getPjpId() ) &&
                        storeID.equals( contactList.get( i ).getStoreId() )) {

                    if (Integer.parseInt( contactList.get( i ).getStock() ) > 0 || Integer.parseInt( contactList.get( i ).getOrder() ) > 0) {
                        listFlag.get( 1 ).setFlag( true );
                        hs.add( 1 );
                    }
                    break;
                } else {
                    Log.e( "check ", "not equal" + " value O " + contactList.get( i ).getOrder() + " value s " + contactList.get( i ).getStock() );
                    listFlag.get( 1 ).setFlag( false );
                    adapter.notifyDataSetChanged();
                }
                adapter.notifyDataSetChanged();
            }

        }else if(selectedReason!=null){
            listFlag.get( 1 ).setFlag( true );
        }else {
            listFlag.get( 1 ).setFlag( false );
        }
        //get adiitional info

        ArrayList<PopUp> lst = new ArrayList<>();
        lst.addAll( dbh.getAllInfo() );
        Log.e( "count ", "" + lst.size() );
        if (lst.size() > 0) {
            for (int i = 0; i < lst.size(); i++) {
                if (storeID.equals( lst.get( i ).getRefStoreID() )) {
                    if(Integer.parseInt(  lst.get( i ).getNewArrival())>0){
                        listFlag.get( 2 ).setFlag( true );
                    }else {
                        listFlag.get( 2 ).setFlag( false );
                    }

                    if(Integer.parseInt(  lst.get( i ).getBacklist())>0){
                        listFlag.get( 3 ).setFlag( true );
                    }else {
                        listFlag.get( 3 ).setFlag( false );
                    }

                    if(Integer.parseInt(  lst.get( i ).getFaceUp())>0){
                        listFlag.get( 4 ).setFlag( true );
                    }else {
                        listFlag.get( 4 ).setFlag( false );
                    }
                }else{
                    listFlag.get( 2 ).setFlag( false );
                    listFlag.get( 3 ).setFlag( false );
                    listFlag.get( 4 ).setFlag( false );
                }

            }
        }

        adapter.notifyDataSetChanged();
//        if(contactList.size()>0){
//            view1.setBackgroundColor(getResources().getColor(R.color.green));
//        }else view1.setBackgroundColor(getResources().getColor(R.color.orange));


    }

    //Interface onClick listener
    @Override
    public void onClick(String value, int pos) {
        if (pos != 6) {
            hs.add( pos );
        }
        Log.e( "Adapter ", value );

        if (value.equalsIgnoreCase( "Attendance" )) {
            Label = "Attendance";
            intent = new Intent( MainActivity.this, Attendance.class );
            intent.putExtra( "StoreType", getIntent().getStringExtra( "StoreType" ) );
            intent.putExtra( "StoreName", getIntent().getStringExtra( "StoreName" ) );
            intent.putExtra( "Location", getIntent().getStringExtra( "Location" ) );
            intent.putExtra( "ContactName", getIntent().getStringExtra( "ContactName" ) );
            intent.putExtra( "ContactNumber", getIntent().getStringExtra( "ContactNumber" ) );
            intent.putExtra( "Date", getIntent().getStringExtra( "Date" ) );
            intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
            intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
            intent.putExtra( "Id", getIntent().getStringExtra( "Id" ) );
            intent.putExtra( "Label", Label );
            intent.putExtra( "assigned_store_id", getIntent().getStringExtra( "assigned_store_id" ) );
            intent.putExtra( "deviation", getIntent().getStringExtra( "deviation" ) );
            intent.putExtra( "latitude", getIntent().getStringExtra( "latitude" ) );
            intent.putExtra( "longitude", getIntent().getStringExtra( "longitude" ) );
            startActivityForResult( intent, 100 );

        } else if (value.equalsIgnoreCase( "Checkout" )) {
            Label = "Attendance";
            intent = new Intent( MainActivity.this, Attendance.class );
            intent.putExtra( "StoreType", getIntent().getStringExtra( "StoreType" ) );
            intent.putExtra( "StoreName", getIntent().getStringExtra( "StoreName" ) );
            intent.putExtra( "Location", getIntent().getStringExtra( "Location" ) );
            intent.putExtra( "ContactName", getIntent().getStringExtra( "ContactName" ) );
            intent.putExtra( "ContactNumber", getIntent().getStringExtra( "ContactNumber" ) );
            intent.putExtra( "Date", getIntent().getStringExtra( "Date" ) );
            intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
            intent.putExtra( "StoreCode", getIntent().getStringExtra( "StoreCode" ) );
            intent.putExtra( "Id", getIntent().getStringExtra( "Id" ) );
            intent.putExtra( "Label", Label );
            intent.putExtra( "assigned_store_id", getIntent().getStringExtra( "assigned_store_id" ) );
            intent.putExtra( "deviation", getIntent().getStringExtra( "deviation" ) );
            intent.putExtra( "latitude", getIntent().getStringExtra( "latitude" ) );
            intent.putExtra( "longitude", getIntent().getStringExtra( "longitude" ) );
            startActivityForResult( intent, 100 );

        } else if (value.equalsIgnoreCase( "Feedback" )) {
            Label = "Feedback";
            listFlag.get( pos ).setFlag( true );
            intent = new Intent( MainActivity.this, FeedbackActivity.class );
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
        } else if (value.equalsIgnoreCase( "Stock and Order" )) {
            Label = "Stock and Order";
            showPopUp( "stock" );
        } else {
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
                    popUp.setStockReason( stockReason );
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
        /*if(isDeviation.equalsIgnoreCase( "Y" )){
            super.onBackPressed();
        }else {
            if (listFlag.get( 0 ).isFlag() && listFlag.get( 2 ).isFlag() && listFlag.get( 3 ).isFlag()
                    && listFlag.get( 4 ).isFlag() && listFlag.get( 5 ).isFlag() && listFlag.get( 6 ).isFlag()
                    && listFlag.get( 7 ).isFlag() && listFlag.get( 8 ).isFlag()) {
                super.onBackPressed();
            } else if (hs.size() == 0) {
                super.onBackPressed();
            } else {
                Toast.makeText( this, "Complete all the activity.", Toast.LENGTH_SHORT ).show();
            }
        }*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        Log.e( "Rq code ", requestCode + " Res Code " + resultCode + " OK " + Activity.RESULT_OK );

        if (requestCode == 100) {
            if (resultCode == 100) {
                listFlag.get( 0 ).setFlag( true );
//                editorAdd.putString( "checkin", "true" );
            } else {
                listFlag.get( 0 ).setFlag( false );
            }

            if (resultCode == 700) {
                listFlag.get( 5 ).setFlag( true );
                listFlag.get( 0 ).setFlag( true );
//                editorAdd.putString( "checkin", "true" );
//                editorAdd.putString( "checkout", "true" );
            } else {
                listFlag.get( 5 ).setFlag( false );
            }
//            editorAdd.apply();
        }
    }

    public void showPopUp(final String type) {

        final Dialog dialog = new Dialog( this );
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
        dialog.setCancelable( true );
        dialog.setContentView( R.layout.dialog_availability );
        final TextView no = (TextView) dialog.findViewById( R.id.no );
        final TextView yes = (TextView) dialog.findViewById( R.id.yes );
        final TextView save = (TextView) dialog.findViewById( R.id.save );
        final TextView cancel = (TextView) dialog.findViewById( R.id.cancel );
        final Spinner spinner = (Spinner) dialog.findViewById( R.id.spinner_option );
        final LinearLayout layout = (LinearLayout) dialog.findViewById( R.id.layout_parent );
//        final String[] order = {"Select reason", "Stock available", "Customer account on hold", "Others"};
        final String[] stock = {"Select reason", "New Customer", "Head Office Location", "Stock available", "Customer account on hold", "Others"};
        yes.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent( MainActivity.this, FillDetailsActivity.class );
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
                dialog.dismiss();
            }
        } );
        no.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility( View.VISIBLE );
                save.setVisibility( View.VISIBLE );
                cancel.setVisibility( View.VISIBLE );
                yes.setBackgroundColor( getResources().getColor( R.color.grey_alpha ) );
                yes.setEnabled( false );
                if (type.equalsIgnoreCase( "stock" )) {
                    spinner.setAdapter( new ArrayAdapter<String>( MainActivity.this, android.R.layout.simple_list_item_1, stock ) );
                    spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            selectedReason = String.valueOf( stock[i] );
                            stockReason = selectedReason;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }

                    } );
                }
            }
        } );
        save.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listFlag.get( 1 ).setFlag( true );
                dialog.dismiss();
                adapter.notifyDataSetChanged();
            }
        } );
        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listFlag.get( 1 ).setFlag( false );
                listFlag.get( 2 ).setFlag( false );
                dialog.dismiss();
            }
        } );

        dialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
        dialog.show();
    }

    //[{"RefUserID":"manojg@kestone.in","RefStoreID":"34","Backlist":"my list",
//            "NewArrival":"Test Arrival","FaceUp":"May Face","StoreFaceList":"Store Face List","PJPDate":"05/09/2018"}]
//    class AddtionalInfo extends AsyncTask<String, String, String> {
//
//        StringBuilder stringBuilder = new StringBuilder();
//        String Url;
//        JSONObject jsonObject;
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            // Progress.showProgress(FillDetailsActivity.this);
//
//            jsonObject = new JSONObject(  );
//            try {
//                jsonObject.put( "RefUserID" ,UserDetails.getUName());
//                jsonObject.put( "RefStoreID" ,"");
//                jsonObject.put( "Backlist" ,"");
//                jsonObject.put( "NewArrival" ,"");
//                jsonObject.put( "FaceUp" ,"");
//                jsonObject.put( "StoreFaceList" ,"");
//                jsonObject.put( "PJPDate" ,"");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            try {
//                Url = ApiUrl.AdditionalInfo;
//
//                URL url = new URL( Url );
//                //Log.e("URL",Url);
//                HttpURLConnection htp = (HttpURLConnection) url.openConnection();
//
//                Log.e( "Post", "Post" );
//                Log.e( "URL", Url );
//                Log.e( "Params", jsonObject.toString() );
//                htp.setRequestMethod( "POST" );
//                htp.setRequestProperty( "Content-Type", "application/json" );
//                htp.setDoInput( true );
//                htp.setDoOutput( true );
//                htp.setUseCaches( false );
//                htp.connect();
//                printout = new DataOutputStream( htp.getOutputStream() );
//                printout.writeBytes( jsonObject.toString() );
//                printout.flush();
//                printout.close();
//
//
//                InputStream inputStream = htp.getInputStream();
//                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );
//                String Line;
//                while ((Line = bufferedReader.readLine()) != null) {
//                    stringBuilder.append( Line );
//                }
//                return stringBuilder.toString();
//            } catch (Exception e) {
//                Log.e( "Error", e.getMessage() );
//            }
//            return "null";
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            Log.d( "Distributor", s );
//            Progress.closeProgress();
//
//
//            SharedPreferences sharedPreferences1 = getSharedPreferences( "Distributor", MODE_PRIVATE );
//            SharedPreferences.Editor editor = sharedPreferences1.edit();
//            editor.putString( "DistributorList", s );
//            editor.apply();
//
//        }
//    }

    class GenreFetch extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Progress.showProgress( MainActivity.this );
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
            //Progress.closeProgress();

            Log.d( "Author", s );

            SharedPreferences sharedPreferences1 = getSharedPreferences( "AuthoData", MODE_PRIVATE );
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString( "AuthorType", s );
            editor.apply();

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
