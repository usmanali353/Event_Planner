package com.example.event_planner.Adapters;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.event_planner.Firebase_Operations.Firebase_Operations;
import com.example.event_planner.Model.Themes;
import com.example.event_planner.R;
import com.example.event_planner.food_list_page;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import fr.ganfra.materialspinner.MaterialSpinner;

public class themes_list_adapter extends RecyclerView.Adapter<themes_list_adapter.themes_list_viewholder> {
    ArrayList<Themes> themes;
    Context context;
    String hotel_id,page;
    SharedPreferences prefs;
    ArrayList<String> themeId;
    Fragment fragment;
    public themes_list_adapter(ArrayList<Themes> themes, Context context, String hotel_id, String page, ArrayList<String> themeId, Fragment fragment) {
        this.themes = themes;
        this.context = context;
        this.hotel_id=hotel_id;
        this.page=page;
        prefs= PreferenceManager.getDefaultSharedPreferences(context);
        this.themeId=themeId;
        this.fragment=fragment;
    }

    @NonNull
    @Override
    public themes_list_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new themes_list_viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_list_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull themes_list_viewholder holder, final int position) {

        if(page.equals("Theme Ordering")){
            holder.hotel_name.setText(String.valueOf(themes.get(position).getTheme_name()));
            Glide.with(context).load(themes.get(position).getTheme_image()).into(holder.img);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!themes.get(position).getAvailable()) {
                        Toast.makeText(context,"Theme is not available right now",Toast.LENGTH_LONG).show();
                    }else {
                        prefs.edit().putString("selected_theme", new Gson().toJson(themes.get(position))).apply();
                        Intent i = new Intent(context, food_list_page.class);
                        i.putExtra("hotel_id", hotel_id);
                        context.startActivity(i);
                    }
                }
            });
        }else if(page.equals("Theme Update")||page.equals("Theme offering")){
            holder.hotel_name.setText(String.valueOf(themes.get(position).getTheme_name()));
            Glide.with(context).load(themes.get(position).getTheme_image()).into(holder.img);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View themeUpdate =LayoutInflater.from(context).inflate(R.layout.update_layout,null);
                    final TextInputEditText name=themeUpdate.findViewById(R.id.name_txt);
                    final TextInputEditText price=themeUpdate.findViewById(R.id.price_txt);
                    final MaterialSpinner Availability=themeUpdate.findViewById(R.id.available);
                    name.setText(themes.get(position).getTheme_name());
                    price.setText(String.valueOf(themes.get(position).getTheme_price()));
                    if(themes.get(position).getAvailable()) {
                        Availability.setSelection(1);
                    }else{
                        Availability.setSelection(2);
                    }
                    AlertDialog.Builder updateFoodDialog =new AlertDialog.Builder(context);
                    updateFoodDialog.setTitle("Update Theme Details");
                    updateFoodDialog.setView(themeUpdate);
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
                                HashMap<String, Object> map=new HashMap<>();
                                map.put("theme_name",name.getText().toString());
                                map.put("theme_price",Integer.parseInt(price.getText().toString()));
                                if(Availability.getSelectedItem().toString().equals("Yes")) {
                                    map.put("available", true);
                                }else{
                                    map.put("available", false);
                                }
                                Firebase_Operations.update_theme(context,map,FirebaseAuth.getInstance().getCurrentUser().getUid(),themeId.get(position),fragment);
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
        return themes.size();
    }

    class themes_list_viewholder extends RecyclerView.ViewHolder{
         TextView hotel_name;
         ImageView img;
         CardView cardView;
        public themes_list_viewholder(@NonNull View itemView) {
            super(itemView);
            hotel_name=itemView.findViewById(R.id.name);
            img=itemView.findViewById(R.id.hotel_image);
            cardView=itemView.findViewById(R.id.card);
        }
    }
}
