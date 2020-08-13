package com.example.event_planner.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.event_planner.R;
import com.example.event_planner.services_page;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

public class food_list_adapter extends RecyclerView.Adapter<food_list_adapter.food_list_viewholder> {
    public food_list_adapter(ArrayList<Food> foods, Context context,Button add_food_btn,String hotel_id,String page,ArrayList<String>foodId,Fragment fragment) {
        this.foods = foods;
        this.context = context;
        this.add_food_btn=add_food_btn;
        this.hotel_id=hotel_id;
        this.page=page;
        prefs= PreferenceManager.getDefaultSharedPreferences(context);
        this.foodId=foodId;
        this.fragment=fragment;
    }
   String hotel_id,page;
    Fragment fragment;
    ArrayList<Food> foods;
    ArrayList<String> foodId;
    SharedPreferences prefs;
    Bitmap bitmap;
Context context;
Button add_food_btn;

ArrayList<Food> selected_food_list=new ArrayList<Food>();
    @NonNull
    @Override
    public food_list_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new food_list_viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.food_list_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final food_list_viewholder holder, final int position) {

        if(page.equals("Food Offering")||page.equals("Food Update")){
            holder.food_chkbox.setVisibility(View.GONE);
            holder.food_name.setText(foods.get(position).getFood_name());
            Glide.with(context).load(foods.get(position).getFood_image_url()).into(holder.food_image);
        } else{
            if(!foods.get(position).getAvailable()){
               holder.food_chkbox.setEnabled(false);
            }
            holder.food_name.setText(foods.get(position).getFood_name());
            Glide.with(context).load(foods.get(position).getFood_image_url()).into(holder.food_image);
           holder.food_chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   if(isChecked){
                       selected_food_list.add(foods.get(position));
                   }else {
                       selected_food_list.remove(position);
                   }
               }
           });
            add_food_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selected_food_list.size()>0){
                        prefs.edit().putString("selected_food",new Gson().toJson(selected_food_list)).apply();
                        context.startActivity(new Intent(context, services_page.class).putExtra("hotel_id",hotel_id));
                    }else{
                        Toast.makeText(context,"Select Food to be served in the Event",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
        if(page.equals("Food Update")){
            holder.food_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View foodUpdate =LayoutInflater.from(context).inflate(R.layout.update_layout,null);
                    final TextInputEditText name=foodUpdate.findViewById(R.id.name_txt);
                    final TextInputEditText price=foodUpdate.findViewById(R.id.price_txt);
                    final MaterialSpinner Availability=foodUpdate.findViewById(R.id.available);
                    name.setText(foods.get(position).getFood_name());
                    price.setText(String.valueOf(foods.get(position).getPrice()));
                    if(foods.get(position).getAvailable()) {
                        Availability.setSelection(1);
                    }else{
                        Availability.setSelection(2);
                    }
                    AlertDialog.Builder updateFoodDialog =new AlertDialog.Builder(context);
                    updateFoodDialog.setTitle("Update Food Details");
                    updateFoodDialog.setView(foodUpdate);
                    updateFoodDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(TextUtils.isEmpty(name.getText().toString())){
                                name.setError("Enter Name");
                            }else if(TextUtils.isEmpty(price.getText().toString())){
                                price.setError("Enter Price");
                            }else if(Integer.parseInt(price.getText().toString())<50){
                                price.setError("Entered Price too less");
                            }else if(Availability.getSelectedItem()==null){
                                 Availability.setError("Select Availability");
                            }else{
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("food_name", name.getText().toString());
                                    map.put("price", Integer.parseInt(price.getText().toString()));
                                    if (Availability.getSelectedItem().toString().equals("Yes")) {
                                        map.put("available", true);
                                    } else {
                                        map.put("available", false);
                                    }
                                Firebase_Operations.update_food(context,map, FirebaseAuth.getInstance().getCurrentUser().getUid(),foodId.get(position),fragment);
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
        if(page.equals("Food Offering")){
            holder.food_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View pv = inflater.inflate(R.layout.image_preview, null);
                    AlertDialog.Builder preview = new AlertDialog.Builder(context);
                    ImageView img = pv.findViewById(R.id.photo_view);
                    Glide.with(context).load(foods.get(position).getFood_image_url()).into(img);
                    preview.setView(pv);
                    preview.show();
                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    class food_list_viewholder extends RecyclerView.ViewHolder{
        TextView food_name;
        ImageView food_image;
        AppCompatCheckBox food_chkbox;
        CardView food_card;
        public food_list_viewholder(@NonNull View itemView) {
            super(itemView);
            food_name=itemView.findViewById(R.id.name);
            food_image=itemView.findViewById(R.id.img);
            food_chkbox=itemView.findViewById(R.id.food_chkbox);
            food_card=itemView.findViewById(R.id.card);
        }
    }
}
