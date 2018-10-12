package com.kestone.kestoneretail.Fragments;


import android.app.Dialog;
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
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.kestone.kestoneretail.ApiDetails.ApiUrls.ApiUrl;
import com.kestone.kestoneretail.DataHolders.DistributorData;
import com.kestone.kestoneretail.DataHolders.UserDetails;
import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.DatabasePackage.Reporting;
import com.kestone.kestoneretail.Progress;
import com.kestone.kestoneretail.R;
import com.kestone.kestoneretail.RecyclerAdapter.DistributorAdapter;
import com.kestone.kestoneretail.RecyclerAdapter.MerchantStockAdapter;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class MerchantStockList extends Fragment implements View.OnClickListener {

    private TextView submitTv;
    View view;
    private RelativeLayout backRl;
    private JSONArray jsonArray;
    SharedPreferences sharedPref;
    public static List<Reporting> contactList;
    public static List<Reporting> authorList;
    RecyclerView recyclerView2;

    DatabaseHandler db2;
    DataOutputStream printout;
    private TextView storeCodeTv, storeNameTv;
    MerchantStockAdapter merchantStockAdapter;


    public MerchantStockList() {
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


        db2 = new DatabaseHandler(getContext(), "StockDb");

        // Reading all contacts
        contactList = db2.getContact(Integer.parseInt(sharedPref.getString("Id", "")));

        authorList = db2.getAllAuthors(Integer.parseInt(sharedPref.getString("Id", "")));
        Log.d("Reading: ", contactList.size() + "");

        recyclerView2 = (RecyclerView) view.findViewById(R.id.recyclerView2);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(linearLayoutManager2);
        merchantStockAdapter = new MerchantStockAdapter(getContext(), authorList);
        //recyclerView2.setAdapter(merchantStockAdapter);

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new MechantStock())
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
    public void onResume() {
        super.onResume();
        recyclerView2.setAdapter(merchantStockAdapter);
        merchantStockAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvConfirm:

                if (contactList.size() > 0) {

                    Time today = new Time(Time.getCurrentTimezone());
                    today.setToNow();
                    Log.d("Date", today.monthDay + "-" + today.month + "-" + today.year);

                    int month = today.month + 1;
                    String mo;
                    if (month < 10) {
                        mo = "0" + month;
                    } else mo = month + "";


                    String date = today.monthDay + "-" + mo + "-" + today.year;

                   // Progress.showProgress(getContext());

                    jsonArray = new JSONArray();
                    JSONObject jsonObject;

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("StoreDetails", getContext().MODE_PRIVATE);


//                    for (int i = 0; i < contactList.size(); i++) {
//                        Reporting contact = contactList.get(i);
//
//                        jsonObject = new JSONObject();
//                        try {
//
//                            jsonObject.put("PJPDate", contact.getDate());
//                            jsonObject.put("RefUserID", UserDetails.getUName());
//                            jsonObject.put("StockQty", contact.getStock());
//                            jsonObject.put("SalesQty", "");
//                            jsonObject.put("RefBookID", contact.getBookId());
//                            jsonObject.put("OrderQty", "");
//                            jsonObject.put("RefStoreID", contact.getStoreId());
//
//                            jsonArray.put(jsonObject);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Progress.closeProgress();
//                        }
//                    }
//
//
//                    new FinalMerchantUpload().execute();
                    String pjpDateStr = sharedPreferences.getString("Date","");
                    String storeId = sharedPreferences.getString("assigned_store_id","");

                    ArrayList<DistributorData> distributorList = new ArrayList<>();

                    SharedPreferences shrd = getActivity().getSharedPreferences("Distributor", Context.MODE_PRIVATE);
                    String distList = shrd.getString("DistributorList", "");
                    if (distList.length() > 0) {


                        try {
                            JSONArray jArr = new JSONArray(distList);

                            for (int i = 0; i < jArr.length(); i++) {
                                JSONObject jOb = jArr.getJSONObject(i);
                                DistributorData distributorData = new DistributorData();
                                distributorData.setDistributorId(jOb.getString("ID"));
                                distributorData.setDistributorName(jOb.getString("DistributorName"));
                                distributorData.setZone(jOb.getString("Zone"));
                                distributorData.setCity(jOb.getString("City"));

                                if(jOb.getString("Zone").equalsIgnoreCase(UserDetails.URegion)){
                                    distributorList.add(distributorData);
                                }


                            }

                            Log.d("dist Size", distributorList.size()+"");

                            Dialog dialog = new Dialog(getContext());
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.alert_distributor);
                            dialog.setCancelable(true);

                            RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(new DistributorAdapter(distributorList,pjpDateStr,storeId,getContext(),"Stock",dialog));


                            dialog.show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }}


                    Log.d("final Json Array", jsonArray.toString());

                } else Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layout_back:
                getActivity().finish();

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
                Url = ApiUrl.Retail;

                URL url = new URL(Url);
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e("Post", "Post");
                Log.e("URL", Url);
                Log.e("Params", jsonArray.toString());
                htp.setRequestMethod("POST");
                htp.setRequestProperty("Connection", "Keep-Alive");
                htp.setRequestProperty("Content-Type", "application/json");
                htp.setChunkedStreamingMode(1024);
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

                if (jsonObject.getString("retval").equalsIgnoreCase("Registration Successfull.")) {

                    DatabaseHandler databaseHandler = new DatabaseHandler(getContext(), "StockDb");
                    // databaseHandler.deleteAll("Report");

                    databaseHandler.deleteWithId(Integer.parseInt(sharedPref.getString("Id", "")));
                    Progress.closeProgress();

                    Toast.makeText(getContext(), "Data Uploaded", Toast.LENGTH_SHORT).show();
                    merchantStockAdapter.notifyDataSetChanged();

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
