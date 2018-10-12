package com.kestone.kestoneretail.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.kestone.kestoneretail.DataHolders.PosmData;
import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.DatabasePackage.Reporting;
import com.kestone.kestoneretail.Progress;
import com.kestone.kestoneretail.R;
import com.kestone.kestoneretail.RecyclerAdapter.PosmTypeAdapter;
import com.master.permissionhelper.PermissionHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostDeployDetails extends Fragment implements View.OnClickListener {
    private static final int CAMERA_REQUEST = 1888;
    private TextView submitTv, productCategoryTv;
    private EditText quantityEt, commentsEdt;
    View view;
    private RelativeLayout layout_takephoto, backRl;
    private ImageView takePhoto;
    private CardView commentCard;
    Bitmap photo;


    public PostDeployDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_post_deploy_details, container, false);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("StoreDetails", Context.MODE_PRIVATE);


        initializeLayout();

        TextView textView = (TextView) view.findViewById(R.id.storeNameTv);
        textView.setText(sharedPref.getString("StoreType", "N/A"));
        TextView textView1 = (TextView) view.findViewById(R.id.storeCodeTv);
        textView1.setText(sharedPref.getString("StoreName", "N/A"));
        layout_takephoto = (RelativeLayout) view.findViewById(R.id.layout_takephoto);

        layout_takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper permissionHelper = new PermissionHelper(getActivity(), new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
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

        productCategoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences3 = getContext().getSharedPreferences("PosmData", Context.MODE_PRIVATE);
                populatePosm(sharedPreferences3.getString("PosmType", ""));

            }
        });

        return view;
    }

    private void initializeLayout() {
        takePhoto = (ImageView) view.findViewById(R.id.takePhoto);
        submitTv = (TextView) view.findViewById(R.id.submitTv);
        submitTv.setOnClickListener(this);
        backRl = (RelativeLayout) view.findViewById(R.id.backRl);
        backRl.setOnClickListener(this);
        productCategoryTv = (TextView) view.findViewById(R.id.productCategoryTv);
        quantityEt = (EditText) view.findViewById(R.id.quantityEt);
        commentsEdt = (EditText) view.findViewById(R.id.commentsEdt);
        commentCard = (CardView) view.findViewById(R.id.commentCard);
        commentCard.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            takePhoto.setImageBitmap(photo);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitTv:

                if (productCategoryTv.getText().length() > 0 && quantityEt.getText().length() > 0 && takePhoto.getDrawable() != null) {

                    SharedPreferences mSharedPreferences = getContext().getSharedPreferences("StoreDetails", Context.MODE_PRIVATE);

                    //Converting Image to string

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();

                    String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);


                    DatabaseHandler db = new DatabaseHandler(getContext(), "Postdeploy");

                    // Inserting Contacts
                    Log.d("Insert: ", "Inserting ..");
                    if (productCategoryTv.getText().toString().equalsIgnoreCase("MISC")) {
                        db.addContact(new Reporting(mSharedPreferences.getString("Id", ""), commentsEdt.getText().toString(), quantityEt.getText().toString(),
                                encodedImage, "", "", "","",mSharedPreferences.getString("assigned_store_id",""),mSharedPreferences.getString("Date",""),""));

                    } else {
                        db.addContact(new Reporting(mSharedPreferences.getString("Id", ""), productCategoryTv.getText().toString(), quantityEt.getText().toString(),
                                encodedImage, "", "","","",mSharedPreferences.getString("assigned_store_id",""),mSharedPreferences.getString("Date",""),""));
                    }

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new FinalMerchantList())
                            .commit();

                } else Toast.makeText(getContext(), "Fill All Details", Toast.LENGTH_SHORT).show();
                break;
            case R.id.backRl:
                getActivity().finish();

        }
    }

    public void populatePosm(String response) {

        if (response.length() > 0) {
            Progress.showProgress(getContext());


            ArrayList<PosmData> posmDataList = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    PosmData genreData = new PosmData();

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    genreData.setPosm_type(jsonObject1.getString("POSMType"));
                    posmDataList.add(genreData);

                }
                Progress.closeProgress();


                LayoutInflater inflater3 = LayoutInflater.from(getActivity());
                final View dialogLayout3 = inflater3.inflate(R.layout.custom_posm_type, null);
                final AlertDialog.Builder builder3 = new AlertDialog.Builder(getActivity());
                builder3.setView(dialogLayout3);
                builder3.setCancelable(true);
                final AlertDialog customAlertDialog3 = builder3.create();

                final RecyclerView recyclerView = (RecyclerView) dialogLayout3.findViewById(R.id.recyclerView);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(new PosmTypeAdapter(getContext(), posmDataList, customAlertDialog3, productCategoryTv, commentCard));


                customAlertDialog3.show();


            } catch (JSONException e) {
                e.printStackTrace();
                Progress.closeProgress();
            }
        } else
            Toast.makeText(getContext(), "No data, Sync Data First", Toast.LENGTH_SHORT).show();


    }

}
