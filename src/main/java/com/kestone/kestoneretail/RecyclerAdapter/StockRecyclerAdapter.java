package com.kestone.kestoneretail.RecyclerAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.DatabasePackage.Reporting;
import com.kestone.kestoneretail.OrderDetails;
import com.kestone.kestoneretail.R;

import java.util.List;


public class StockRecyclerAdapter extends RecyclerView.Adapter<StockRecyclerAdapter.Alphabates> {
    Context context;
    List<Reporting> reportList;
    String PjpStoredId;


    public StockRecyclerAdapter(Context context, List<Reporting> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @Override
    public StockRecyclerAdapter.Alphabates onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_cell, parent, false);


        SharedPreferences sharedPreferences = context.getSharedPreferences("PjpData",Context.MODE_PRIVATE);
        PjpStoredId = sharedPreferences.getString("PJPId","N/A");

        return new Alphabates(v);
    }

    @Override
    public void onBindViewHolder(StockRecyclerAdapter.Alphabates holder, final int position) {

        final Reporting reporting = reportList.get(position);

        Log.d("PJPId",reporting.getAuthor());

        holder.category.setText(reporting.getAuthor());
        holder.family.setText(reporting.getCount()+"");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetails.class);
                intent.putExtra("Author",reporting.getAuthor());
                intent.putExtra("Label","Sales");
                context.startActivity(intent);
            }
        });


        holder.img_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPref = context.getSharedPreferences("StoreDetails", Context.MODE_PRIVATE);
                DatabaseHandler db = new DatabaseHandler(context,"ReportingDb");
                db.deleteContact(reporting);
                Toast.makeText(context, "Item Removed Successfully", Toast.LENGTH_SHORT).show();
                reportList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, reportList.size());

            }
        });


    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public class Alphabates extends RecyclerView.ViewHolder {
        //MaterialRippleLayout storeCell;

        TextView category, family, quantity,salesTv, orderTv;
        ImageView img_minus;

        public Alphabates(View itemView) {
            super(itemView);

            //storeCell = (MaterialRippleLayout) itemView.findViewById(R.id.storeCell);
            category = (TextView) itemView.findViewById(R.id.productCategory);
            family = (TextView) itemView.findViewById(R.id.productFamily);
            quantity = (TextView) itemView.findViewById(R.id.quantityTv);
            img_minus = (ImageView) itemView.findViewById(R.id.img_minus);
            salesTv = (TextView) itemView.findViewById(R.id.salesTv);
            orderTv = (TextView) itemView.findViewById(R.id.orderTv);



        }
    }
}

