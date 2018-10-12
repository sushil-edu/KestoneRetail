package com.kestone.kestoneretail.ApiDetails.ApiListeners;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ApiListener extends AsyncTask<String, String, String> {
    private ApiResponse apiResponse;
    StringBuilder stringBuilder = new StringBuilder();
    String Url, Type = "";
    JSONObject jsonObject;
    JSONArray jsonArray;
    DataOutputStream printout;
    Activity activity;
    String dataType="";

    public ApiListener(Activity activity, String Url, JSONObject jsonObject) {
        this.apiResponse = (ApiResponse) activity;
        this.Url = Url;
        this.jsonObject = jsonObject;
        this.activity = activity;
        this.Type = "";
    }


    public ApiListener(Activity activity, String Url, JSONArray jsonArray, String dataType) {
        this.apiResponse = (ApiResponse) activity;
        this.Url = Url;
        this.jsonArray = jsonArray;
        this.activity = activity;
        this.Type = "";
        this.dataType = dataType;
    }


    public ApiListener(Activity activity, String Url, String ApiType) {
        this.apiResponse = (ApiResponse) activity;
        this.Url = Url;
        this.activity = activity;
        this.Type = ApiType;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            Log.e("Inside","Do in Background");
            URL url = new URL(Url);
            //Log.e("URL",Url);
            HttpURLConnection htp = (HttpURLConnection) url.openConnection();
            if (Type.equalsIgnoreCase("GET")) {
                Log.e("Get", "Get");
                Log.e("URL", Url);
                htp.setRequestMethod("GET");
                htp.setRequestProperty("Content-Type", "application/json");
                htp.setDoInput(true);
                htp.setUseCaches(false);
                htp.connect();
            } else if (dataType.equals("array")) {
                Log.e("Post", "Post");
                Log.e("URL", Url);
                Log.e("Params", jsonArray.toString());
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
            } else {
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
            }
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
        //Log.e("Response",s);
        apiResponse.onApiResponse(s);
    }
}
