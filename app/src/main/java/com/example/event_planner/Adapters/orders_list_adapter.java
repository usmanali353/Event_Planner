package com.example.event_planner.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.event_planner.BookingDetailPage;
import com.example.event_planner.Model.Orders;
import com.example.event_planner.R;
import com.google.gson.Gson;

import java.util.ArrayList;

public class orders_list_adapter extends RecyclerView.Adapter<orders_list_adapter.orders_list_viewholder> {
    ArrayList<Orders> orders;
    Context context;
    ArrayList<String> order_id;
    public orders_list_adapter(ArrayList<Orders> orders, Context context,ArrayList<String> orders_id) {
        this.orders = orders;
        this.context = context;
        this.order_id=orders_id;
    }

    @NonNull
    @Override
    public orders_list_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new orders_list_viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_list_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull orders_list_viewholder holder, final int position) {
         holder.order_date.setText(orders.get(position).getDate());
         holder.order_id.setText(orders.get(position).getHotel_name());
         int total_price=orders.get(position).getFoods_price()+orders.get(position).getServices_price()+orders.get(position).getTheme_price();
         holder.order_price.setText("Rs "+total_price);
         if(orders.get(position).getStatus().equals("Unconfirmed")||orders.get(position).getStatus().equals("Canceled")){
             holder.order_statue.setTextColor(Color.parseColor("#FF0000"));
         }else{
             holder.order_statue.setTextColor(Color.parseColor("#008000"));
         }
         holder.order_statue.setText(orders.get(position).getStatus());
         holder.order_details.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 context.startActivity(new Intent(context, BookingDetailPage.class).putExtra("order_id",order_id.get(position)).putExtra("order_data",new Gson().toJson(orders.get(position))));
             }
         });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class orders_list_viewholder extends RecyclerView.ViewHolder{
        TextView order_date,order_id,order_price,order_details,order_statue;
        public orders_list_viewholder(@NonNull View itemView) {
            super(itemView);
            order_date=itemView.findViewById(R.id.order_date);
            order_id=itemView.findViewById(R.id.order_id);
            order_price=itemView.findViewById(R.id.order_price);
            order_details=itemView.findViewById(R.id.order_detail);
            order_statue=itemView.findViewById(R.id.order_status);
        }
    }
}
