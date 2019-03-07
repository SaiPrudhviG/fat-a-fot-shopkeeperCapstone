package com.fat_a_fot.fat_shopkeeper;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

public class RecyclerViewAdapterCompletOrder extends RecyclerView.Adapter<RecyclerViewAdapterCompletOrder.MyViewHolder>{
    private JSONArray myValues;
    private OrderStatus orderstatus;
    private Context context;
    public RecyclerViewAdapterCompletOrder (JSONArray myValues , Context context){
        this.myValues= myValues;
        this.context = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewcompletorder, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        try {
            Picasso.with(context).load(myValues.getJSONObject(position).getJSONObject("item").getString("primary_image")).into(holder.item_image);
            holder.item_name.setText(myValues.getJSONObject(position).getJSONObject("item").getString("product_name")+" ("+ myValues.getJSONObject(position).getJSONObject("item").getString("varient_quantity")+")");
            holder.category.setText(myValues.getJSONObject(position).getJSONObject("item").getString("cat_name")
                    +" / "+myValues.getJSONObject(position).getJSONObject("item").getString("size"));
            holder.order_id.setText(myValues.getJSONObject(position).getString("orderid"));

            if(myValues.getJSONObject(position).getJSONObject("deliveryboy").toString() != null){
                holder.deliveryboy_details.setVisibility(View.VISIBLE);
                holder.deliveryboy_name.setText(myValues.getJSONObject(position).getJSONObject("deliveryboy").getString("name"));
                holder.deliveryboy_mobile.setText(myValues.getJSONObject(position).getJSONObject("deliveryboy").getString("mobile"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return myValues.length();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView item_name,deliveryboy_name, order_id , deliveryboy_mobile,category;
        private LinearLayout deliveryboy_details;
        private ImageView item_image;
        public MyViewHolder(View itemView) {
            super(itemView);
            item_image = (ImageView)itemView.findViewById(R.id.item_image);
            deliveryboy_details = (LinearLayout)itemView.findViewById(R.id.deliveryboy_details);
            item_name = (TextView)itemView.findViewById(R.id.item_name);
            deliveryboy_name = (TextView)itemView.findViewById(R.id.deliveryboy_name);
            order_id = (TextView)itemView.findViewById(R.id.order_id);
            deliveryboy_mobile = (TextView)itemView.findViewById(R.id.deliveryboy_mobile);
            category = (TextView)itemView.findViewById(R.id.category);
            deliveryboy_details.setVisibility(View.GONE);


            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        Fragment newFragment = new OrderDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("id",order_id.getText().toString());
                        newFragment.setArguments(bundle);

                        AppCompatActivity activity1 = (AppCompatActivity) v.getContext();
                        activity1.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, newFragment).addToBackStack(null).commit();
                    }
                }
            });
        }
    }
}
