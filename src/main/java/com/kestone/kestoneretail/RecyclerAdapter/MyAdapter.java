package com.kestone.kestoneretail.RecyclerAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kestone.kestoneretail.DataHolders.HolderFlag;
import com.kestone.kestoneretail.DatabasePackage.DBHelpher;
import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.DatabasePackage.Reporting;
import com.kestone.kestoneretail.Interface.OnItemClick;
import com.kestone.kestoneretail.R;
import com.kestone.kestoneretail.RowItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    Context context;
    ArrayList<RowItem> list;
    OnItemClick listener;
    DBHelpher dbh;
    ArrayList<HolderFlag> listFlag;
    DatabaseHandler db;
    List<Reporting> reportingList;
    SharedPreferences sharedpreferences;
    int dblReport = 0, dblOrder = 0, flagNext = 0, flagPre = 0;
    String storeCode, storeId, pjpID;
    String isDeviation;
    HashSet<Integer> hs = new HashSet<>();

    public MyAdapter(Context context, ArrayList<RowItem> list, ArrayList<HolderFlag> listFlag, String pjpID, String storeId, String storeCode, OnItemClick listener, String isDeviation) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        dbh = new DBHelpher( context, "PopUpDb" );
        this.listFlag = listFlag;
        this.pjpID = pjpID;
        this.storeId = storeId;
        this.storeCode = storeCode;
        this.isDeviation=isDeviation;
        sharedpreferences = context.getSharedPreferences( "StoreDetails", Context.MODE_PRIVATE );
        db = new DatabaseHandler( context, "ReportingDb" );
        reportingList = db.getAllContacts();//(Integer.parseInt(sharedpreferences.getString("Id", "")));

        Log.e( "SID ", storeId + " CODE " + storeCode + " PJPID " + pjpID );

        if (reportingList.size() > 1) {
//            for (int i = 0; i < reportingList.size(); i++) {
//                if (pjpID == reportingList.get( i ).getPjpId() && storeId == reportingList.get( i ).getStoreId() && storeCode.contains( reportingList.get( i ).getStoreId() ))
//                Log.e("PJP ID", reportingList.get( i ).getPjpId()+" SID "+reportingList.get( i ).getStoreId()+" CODE "+reportingList.get( i ).getId());
//                    Log.e("Value Stock ", reportingList.get( i ).getStock()+" order"+reportingList.get( i ).getOrder());
            dblReport = Integer.parseInt( reportingList.get( 1 ).getStock() );
            dblOrder = Integer.parseInt( reportingList.get( 1 ).getOrder() );
//            }
        }

    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.custom_layout, parent, false );

        return new ViewHolder( v );
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final RowItem rowItem = list.get( position );
        final HolderFlag hf = listFlag.get( position );
        holder.typeTv.setText( rowItem.getTitle() );

        if (dblReport > 0) {
            listFlag.get( 1 ).setFlag( true );
        }

        if (dblOrder > 0) {
            listFlag.get( 2 ).setFlag( true );
        }

        Glide.with( context ).load( rowItem.getImageId() )
                .thumbnail( 0.5f )
                .crossFade()
                .diskCacheStrategy( DiskCacheStrategy.ALL )
                .into( holder.icon_list );

        holder.card_list.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e( "Flag next ", "" + flagNext + " Pos " + position );
                    listener.onClick( rowItem.getTitle(), position );
            }
        } );

        if (hf.isFlag()) {
            holder.icon_tick.setImageDrawable( context.getResources().getDrawable( R.drawable.circle_check ) );
        } else {
            holder.icon_tick.setImageDrawable( context.getResources().getDrawable( R.drawable.circle_check_grey ) );
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView typeTv;
        ImageView icon_list, icon_tick;
        CardView card_list;

        public ViewHolder(View itemView) {
            super( itemView );
            typeTv = (TextView) itemView.findViewById( R.id.tv_name );
            icon_list = (ImageView) itemView.findViewById( R.id.image_menu_icon );
            icon_tick = (ImageView) itemView.findViewById( R.id.image_tick );
            card_list = (CardView) itemView.findViewById( R.id.card_list );

        }
    }
}
