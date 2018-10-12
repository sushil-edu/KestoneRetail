package com.kestone.kestoneretail.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.kestoneretail.DataHolders.BookData;
import com.kestone.kestoneretail.DataHolders.GenreData;
import com.kestone.kestoneretail.DataHolders.HolderFlag;
import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.DatabasePackage.Reporting;
import com.kestone.kestoneretail.Progress;
import com.kestone.kestoneretail.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import in.galaxyofandroid.widgets.AwesomeEditText;

public class AddDataFragment extends Fragment {
    AutoCompleteTextView authorByTv;
    private RecyclerView recyclerView;
    private LinearLayout layout_collectiontype;
    private TextView txtGenreType, authorTv;
    public ArrayList<BookData> bookDataList = new ArrayList<>();
    private BookListAdapter bookListAdapter;
    HashMap<Integer, Integer> salesMap;
    HashMap<Integer, Integer> stockMap;
    HashMap<Integer, Integer> orderMap;
    CardView authorCard;


    public AddDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_data, container, false);


        authorTv = (TextView) view.findViewById(R.id.authorTv);
        authorCard = (CardView) view.findViewById(R.id.authorCard);
        txtGenreType = (TextView) view.findViewById(R.id.txtGenreType);
        TextView tvConfirm = (TextView) view.findViewById(R.id.tvConfirm);
        layout_collectiontype = (LinearLayout) view.findViewById(R.id.layout_collectiontype);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookListAdapter = new BookListAdapter(getContext(), tvConfirm, bookDataList);
        recyclerView.setAdapter(bookListAdapter);


        SharedPreferences sharedPreferences1 = getContext().getSharedPreferences("AuthoData", Context.MODE_PRIVATE);
        List<String> AuthorList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(sharedPreferences1.getString("AuthorType", ""));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObj = jsonArray.getJSONObject(i);
                AuthorList.add(jObj.getString("Author"));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.select_dialog_item, AuthorList);
        authorByTv = (AutoCompleteTextView) view.findViewById(R.id.authorByTv);
        authorByTv.setThreshold(1);
        authorByTv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        authorByTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                bookDataList.clear();
                bookListAdapter.notifyDataSetChanged();
                salesMap = new LinkedHashMap<>();
                salesMap.clear();
                stockMap = new LinkedHashMap<>();
                stockMap.clear();
                orderMap = new LinkedHashMap<>();
                orderMap.clear();

                if (s.length() > 0 && txtGenreType.getText().toString().length() > 0) {

                    SharedPreferences sharedPreferences1 = getContext().getSharedPreferences("BookData", Context.MODE_PRIVATE);

                    if (authorByTv.getText().toString().equalsIgnoreCase("All")) {
                        populateAllBook(sharedPreferences1.getString("BookType", ""));
                    } else {

                        populateBook(sharedPreferences1.getString("BookType", ""));

                    }


                } else if (s.length() == 0) {

                }
            }
        });


        view.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new StockSummary())
                        .commit();
            }
        });

        layout_collectiontype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                authorByTv.setText("");
                bookDataList.clear();
                bookListAdapter.notifyDataSetChanged();

                SharedPreferences sharedPreferences1 = getContext().getSharedPreferences("GenreData", Context.MODE_PRIVATE);
                populateGenre(sharedPreferences1.getString("GenreType", ""));
            }
        });


        return view;
    }


    public void populateBook(String response) {

        Log.d("Saved Books", response);

        if (response.length() > 0) {
            Progress.showProgress(getContext());

            Log.d("Books ", "OK");
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {

                    BookData bookData = new BookData();

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    if (jsonObject1.getString("Author").contains(authorByTv.getText().toString())) {
                        List<String> authorList = Arrays.asList(jsonObject1.getString("Category").split(","));
                        for (int k = 0; k < authorList.size(); k++) {

                            Log.d("Author", authorList.get(k));

                            if (txtGenreType.getText().toString().equals(authorList.get(k))) {
                                bookData.setId(jsonObject1.getString("Id"));
                                bookData.setTitle(jsonObject1.getString("BookTitle"));
                                bookData.setAuthor(jsonObject1.getString("Author"));
                                bookDataList.add(bookData);
                            }
                        }
                    }
                }

                Progress.closeProgress();

                if (bookDataList.size() > 0) {

                    bookListAdapter.notifyDataSetChanged();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Progress.closeProgress();
            }


            Progress.closeProgress();

        } else Toast.makeText(getContext(), "No Data, Sync Data First", Toast.LENGTH_SHORT).show();
    }

    public void populateAllBook(String response) {

        Log.d("Save Book", response);

        Log.d("title", "title");

        if (response.length() > 0) {
            Progress.showProgress(getContext());


            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {

                    BookData bookData = new BookData();

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    bookData.setId(jsonObject1.getString("Id"));
                    bookData.setTitle(jsonObject1.getString("BookTitle"));
                    bookData.setAuthor(jsonObject1.getString("Author"));
                    bookDataList.add(bookData);

                    break;


                }

                Progress.closeProgress();


                if (bookDataList.size() > 0) {
                    bookListAdapter.notifyDataSetChanged();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Progress.closeProgress();
            }


            Progress.closeProgress();

        } else Toast.makeText(getContext(), "No Data, Sync Data First", Toast.LENGTH_SHORT).show();
    }

    public void populateGenre(String response) {

        Progress.showProgress(getContext());


        if (response.length() > 0) {

            ArrayList<GenreData> genreDataArrayList = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    GenreData genreData = new GenreData();

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    genreData.setId(jsonObject1.getString("ID"));
                    genreData.setCategory_type(jsonObject1.getString("CategoryType"));
                    genreDataArrayList.add(genreData);

                }
                Progress.closeProgress();

                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View dialogLayout = inflater.inflate(R.layout.genre_type_alert, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialogLayout);

                final AlertDialog customAlertDialog = builder.create();

                final RecyclerView recyclerView = (RecyclerView) dialogLayout.findViewById(R.id.recyclerView);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(new GenreAdapter(getContext(), genreDataArrayList, customAlertDialog, txtGenreType));


                customAlertDialog.show();


            } catch (JSONException e) {
                e.printStackTrace();
                Progress.closeProgress();
            }


        } else {
            Toast.makeText(getContext(), "No data, Sync Data First", Toast.LENGTH_SHORT).show();
            Progress.closeProgress();
        }

        Progress.closeProgress();

    }

    public void populateTopBook(String response) {

        Log.d("Saved Books", response);

        if (response.length() > 0) {
            Progress.showProgress(getContext());

            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {

                    BookData bookData = new BookData();

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    //  if (jsonObject1.getString("Author").contains(authorByTv.getText().toString())) {
                    List<String> authorList = Arrays.asList(jsonObject1.getString("Category").split(","));
                    for (int k = 0; k < authorList.size(); k++) {


                        Log.d("Author", authorList.get(k));

                        if (txtGenreType.getText().toString().equals(authorList.get(k))) {
                            bookData.setId(jsonObject1.getString("Id"));
                            bookData.setTitle(jsonObject1.getString("BookTitle"));
                            bookData.setAuthor(jsonObject1.getString("Author"));
                            bookDataList.add(bookData);
                        }
                    }
                }

                // }

                Progress.closeProgress();

                if (bookDataList.size() > 0) {

                    bookListAdapter.notifyDataSetChanged();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Progress.closeProgress();
            }


            Progress.closeProgress();

        } else Toast.makeText(getContext(), "No Data, Sync Data First", Toast.LENGTH_SHORT).show();
    }

    class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.MyHolder> {

        Context context;
        TextView tvConfirm;

        private ArrayList<BookData> bookData;

        public BookListAdapter(Context context, TextView tvConfirm, ArrayList<BookData> bookData) {

            this.context = context;
            this.tvConfirm = tvConfirm;
            this.bookData = bookData;


        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_cell, parent, false);
            tvConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Sales Map", salesMap.toString());
                    Log.d("Stock Map", stockMap.toString());
                    Log.d("Order Map", orderMap.toString());
                    if (txtGenreType.getText().length() > 0) {

                        if (txtGenreType.getText().toString().contains("Top")) {

                            if (salesMap.size() == 0 && stockMap.size() == 0 && orderMap.size() == 0) {
                                Toast.makeText(context, "No data to save", Toast.LENGTH_SHORT).show();
                            } else {
                                Progress.showProgress(context);

                                SharedPreferences mSharedPreferences = getContext().getSharedPreferences("StoreDetails", Context.MODE_PRIVATE);

                                for (int i = 0; i < bookData.size(); i++) {
                                    if (stockMap.containsKey(i) || salesMap.containsKey(i) || orderMap.containsKey(i)) {
                                        BookData bookDataObj = bookData.get(i);
                                        String stockStr, salesStr, orderStr;

                                        if (stockMap.containsKey(i)) {
                                            stockStr = stockMap.get(i) + "";
                                        } else stockStr = "0";

                                        if (salesMap.containsKey(i)) {
                                            salesStr = salesMap.get(i) + "";
                                        } else salesStr = "0";

                                        if (orderMap.containsKey(i)) {
                                            orderStr = orderMap.get(i) + "";
                                        } else orderStr = "0";

                                        Log.d("PjpId", mSharedPreferences.getString("Id", ""));

                                        DatabaseHandler db = new DatabaseHandler(context, "ReportingDb");
                                        db.addContact(new Reporting(mSharedPreferences.getString("Id", ""), txtGenreType.getText().toString(),
                                                bookDataObj.getAuthor(), bookDataObj.getTitle(), stockStr, salesStr, orderStr, bookDataObj.getId(),
                                                mSharedPreferences.getString("assigned_store_id", ""), mSharedPreferences.getString("Date", ""), ""));


                                    }
                                }

                                Progress.closeProgress();
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.container, new StockSummary())
                                        .commit();
                            }
                        }else if(!txtGenreType.getText().toString().contains("Top") && authorByTv.getText().length() > 0){

                            if (salesMap.size() == 0 && stockMap.size() == 0 && orderMap.size() == 0) {
                                Toast.makeText(context, "No data to save", Toast.LENGTH_SHORT).show();
                            } else {
                                Progress.showProgress(context);

                                SharedPreferences mSharedPreferences = getContext().getSharedPreferences("StoreDetails", Context.MODE_PRIVATE);

                                for (int i = 0; i < bookData.size(); i++) {
                                    if (stockMap.containsKey(i) || salesMap.containsKey(i) || orderMap.containsKey(i)) {
                                        BookData bookDataObj = bookData.get(i);
                                        String stockStr, salesStr, orderStr;

                                        if (stockMap.containsKey(i)) {
                                            stockStr = stockMap.get(i) + "";
                                        } else stockStr = "0";

                                        if (salesMap.containsKey(i)) {
                                            salesStr = salesMap.get(i) + "";
                                        } else salesStr = "0";

                                        if (orderMap.containsKey(i)) {
                                            orderStr = orderMap.get(i) + "";
                                        } else orderStr = "0";

                                        Log.d("PjpId", mSharedPreferences.getString("Id", ""));

                                        DatabaseHandler db = new DatabaseHandler(context, "ReportingDb");
                                        db.addContact(new Reporting(mSharedPreferences.getString("Id", ""), txtGenreType.getText().toString(),
                                                bookDataObj.getAuthor(), bookDataObj.getTitle(), stockStr, salesStr, orderStr, bookDataObj.getId(),
                                                mSharedPreferences.getString("assigned_store_id", ""), mSharedPreferences.getString("Date", ""), ""));


                                    }
                                }

                                Progress.closeProgress();
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.container, new StockSummary())
                                        .commit();
                            }
                        }else {
                            Toast.makeText(context, "Enter Author", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(context, "Enter Category", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {

            if (stockMap.containsKey(position)) {

                holder.stockEt.setText(stockMap.get(position) + "");

            } else holder.stockEt.setText("");


            if (salesMap.containsKey(position)) {

                holder.salesEt.setText(salesMap.get(position) + "");

            } else holder.salesEt.setText("");


            if (orderMap.containsKey(position)) {

                holder.orderEt.setText(orderMap.get(position) + "");

            } else holder.orderEt.setText("");

            BookData bookDataObj = bookData.get(position);
            holder.bookNameTv.setText(bookDataObj.getTitle());


        }


        @Override
        public int getItemCount() {
            return bookData.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            AwesomeEditText stockEt, salesEt, orderEt;
            TextView bookNameTv;

            public MyHolder(View itemView) {
                super(itemView);
                stockEt = (AwesomeEditText) itemView.findViewById(R.id.stockEt);
                salesEt = (AwesomeEditText) itemView.findViewById(R.id.salesEt);
                orderEt = (AwesomeEditText) itemView.findViewById(R.id.orderEt);
                bookNameTv = (TextView) itemView.findViewById(R.id.bookNameTv);

                stockEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() > 0) {
                            stockMap.put(getAdapterPosition(), Integer.parseInt(s.toString()));
                        } else if (s.length() == 0 && stockMap.containsKey(getAdapterPosition())) {
                            stockMap.remove(getAdapterPosition());
                        }
                    }
                });


                salesEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() > 0) {
                            salesMap.put(getAdapterPosition(), Integer.parseInt(s.toString()));
                        } else if (s.length() == 0 && salesMap.containsKey(getAdapterPosition())) {
                            salesMap.remove(getAdapterPosition());
                        }
                    }
                });

                orderEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() > 0) {
                            orderMap.put(getAdapterPosition(), Integer.parseInt(s.toString()));
                        } else if (s.length() == 0 && orderMap.containsKey(getAdapterPosition())) {
                            orderMap.remove(getAdapterPosition());
                        }
                    }
                });

            }
        }
    }

    public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.Alphabates> {
        Context context;
        ArrayList<GenreData> kcDataArrayList;
        AlertDialog alertDialog;
        TextView textView;


        public GenreAdapter(Context context, ArrayList<GenreData> kcDataArrayList, AlertDialog alertDialog, TextView textView) {
            this.context = context;
            this.kcDataArrayList = kcDataArrayList;
            this.alertDialog = alertDialog;
            this.textView = textView;
        }

        @Override
        public GenreAdapter.Alphabates onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_type_cell, parent, false);
            return new Alphabates(v);
        }

        @Override
        public void onBindViewHolder(GenreAdapter.Alphabates holder, int position) {

            final GenreData genreData = kcDataArrayList.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();

                    textView.setText(genreData.getCategory_type());
                    if (genreData.getCategory_type().contains("Top")) {
                        SharedPreferences sharedPreferences1 = getContext().getSharedPreferences("BookData", Context.MODE_PRIVATE);
                        populateTopBook(sharedPreferences1.getString("BookType", ""));
                        authorTv.setVisibility(View.GONE);
                        authorCard.setVisibility(View.GONE);
                    } else {
                        authorTv.setVisibility(View.VISIBLE);
                        authorCard.setVisibility(View.VISIBLE);
                    }

                }
            });

            holder.typeTv.setText(genreData.getCategory_type());


            holder.radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    textView.setText(genreData.getCategory_type());
                    if (genreData.getCategory_type().contains("Top")) {
                        SharedPreferences sharedPreferences1 = getContext().getSharedPreferences("BookData", Context.MODE_PRIVATE);
                        populateTopBook(sharedPreferences1.getString("BookType", ""));
                        authorTv.setVisibility(View.GONE);
                        authorCard.setVisibility(View.GONE);
                    } else {
                        authorTv.setVisibility(View.VISIBLE);
                        authorCard.setVisibility(View.VISIBLE);
                    }


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
}
