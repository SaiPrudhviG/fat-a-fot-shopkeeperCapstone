package com.fat_a_fot.fat_shopkeeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{
    private JSONArray myValues;
    private OrderStatus orderstatus;
    private Context context;
    public RecyclerViewAdapter (JSONArray myValues , Context context){
        this.myValues= myValues;
        this.context = context;

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        try {
            Picasso.with(context).load(myValues.getJSONObject(position).getJSONObject("item").getString("primary_image")).into(holder.item_image);
            holder.item_name.setText(myValues.getJSONObject(position).getJSONObject("item").getString("product_name")+" ("+ myValues.getJSONObject(position).getJSONObject("item").getString("varient_quantity")+")");
            holder.category.setText(myValues.getJSONObject(position).getJSONObject("item").getString("cat_name")
                    +" / "+myValues.getJSONObject(position).getJSONObject("item").getString("varient"));
            holder.order_id.setText(myValues.getJSONObject(position).getString("orderid"));
            if(myValues.getJSONObject(position).getString("status").equals("SA1")){
                holder.deliveryboy_details.setVisibility(View.GONE);
                holder.delivery_in_process.setVisibility(View.GONE);
                holder.order_pickup_by_deliveryboy.setVisibility(View.GONE);
                holder.ready_for_pickup.setVisibility(View.VISIBLE);

                holder.ready_for_pickup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderstatus = new OrderStatus(v.getContext());
                        orderstatus.updateOrderStatus(holder.order_id.getText().toString());
                    }
                });
            }

            if(myValues.getJSONObject(position).getString("status").equals("SCD1")){
                holder.ready_for_pickup.setVisibility(View.GONE);
                holder.order_pickup_by_deliveryboy.setVisibility(View.VISIBLE);
                holder.deliveryboy_details.setVisibility(View.GONE);
                holder.delivery_in_process.setVisibility(View.GONE);
            }

            if(myValues.getJSONObject(position).getString("status").equals("DB1")){
                holder.deliveryboy_name.setText(myValues.getJSONObject(position).getJSONObject("deliveryboy").getString("name"));
                holder.deliveryboy_mobile.setText(myValues.getJSONObject(position).getJSONObject("deliveryboy").getString("mobile"));
                holder.ready_for_pickup.setVisibility(View.GONE);
                holder.delivery_in_process.setVisibility(View.GONE);
                holder.delivery_in_process.setVisibility(View.VISIBLE);
                holder.deliveryboy_details.setVisibility(View.VISIBLE);
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
        private Button ready_for_pickup,order_pickup_by_deliveryboy,delivery_in_process;
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
            ready_for_pickup = (Button)itemView.findViewById(R.id.ready_for_pickup);
            order_pickup_by_deliveryboy = (Button)itemView.findViewById(R.id.order_pickup_by_deliveryboy);
            delivery_in_process = (Button)itemView.findViewById(R.id.delivery_in_process);

            delivery_in_process.setVisibility(View.GONE);
            ready_for_pickup.setVisibility(View.GONE);
            order_pickup_by_deliveryboy.setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
//                        Fragment newFragment = new OrderDetailsFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putString("id",order_id.getText().toString());
//                        newFragment.setArguments(bundle);

//                        AppCompatActivity activity1 = (AppCompatActivity) v.getContext();
//                        activity1.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, newFragment).addToBackStack(null).commit();
                    }
                }
            });
        }
    }
}
