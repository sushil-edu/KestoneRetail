package com.kestone.kestoneretail.RecyclerAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kestone.kestoneretail.DatabasePackage.DatabaseHandler;
import com.kestone.kestoneretail.DatabasePackage.Reporting;
import com.kestone.kestoneretail.R;

import java.util.List;

public class PostDeployRecyclerAdapter extends RecyclerView.Adapter<PostDeployRecyclerAdapter.Alphabates> {
    Context context;
    List<Reporting> contactList;

    public PostDeployRecyclerAdapter(Context context, List<Reporting> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @Override
    public PostDeployRecyclerAdapter.Alphabates onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.post_cell, parent, false );
        return new Alphabates( v );
    }

    @Override
    public void onBindViewHolder(PostDeployRecyclerAdapter.Alphabates holder, final int position) {

        final Reporting contact = contactList.get( position );
        holder.category.setText( contact.getCategory() );
        holder.family.setText( contact.getAuthor() );

        holder.img_minus.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DatabaseHandler db = new DatabaseHandler( context, "Postdeploy" );
                db.deleteContact( contact );
                Toast.makeText( context, "Item Removed Successfully ", Toast.LENGTH_SHORT ).show();
                contactList.remove( position );
                notifyItemRemoved( position );
                notifyItemRangeChanged( position, contactList.size() );
            }
        } );

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class Alphabates extends RecyclerView.ViewHolder {
        //MaterialRippleLayout storeCell;
        TextView category, family;
        ImageView img_minus;

        public Alphabates(View itemView) {
            super( itemView );

            //storeCell = (MaterialRippleLayout) itemView.findViewById(R.id.storeCell);
            category = (TextView) itemView.findViewById( R.id.productCategory );
            family = (TextView) itemView.findViewById( R.id.productFamily );
            img_minus = (ImageView) itemView.findViewById( R.id.img_minus );
        }
    }
}

