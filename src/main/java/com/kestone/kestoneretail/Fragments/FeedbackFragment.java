package com.kestone.kestoneretail.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.kestoneretail.ApiDetails.ApiUrls.ApiUrl;
import com.kestone.kestoneretail.DataHolders.UserDetails;
import com.kestone.kestoneretail.Progress;
import com.kestone.kestoneretail.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment implements View.OnClickListener {

        View v;
        RelativeLayout back, next, layout_takephoto;
        TextView txtStoreType, txtType, txtMAllowed, txtNotAllowed, nexttxt, txtnext;
        ImageView arrowtext;
        LinearLayout layout_storetype, layout_store, layout_merchandisingallowed, layout_resignfornotallowed;
        CardView notallow, frontpic;
        JSONObject jsonObject;
        DataOutputStream printout;
        EditText commentsEdt;
        SharedPreferences sharedPref;

    public FeedbackFragment() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            v = inflater.inflate(R.layout.fragment_feedback, container, false);


            back = (RelativeLayout) v.findViewById(R.id.layout_back);
            next = (RelativeLayout) v.findViewById(R.id.layout_next);
            arrowtext = (ImageView) v.findViewById(R.id.arrowright);
            nexttxt = (TextView) v.findViewById(R.id.submit);
            commentsEdt = (EditText) v.findViewById(R.id.commentsEdt);
            back.setOnClickListener(this);
            next.setOnClickListener(this);


            txtStoreType = (TextView) v.findViewById(R.id.txtStoreType);
            txtType = (TextView) v.findViewById(R.id.txtType);
            txtMAllowed = (TextView) v.findViewById(R.id.txtMAllowed);

            layout_storetype = (LinearLayout) v.findViewById(R.id.layout_storetype);
            layout_store = (LinearLayout) v.findViewById(R.id.layout_store);
            layout_merchandisingallowed = (LinearLayout) v.findViewById(R.id.layout_merchandisingallowed);

//
            layout_storetype.setOnClickListener(this);
            layout_store.setOnClickListener(this);
            layout_merchandisingallowed.setOnClickListener(this);


            sharedPref = getActivity().getSharedPreferences("StoreDetails", Context.MODE_PRIVATE);

            txtStoreType.setText(sharedPref.getString("StoreType", "N/A"));
            txtType.setText(sharedPref.getString("StoreName", "N/A"));

            return v;
        }

        @Override
        public void onClick(View v) {
            Bundle args;
            switch (v.getId()) {
                case R.id.layout_back:
                    getActivity().setResult(Activity.RESULT_CANCELED);
                    getActivity().finish();


                    break;
                case R.id.layout_next:

                    // getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,new StockSummary()).commit();


                    if(txtStoreType.getText().length()>0&&txtType.getText().length()>0&&commentsEdt.getText().length()>0){


                        Time today = new Time(Time.getCurrentTimezone());
                        today.setToNow();
                        Log.d("Date", today.monthDay+"-"+today.month+"-"+today.year);

                        int month = today.month+1;
                        String mo;
                        if(month<10){
                            mo = "0"+month;
                        }else mo = month +"";


                        String date = today.monthDay+"-"+mo+"-"+today.year;
                        Progress.showProgress(getContext());

                        jsonObject = new JSONObject();
                        try {

                            jsonObject.put("RefUserID", UserDetails.getUName());
                            jsonObject.put("RefStoreID",sharedPref.getString("assigned_store_id","N/A"));
                            jsonObject.put("PJPDate",sharedPref.getString("Date","N/A"));
                            jsonObject.put("Comments",commentsEdt.getText().toString());

                            new FeedbackUpload().execute();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Progress.closeProgress();
                        }

                    }else
                        Toast.makeText(getContext(), "Fill all data", Toast.LENGTH_SHORT).show();

                    break;



                case R.id.layout_merchandisingallowed:

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setMessage("Merchandising Allowed?");
                    alertDialogBuilder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    txtMAllowed.setText("Yes");
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            txtMAllowed.setText("No");
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    break;


            }


        }


        class FeedbackUpload extends AsyncTask<String, String, String> {

            StringBuilder stringBuilder = new StringBuilder();
            String Url;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                try {

                    Url = ApiUrl.Feedback;

                    URL url = new URL(Url);
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

                Log.d("Feedback Response", s);
                Progress.closeProgress();

                try {
                    JSONObject jObj = new JSONObject(s);
                    if(jObj.has("retval")){
                        Toast.makeText(getContext(),jObj.getString("retval"),Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
