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


public class MerchantStockAdapter extends RecyclerView.Adapter<MerchantStockAdapter.Alphabates> {
    Context context;
    List<Reporting> contactList;


    public MerchantStockAdapter(Context context, List<Reporting> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @Override
    public MerchantStockAdapter.Alphabates onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_cell, parent, false);

        return new Alphabates(v);
    }

    @Override
    public void onBindViewHolder(MerchantStockAdapter.Alphabates holder, final int position) {

        final Reporting contact = contactList.get(position);
        holder.category.setText(contact.getAuthor());
        holder.family.setText(contact.getCount()+"");

        Log.d("contact",contact.toString());

        holder.img_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPref = context.getSharedPreferences("StoreDetails", Context.MODE_PRIVATE);
                DatabaseHandler db = new DatabaseHandler(context,"StockDb");
                db.deleteContact(contact);
                Toast.makeText(context, "Item Removed Successfully", Toast.LENGTH_SHORT).show();
                contactList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, contactList.size());


            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetails.class);
                intent.putExtra("Author",contact.getAuthor());
                intent.putExtra("Label","Merchant");
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class Alphabates extends RecyclerView.ViewHolder {
        //MaterialRippleLayout storeCell;

        TextView category, family, quantity;
        ImageView img_minus;

        public Alphabates(View itemView) {
            super(itemView);

            //storeCell = (MaterialRippleLayout) itemView.findViewById(R.id.storeCell);
            category = (TextView) itemView.findViewById(R.id.productCategory);
            family = (TextView) itemView.findViewById(R.id.productFamily);
            quantity = (TextView) itemView.findViewById(R.id.quantityTv);
            img_minus = (ImageView) itemView.findViewById(R.id.img_minus);

        }
    }
}

