package com.kestone.kestoneretail.RecyclerAdapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kestone.kestoneretail.DataHolders.PosmData;
import com.kestone.kestoneretail.R;

import java.util.ArrayList;


public class PosmTypeAdapter extends RecyclerView.Adapter<PosmTypeAdapter.Alphabates>
{
    Context context;
    ArrayList<PosmData> kcDataArrayList;
    AlertDialog alertDialog;
    TextView textView;
    CardView commentCard;


    public PosmTypeAdapter(Context context, ArrayList<PosmData> kcDataArrayList, AlertDialog alertDialog, TextView textView, CardView commentCard) {
        this.context = context;
        this.kcDataArrayList = kcDataArrayList;
        this.alertDialog = alertDialog;
        this.textView = textView;
        this.commentCard = commentCard;
    }

    @Override
    public PosmTypeAdapter.Alphabates onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.store_type_cell,parent,false);
        return new Alphabates(v);
    }

    @Override
    public void onBindViewHolder(PosmTypeAdapter.Alphabates holder, int position)
    {

        final PosmData storeTypeData = kcDataArrayList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                textView.setText(storeTypeData.getPosm_type());
                if(storeTypeData.getPosm_type().equalsIgnoreCase("MISC")){
                     commentCard.setVisibility(View.VISIBLE);
                }else commentCard.setVisibility(View.GONE);
            }
        });

        holder.typeTv.setText(storeTypeData.getPosm_type());


        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                textView.setText(storeTypeData.getPosm_type());

                if(storeTypeData.getPosm_type().equalsIgnoreCase("MISC")){
                    commentCard.setVisibility(View.VISIBLE);
                }else commentCard.setVisibility(View.GONE);

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




