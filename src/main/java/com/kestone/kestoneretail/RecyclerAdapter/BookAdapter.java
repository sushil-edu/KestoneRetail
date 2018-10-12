package com.kestone.kestoneretail.RecyclerAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.kestoneretail.ApiDetails.ApiUrls.ApiUrl;
import com.kestone.kestoneretail.DataHolders.BookData;
import com.kestone.kestoneretail.Progress;
import com.kestone.kestoneretail.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class BookAdapter extends RecyclerView.Adapter<BookAdapter.Alphabates> {
    Context context;
    ArrayList<BookData> kcDataArrayList;
    AlertDialog alertDialog;
    TextView textView, previousStockByTv;
    JSONObject jsonObj;
    DataOutputStream printout;
    String genre, author, product = "";

    public BookAdapter(Context context, ArrayList<BookData> kcDataArrayList, AlertDialog alertDialog, TextView textView, String genre
            , String author, TextView previousStockByTv) {
        this.context = context;
        this.kcDataArrayList = kcDataArrayList;
        this.alertDialog = alertDialog;
        this.textView = textView;
        this.genre = genre;
        this.author = author;
        this.previousStockByTv = previousStockByTv;
    }

    @Override
    public BookAdapter.Alphabates onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_type_cell, parent, false);
        return new Alphabates(v);
    }

    @Override
    public void onBindViewHolder(BookAdapter.Alphabates holder, int position) {

        final BookData bookData = kcDataArrayList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                textView.setText(bookData.getTitle());
                product = bookData.getTitle();
                if (product.length() > 0) {
                    // new StockFetch().execute();

                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        //new StockFetch().execute();
                    } else {
                        previousStockByTv.setText("");
                        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        holder.typeTv.setText(bookData.getTitle());


        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                textView.setText(bookData.getTitle());

            }
        });

    }

    @Override
    public int getItemCount() {
        return kcDataArrayList.size();
    }

    public class Alphabates extends RecyclerView.ViewHolder {
        TextView typeTv;
        RadioButton radioButton;

        //MaterialRippleLayout storeCell;
        public Alphabates(View itemView) {
            super(itemView);
            typeTv = (TextView) itemView.findViewById(R.id.typeTv);
            radioButton = (RadioButton) itemView.findViewById(R.id.rd1);

        }
    }

}




