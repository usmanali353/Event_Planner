package com.example.event_planner.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.event_planner.Model.service_provider;
import com.example.event_planner.R;
import com.example.event_planner.hotel_details_page;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class hotels_list_adapter extends RecyclerView.Adapter<hotels_list_adapter.hotels_list_viewholder> {
       public hotels_list_adapter(ArrayList<service_provider> snapshots, Context context,List<String> hotel_ids){
           this.context=context;
           this.snapshots=snapshots;
           this.hotel_ids=hotel_ids;
       }
      ArrayList<service_provider> snapshots;
       List<String> hotel_ids;
       Context context;
    @NonNull
    @Override
    public hotels_list_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new hotels_list_viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_list_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull hotels_list_viewholder holder, final int position) {
         holder.hotel_name.setText(String.valueOf(snapshots.get(position).getName()));
        Glide.with(context).load(snapshots.get(position).getImage_url()).into(holder.img);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context, hotel_details_page.class);
                i.putExtra("hotel_info",new Gson().toJson(snapshots.get(position)));
                i.putExtra("hotel_id",hotel_ids.get(position));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return snapshots.size();
    }

    class hotels_list_viewholder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView img;
         TextView hotel_name;
        public hotels_list_viewholder(@NonNull View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.card);
            img=itemView.findViewById(R.id.hotel_image);
            hotel_name=itemView.findViewById(R.id.name);
        }
    }
}
