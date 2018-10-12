package com.kestone.kestoneretail.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.kestoneretail.ApiDetails.ApiUrls.ApiUrl;
import com.kestone.kestoneretail.DataHolders.UserDetails;
import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.DatabasePackage.MerchantData;
import com.kestone.kestoneretail.DatabasePackage.MerchantHandler;
import com.kestone.kestoneretail.DatabasePackage.Reporting;
import com.kestone.kestoneretail.Progress;
import com.kestone.kestoneretail.R;
import com.kestone.kestoneretail.RecyclerAdapter.PreDeployRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreDeployDetailsFragment extends Fragment {
    private LinearLayout backLl;
    private TextView confirmTv;
    private FloatingActionButton fab;
    private TextView storeCodeTv, storeNameTv;
    private DatabaseHandler db;
    private MerchantHandler mb;
    private SharedPreferences sharedPref;
    private JSONArray jsonArray;
    private List<Reporting> contacts;
    private List<MerchantData> merchantList;
    DataOutputStream printout;
    JSONObject jObj;


    View v;


    public PreDeployDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_pre_deploy_details, container, false);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);

        initializeLayout();

        backLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new MerchantFragment())
                        .commit();
            }
        });


        sharedPref = getActivity().getSharedPreferences("StoreDetails", Context.MODE_PRIVATE);
        storeNameTv.setText(sharedPref.getString("StoreName", "N/A"));
        storeCodeTv.setText(sharedPref.getString("StoreType", "N/A"));


        db = new DatabaseHandler(getContext(), "Predeploy");
        // Reading all contacts
        contacts = db.getAllContacts();
        Log.d("Reading: ", contacts.size() + "");


        mb = new MerchantHandler(getContext(), "ImageDb");
        merchantList = mb.getAllMerchantDatas();
        Log.d("Image: ", merchantList.size() + "");

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new PreDeployRecyclerAdapter(getContext(), contacts, merchantList, fab));

        if (contacts.size() < 1) {

            fab.setVisibility(View.VISIBLE);

        } else fab.setVisibility(View.GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new MerchantDetailsFragment())
                        .commit();

            }
        });


        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (contacts.size() > 0) {


                    SharedPreferences sharedPreferences =
                            getContext().getSharedPreferences("MerchantStatusFile", Context.MODE_PRIVATE);

                    jObj = new JSONObject();
                    try {


                        Time today = new Time(Time.getCurrentTimezone());
                        today.setToNow();
                        Log.d("Date", today.monthDay + "-" + today.month + "-" + today.year);

                        int month = today.month + 1;
                        String mo;
                        if (month < 10) {
                            mo = "0" + month;
                        } else mo = month + "";


                        String date = today.monthDay + "-" + mo + "-" + today.year;

                        jObj.put("RefStoreID",sharedPref.getString("assigned_store_id", "N/A") );
                        jObj.put("ResoanForNotAllow", "");
                        jObj.put("RefUserID", UserDetails.UName);
                        jObj.put("PJPDate", sharedPreferences.getString("pjp_date", "N/A"));
                        jObj.put("MerchandiserAllowed", "Yes");
                        jObj.put("Photo", sharedPreferences.getString("image", "N/A"));




                        new MerchantStatus().execute();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Progress.closeProgress();
                    }

                } else
                    Toast.makeText(getContext(), "Add Predeployment details first", Toast.LENGTH_SHORT).show();
            }

        });

        return v;
    }

    private void initializeLayout() {

        backLl = (LinearLayout) v.findViewById(R.id.back_confirm);
        confirmTv = (TextView) v.findViewById(R.id.tvConfirm);
        storeCodeTv = (TextView) v.findViewById(R.id.storeCodeTv);
        storeNameTv = (TextView) v.findViewById(R.id.storeNameTv);

    }

//Data Upload

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
                Url = ApiUrl.Deployment;

                URL url = new URL(Url);
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

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


            Log.d("Pre depoloyment",s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getString("retval").equalsIgnoreCase("Profile updated successfully.")) {
                    for (int i = 0; i < contacts.size(); i++) {

                        Reporting contact = contacts.get(i);
                        MerchantData merchantData = merchantList.get(i);
                        db.deleteContact(contact);
                        mb.deleteMerchantData(merchantData);

                    }


                    Progress.closeProgress();

                    Toast.makeText(getActivity(), "Data Uploaded", Toast.LENGTH_SHORT).show();

                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "There was some Problem", Toast.LENGTH_SHORT).show();
                    Progress.closeProgress();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Progress.closeProgress();
            }

        }
    }


    public class MerchantStatus extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        DataOutputStream printout;


        @Override
        protected String doInBackground(String... params) {
            try {
                //Log.e("Inside","Do in Background");
                URL url = new URL(ApiUrl.MerchandiserAllow);
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jObj);

                Log.e("Post", "Post");
                Log.e("URL", url.toString());
                Log.e("Params", jsonArray.toString());
                htp.setRequestMethod("POST");
                htp.setRequestProperty("Content-Type", "application/json");
                htp.setDoInput(true);
                htp.setDoOutput(true);
                htp.setUseCaches(false);
                htp.connect();
                printout = new DataOutputStream(htp.getOutputStream());
                printout.writeBytes(jsonArray.toString());
                printout.flush();
                printout.close();
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
            Log.e("Response", s);


            try {
                JSONObject jObj = new JSONObject(s);
                if (jObj.has("retval")) {

                    if (jObj.getString("retval").equalsIgnoreCase("Merchandiser successfully.")) {


                        if (contacts.size() > 0) {

//                    getActivity().getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.container, new PostDeployDetails())
//                            .commit();

                            Time today = new Time(Time.getCurrentTimezone());
                            today.setToNow();
                            Log.d("Date", today.monthDay + "-" + today.month + "-" + today.year);

                            int month = today.month + 1;
                            String mo;
                            if (month < 10) {
                                mo = "0" + month;
                            } else mo = month + "";


                            String date = today.monthDay + "-" + today.month + "-" + today.year;

                            Progress.showProgress(getContext());

                            jsonArray = new JSONArray();
                            JSONObject jsonObject;


                            for (int i = 0; i < contacts.size(); i++) {
                                Reporting contact = contacts.get(i);
                                MerchantData merchantData = merchantList.get(i);

                                jsonObject = new JSONObject();
                                try {

                                    jsonObject.put("RefStoreID", sharedPref.getString("assigned_store_id", "N/A"));
                                    jsonObject.put("PJPDate", sharedPref.getString("Date", "N/A"));
                                    jsonObject.put("RefUserID", UserDetails.getUName());
                                    jsonObject.put("DeploymentType", "PRE");
                                    jsonObject.put("POSMType", contact.getCategory());
                                    jsonObject.put("Quantity", contact.getAuthor());
                                    jsonObject.put("Condition", contact.getBookname());
                                    jsonObject.put("Image", merchantData.get_name());
                                    jsonObject.put("IsPrePost", "PRE");


                                    jsonArray.put(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Progress.closeProgress();
                                }
                            }


                            new FinalMerchantUpload().execute();

                            Log.d("final Json Array", jsonArray.toString());

                        } else Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();


                    } else Toast.makeText(getContext(), "Some Problem", Toast.LENGTH_LONG);

                } else Toast.makeText(getContext(), "Some Problem", Toast.LENGTH_LONG);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

}
