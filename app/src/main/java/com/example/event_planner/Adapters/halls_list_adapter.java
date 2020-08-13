package com.example.event_planner.Adapters;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.event_planner.Firebase_Operations.Firebase_Operations;
import com.example.event_planner.Model.Halls;
import com.example.event_planner.R;
import com.example.event_planner.theme_list_page;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import fr.ganfra.materialspinner.MaterialSpinner;

public class halls_list_adapter extends RecyclerView.Adapter<halls_list_adapter.halls_list_viewholder> {
    String hotel_id,page;
    ArrayList<Halls> halls;
    ArrayList<String> hallId;
    Context context;
    Button add_hall_btn;
    Fragment fragment;
    ArrayList<Halls> selected_halls_list=new ArrayList<Halls>();
    public halls_list_adapter(String hotel_id, String page, ArrayList<Halls> halls, ArrayList<String> hallId, Context context, Button add_hall_btn,Fragment fragment) {
        this.hotel_id = hotel_id;
        this.page = page;
        this.halls = halls;
        this.hallId = hallId;
        this.context = context;
        this.add_hall_btn = add_hall_btn;
        this.fragment=fragment;
        prefs= PreferenceManager.getDefaultSharedPreferences(context);
    }

    SharedPreferences prefs;
    @NonNull
    @Override
    public halls_list_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new halls_list_viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.food_list_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final halls_list_viewholder holder, final int position) {
        if(page.equals("Hall Offering")||page.equals("Hall Update")){
            holder.hall_chkbox.setVisibility(View.GONE);
            holder.hall_name.setText(halls.get(position).getName());
            Glide.with(context).load(halls.get(position).getHall_image_url()).into(holder.hall_image);
        }else{
            if(!halls.get(position).getAvailable()){
                holder.hall_chkbox.setEnabled(false);
            }
                holder.hall_name.setText(halls.get(position).getName());
                Glide.with(context).load(halls.get(position).getHall_image_url()).into(holder.hall_image);
            holder.hall_chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                     if(isChecked){
                         Log.e("onCheck","chkbox checked");
                      selected_halls_list.add(halls.get(position));
                     }else{
                         Log.e("onCheck","chkbox unchecked");
                         selected_halls_list.remove(position);
                     }
                }
            });
            add_hall_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selected_halls_list.size()>0) {

                        //context.startActivity(new Intent(context, theme_list_page.class).putExtra("hotel_id", hotel_id));
                        Firebase_Operations.check_if_hotel_already_booked(context,prefs.getString("event_time",""),prefs.getString("event_date",""),hotel_id,selected_halls_list);
                    }else{
                        Toast.makeText(context,"Select atleast one hall to organise Party",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        if(page.equals("Hall Update")){
            holder.hall_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View foodUpdate =LayoutInflater.from(context).inflate(R.layout.update_layout,null);
                    final TextInputEditText name=foodUpdate.findViewById(R.id.name_txt);
                    final TextInputEditText price=foodUpdate.findViewById(R.id.price_txt);
                    final MaterialSpinner Availability=foodUpdate.findViewById(R.id.available);
                    name.setText(halls.get(position).getName());
                    price.setText(String.valueOf(halls.get(position).getPrice()));
                    if(halls.get(position).getAvailable()) {
                        Availability.setSelection(1);
                    }else{
                        Availability.setSelection(2);
                    }
                    AlertDialog.Builder updateFoodDialog =new AlertDialog.Builder(context);
                    updateFoodDialog.setTitle("Update Hall Details");
                    updateFoodDialog.setView(foodUpdate);
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
                                map.put("name", name.getText().toString());
                                map.put("price", Integer.parseInt(price.getText().toString()));
                                if (Availability.getSelectedItem().toString().equals("Yes")) {
                                    map.put("available", true);
                                } else {
                                    map.put("available", false);
                                }
                                Firebase_Operations.update_Halls(context, map, FirebaseAuth.getInstance().getCurrentUser().getUid(), hallId.get(position),fragment);
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
        return halls.size();
    }

    class halls_list_viewholder extends RecyclerView.ViewHolder {
        TextView hall_name;
        ImageView hall_image;
        AppCompatCheckBox hall_chkbox;
        CardView hall_card;
        public halls_list_viewholder(@NonNull View itemView) {
            super(itemView);
            hall_name=itemView.findViewById(R.id.name);
            hall_image=itemView.findViewById(R.id.img);
            hall_chkbox=itemView.findViewById(R.id.food_chkbox);
            hall_card=itemView.findViewById(R.id.card);
        }


    }
}
