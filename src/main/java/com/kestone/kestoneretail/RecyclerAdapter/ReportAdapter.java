package com.kestone.kestoneretail.RecyclerAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kestone.kestoneretail.DataHolders.ReportData;
import com.kestone.kestoneretail.R;

import java.util.ArrayList;


public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyHolder> {

    Context context;
    ArrayList<ReportData> reportList;

    public ReportAdapter(Context context, ArrayList<ReportData> reportList){

        this.context = context;
        this.reportList = reportList;

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_cell,parent,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        ReportData reportData = reportList.get(position);
        holder.bookTitleTv.setText(reportData.getBook());
        holder.storeNameTv.setText(reportData.getStore());
        holder.orderQtyTv.setText(reportData.getOrderQty());
        holder.salesQtyTv.setText(reportData.getSalesQty());
        holder.stockQtyTv.setText(reportData.getStockQty());

    }


    @Override
    public int getItemCount() {
        return reportList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView bookTitleTv, storeNameTv, stockQtyTv, salesQtyTv, orderQtyTv;

        public MyHolder(View itemView) {
            super(itemView);
            bookTitleTv = (TextView) itemView.findViewById(R.id.booktitleTv);
            storeNameTv = (TextView) itemView.findViewById(R.id.storeNameTv);
            stockQtyTv = (TextView) itemView.findViewById(R.id.stockQtyTv);
            salesQtyTv = (TextView) itemView.findViewById(R.id.salesQtyTv);
            orderQtyTv = (TextView) itemView.findViewById(R.id.orderQtyTv);
        }
    }
}
