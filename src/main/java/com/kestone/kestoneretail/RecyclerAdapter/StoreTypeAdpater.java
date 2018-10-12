package com.kestone.kestoneretail.RecyclerAdapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kestone.kestoneretail.DataHolders.StoreTypeData;
import com.kestone.kestoneretail.R;

import java.util.ArrayList;

/**
 * Created by user on 06/05/17.
 */

public class StoreTypeAdpater extends RecyclerView.Adapter<StoreTypeAdpater.Alphabates>
{
    Context context;
    ArrayList<StoreTypeData> kcDataArrayList;
    AlertDialog alertDialog;
    TextView textView;


    public StoreTypeAdpater(Context context, ArrayList<StoreTypeData> kcDataArrayList, AlertDialog alertDialog, TextView textView) {
        this.context = context;
        this.kcDataArrayList = kcDataArrayList;
        this.alertDialog = alertDialog;
        this.textView = textView;
    }

    @Override
    public StoreTypeAdpater.Alphabates onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.store_type_cell,parent,false);
        return new Alphabates(v);
    }

    @Override
    public void onBindViewHolder(StoreTypeAdpater.Alphabates holder, int position)
    {

        final StoreTypeData storeTypeData = kcDataArrayList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                textView.setText(storeTypeData.getStore_formate());
            }
        });

        holder.typeTv.setText(storeTypeData.getStore_formate());


        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                textView.setText(storeTypeData.getStore_formate());

            }
        });

    }

    @Override
    public int getItemCount()
    {
        return kcDataArrayList.size();
    }

    public class Alphabates extends RecyclerView.ViewHolder
    {     TextView typeTv;
        RadioButton radioButton;
        //MaterialRippleLayout storeCell;
        public Alphabates(View itemView)
        {
            super(itemView);
            typeTv = (TextView) itemView.findViewById(R.id.typeTv);
            radioButton = (RadioButton) itemView.findViewById(R.id.rd1);

        }
    }
}




