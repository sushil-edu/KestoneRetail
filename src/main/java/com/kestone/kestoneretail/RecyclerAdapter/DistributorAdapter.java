package com.kestone.kestoneretail.RecyclerAdapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kestone.kestoneretail.DataHolders.DistributorData;
import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.R;

import java.util.ArrayList;


public class DistributorAdapter extends RecyclerView.Adapter<DistributorAdapter.MyHolder> {
    ArrayList<DistributorData>distributorList;
    String date;
    Context context;
    String storeId;
    String tag;
    Dialog dialog;

    public DistributorAdapter(ArrayList<DistributorData>distributorList,String date, String storeId, Context context,String tag, Dialog dialog){

        this.distributorList = distributorList;
        this.storeId = storeId;
        this.date = date;
        this.context = context;
        this.tag = tag;
        this.dialog = dialog;


    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.distributor_cell,parent,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        final DistributorData distributorData = distributorList.get(position);
        holder.distributorName.setText(distributorData.getDistributorName());
        holder.distributorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tag.equals("Stock")){

                    DatabaseHandler databaseHandler = new DatabaseHandler(context,"StockDb");

                    databaseHandler.updateDistributor(distributorData.getDistributorId(),storeId,date);
                    dialog.cancel();
                }else {
                    DatabaseHandler databaseHandler = new DatabaseHandler(context,"ReportingDb");

                    databaseHandler.updateDistributor(distributorData.getDistributorId(),storeId,date);
                    dialog.cancel();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return distributorList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView distributorName;
        public MyHolder(View itemView) {
            super(itemView);
            distributorName = (TextView) itemView.findViewById(R.id.distributorName);
        }
    }
}
