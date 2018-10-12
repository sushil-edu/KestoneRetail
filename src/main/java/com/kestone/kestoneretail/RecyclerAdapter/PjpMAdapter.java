package com.kestone.kestoneretail.RecyclerAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.kestone.kestoneretail.DataHolders.PjpData;
import com.kestone.kestoneretail.MerchantActivity;
import com.kestone.kestoneretail.R;

import java.util.ArrayList;

import in.galaxyofandroid.widgets.AwesomeRelativeLayout;


public class PjpMAdapter extends RecyclerView.Adapter<PjpMAdapter.MyHolder> {


    private ArrayList<PjpData> pjpDataList;
    private Context context;

    public PjpMAdapter(Context context, ArrayList<PjpData> pjpDataList) {

        this.context = context;
        this.pjpDataList = pjpDataList;

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.merchant_pjp_cell, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        final PjpData pjpData = pjpDataList.get(position);


        holder.storeTypeTv.setText(pjpData.getStoreCode());
        holder.storeNameTv.setText(pjpData.getStoreName());

        if (pjpData.getPreDeployment().equalsIgnoreCase("0")) {

            holder.preIndicator.setBackgroundColor(context.getResources().getColor(R.color.orange));

        } else holder.preIndicator.setBackgroundColor(context.getResources().getColor(R.color.green));

        if (pjpData.getPostDeloyment().equalsIgnoreCase("0")) {

            holder.postIndicator.setBackgroundColor(context.getResources().getColor(R.color.orange));

        } else holder.postIndicator.setBackgroundColor(context.getResources().getColor(R.color.green));

        if (pjpData.getStoreCompleteness().equalsIgnoreCase("0")) {

            holder.stockIndicator.setBackgroundColor(context.getResources().getColor(R.color.orange));

        } else holder.stockIndicator.setBackgroundColor(context.getResources().getColor(R.color.green));



        if (pjpData.getIsDeviation().equalsIgnoreCase("y")) {
            holder.indicatorTv.setText("D");

        } else {
            holder.indicatorTv.setText("P");

        }

        if(pjpData.getAttendence().equals("0")){
            holder.attendanceIndicatorTv.setText("A");
        }else if(pjpData.getAttendence().equals("1")){
            holder.attendanceIndicatorTv.setText("I");
        }else if(pjpData.getAttendence().equals("2")){
            holder.attendanceIndicatorTv.setText("O");
        }


        holder.storeCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String PjpStoredId;
                SharedPreferences sharedPreferences = context.getSharedPreferences("PjpDataMerchant",Context.MODE_PRIVATE);
                PjpStoredId = sharedPreferences.getString("PJPId","N/A");

                if (pjpData.getStoreCompleteness().equalsIgnoreCase("0")) {

                    Intent intent = new Intent(context, MerchantActivity.class);
                    intent.putExtra("StoreType", pjpData.getStoreCode());
                    intent.putExtra("StoreName", pjpData.getStoreName());
                    intent.putExtra("Date", pjpData.getPJPDate());
                    intent.putExtra("StoreCode", pjpData.getStoreCode());
                    intent.putExtra("Id", pjpData.getPJPID());
                    intent.putExtra("assigned_store_id", pjpData.getStoreID());
                    intent.putExtra("deviation", pjpData.getIsDeviation());
                    intent.putExtra("latitude", pjpData.getStore_latitude());
                    intent.putExtra("longitude", pjpData.getStore_longitude());
                    intent.putExtra("StoreCategory", pjpData.getStore_category());
                    context.startActivity(intent);


                }else
                    Toast.makeText(context, "PJP already completed", Toast.LENGTH_SHORT).show();


            }
        });

    }


    @Override
    public int getItemCount() {
        return pjpDataList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        MaterialRippleLayout storeCell;
        private TextView storeTypeTv, storeNameTv, indicatorTv, attendanceIndicatorTv;
        private AwesomeRelativeLayout checkInBtn;
        private View preIndicator, postIndicator, stockIndicator;

        public MyHolder(View itemView) {
            super(itemView);

            storeCell = (MaterialRippleLayout) itemView.findViewById(R.id.storeCell);
            storeNameTv = (TextView) itemView.findViewById(R.id.storeNameTv);
            storeTypeTv = (TextView) itemView.findViewById(R.id.storeTypeTv);
            checkInBtn = (AwesomeRelativeLayout) itemView.findViewById(R.id.checkInBtn);
            preIndicator = itemView.findViewById(R.id.preIndicator);
            postIndicator = itemView.findViewById(R.id.postIndicator);
            stockIndicator = itemView.findViewById(R.id.stockIndicator);
            indicatorTv = (TextView) itemView.findViewById(R.id.indicatorTv);
            attendanceIndicatorTv = (TextView) itemView.findViewById(R.id.attendanceIndicatorTv);

        }
    }
}
