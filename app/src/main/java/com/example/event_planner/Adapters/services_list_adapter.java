package com.example.event_planner.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.event_planner.Firebase_Operations.Firebase_Operations;
import com.example.event_planner.Model.Food;
import com.example.event_planner.Model.Services;
import com.example.event_planner.R;
import com.example.event_planner.reservation_details_page;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import fr.ganfra.materialspinner.MaterialSpinner;

public class services_list_adapter extends RecyclerView.Adapter<services_list_adapter.services_list_viewholder> {
    ArrayList<Services> services;
    Context context;
    Button add_services_btn;
    String hotel_id,page;
    SharedPreferences prefs;
    ArrayList<Services> selected_services_list =new ArrayList<Services>();
    ArrayList<String> serviceId;
    Fragment fragment;
    public services_list_adapter(ArrayList<Services> services, Context context, Button add_services_btn, String hotel_id, String page, ArrayList<String> serviceId, Fragment fragment) {
        this.services = services;
        this.context = context;
        this.add_services_btn=add_services_btn;
        this.hotel_id=hotel_id;
        this.page=page;
        this.fragment=fragment;
        this.serviceId=serviceId;
        prefs= PreferenceManager.getDefaultSharedPreferences(context);
    }

    @NonNull
    @Override
    public services_list_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new services_list_viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.food_list_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final services_list_viewholder holder, final int position) {
        if(page.equals("Other Services Offering")||page.equals("Other Services Update")){
            holder.service_chkbox.setVisibility(View.GONE);
//            add_services_btn.setVisibility(View.GONE);
            holder.service_name.setText(services.get(position).getService_name());
            Glide.with(context).load(services.get(position).getService_image_url()).into(holder.service_image);
        }else {
            if(!services.get(position).getAvailable()){
              holder.service_chkbox.setEnabled(false);
            }
            holder.service_name.setText(services.get(position).getService_name());
            Glide.with(context).load(services.get(position).getService_image_url()).into(holder.service_image);
           holder.service_chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   if(isChecked){
                       selected_services_list.add(services.get(position));
                   }else{
                       selected_services_list.remove(position);
                   }
               }
           });
            add_services_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selected_services_list.size() > 0) {
                        prefs.edit().putString("selected_services", new Gson().toJson(selected_services_list)).apply();
                        context.startActivity(new Intent(context, reservation_details_page.class).putExtra("hotel_id",hotel_id));
                        ((AppCompatActivity) context).finish();
                    }
                }
            });
        }
        if(page.equals("Other Services Update")){
            holder.service_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View serviceUpdate =LayoutInflater.from(context).inflate(R.layout.update_layout,null);
                    final TextInputEditText name=serviceUpdate.findViewById(R.id.name_txt);
                    final TextInputEditText price=serviceUpdate.findViewById(R.id.price_txt);
                    final MaterialSpinner Availability=serviceUpdate.findViewById(R.id.available);
                    name.setText(services.get(position).getService_name());
                    price.setText(String.valueOf(services.get(position).getService_price()));
                    if(services.get(position).getAvailable()) {
                        Availability.setSelection(1);
                    }else{
                        Availability.setSelection(2);
                    }
                    AlertDialog.Builder updateFoodDialog =new AlertDialog.Builder(context);
                    updateFoodDialog.setTitle("Update Service Details");
                    updateFoodDialog.setView(serviceUpdate);
                    updateFoodDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(TextUtils.isEmpty(name.getText().toString())){
                                name.setError("Enter Name");
                            }else if(TextUtils.isEmpty(price.getText().toString())){
                                price.setError("Enter Price");
                            }else if(Integer.parseInt(price.getText().toString())<1000){
                                price.setError("Entered Price too less");
                            }else if(Availability.getSelectedItem()==null){
                                Availability.setError("Select Availability");
                            }else{
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("service_name", name.getText().toString());
                                    map.put("service_price", Integer.parseInt(price.getText().toString()));
                                    if (Availability.getSelectedItem().toString().equals("Yes")) {
                                        map.put("available", true);
                                    } else {
                                        map.put("available", false);
                                    }
                                Firebase_Operations.update_other_services(context,map, FirebaseAuth.getInstance().getCurrentUser().getUid(),serviceId.get(position),fragment);
                            }
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    class services_list_viewholder extends RecyclerView.ViewHolder{
        TextView service_name;
        ImageView service_image;
        AppCompatCheckBox service_chkbox;
        CardView service_card;
        public services_list_viewholder(@NonNull View itemView) {
            super(itemView);
            service_name=itemView.findViewById(R.id.name);
            service_image=itemView.findViewById(R.id.img);
            service_chkbox=itemView.findViewById(R.id.food_chkbox);
            service_card=itemView.findViewById(R.id.card);
        }
    }
}
