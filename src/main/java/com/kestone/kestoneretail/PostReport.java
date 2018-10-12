package com.kestone.kestoneretail;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.kestone.kestoneretail.ApiDetails.ApiUrls.ApiUrl;
import com.kestone.kestoneretail.Fragments.FinalMerchantList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostReport extends AppCompatActivity {

    public static String MyPREFERENCES = "StoreDetails";
    SharedPreferences sharedPreferences1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_report);

        getSupportActionBar().setTitle("Post Deployment Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences1 = getSharedPreferences("GenreData", MODE_PRIVATE);

        //SharedPrferences data Saving
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString("StoreType", getIntent().getStringExtra("StoreType"));
        editor.putString("StoreName", getIntent().getStringExtra("StoreName"));
        editor.putString("Location", getIntent().getStringExtra("Location"));
        editor.putString("ContactName", getIntent().getStringExtra("ContactName"));
        editor.putString("ContactNumber", getIntent().getStringExtra("ContactNumber"));
        editor.putString("Date", getIntent().getStringExtra("Date"));
        editor.putString("StoreCode", getIntent().getStringExtra("StoreCode"));
        editor.putString("Label", getIntent().getStringExtra("Label"));
        editor.putString("Id", getIntent().getStringExtra("Id"));
        editor.commit();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new FinalMerchantList())
                .commit();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sales_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_syncData) {
            new GenreFetch().execute();
        } else onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    public class GenreFetch extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Progress.showProgress(PostReport.this);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.Category;

                URL url = new URL(Url);
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e("Get", "Get");
                Log.e("URL", Url);
                htp.setRequestMethod("GET");
                htp.setRequestProperty("Content-Type", "application/json");
                htp.setDoInput(true);
                htp.setUseCaches(false);
                htp.connect();


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
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString("GenreType", s);
            editor.apply();

            new BookFetch().execute();
        }
    }
    class BookFetch extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress.showProgress(MainActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.Book;

                URL url = new URL(Url);
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e("Get", "Get");
                Log.e("URL", Url);
                htp.setRequestMethod("GET");
                htp.setRequestProperty("Content-Type", "application/json");
                htp.setDoInput(true);
                htp.setUseCaches(false);
                htp.connect();


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

            //Progress.closeProgress();

            Log.d("getALlBooks Reponse",s);
            SharedPreferences sharedPreferences1 = getSharedPreferences("BookData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString("BookType", s);
            editor.apply();

            new AuthorFetch().execute();

        }
    }
    class AuthorFetch extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress.showProgress(FillDetailsActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.Author;

                URL url = new URL(Url);
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e("Get", "Get");
                Log.e("URL", Url);
                htp.setRequestMethod("GET");
                htp.setRequestProperty("Content-Type", "application/json");
                htp.setDoInput(true);
                htp.setUseCaches(false);
                htp.connect();


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


            SharedPreferences sharedPreferences1 = getSharedPreferences("AuthoData",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString("AuthorType",s);
            editor.apply();

            new ReasonFetch().execute();

        }
    }

    class ReasonFetch extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress.showProgress(FillDetailsActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.Reason;

                URL url = new URL(Url);
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e("Get", "Get");
                Log.e("URL", Url);
                htp.setRequestMethod("GET");
                htp.setRequestProperty("Content-Type", "application/json");
                htp.setDoInput(true);
                htp.setUseCaches(false);
                htp.connect();


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

            SharedPreferences sharedPreferences1 = getSharedPreferences("ReasonData",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString("ReasonType",s);
            editor.apply();

            new PosmFetch().execute();

        }
    }

    class PosmFetch extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress.showProgress(FillDetailsActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.Posm;

                URL url = new URL(Url);
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e("Get", "Get");
                Log.e("URL", Url);
                htp.setRequestMethod("GET");
                htp.setRequestProperty("Content-Type", "application/json");
                htp.setDoInput(true);
                htp.setUseCaches(false);
                htp.connect();


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

            Log.d("Author",s);


            SharedPreferences sharedPreferences1 = getSharedPreferences("PosmData",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString("PosmType",s);
            editor.apply();

            //Progress.closeProgress();

            new DistributorFetch().execute();

        }
    }

    class DistributorFetch extends AsyncTask<String, String, String> {

        StringBuilder stringBuilder = new StringBuilder();
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress.showProgress(FillDetailsActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Url = ApiUrl.Distributors;

                URL url = new URL(Url);
                //Log.e("URL",Url);
                HttpURLConnection htp = (HttpURLConnection) url.openConnection();

                Log.e("Get", "Get");
                Log.e("URL", Url);
                htp.setRequestMethod("GET");
                htp.setRequestProperty("Content-Type", "application/json");
                htp.setDoInput(true);
                htp.setUseCaches(false);
                htp.connect();


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

            Log.d("Distributor",s);

            Progress.closeProgress();

            SharedPreferences sharedPreferences1 = getSharedPreferences("Distributor",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString("DistributorList",s);
            editor.apply();

        }
    }
}
