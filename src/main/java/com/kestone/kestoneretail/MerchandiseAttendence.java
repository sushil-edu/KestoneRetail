package com.kestone.kestoneretail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.master.permissionhelper.PermissionHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import in.galaxyofandroid.widgets.AwesomeRelativeLayout;

public class MerchandiseAttendence extends AppCompatActivity implements ApiResponse {

    private static final int CAMERA_REQUEST = 1888;
    //for location utility
    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final String TAG = "MerchantAttendance";
    static String status = "0";
    private static int clickCount = 0;
    TextView attendanceTv;
    AwesomeRelativeLayout button;
    ImageView imageView;
    JSONObject jObj;
    DataOutputStream printout;
    Bitmap photo;
    RelativeLayout awesomeRelativeLayout;
    private double latitude = 0.0, longitude = 0.0;
    // GPSTracker class
//    TrackGPS gps;
    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private String mLastUpdateTime;

    // boolean flag to toggle the ui

    private Boolean mRequestingLocationUpdates;


    private SharedPreferences permissionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_merchandise_attendence );

        getSupportActionBar().setTitle( "Merchant Attendance" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        button = (AwesomeRelativeLayout) findViewById( R.id.takeImgBtn );
        attendanceTv = (TextView) findViewById( R.id.attendanceTv );
        imageView = (ImageView) findViewById( R.id.imageView );

        init();
        startLocation();
        clickCount = 0;

        permissionStatus = getSharedPreferences( "permissionStatus", MODE_PRIVATE );

        if (new ConnectionStatus( this ).isNetworkAvailable()) {
            new AttendanceStatus().execute();
        } else {
            Toast.makeText( MerchandiseAttendence.this, "No Internet Connection", Toast.LENGTH_SHORT ).show();
        }

        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PermissionHelper permissionHelper = new PermissionHelper( MerchandiseAttendence.this,
                        new String[]{Manifest.permission.CAMERA}, 100 );
                permissionHelper.request( new PermissionHelper.PermissionCallback() {
                    @Override
                    public void onPermissionGranted() {

                        Intent cameraIntent = new Intent( android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
                        startActivityForResult( cameraIntent, CAMERA_REQUEST );
                    }

                    @Override
                    public void onPermissionDenied() {
                        Log.d( "Permission", "onPermissionDenied() called" );
                    }

                    @Override
                    public void onPermissionDeniedBySystem() {
                        Log.d( "Permission", "onPermissionDeniedBySystem() called" );
                    }
                } );

            }
        } );

        //Awesome Relative Layout
        awesomeRelativeLayout = (RelativeLayout) findViewById( R.id.checkInBtn );
        awesomeRelativeLayout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                PermissionHelper permissionHelper = new PermissionHelper(MerchandiseAttendence.this, new String[]{Manifest.permission.CAMERA,
//                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
//                permissionHelper.request(new PermissionHelper.PermissionCallback() {
//                    @Override
//                    public void onPermissionGranted() {
//                        Log.d("Permission", "onPermissionGranted() called");
//
//                        // create class object
//                        gps = new TrackGPS(MerchandiseAttendence.this);
//
//                        // check if GPS enabled
//                        if (gps.canGetLocation()) {
                startLocation();
                if (mCurrentLocation != null || clickCount == 3) {
//
                    if (clickCount == 3) {
                        latitude = 0.0;
                        longitude = 0.0;
                    } else {
                        latitude = mCurrentLocation.getLatitude();
                        longitude = mCurrentLocation.getLongitude();
                    }
                    Time today = new Time( Time.getCurrentTimezone() );
                    today.setToNow();
                    Log.d( "", "" + today.hour + ":" + today.minute );
                    String time = "" + today.hour + ":" + today.minute;

                    Log.d( "date", getIntent().getStringExtra( "Date" ) );
                    String dateStr = getIntent().getStringExtra( "Date" );

                    if (status.equalsIgnoreCase( "0" )) {

                        if (imageView.getDrawable() != null) {

//                            if (!String.valueOf( Double.valueOf( latitude + "" ) ).equals( "0.0" ) && !String.valueOf( Double.valueOf( longitude + "" ) ).equals( "0.0" )) {

                                        if (CalculationByDistance(Double.valueOf(latitude + ""), Double.valueOf(longitude + "")
                                                , Double.valueOf(getIntent().getStringExtra("latitude")), Double.valueOf(getIntent().getStringExtra("longitude"))) <= 500) {

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                photo.compress( Bitmap.CompressFormat.PNG, 0, baos ); //bm is the bitmap object
                                byte[] b = baos.toByteArray();

                                String encodedImage = Base64.encodeToString( b, Base64.DEFAULT );

                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put( "RefStoreID", getIntent().getStringExtra( "assigned_store_id" ) );
                                    jsonObject.put( "RefUserID", UserDetails.getUName() );
                                    jsonObject.put( "Latitude", latitude + "" );
                                    jsonObject.put( "Longitude", longitude + "" );
                                    jsonObject.put( "Checkin", dateStr + " " + time );
                                    jsonObject.put( "PJPDate", dateStr );
                                    jsonObject.put( "StorePic", encodedImage );

                                    if (new ConnectionStatus( MerchandiseAttendence.this ).isNetworkAvailable()) {
//                                        Progress.showProgress( MerchandiseAttendence.this );
//
//                                        new ApiListener( MerchandiseAttendence.this, ApiUrl.checkin, jsonObject ).execute();
                                    } else {
                                        Toast.makeText( MerchandiseAttendence.this, "No Internet Connection", Toast.LENGTH_SHORT ).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Progress.closeProgress();
                                }

                                        } else {
                                            Toast.makeText(MerchandiseAttendence.this, "Invalid Location", Toast.LENGTH_SHORT).show();
                                        }

//                            } else
//                                Toast.makeText( MerchandiseAttendence.this, "Location Not Captured, Click Again", Toast.LENGTH_SHORT ).show();


                        } else
                            Toast.makeText( MerchandiseAttendence.this, "Take Image of Store first", Toast.LENGTH_SHORT ).show();

                    } else if (status.equalsIgnoreCase( "1" )) {

                        if (imageView.getDrawable() != null) {

//                            if (!String.valueOf( Double.valueOf( latitude + "" ) ).equals( "0.0" ) && !String.valueOf( Double.valueOf( longitude + "" ) ).equals( "0.0" )) {


                                        if (CalculationByDistance(Double.valueOf(latitude + ""), Double.valueOf(longitude + "")
                                                , Double.valueOf(getIntent().getStringExtra("latitude")), Double.valueOf(getIntent().getStringExtra("longitude"))) <= 500) {


                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                photo.compress( Bitmap.CompressFormat.PNG, 10, baos ); //bm is the bitmap object
                                byte[] b = baos.toByteArray();

                                String encodedImage = Base64.encodeToString( b, Base64.DEFAULT );

                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put( "RefStoreID", getIntent().getStringExtra( "assigned_store_id" ) );
                                    jsonObject.put( "RefUserID", UserDetails.getUName() );
                                    jsonObject.put( "PJPDate", dateStr );
                                    jsonObject.put( "Checkout", dateStr + " " + time );
                                    jsonObject.put( "StorePic", encodedImage );
                                    jsonObject.put( "Latitude", latitude + "" );
                                    jsonObject.put( "Longitude", longitude + "" );

                                    if (new ConnectionStatus( MerchandiseAttendence.this ).isNetworkAvailable()) {

                                        Progress.showProgress( MerchandiseAttendence.this );
                                        new ApiListener( MerchandiseAttendence.this, ApiUrl.checkout, jsonObject ).execute();
                                    } else {
                                        Toast.makeText( MerchandiseAttendence.this, "No Internet Connection", Toast.LENGTH_SHORT ).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Progress.closeProgress();
                                }
                                        } else {
                                            Toast.makeText(MerchandiseAttendence.this, "Invalid Location", Toast.LENGTH_SHORT).show();
                                        }


//                            } else
//                                Toast.makeText( MerchandiseAttendence.this, "Location Not Captured, Click Again", Toast.LENGTH_SHORT ).show();

                        } else
                            Toast.makeText( MerchandiseAttendence.this, "Take Image of Store first", Toast.LENGTH_SHORT ).show();


                    } else if (status.equalsIgnoreCase( "2" )) {
                        Toast.makeText( MerchandiseAttendence.this, "Aleready Checked Out", Toast.LENGTH_SHORT ).show();
                    }
                } else {

                    Toast.makeText( MerchandiseAttendence.this, "Location not capture. Try again", Toast.LENGTH_SHORT ).show();
                    clickCount++;
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
//                    gps.showSettingsAlert();

                }


            }

//                    @Override
//                    public void onPermissionDenied() {
//                        Log.d("Permission", "onPermissionDenied() called");
//                    }
//
//                    @Override
//                    public void onPermissionDeniedBySystem() {
//                        Log.d("Permission", "onPermissionDeniedBySystem() called");
//                    }
//                });


//            }
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
                                    rae.startResolutionForResult( MerchandiseAttendence.this, REQUEST_CHECK_SETTINGS );
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i( TAG, "PendingIntent unable to execute request." );
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e( TAG, errorMessage );

                                Toast.makeText( MerchandiseAttendence.this, errorMessage, Toast.LENGTH_LONG ).show();
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

    @Override
    public void onApiResponse(String response) {

        Log.d( "Response", response );

        try {

            JSONObject jsonObject = new JSONObject( response );
            CheckInData checkInData = new CheckInData();

            if (status.equalsIgnoreCase( "0" )) {
                checkInData.setAttendance( jsonObject.getString( "retval" ) );

                Toast.makeText( MerchandiseAttendence.this, "Check In Successfull ", Toast.LENGTH_SHORT ).show();
                setResult( 100 );
                finish();
            } else if (status.equalsIgnoreCase( "1" )) {
                checkInData.setAttendance( jsonObject.getString( "retval" ) );
                setResult( 700 );
                finish();
                Toast.makeText( MerchandiseAttendence.this, "Check Out Successfull ", Toast.LENGTH_SHORT ).show();
            }


            if (checkInData.getAttendance().equalsIgnoreCase( "Checkin Successfull." )) {
                attendanceTv.setText( "Check Out" );
                status = "1";
                setResult( 200 );
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Progress.closeProgress();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check for the integer request code originally supplied to startResolutionForResult().
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e( "Alert ", "User agreed to make required location settings changes." );
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e( "Alert ", "User chose not to make required location settings changes." );
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
            case CAMERA_REQUEST:
            case RESULT_OK:
                photo = (Bitmap) data.getExtras().get( "data" );
                imageView.setImageBitmap( photo );
                awesomeRelativeLayout.setVisibility( View.VISIBLE );
                break;
        }

    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
////        gps = new TrackGPS( MerchandiseAttendence.this );
////        gps.stopUsingGPS();
//        stopLocationUpdates();
//    }

    public double CalculationByDistance(double initialLat, double initialLong,
                                        double finalLat, double finalLong) {
        int R = 6371; // km (Earth radius)
        double dLat = toRadians( finalLat - initialLat );
        double dLon = toRadians( finalLong - initialLong );
        initialLat = toRadians( initialLat );
        finalLat = toRadians( finalLat );

        double a = Math.sin( dLat / 2 ) * Math.sin( dLat / 2 ) +
                Math.sin( dLon / 2 ) * Math.sin( dLon / 2 ) * Math.cos( initialLat ) * Math.cos( finalLat );
        double c = 2 * Math.atan2( Math.sqrt( a ), Math.sqrt( 1 - a ) );

        Log.d( "CalculationDistance", R * c * 1000 + "" );

        return R * c * 1000;
    }

    public double toRadians(Double deg) {
        return deg * (Math.PI / 180);
    }


    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission( this,
                Manifest.permission.ACCESS_FINE_LOCATION );
        return permissionState == PackageManager.PERMISSION_GRANTED;
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

                Progress.showProgress( MerchandiseAttendence.this );


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
                Log.e( "Params", jObj.toString() );
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
            Progress.closeProgress();

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

