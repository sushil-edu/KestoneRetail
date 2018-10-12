package com.kestone.kestoneretail.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import android.widget.ImageView;
import android.widget.RelativeLayout;

import android.widget.Toast;

import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.DatabasePackage.MerchantData;
import com.kestone.kestoneretail.DatabasePackage.MerchantHandler;
import com.kestone.kestoneretail.DatabasePackage.Reporting;
import com.kestone.kestoneretail.R;
import com.master.permissionhelper.PermissionHelper;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

public class MerchantDetailsFragment extends Fragment implements View.OnClickListener {
    private static final int CAMERA_REQUEST = 1888;
    private TextView submitTv, productCategoryTv, selectCapacity;
    View view;
    private EditText quantityEt;
    private RelativeLayout layout_takephoto, backRl;
    private ImageView takePhoto;
    Bitmap photo;


    public MerchantDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_merchant_details, container, false);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("StoreDetails", Context.MODE_PRIVATE);


        initializeLayout();

        TextView textView = (TextView) view.findViewById(R.id.storeCodeTv);
        textView.setText(sharedPref.getString("StoreType", "N/A"));
        TextView textView1 = (TextView) view.findViewById(R.id.storeNameTv);
        textView1.setText(sharedPref.getString("StoreName", "N/A"));
        layout_takephoto = (RelativeLayout) view.findViewById(R.id.layout_takephoto);

        layout_takephoto.setOnClickListener(new View.OnClickListener() {
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


        selectCapacity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater inflater3 = LayoutInflater.from(getActivity());
                final View dialogLayout3 = inflater3.inflate(R.layout.custom_posm_condition, null);
                final AlertDialog.Builder builder3 = new AlertDialog.Builder(getActivity());
                builder3.setView(dialogLayout3);
                builder3.setCancelable(true);
                final AlertDialog customAlertDialog3 = builder3.create();


                LinearLayout firstLin = (LinearLayout) dialogLayout3.findViewById(R.id.firstLin);
                LinearLayout secondLin = (LinearLayout) dialogLayout3.findViewById(R.id.secondLin);
                LinearLayout thirdLin = (LinearLayout) dialogLayout3.findViewById(R.id.thirdLin);
                RadioButton radioButton1 = (RadioButton) dialogLayout3.findViewById(R.id.rd_reason1);
                RadioButton radioButton2 = (RadioButton) dialogLayout3.findViewById(R.id.rd_reason2);
                RadioButton radioButton3 = (RadioButton) dialogLayout3.findViewById(R.id.rd_reason3);

                radioButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        selectCapacity.setText("Good");
                        customAlertDialog3.dismiss();
                    }
                });


                firstLin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selectCapacity.setText("Good");
                        customAlertDialog3.dismiss();
                    }
                });

                radioButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        selectCapacity.setText("Faded");
                        customAlertDialog3.dismiss();
                    }
                });

                secondLin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selectCapacity.setText("Faded");
                        customAlertDialog3.dismiss();
                    }
                });

                radioButton3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        selectCapacity.setText("Tore Down");
                        customAlertDialog3.dismiss();
                    }
                });


                thirdLin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectCapacity.setText("Tore Down");
                        customAlertDialog3.dismiss();
                    }
                });

                customAlertDialog3.show();


            }
        });


        productCategoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setMessage("POSM present?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                productCategoryTv.setText("Yes");

                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        productCategoryTv.setText("No");

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });


        return view;
    }

    private void initializeLayout() {
        takePhoto = (ImageView) view.findViewById(R.id.takePhoto);
        submitTv = (TextView) view.findViewById(R.id.submitTv);
        submitTv.setOnClickListener(this);

        productCategoryTv = (TextView) view.findViewById(R.id.productCategoryTv);
        quantityEt = (EditText) view.findViewById(R.id.quantityEt);
        selectCapacity = (TextView) view.findViewById(R.id.selectCapacity);

        backRl = (RelativeLayout) view.findViewById(R.id.backRl);
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

                if (productCategoryTv.getText().length() > 0 && quantityEt.getText().length() > 0
                        && selectCapacity.getText().length() > 0 && takePhoto.getDrawable() != null) {

                    SharedPreferences mSharedPreferences = getContext().getSharedPreferences("StoreDetails",Context.MODE_PRIVATE);



                    DatabaseHandler db = new DatabaseHandler(getContext(), "Predeploy");

                    // Inserting Contacts
                    Log.d("Insert: ", "Inserting ..");
                    db.addContact(new Reporting(mSharedPreferences.getString("Id",""),productCategoryTv.getText().toString(), quantityEt.getText().toString(),
                            selectCapacity.getText().toString(),"","","","",
                            mSharedPreferences.getString("assigned_store_id",""),mSharedPreferences.getString("Date",""),""));

                    // Inserting Contacts
                    Log.d("Insert: ", "Inserting ..");


                    //Converting Image to string

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();

                    String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                    MerchantHandler mb = new MerchantHandler(getContext(), "ImageDb");
                    mb.addMerchantData(new MerchantData(encodedImage));
                    // Inserting Images
                    Log.d("Image Insert: ", "Inserting ..");

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new PreDeployDetailsFragment())
                            .commit();

                } else
                    Toast.makeText(getContext(), "Fill All Details First", Toast.LENGTH_SHORT).show();

                break;

            case R.id.backRl:


                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new PreDeployDetailsFragment())
                        .commit();
        }
    }
}
