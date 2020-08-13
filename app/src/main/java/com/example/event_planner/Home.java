package com.example.event_planner;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.event_planner.Firebase_Operations.Firebase_Operations;
import com.example.event_planner.Model.user;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.HashMap;

import fr.ganfra.materialspinner.MaterialSpinner;

public class Home extends AppCompatActivity {
    Toolbar toolbar;
    SharedPreferences prefs;
    RecyclerView hotels_list;
    FloatingActionButton fab_sign_out,fab_orders,fab_update_profile;
    user u;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefs= PreferenceManager.getDefaultSharedPreferences(this);
        hotels_list=findViewById(R.id.hotels_list);
        fab_sign_out=findViewById(R.id.fab_logout);
        fab_orders=findViewById(R.id.fab_your_orders);
        fab_update_profile=findViewById(R.id.fab_update_profile);
        hotels_list.setLayoutManager(new LinearLayoutManager(this));
         u=new Gson().fromJson(prefs.getString("user_info",""),user.class);
        if(u!=null) {
            Firebase_Operations.get_hotel_by_city(u.getCity(), hotels_list, this);
        }
        fab_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                prefs.edit().remove("user_info").apply();
                prefs.edit().remove("user_role").apply();
                prefs.edit().remove("selected_food").apply();
                prefs.edit().remove("selected_halls").apply();
                prefs.edit().remove("selected_services").apply();
                prefs.edit().remove("event_time").apply();
                prefs.edit().remove("selected_theme").apply();
                prefs.edit().remove("event_date").apply();
                prefs.edit().remove("no_of_guests").apply();
                prefs.edit().remove("hotel_name").apply();
                prefs.edit().remove("selected_party_type").apply();
                startActivity(new Intent(Home.this,Login.class));
                finish();
            }
        });
        fab_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View update_profile_view = LayoutInflater.from(Home.this).inflate(R.layout.update_profile_layout,null);
                final TextInputEditText name=update_profile_view.findViewById(R.id.name_txt);
                final TextInputEditText email=update_profile_view.findViewById(R.id.email_txt);
                final TextInputEditText phone=update_profile_view.findViewById(R.id.phone_txt);
                final MaterialSpinner  city=update_profile_view.findViewById(R.id.choose_city);
                final TextInputEditText address=update_profile_view.findViewById(R.id.address_txt);
                final TextInputEditText price=update_profile_view.findViewById(R.id.price_txt);
                final TextInputEditText capacity=update_profile_view.findViewById(R.id.capacity_txt);
                price.setVisibility(View.GONE);
                capacity.setVisibility(View.GONE);
                name.setText(u.getName());
                email.setText(u.getEmail());
                phone.setText(u.getPhone());
                address.setText(u.getAddress());
                city.setSelection(1);
                android.app.AlertDialog update_profile_dialog =new android.app.AlertDialog.Builder(Home.this)
                .setTitle("Update Profile")
                .setView(update_profile_view)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                update_profile_dialog.show();
                update_profile_dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(name.getText().toString())){
                            name.setError("Enter Name");
                        }else if(TextUtils.isEmpty(email.getText().toString())){
                            email.setError("Enter Email");
                        }else if(TextUtils.isEmpty(phone.getText().toString())){
                            phone.setError("Enter Phone");
                        }else if(TextUtils.isEmpty(address.getText().toString())){
                            address.setError("Enter Address");
                        }else if(city.getSelectedItem()==null){
                            city.setError("Choose Your City");
                        }else{
                            HashMap<String,Object> map=new HashMap<>();
                            map.put("name",name.getText().toString());
                            map.put("address",address.getText().toString());
                            map.put("email",email.getText().toString());
                            map.put("city",city.getSelectedItem().toString());
                            map.put("phone",phone.getText().toString());
                            user users=new user(name.getText().toString(),city.getSelectedItem().toString(),email.getText().toString(),address.getText().toString(),phone.getText().toString(),u.getCategory());
                            prefs.edit().putString("user_info",new Gson().toJson(users)).apply();
                            prefs.edit().putString("user_role",u.getCategory()).apply();
                            Firebase_Operations.update_customer_profile(Home.this,map,FirebaseAuth.getInstance().getCurrentUser().getUid());
                        }
                    }
                });
            }
        });

        fab_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,BookingListPage.class));
            }
        });
    }
}
