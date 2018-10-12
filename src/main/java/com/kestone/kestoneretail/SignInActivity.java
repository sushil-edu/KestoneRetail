package com.kestone.kestoneretail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kestone.kestoneretail.ApiDetails.ApiListeners.ApiListener;
import com.kestone.kestoneretail.ApiDetails.ApiListeners.ApiResponse;
import com.kestone.kestoneretail.ApiDetails.ApiUrls.ApiUrl;
import com.kestone.kestoneretail.DataHolders.UserDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignInActivity extends AppCompatActivity implements ApiResponse {

    private Button signInBtn;
    private EditText loginPassword, loginUser;
    String hash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getSupportActionBar().hide();

        LinearLayout syncLl = (LinearLayout) findViewById(R.id.syncLl);
        signInBtn = (Button) findViewById(R.id.signinBtn);
        loginPassword = (EditText) findViewById(R.id.etPassword);
        loginUser = (EditText) findViewById(R.id.etUsername);

        syncLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Progress.showProgress(SignInActivity.this);
                new ApiListener(SignInActivity.this, ApiUrl.Login, "Get").execute();
            }
        });


        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("SingIn", "Sign in clicked!!");

                SharedPreferences sharedPreferences = getSharedPreferences("SignInData", MODE_PRIVATE);
                String response = sharedPreferences.getString("SignIn", "");


                if (loginUser.getText().toString().length() <= 0 || loginPassword.getText().toString().length() <= 0) {
                    Toast.makeText(SignInActivity.this, "Please Enter Vaild Username & Passoword", Toast.LENGTH_SHORT).show();
                } else {

                    if (response.length() > 0) {

                        int flag=0;

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                if(jsonObject.getString("EmailId").equals(loginUser.getText().toString())&&
                                        jsonObject.getString("Password").equals(loginPassword.getText().toString())){

                                    flag = 1;

                                    UserDetails.setUName(jsonObject.getString("EmailId"));
                                    UserDetails.setUEmail(jsonObject.getString("Password"));
                                    UserDetails.setURegion(jsonObject.getString("Region"));

                                    SharedPreferences sharedPreferences1 = getSharedPreferences("SignInCredentials", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                                    editor.putString("Name", jsonObject.getString("EmailId"));
                                    editor.putString("Password", jsonObject.getString("Password"));
                                    editor.putString("Region", UserDetails.URegion);
                                    editor.apply();


                                    if (jsonObject.getString("UserType").equalsIgnoreCase("0")) {
                                        Intent intent = new Intent(SignInActivity.this, DashBoard.class);
                                        intent.putExtra("email", UserDetails.getUName());
                                        intent.putExtra("Region",UserDetails.URegion);
                                        startActivity(intent);
                                        finish();
                                    } else if (jsonObject.getString("UserType").equalsIgnoreCase("1")) {
                                        Intent intent = new Intent(SignInActivity.this, MerchantDashBoard.class);
                                        intent.putExtra("email", UserDetails.getUName());
                                        intent.putExtra("Region",UserDetails.URegion);
                                        startActivity(intent);
                                        finish();
                                    }

                                    break;


                                }

                            }

                            if(flag==0){
                                Toast.makeText(SignInActivity.this, "Wrong Email or Password", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else
                        Toast.makeText(SignInActivity.this, "Sync Data First", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    @Override
    public void onApiResponse(String response) {
        Progress.closeProgress();
        Log.e("Response", response);

        if (response.length() > 0) {


            SharedPreferences sharedPreferences = getSharedPreferences("SignInData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("SignIn", response);
            editor.commit();


        } else Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();

    }

}
