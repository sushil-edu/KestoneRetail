package com.kestone.kestoneretail.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Base64;
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
import com.kestone.kestoneretail.DataHolders.ReasonData;
import com.kestone.kestoneretail.DataHolders.UserDetails;
import com.kestone.kestoneretail.Progress;
import com.kestone.kestoneretail.R;
import com.kestone.kestoneretail.RecyclerAdapter.ResonTypeAdapter;
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
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MerchantFragment extends Fragment {


    private static final int CAMERA_REQUEST = 1888;
    View v;
    private RelativeLayout takePictureRl, nextRl, backRl;
    private LinearLayout merchandisingallowedLl, reasonLl;
    private ImageView imageview;
    private TextView merchandisingTv, submitTv, nextTv, merchandisingStatus, reasonTv;
    private CardView reasonCard,commentCard;
    private EditText commentsEdt;
    Bitmap photo;
    SharedPreferences sharedPref;

    JSONObject jsonObject;

    public MerchantFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_merchant, container, false);

        sharedPref = getActivity().getSharedPreferences("StoreDetails", Context.MODE_PRIVATE);


        initializeLayout();

        TextView textView = (TextView) v.findViewById(R.id.txtStoreType);
        textView.setText(sharedPref.getString("StoreType", "N/A"));
        TextView textView1 = (TextView) v.findViewById(R.id.txtStoreName);
        textView1.setText(sharedPref.getString("StoreName", "N/A"));

        takePictureRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper permissionHelper = new PermissionHelper(getActivity(), new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                permissionHelper.request(new PermissionHelper.PermissionCallback() {
                    @Override
                    public void onPermissionGranted() {

                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }

                    @Override
                    public void onPermissionDenied() {
                        Log.d("Permission", "onPermissionDenied() called");
                    }

                    @Override
                    public void onPermissionDeniedBySystem() {
                        Log.d("Permission", "onPermissionDeniedBySystem() called");
                    }
                });
            }
        });

        merchandisingallowedLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setMessage("Merchandising Allowed?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                merchandisingStatus.setText("Yes");
                                reasonCard.setVisibility(View.GONE);
                                submitTv.setVisibility(View.INVISIBLE);
                                nextTv.setVisibility(View.VISIBLE);
                                nextRl.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        merchandisingStatus.setText("No");
                        reasonCard.setVisibility(View.VISIBLE);
                        submitTv.setVisibility(View.VISIBLE);
                        nextTv.setVisibility(View.INVISIBLE);
                        nextRl.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        reasonLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences sharedPreferences3 = getContext().getSharedPreferences("ReasonData", Context.MODE_PRIVATE);
                populateCity(sharedPreferences3.getString("ReasonType", ""));



            }
        });

        backRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        nextRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (merchandisingStatus.getText().length() > 0 && imageview.getDrawable() != null) {

                    Progress.showProgress(getContext());

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();

                    String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);


                    if (merchandisingStatus.getText().toString().equalsIgnoreCase("Yes")) {

                        SharedPreferences sharedPreferences =
                                getContext().getSharedPreferences("MerchantStatusFile", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        Time today = new Time(Time.getCurrentTimezone());
                        today.setToNow();
                        Log.d("Date", today.monthDay+"-"+today.month+"-"+today.year);

                        int month = today.month+1;
                        String mo;
                        if(month<10){
                            mo = "0"+month;
                        }else mo = month +"";


                        String date = today.year+"-"+mo+"-"+today.monthDay;

                        editor.putString("store_id", sharedPref.getString("assigned_store_id", "N/A"));
                        editor.putString("store_allowed", merchandisingStatus.getText().toString());
                        editor.putString("store_comment", "");
                        editor.putString("image", encodedImage);
                        editor.putString("merchant_ofcr_id", UserDetails.UName);
                        editor.putString("pjp_date", date);
                        editor.putString("created", date);
                        editor.commit();

                        Progress.closeProgress();

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new PreDeployDetailsFragment())
                                .commit();




                    } else if (merchandisingStatus.getText().toString().equalsIgnoreCase("No")) {

                        if (reasonTv.getText().length() > 0) {

                            jsonObject = new JSONObject();
                            try {



                                Time today = new Time(Time.getCurrentTimezone());
                                today.setToNow();
                                Log.d("Date", today.monthDay+"-"+today.month+"-"+today.year);

                                int month = today.month+1;
                                String mo;
                                if(month<10){
                                    mo = "0"+month;
                                }else mo = month +"";


                                String date = today.monthDay+"-"+mo+"-"+today.year;


                                if(reasonTv.getText().toString().equalsIgnoreCase("others")){
                                    jsonObject.put("ResoanForNotAllow",commentsEdt.getText().toString());
                                }else {
                                    jsonObject.put("ResoanForNotAllow",reasonTv.getText().toString());
                                }

                                jsonObject.put("RefStoreID",sharedPref.getString("assigned_store_id","N/A"));
                                jsonObject.put("Photo",encodedImage);
                                jsonObject.put("RefUserID", UserDetails.UName);
                                jsonObject.put("PJPDate",sharedPref.getString("Date","N/A"));
                                jsonObject.put("MerchandiserAllowed","No");


                                new MerchantStatus().execute();

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Progress.closeProgress();
                            }


                        } else
                            Toast.makeText(getContext(), "Select Reason", Toast.LENGTH_SHORT).show();


                    }


                } else Toast.makeText(getContext(), "Fill Details", Toast.LENGTH_SHORT).show();




            }
        });

        return v;
    }

    private void initializeLayout() {
        takePictureRl = (RelativeLayout) v.findViewById(R.id.layout_takephoto);
        imageview = (ImageView) v.findViewById(R.id.takePhoto);
        merchandisingallowedLl = (LinearLayout) v.findViewById(R.id.layout_merchandisingallowed);
        merchandisingTv = (TextView) v.findViewById(R.id.txtMershadisingAllowed);
        reasonCard = (CardView) v.findViewById(R.id.notAllow);
        nextTv = (TextView) v.findViewById(R.id.nextb);
        nextRl = (RelativeLayout) v.findViewById(R.id.layout_next);
        submitTv = (TextView) v.findViewById(R.id.submitTv);
        merchandisingStatus = (TextView) v.findViewById(R.id.txtMAllowed);
        reasonTv = (TextView) v.findViewById(R.id.txtNotAllowed);
        reasonLl = (LinearLayout) v.findViewById(R.id.layout_resignfornotallowed);
        backRl = (RelativeLayout) v.findViewById(R.id.layout_back);
        commentCard = (CardView) v.findViewById(R.id.commentCard);
        commentCard.setVisibility(View.GONE);
        commentsEdt = (EditText) v.findViewById(R.id.commentsEdt);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            imageview.setImageBitmap(photo);
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
                jsonArray.put(jsonObject);

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

            Progress.closeProgress();

            try {
                JSONObject jObj = new JSONObject(s);
                if (jObj.has("retval")) {

                    if (jObj.getString("retval").equalsIgnoreCase("Merchandiser successfully.")) {

                            getActivity().finish();

                    } else Toast.makeText(getContext(), "Some Problem", Toast.LENGTH_LONG);

                } else Toast.makeText(getContext(), "Some Problem", Toast.LENGTH_LONG);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }

    public void populateCity(String response) {

        Log.d("reason",response);

        if (response.length() > 0) {
            Progress.showProgress(getContext());



            ArrayList<ReasonData> reasonDataList = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    ReasonData genreData = new ReasonData();

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    genreData.setReason(jsonObject1.getString("Reason"));
                    reasonDataList.add(genreData);

                }
                Progress.closeProgress();


                LayoutInflater inflater3 = LayoutInflater.from(getActivity());
                final View dialogLayout3 = inflater3.inflate(R.layout.custom_reason, null);
                final AlertDialog.Builder builder3 = new AlertDialog.Builder(getActivity());
                builder3.setView(dialogLayout3);
                builder3.setCancelable(true);
                final AlertDialog customAlertDialog3 = builder3.create();

                final RecyclerView recyclerView = (RecyclerView) dialogLayout3.findViewById(R.id.recyclerView);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(new ResonTypeAdapter(getContext(), reasonDataList, customAlertDialog3, reasonTv,commentCard));


                customAlertDialog3.show();


            } catch (JSONException e) {
                e.printStackTrace();
                Progress.closeProgress();
            }
        } else
            Toast.makeText(getContext(), "No data, Sync Data First", Toast.LENGTH_SHORT).show();


    }


}
