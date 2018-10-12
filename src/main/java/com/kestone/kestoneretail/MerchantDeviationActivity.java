package com.kestone.kestoneretail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.kestoneretail.ApiDetails.ApiListeners.ApiListener;
import com.kestone.kestoneretail.ApiDetails.ApiListeners.ApiResponse;
import com.kestone.kestoneretail.ApiDetails.ApiUrls.ApiUrl;
import com.kestone.kestoneretail.DataHolders.CityData;
import com.kestone.kestoneretail.DataHolders.StoreNameData;
import com.kestone.kestoneretail.DataHolders.StoreTypeData;
import com.kestone.kestoneretail.DataHolders.UserDetails;
import com.kestone.kestoneretail.RecyclerAdapter.CityTypeAdapter;
import com.kestone.kestoneretail.RecyclerAdapter.StoreNameAdapter;
import com.kestone.kestoneretail.RecyclerAdapter.StoreTypeAdpater;

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

public class MerchantDeviationActivity extends AppCompatActivity implements View.OnClickListener, ApiResponse

{

    private Button updateBtn;
    private CardView cardView1, cardView2, cardView3;
    private TextView edtStoreType, edtStoreName, edtCity;
    private EditText edtComment;
    JSONObject jsonObj;
    DataOutputStream printout;
    String sales_pjp_id ,region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deviation);

        getSupportActionBar().setTitle("Deviation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        SharedPreferences sharedPreferences = getSharedPreferences("SignInCredentials", MODE_PRIVATE);
        region = sharedPreferences.getString("Region", "");

        Log.d("region",region);

        sales_pjp_id = getIntent().getStringExtra("sales_pjp_id");

        updateBtn = (Button) findViewById(R.id.updateBtn);
        cardView1 = (CardView) findViewById(R.id.storeTypeCard);
        cardView2 = (CardView) findViewById(R.id.storeNameCard);
        cardView3 = (CardView) findViewById(R.id.commentCard);
        edtStoreType = (TextView) findViewById(R.id.edtStoreType);
        edtStoreType.setOnClickListener(this);
        edtStoreName = (TextView) findViewById(R.id.edtStoreName);
        edtStoreName.setOnClickListener(this);
        edtComment = (EditText) findViewById(R.id.edtComment);
        edtCity = (TextView) findViewById(R.id.edtCity);
        edtCity.setOnClickListener(this);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtStoreType.getText().length() > 0 && edtStoreName.getText().length() > 0 && edtComment.getText().length() > 0) {

                    if (new ConnectionStatus(MerchantDeviationActivity.this).isNetworkAvailable()) {
                        new DeivationUpdate().execute();
                    }else {
                        Toast.makeText(MerchantDeviationActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }


                } else
                    Toast.makeText(MerchantDeviationActivity.this, "Fill All Details First", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sales_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
            Intent intent = new Intent(MerchantDeviationActivity.this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_syncData) {
            new StoreTypeFetch().execute();
        } else onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edtStoreType:
                edtStoreName.setText("");
                SharedPreferences sharedPreferences1 = getSharedPreferences("StoreData", Context.MODE_PRIVATE);
                populateGenre(sharedPreferences1.getString("StoreType", ""));
                break;

            case R.id.edtStoreName:
                if (edtStoreType.getText().length() > 0 ) {
                    SharedPreferences sharedPreferences2 = getSharedPreferences("StoreNameData", MODE_PRIVATE);
                    populateStoreName(sharedPreferences2.getString("StoreName", ""));
                } else
                    Toast.makeText(this, "Select Store Type First", Toast.LENGTH_SHORT).show();

                break;

            case R.id.edtCity:

                SharedPreferences sharedPreferences3 = getSharedPreferences("CityData", Context.MODE_PRIVATE);
                //populateCity(sharedPreferences3.getString("CityType", ""));
                break;

        }
    }

    @Override
    public void onApiResponse(String response) {
        Log.e("All Store", response);
        Progress.closeProgress();
        SharedPreferences sharedPreferences1 = getSharedPreferences("StoreNameData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        editor.putString("StoreName", response);
        editor.apply();


    }


    class DeivationUpdate extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


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


            String date = today.year + "-" + mo + "-" + dayStr;

            jsonObj = new JSONObject();
            try {

                jsonObj.put("RefUserID", UserDetails.getUName());
                jsonObj.put("RefStoreID", StoreNameAdapter.store_id);
                jsonObj.put("PJPDate", date);
                jsonObj.put("Comments", edtComment.getText().toString());

                Progress.showProgress(MerchantDeviationActivity.this);

            } catch (JSONException e) {
                e.printStackTrace();
                Progress.closeProgress();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.Deviation;

                URL url = new URL(Url);
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e("Post", "Post");
                Log.e("URL", Url);
                Log.e("Params", jsonObj.toString());
                htp.setRequestMethod("POST");
                htp.setRequestProperty("Content-Type", "application/json");
                htp.setDoInput(true);
                htp.setDoOutput(true);
                htp.setUseCaches(false);
                htp.connect();
                printout = new DataOutputStream(htp.getOutputStream());
                printout.writeBytes(jsonObj.toString());
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

            Progress.closeProgress();

            Log.d("Update Pjp Response", s);

            try {
                JSONObject jobj = new JSONObject(s);
                if (jobj.getString("retval").equalsIgnoreCase("Deviation Added.")) {


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


                        String date = today.year + "-" + mo + "-" + dayStr;

                        jsonObject.put("EmailID", UserDetails.getUName());
                        jsonObject.put("PJPDate",date);

                        Progress.showProgress(MerchantDeviationActivity.this);
                        if (getIntent().getStringExtra("Type").equalsIgnoreCase("Merchant")) {
                            new FetchPjp(MerchantDeviationActivity.this, ApiUrl.MerchandiserPJP, jsonObject).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        } else
                            new FetchPjp(MerchantDeviationActivity.this, ApiUrl.SalesPJP, jsonObject).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Progress.closeProgress();
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    public void populateGenre(String response) {

        if (response.length() > 0) {
            Progress.showProgress(MerchantDeviationActivity.this);

            edtStoreName.setText("");


            ArrayList<StoreTypeData> storeTypeList = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    StoreTypeData genreData = new StoreTypeData();

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    genreData.setStore_formate(jsonObject1.getString("StoreCategory"));
                    storeTypeList.add(genreData);

                }
                Progress.closeProgress();


                LayoutInflater inflater = LayoutInflater.from(MerchantDeviationActivity.this);
                final View dialogLayout = inflater.inflate(R.layout.store_type, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MerchantDeviationActivity.this);

                builder.setView(dialogLayout);

                final AlertDialog customAlertDialog = builder.create();

                final RecyclerView recyclerView = (RecyclerView) dialogLayout.findViewById(R.id.recyclerView);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MerchantDeviationActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(new StoreTypeAdpater(MerchantDeviationActivity.this, storeTypeList, customAlertDialog, edtStoreType));


                customAlertDialog.show();


            } catch (JSONException e) {
                e.printStackTrace();
                Progress.closeProgress();
            }
        } else
            Toast.makeText(this, "No data, Sync Data First", Toast.LENGTH_SHORT).show();


    }


    public void populateStoreName(String response) {


        if (response.length() > 0) {
            ArrayList<StoreNameData> storeTypeList = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jObject = jsonArray.getJSONObject(i);

                    StoreNameData genreData = new StoreNameData();

                    if (jObject.getString("StoreCategory").equalsIgnoreCase(edtStoreType.getText().toString())
                            && jObject.getString("Region").equalsIgnoreCase(region)) {

                        genreData.setStore_name(jObject.getString("StoreName"));
                        genreData.setId(jObject.getString("ID"));
                        storeTypeList.add(genreData);

                    }



                }
                Progress.closeProgress();


                LayoutInflater inflater = LayoutInflater.from(MerchantDeviationActivity.this);
                final View dialogLayout = inflater.inflate(R.layout.store_type, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MerchantDeviationActivity.this);

                builder.setView(dialogLayout);

                final AlertDialog customAlertDialog = builder.create();

                final RecyclerView recyclerView = (RecyclerView) dialogLayout.findViewById(R.id.recyclerView);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MerchantDeviationActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(new StoreNameAdapter(MerchantDeviationActivity.this, storeTypeList, customAlertDialog, edtStoreName));


                if (storeTypeList.size() > 0) {
                    customAlertDialog.show();
                } else
                    Toast.makeText(this, "No store at this location", Toast.LENGTH_SHORT).show();


            } catch (JSONException e) {
                e.printStackTrace();
                Progress.closeProgress();
            }
        } else Toast.makeText(this, "No Data, Sync data first", Toast.LENGTH_SHORT).show();


    }



    class StoreTypeFetch extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Progress.showProgress(MerchantDeviationActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.StoreCategory;

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
            //Progress.closeProgress();
            Log.e("Post", s);
            SharedPreferences sharedPreferences1 = getSharedPreferences("StoreData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString("StoreType", s);
            editor.apply();

            new ApiListener(MerchantDeviationActivity.this, ApiUrl.Store, "GET").execute();


        }
    }



    public class FetchPjp extends AsyncTask<String, String, String> {
        private ApiResponse apiResponse;
        StringBuilder stringBuilder = new StringBuilder();
        String Url, Type = "";
        JSONObject jsonObject;
        DataOutputStream printout;
        Activity activity;

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
                URL url = new URL(ApiUrl.MerchandiserPJP);
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e("Post", "Post");
                Log.e("URL", Url);
                Log.e("Params", jsonObject.toString());
                htp.setRequestMethod("POST");
                htp.setRequestProperty("Content-Type", "application/json");
                htp.setDoInput(true);
                htp.setDoOutput(true);
                htp.setUseCaches(false);
                htp.connect();
                printout = new DataOutputStream(htp.getOutputStream());
                printout.writeBytes(jsonObject.toString());
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
            Log.d("PJP response", s);

            if (getIntent().getStringExtra("Type").equalsIgnoreCase("Merchant")) {

                SharedPreferences sharedPreferences = getSharedPreferences("PJPMData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("PJPMREsponse", s);
                editor.apply();
            } else {


                SharedPreferences sharedPreferences = getSharedPreferences("PJPData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("PJPREsponse", s);
                editor.apply();
            }
            Progress.closeProgress();


            Intent intent = new Intent(MerchantDeviationActivity.this, MerchantDashBoard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("email", UserDetails.getUName());
            startActivity(intent);
            finish();
        }
    }

}
