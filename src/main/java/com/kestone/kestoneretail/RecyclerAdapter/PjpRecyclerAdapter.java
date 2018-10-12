package com.kestone.kestoneretail.RecyclerAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.kestone.kestoneretail.DataHolders.PjpData;
import com.kestone.kestoneretail.DataHolders.PjpHolder;
import com.kestone.kestoneretail.MainActivity;
import com.kestone.kestoneretail.R;

import java.util.ArrayList;

import in.galaxyofandroid.widgets.AwesomeRelativeLayout;

import static android.content.Context.MODE_PRIVATE;

public class PjpRecyclerAdapter extends RecyclerView.Adapter<PjpRecyclerAdapter.MyHolder> {

    private ArrayList<PjpData> pjpDataList;
    private Context context;

    public PjpRecyclerAdapter(Context context, ArrayList<PjpData> pjpDataList) {

        this.context = context;
        this.pjpDataList = pjpDataList;

    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pjp_cell, parent, false);

        return new MyHolder(view);

    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        final PjpData pjpData = pjpDataList.get(position);


        holder.storeTypeTv.setText(pjpData.getStoreCode());
        holder.storeNameTv.setText(pjpData.getStoreName());

        if (pjpData.getStoreCompleteness().equalsIgnoreCase("0")) {

            holder.sales.setBackgroundColor(context.getResources().getColor(R.color.orange));

        } else holder.sales.setBackgroundColor(context.getResources().getColor(R.color.green));


        if (pjpData.getIsDeviation().equalsIgnoreCase("y")) {
            holder.indicatorTv.setText("D");

        } else {
            holder.indicatorTv.setText("P");

        }

        if (pjpData.getAttendence().equals("0")) {
            holder.attendanceIndicatorTv.setText("A");
        } else if (pjpData.getAttendence().equals("1")) {
            holder.attendanceIndicatorTv.setText("I");
        } else if (pjpData.getAttendence().equals("2")) {
            holder.attendanceIndicatorTv.setText("O");
        }

        holder.storeCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String PjpStoredId;
                SharedPreferences sharedPreferences = context.getSharedPreferences("PjpData", MODE_PRIVATE);
                PjpStoredId = sharedPreferences.getString("PJPId", "N/A");


                if (pjpData.getStoreCompleteness().equalsIgnoreCase("0")) {

                    Intent intent = new Intent(context, MainActivity.class);
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

                    SharedPreferences sharedPrefAdd = context.getSharedPreferences( "AdditionalInfo", MODE_PRIVATE );
                    SharedPreferences.Editor editorAdd = sharedPrefAdd.edit();
                    editorAdd.clear();
                    editorAdd.commit();


                } else
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
        private View sales;
        private String formattedDate;
        private RelativeLayout topRl;

        public MyHolder(View itemView) {
            super(itemView);

            storeCell = (MaterialRippleLayout) itemView.findViewById(R.id.storeCell);
            storeNameTv = (TextView) itemView.findViewById(R.id.storeNameTv);
            storeTypeTv = (TextView) itemView.findViewById(R.id.storeTypeTv);
            indicatorTv = (TextView) itemView.findViewById(R.id.indicatorTv);
            checkInBtn = (AwesomeRelativeLayout) itemView.findViewById(R.id.checkInBtn);
            sales = itemView.findViewById(R.id.salesView);
            topRl = (RelativeLayout) itemView.findViewById(R.id.topRl);
            attendanceIndicatorTv = (TextView) itemView.findViewById(R.id.attendanceStatusTv);


        }
    }
}
