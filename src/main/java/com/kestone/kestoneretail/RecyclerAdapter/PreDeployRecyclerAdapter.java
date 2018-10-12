package com.kestone.kestoneretail.RecyclerAdapter;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.DatabasePackage.MerchantData;
import com.kestone.kestoneretail.DatabasePackage.MerchantHandler;
import com.kestone.kestoneretail.DatabasePackage.Reporting;
import com.kestone.kestoneretail.R;

import java.util.List;

/**
 * Created by user on 01/05/17.
 */

public class PreDeployRecyclerAdapter extends RecyclerView.Adapter<PreDeployRecyclerAdapter.Alphabates> {
    Context context;
    List<Reporting> contactList;
    FloatingActionButton fab;
    List<MerchantData> merchantList;
    MerchantData merchantData ;

    public PreDeployRecyclerAdapter(Context context, List<Reporting> contactList, List<MerchantData> merchantList, FloatingActionButton fab) {
        this.context = context;
        this.contactList = contactList;
        this.merchantList = merchantList;
        this.fab = fab;
    }

    public PreDeployRecyclerAdapter(Context context, List<Reporting> contactList, List<MerchantData> merchantList) {
        this.context = context;
        this.contactList = contactList;
        this.merchantList = merchantList;
        fab = null;
    }

    @Override
    public PreDeployRecyclerAdapter.Alphabates onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pre_cell, parent, false);
        return new Alphabates(v);
    }

    @Override
    public void onBindViewHolder(PreDeployRecyclerAdapter.Alphabates holder, final int position) {


        final Reporting contact = contactList.get(position);



        holder.category.setText(contact.getCategory());
        holder.family.setText(contact.getAuthor());
        holder.quantity.setText(contact.getBookname());

        holder.img_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                merchantData = merchantList.get(0);

                DatabaseHandler db = new DatabaseHandler(context, "Predeploy");
                db.deleteContact(contact);

                MerchantHandler mb = new MerchantHandler(context,"ImageDb");
                mb.deleteMerchantData(merchantData);

                Toast.makeText(context, "Item Removed Successfully", Toast.LENGTH_SHORT).show();
                contactList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, contactList.size());



                if (fab != null) {


                    if (contactList.size() < 1) {
                        fab.setVisibility(View.VISIBLE);
                    } else fab.setVisibility(View.GONE);

                }
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

            category = (TextView) itemView.findViewById(R.id.productCategory);
            family = (TextView) itemView.findViewById(R.id.productFamily);
            quantity = (TextView) itemView.findViewById(R.id.quantityTv);
            img_minus = (ImageView) itemView.findViewById(R.id.img_minus);
            //storeCell = (MaterialRippleLayout) itemView.findViewById(R.id.storeCell);

        }
    }
}
