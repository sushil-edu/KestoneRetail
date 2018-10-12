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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.kestone.kestoneretail.ApiDetails.ApiUrls.ApiUrl;
import com.kestone.kestoneretail.DataHolders.UserDetails;
import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.DatabasePackage.Reporting;
import com.kestone.kestoneretail.Progress;
import com.kestone.kestoneretail.R;
import com.kestone.kestoneretail.RecyclerAdapter.PostDeployRecyclerAdapter;

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
public class FinalMerchantList extends Fragment implements View.OnClickListener {

    private TextView submitTv;
    View view;
    private RelativeLayout backRl;
    private JSONArray jsonArray;
    SharedPreferences sharedPref;
    List<Reporting> contactList;
    DatabaseHandler db2;
    DataOutputStream printout;
    private TextView storeCodeTv, storeNameTv;


    public FinalMerchantList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_final_merchant_list, container, false);

        initializeLayout();

        sharedPref = getActivity().getSharedPreferences("StoreDetails", Context.MODE_PRIVATE);
        storeNameTv.setText(sharedPref.getString("StoreName", "N/A"));
        storeCodeTv.setText(sharedPref.getString("StoreType", "N/A"));


        db2 = new DatabaseHandler(getContext(), "Postdeploy");

        // Reading all contacts
        contactList = db2.getAllContacts();
        Log.d("Reading: ", contactList.size() + "");

        RecyclerView recyclerView2 = (RecyclerView) view.findViewById(R.id.recyclerView2);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(linearLayoutManager2);
        recyclerView2.setAdapter(new PostDeployRecyclerAdapter(getContext(), contactList));

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new PostDeployDetails())
                        .commit();
            }
        });
        return view;
    }

    private void initializeLayout() {
        submitTv = (TextView) view.findViewById(R.id.tvConfirm);
        submitTv.setOnClickListener(this);
        backRl = (RelativeLayout) view.findViewById(R.id.layout_back);
        backRl.setOnClickListener(this);

        storeCodeTv = (TextView) view.findViewById(R.id.storeCodeTv);
        storeNameTv = (TextView) view.findViewById(R.id.storeNameTv);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvConfirm:

                if (contactList.size() > 0) {



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

                    jsonArray = new JSONArray();
                    JSONObject jsonObject;


                    for (int i = 0; i < contactList.size(); i++) {
                        Reporting contact = contactList.get(i);

                        jsonObject = new JSONObject();
                        try {

                            jsonObject.put("RefStoreID", sharedPref.getString("assigned_store_id", "N/A"));
                            jsonObject.put("PJPDate", sharedPref.getString("Date", "N/A"));
                            jsonObject.put("RefUserID", UserDetails.getUName());
                            jsonObject.put("DeploymentType", "POST");
                            jsonObject.put("POSMType", contact.getCategory());
                            jsonObject.put("Quantity", contact.getAuthor());
                            jsonObject.put("Condition", "done");
                            jsonObject.put("Image", contact.getBookname());
                            jsonObject.put("IsPrePost", "POST");

                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Progress.closeProgress();
                        }
                    }


                    new FinalMerchantUpload().execute();

                    Log.d("final Json Array", jsonArray.toString());

                } else Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layout_back:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new PostDeployDetails())
                        .commit();

        }

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

            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getString("retval").equalsIgnoreCase("Profile updated successfully.")) {
                    for (int i = 0; i < contactList.size(); i++) {
                        Reporting contact = contactList.get(i);
                        db2.deleteContact(contact);
                    }


                    Progress.closeProgress();

                    Toast.makeText(getContext(), "Data Uploaded", Toast.LENGTH_SHORT).show();

                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), "There was some Problem", Toast.LENGTH_SHORT).show();
                    Progress.closeProgress();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Progress.closeProgress();
            }


            Log.d("Collection Response", s);
        }
    }
}

