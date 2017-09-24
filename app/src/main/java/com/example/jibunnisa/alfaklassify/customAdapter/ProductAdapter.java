package com.example.jibunnisa.alfaklassify.customAdapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.jibunnisa.alfaklassify.R;
import com.example.jibunnisa.alfaklassify.model.PostingProduct;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by nexttel_1 on 5/10/2017.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private ArrayList<PostingProduct> products;
    Listener mListener;
    public ProductAdapter(Context context, ArrayList<PostingProduct> products){
this.context = context;
        this.products = products;
    }

    public static interface Listener{
        public void onClick(int position);
    }
    public void setListener(Listener listener) {
        mListener = listener;
    }
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_row,parent,false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, final int position) {
        Picasso.with(context).load(products.get(position).getImageUrl()).into(holder.productIV);
        //holder.momentIV.setImageResource(R.mipmap.ic_launcher);
        String itemName = products.get(position).getBrandName()+" "+products.get(position).getModelName();
        holder.itemNameTV.setText(itemName);
        String description = products.get(position).getLocationName()+","+products.get(position).getTypeName();
        holder.descriptionTV.setText(description);
        holder.itemPriceTV.setText("Tk "+String.valueOf(products.get(position).getPrice()));
        holder.dateTV.setText(String.valueOf(products.get(position).getDate()));
        holder.timeTV.setText(String.valueOf(products.get(position).getTime()));
        CardView cardView = holder.mCardView;
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mListener != null){
                    mListener.onClick(position);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView productIV;
        private TextView itemNameTV;
        private TextView descriptionTV;
        private TextView itemPriceTV;
        private TextView dateTV;
        private TextView timeTV;
        CardView mCardView;
        public ProductViewHolder(View itemView) {
            super(itemView);
            productIV = (ImageView) itemView.findViewById(R.id.product_thumb);
            itemNameTV = (TextView) itemView.findViewById(R.id.item_name);
            descriptionTV = (TextView) itemView.findViewById(R.id.item_short_desc);
            itemPriceTV = (TextView) itemView.findViewById(R.id.item_price);
            dateTV = (TextView) itemView.findViewById(R.id.datetv);
            timeTV = (TextView) itemView.findViewById(R.id.timetv);
            mCardView = (CardView) itemView;

        }
    }
}
