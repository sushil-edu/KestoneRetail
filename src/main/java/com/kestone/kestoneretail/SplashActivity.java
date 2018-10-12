package com.kestone.kestoneretail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.kestone.kestoneretail.DataHolders.UserDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {


    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar toolbar = getSupportActionBar();
        toolbar.hide();

        SharedPreferences sharedPreferences1 = getSharedPreferences("SignInCredentials", MODE_PRIVATE);
        String name = sharedPreferences1.getString("Name", "");
        String password = sharedPreferences1.getString("Password", "");

        if (name.length() > 0 && password.length() > 0) {

            autoSignIn(sharedPreferences1.getString("Name", ""), sharedPreferences1.getString("Password", ""));

        } else {
            intent = new Intent(this, SignInActivity.class);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    finish();
                }
            }, 2000);

        }
    }

    public void autoSignIn(String name, String pass) {

        SharedPreferences sharedPreferences = getSharedPreferences("SignInData", MODE_PRIVATE);
        String response = sharedPreferences.getString("SignIn", "");


        if (response.length() > 0) {

            try {

                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (jsonObject.getString("EmailId").equals(name) &&
                            jsonObject.getString("Password").equals(pass)) {


                        UserDetails.setUName(jsonObject.getString("EmailId"));
                        UserDetails.setUEmail(jsonObject.getString("Password"));
                        UserDetails.setURegion(jsonObject.getString("Region"));

                        SharedPreferences sharedPreferences1 = getSharedPreferences("SignInCredentials", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences1.edit();
                        editor.putString("Name", UserDetails.getUName());
                        editor.putString("Password", pass);
                        editor.putString("Region", UserDetails.URegion);
                        editor.apply();


                        if (jsonObject.getString("UserType").equalsIgnoreCase("0")) {
                            final Intent intent = new Intent(SplashActivity.this, DashBoard.class);
                            intent.putExtra("email", UserDetails.getUName());
                            intent.putExtra("Region", UserDetails.URegion);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(intent);
                                    finish();
                                }
                            }, 2000);
                        } else if (jsonObject.getString("UserType").equalsIgnoreCase("1")) {
                            final Intent intent = new Intent(SplashActivity.this, MerchantDashBoard.class);
                            intent.putExtra("email", UserDetails.getUName());
                            intent.putExtra("Region", UserDetails.URegion);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(intent);
                                    finish();
                                }
                            }, 2000);
                        }

                        break;


                    }

                }


            } catch (JSONException e) {
            }


        }

    }
}

