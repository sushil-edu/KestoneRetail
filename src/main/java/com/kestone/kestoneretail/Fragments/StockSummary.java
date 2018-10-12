package com.kestone.kestoneretail.Fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.kestoneretail.ApiDetails.ApiListeners.ApiListener;
import com.kestone.kestoneretail.ApiDetails.ApiListeners.ApiResponse;
import com.kestone.kestoneretail.ApiDetails.ApiUrls.ApiUrl;
import com.kestone.kestoneretail.DataHolders.DistributorData;
import com.kestone.kestoneretail.DataHolders.UserDetails;
import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.DatabasePackage.Reporting;
import com.kestone.kestoneretail.Progress;
import com.kestone.kestoneretail.R;
import com.kestone.kestoneretail.RecyclerAdapter.DistributorAdapter;
import com.kestone.kestoneretail.RecyclerAdapter.StockRecyclerAdapter;

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
public class StockSummary extends Fragment {

    public static List<Reporting> authorsList;
    private LinearLayout backLl;
    private TextView confirmTv;
    private TextView storeCodeTv, storeNameTv;
    private StockRecyclerAdapter stockRecyclerAdapter;
    private DatabaseHandler db;
    RecyclerView recyclerView;


    public StockSummary() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stock_summary, container, false);
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new AddDataFragment())
                        .commit();
            }
        });

        initializeLayout(view);

        final SharedPreferences sharedPref = getActivity().getSharedPreferences("StoreDetails", Context.MODE_PRIVATE);
        storeNameTv.setText(sharedPref.getString("StoreName", "N/A"));
        storeCodeTv.setText(sharedPref.getString("StoreType", "N/A"));

        db = new DatabaseHandler(getContext(), "ReportingDb");

        authorsList = db.getAllAuthors(Integer.parseInt(sharedPref.getString("Id", "")));
        Log.d("authorsList", authorsList.toString());

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        stockRecyclerAdapter = new StockRecyclerAdapter(getContext(), authorsList);
        // recyclerView.setAdapter(stockRecyclerAdapter);

        view.findViewById(R.id.tvConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedpreferences = getContext().getSharedPreferences("StoreDetails", Context.MODE_PRIVATE);


                DatabaseHandler db = new DatabaseHandler(getContext(), "ReportingDb");
                List<Reporting> reportingList = db.getContact(Integer.parseInt(sharedpreferences.getString("Id", "")));


                JSONArray jsonArray = new JSONArray();

                //Progress.showProgress(getContext());

                for (int i = 0; i < reportingList.size(); i++) {
                    Reporting reporting = reportingList.get(i);
                    JSONObject jsonObject = new JSONObject();

                    try {

                        jsonObject.put("RefUserID", UserDetails.getUName());
                        jsonObject.put("RefStoreID", reporting.getStoreId());
                        jsonObject.put("RefBookID", reporting.getBookId());
                        jsonObject.put("StockQty", reporting.getStock());
                        jsonObject.put("SalesQty", reporting.getSales());
                        jsonObject.put("OrderQty", reporting.getOrder());
                        jsonObject.put("PJPDate", reporting.getDate());

                        jsonArray.put(jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Progress.closeProgress();
                    }
                }

                Log.d("Json Array", jsonArray.toString());

                if (jsonArray.length() > 0) {
//                    new DataPost(getActivity(), ApiUrl.Retail, jsonArray, "array").execute();
                    String pjpDateStr = sharedpreferences.getString("Date", "");
                    String storeId = sharedpreferences.getString("assigned_store_id", "");

                    ArrayList<DistributorData> distributorList = new ArrayList<DistributorData>();

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
                            dialog.setCancelable(false);

                            RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(new DistributorAdapter(distributorList,pjpDateStr,storeId,getContext(),"Sale",dialog));


                            dialog.show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        Toast.makeText(getContext(), "No Distributor, go to dashboard and sync data", Toast.LENGTH_SHORT).show();
                    }


                }


            }
        });

        backLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return view;
    }

    private void initializeLayout(View v) {

        backLl = (LinearLayout) v.findViewById(R.id.back_confirm);
        //confirmTv = (TextView) v.findViewById(R.id.tvConfirm);
        storeCodeTv = (TextView) v.findViewById(R.id.storeCodeTv);
        storeNameTv = (TextView) v.findViewById(R.id.storeNameTv);
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(stockRecyclerAdapter);
        recyclerView.getAdapter().notifyDataSetChanged();
    }


    public class DataPost extends AsyncTask<String, String, String> {
        private ApiResponse apiResponse;
        StringBuilder stringBuilder = new StringBuilder();
        String Url, Type = "";
        JSONObject jsonObject;
        JSONArray jsonArray;
        DataOutputStream printout;
        Activity activity;
        String dataType;


        public DataPost(Activity activity, String Url, JSONArray jsonArray, String dataType) {
            this.Url = Url;
            this.jsonArray = jsonArray;
            this.activity = activity;
            this.Type = "";
            this.dataType = dataType;
        }


        @Override
        protected String doInBackground(String... params) {
            try {
                //Log.e("Inside","Do in Background");
                URL url = new URL(Url);
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();
                if (dataType.equals("array")) {
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
            Progress.closeProgress();
            Log.e("Response", s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getString("retval").equals("Registration Successfull.")) {
                    SharedPreferences sharedPref = getActivity().getSharedPreferences("StoreDetails", Context.MODE_PRIVATE);

                    DatabaseHandler databaseHandler = new DatabaseHandler(getContext(), "");
                    // databaseHandler.deleteAll("Report");

                    databaseHandler.deleteWithId(Integer.parseInt(sharedPref.getString("Id", "")));
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), "There was some Problem", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}
