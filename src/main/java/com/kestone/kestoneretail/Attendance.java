package com.kestone.kestoneretail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kestone.kestoneretail.ApiDetails.ApiListeners.ApiListener;
import com.kestone.kestoneretail.ApiDetails.ApiListeners.ApiResponse;
import com.kestone.kestoneretail.ApiDetails.ApiUrls.ApiUrl;
import com.kestone.kestoneretail.DataHolders.CheckInData;
import com.kestone.kestoneretail.DataHolders.UserDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import in.galaxyofandroid.widgets.AwesomeRelativeLayout;

public class Attendance extends AppCompatActivity implements ApiResponse {
    private static final String TAG = "Attendance";
    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    public static int clickCount = 0;
    static String status = "0";
    TextView attendanceTv;
    JSONObject jsonObj;
    JSONObject jObj;
    DataOutputStream printout;
    double latitude = 0.0, longitude = 0.0;
    // location last updated time
    private String mLastUpdateTime;
    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_attendance );

        //Toolbar functions
        getSupportActionBar().setTitle( "Attendance" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );


        attendanceTv = (TextView) findViewById( R.id.attendanceTv );
        TextView textView = (TextView) findViewById( R.id.storeNameTv );
        textView.setText( "StoreName: \n" + getIntent().getStringExtra( "StoreName" ) );

        if (new ConnectionStatus( Attendance.this ).isNetworkAvailable()) {
            new AttendanceStatus().execute();
        } else {
            Toast.makeText( Attendance.this, "No Internet Connection", Toast.LENGTH_SHORT ).show();
        }

        //location
        // initialize the necessary libraries
        init();
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        startLocation();
                    }
                }, 50 );

        //Awesome Relative Layout
        clickCount = 0;
        AwesomeRelativeLayout awesomeRelativeLayout = (AwesomeRelativeLayout) findViewById( R.id.checkInBtn );
        awesomeRelativeLayout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocation();
                if (mCurrentLocation != null || clickCount == 3) {
                    if (clickCount == 3) {
                        latitude = 0.0;
                        longitude = 0.0;
                    } else {
                        latitude = mCurrentLocation.getLatitude();
                        longitude = mCurrentLocation.getLongitude();
                    }
                    String dateStr = getIntent().getStringExtra( "Date" );

                    Time today = new Time( Time.getCurrentTimezone() );
                    today.setToNow();
//                    Log.d( "", "" + today.hour + ":" + today.minute );
                    String time = "" + today.hour + ":" + today.minute;
                    if (status.equalsIgnoreCase( "0" )) {
//                        if (!String.valueOf( Double.valueOf( latitude + "" ) ).equals( "0.0" ) && !String.valueOf( Double.valueOf( longitude + "" ) ).equals( "0.0" )) {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put( "RefStoreID", getIntent().getStringExtra( "assigned_store_id" ) );
                            jsonObject.put( "RefUserID", UserDetails.getUName() );
                            jsonObject.put( "Checkin", dateStr + "  " + time );
                            jsonObject.put( "PJPDate", dateStr );
                            jsonObject.put( "Latitude", latitude + "" );
                            jsonObject.put( "Longitude", longitude + "" );
                            jsonObject.put( "StorePic", "" );

                            Log.d( "Attendance", jsonObject.toString() );

                            if (new ConnectionStatus( Attendance.this ).isNetworkAvailable()) {

                                Progress.showProgress( Attendance.this );
                                    new ApiListener( Attendance.this, ApiUrl.checkin, jsonObject ).execute();
                            } else {
                                Toast.makeText( Attendance.this, "No Internet Connection", Toast.LENGTH_SHORT ).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Progress.closeProgress();

                        }

//                        } else
//                            Toast.makeText( Attendance.this, "Location Not Captured, Click Again", Toast.LENGTH_SHORT ).show();
                    } else if (status.equalsIgnoreCase( "1" )) {

//                        if (!String.valueOf( Double.valueOf( latitude + "" ) ).equals( "0.0" ) && !String.valueOf( Double.valueOf( longitude + "" ) ).equals( "0.0" )) {

                        Log.d( "LatLng1", Double.valueOf( getIntent().getStringExtra( "latitude" ) ) + " , "
                                + Double.valueOf( getIntent().getStringExtra( "longitude" ) ) );

                        Log.d( "LatLng2", Double.valueOf( latitude + "" ) + " , "
                                + Double.valueOf( longitude + "" ) );

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put( "RefStoreID", getIntent().getStringExtra( "assigned_store_id" ) );
                            jsonObject.put( "RefUserID", UserDetails.getUName() );
                            jsonObject.put( "PJPDate", dateStr );
                            jsonObject.put( "Checkout", dateStr + " " + time );
                            jsonObject.put( "Latitude", latitude + "" );
                            jsonObject.put( "Longitude", longitude + "" );
                            jsonObject.put( "StorePic", "" );

                            Log.d( "Attendance", jsonObject.toString() );

                            if (new ConnectionStatus( Attendance.this ).isNetworkAvailable()) {

                                Progress.showProgress( Attendance.this );
                                new ApiListener( Attendance.this, ApiUrl.checkout, jsonObject ).execute();
                            } else {
                                Toast.makeText( Attendance.this, "No Internet Connection", Toast.LENGTH_SHORT ).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Progress.closeProgress();
                        }

//                        } else
//                            Toast.makeText( Attendance.this, "Location Not Captured, Click Again", Toast.LENGTH_SHORT ).show();
//
                    } else if (status.equalsIgnoreCase( "2" )) {
                        Toast.makeText( Attendance.this, "Already Checked Out", Toast.LENGTH_SHORT ).show();
                    }
                } else {
                    Toast.makeText( Attendance.this, "Location not capture. Try again", Toast.LENGTH_SHORT ).show();
                    clickCount++;
                }
            }
        } );


    }


    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient( this );
        mSettingsClient = LocationServices.getSettingsClient( this );

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult( locationResult );
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format( new Date() );
                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval( UPDATE_INTERVAL_IN_MILLISECONDS );
        mLocationRequest.setFastestInterval( FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS );
        mLocationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest( mLocationRequest );
        mLocationSettingsRequest = builder.build();
    }

    public void startLocation() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity( this )
                .withPermission( Manifest.permission.ACCESS_FINE_LOCATION )
                .withListener( new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                } ).check();
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
        Uri uri = Uri.fromParts( "package",
                BuildConfig.APPLICATION_ID, null );
        intent.setData( uri );
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity( intent );
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings( mLocationSettingsRequest )
                .addOnSuccessListener( this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i( "Attendance ", "All location settings are satisfied." );

                        // Toast.makeText( getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT ).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates( mLocationRequest,
                                mLocationCallback, Looper.myLooper() );
                        updateLocationUI();
                    }
                } )
                .addOnFailureListener( this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i( TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings " );
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult( Attendance.this, REQUEST_CHECK_SETTINGS );
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i( TAG, "PendingIntent unable to execute request." );
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e( TAG, errorMessage );

                                Toast.makeText( Attendance.this, errorMessage, Toast.LENGTH_LONG ).show();
                        }
                        updateLocationUI();
                    }
                } );
    }

    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            mCurrentLocation.getLatitude();
            mCurrentLocation.getLongitude();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent in = new Intent();
        if (status.equals( "1" )) {
            setResult( 100, in );
        } else if (status.equals( "2" )) {
            setResult( 700, in );
        } else {
            setResult( 404, in );
        }
        super.onBackPressed();
        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onApiResponse(String response) {
        Progress.closeProgress();
        Log.d( "ATTResponse", response );

        try {

            JSONObject jsonObject = new JSONObject( response );
            CheckInData checkInData = new CheckInData();

            if (status.equalsIgnoreCase( "0" )) {
                checkInData.setAttendance( jsonObject.getString( "retval" ) );
                Toast.makeText( Attendance.this, "Check In Successfully ", Toast.LENGTH_SHORT ).show();
//                Intent intent = getIntent();
                setResult( 100 );
                finish();
            } else if (status.equalsIgnoreCase( "1" )) {
                checkInData.setAttendance( jsonObject.getString( "retval" ) );
                setResult( 700 );
                finish();
                Toast.makeText( Attendance.this, "Check Out Successfully ", Toast.LENGTH_SHORT ).show();
            }
            if (checkInData.getAttendance().equalsIgnoreCase( "Check In Successfully." )) {
                attendanceTv.setText( "Check Out" );
                status = "1";
                setResult( 200 );
            }

        } catch (JSONException e) {
            Log.e( "Exception ", e.getMessage().toString() );
        }


    }

    public double toRadians(Double deg) {
        return deg * (Math.PI / 180);
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent();
        if (status.equals( "1" )) {
            setResult( 100, in );
        } else if (status.equals( "2" )) {
            setResult( 700, in );
        } else {
            setResult( 404, in );
        }
        super.onBackPressed();
    }

    class AttendanceStatus extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            jObj = new JSONObject();
            try {

                String dateStr = getIntent().getStringExtra( "Date" );

                jObj.put( "RefStoreID", getIntent().getStringExtra( "assigned_store_id" ) );
                jObj.put( "RefUserID", UserDetails.getUName() );
                jObj.put( "PJPDate", dateStr );

                Progress.showProgress( Attendance.this );


            } catch (JSONException e) {
                e.printStackTrace();
                Progress.closeProgress();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.AttendenceStatus;

                URL url = new URL( Url );
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e( "Post", "Post" );
                Log.e( "URL", Url );
                Log.e( "Params Attendance", jObj.toString() );
                htp.setRequestMethod( "POST" );
                htp.setRequestProperty( "Content-Type", "application/json" );
                htp.setDoInput( true );
                htp.setDoOutput( true );
                htp.setUseCaches( false );
                htp.connect();
                printout = new DataOutputStream( htp.getOutputStream() );
                printout.writeBytes( jObj.toString() );
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

            Log.d( "Attendance Status", s );

            Progress.closeProgress();

            try {
                JSONArray jsonArray = new JSONArray( s );
                JSONObject jsonObject1 = jsonArray.getJSONObject( 0 );


                if (jsonObject1.getString( "AttStatus" ).equalsIgnoreCase( "1" )) {
                    attendanceTv.setText( "Check Out" );
                    status = "1";
                } else if (jsonObject1.getString( "AttStatus" ).equalsIgnoreCase( "0" )) {
                    attendanceTv.setText( "Check In" );
                    status = "0";
                } else if (jsonObject1.getString( "AttStatus" ).equalsIgnoreCase( "2" )) {
                    attendanceTv.setText( "Check Out" );
                    status = "2";
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d( "Attendance Response", s );
        }
    }
}
