package com.example.event_planner;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.preference.PreferenceManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.example.event_planner.Firebase_Operations.Firebase_Operations;
import com.example.event_planner.Model.service_provider;
import com.example.event_planner.Model.user;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import fr.ganfra.materialspinner.MaterialSpinner;


public class Admin_home extends AppCompatActivity {
    Toolbar toolbar;
    SharedPreferences prefs;
    TextView location,email,hotel_name,phone;
    FloatingActionButton fab,bookings,signout,update_offering,fab_update_profile;
    ImageView header,profile_photo,image;
    Bitmap bitmap=null;
    Uri image_uri=null;
    ColorGenerator generator = ColorGenerator.MATERIAL;
    int color = generator.getRandomColor();
    service_provider sp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024,1024);
        setContentView(R.layout.activity_admin_home);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        prefs= PreferenceManager.getDefaultSharedPreferences(this);
        location=findViewById(R.id.location);
        fab_update_profile=findViewById(R.id.fab_update_profile);
        email=findViewById(R.id.email);
        fab=findViewById(R.id.fab_add_services);
        phone=findViewById(R.id.phonenumber);
        hotel_name=findViewById(R.id.hotel_name);
        header=findViewById(R.id.header_cover_image);
        profile_photo=findViewById(R.id.service_provider_photo);
        bookings=findViewById(R.id.fab_your_orders);
        update_offering=findViewById(R.id.fab_update_services);
        signout=findViewById(R.id.fab_logout);
        try {
             sp = new Gson().fromJson(prefs.getString("user_info",null), service_provider.class);
            if(sp!=null){
                location.setText(sp.getAddress());
                email.setText(sp.getEmail());
                phone.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                hotel_name.setText(sp.getName());
                Glide.with(this).load(sp.getImage_url()).into(header);
                TextDrawable drawable = TextDrawable.builder().buildRect(sp.getName().substring(0,1), color);
                profile_photo.setImageDrawable(drawable);
                update_offering.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Admin_home.this,OfferingUpdatePage.class).putExtra("hotel_id",FirebaseAuth.getInstance().getCurrentUser().getUid()).putExtra("image_url",sp.getImage_url()));
                    }
                });
            }
        }catch(Exception e){
            e.printStackTrace();
            Log.e("exp",e.getMessage());
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu =new PopupMenu(Admin_home.this,view);
                popupMenu.getMenuInflater().inflate(R.menu.admin_page_popup_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.add_food){
                            View v= LayoutInflater.from(Admin_home.this).inflate(R.layout.add_themes_layout,null);
                            TextInputLayout food_name_textinputlayout =v.findViewById(R.id.theme_name_textinputlayout);
                            food_name_textinputlayout.setHint("Food Name");
                            TextInputLayout food_price_textinputlayout =v.findViewById(R.id.theme_price_textinputlayout);
                            food_price_textinputlayout.setHint("Food Price");
                            final TextInputEditText food_name = v.findViewById(R.id.theme_name_txt);
                            final TextInputEditText food_price =v.findViewById(R.id.theme_price_txt);
                            image=v.findViewById(R.id.theme_image);
                            image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                    photoPickerIntent.setType("image/*");
                                    startActivityForResult(photoPickerIntent, 1000);
                                }
                            });
                            AlertDialog add_food_dialog=new AlertDialog.Builder(Admin_home.this)
                              .setTitle("Add Food to the Hotel")
                               .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {

                                   }
                               }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).setView(v).create();
                                add_food_dialog.show();
                                add_food_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(food_name.getText().toString().isEmpty()){
                                            food_name.setError("Enter Food Name");
                                        }else if(food_price.getText().toString().isEmpty()){
                                            food_price.setError("Enter Food Price");
                                        }else if(bitmap==null||image_uri==null){
                                            Toast.makeText(Admin_home.this,"Select Image for Food",Toast.LENGTH_LONG).show();
                                        }else if(Integer.parseInt(food_price.getText().toString())<50){
                                            food_price.setError("Food Price too less");
                                        }else{
                                            Firebase_Operations.add_food_to_the_hotel(FirebaseAuth.getInstance().getCurrentUser().getUid(),Admin_home.this,food_name.getText().toString(),image_uri,Integer.parseInt(food_price.getText().toString()));
                                            food_name.setText("");
                                            food_price.setText("");
                                            image.setImageResource(R.drawable.upload);
                                        }
                                    }
                                });

                        }
                        else if(item.getItemId()==R.id.add_themes){
                            View v= LayoutInflater.from(Admin_home.this).inflate(R.layout.add_themes_layout,null);
                            TextInputLayout theme_name_textinputlayout =v.findViewById(R.id.theme_name_textinputlayout);
                            theme_name_textinputlayout.setHint("Theme Name");
                            TextInputLayout theme_price_textinputlayout =v.findViewById(R.id.theme_price_textinputlayout);
                            theme_price_textinputlayout.setHint("Theme Price");
                            final TextInputEditText theme_name = v.findViewById(R.id.theme_name_txt);
                            final TextInputEditText theme_price =v.findViewById(R.id.theme_price_txt);
                            image=v.findViewById(R.id.theme_image);
                            image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                    photoPickerIntent.setType("image/*");
                                    startActivityForResult(photoPickerIntent, 1000);
                                }
                            });
                            AlertDialog add_theme_dialog=new AlertDialog.Builder(Admin_home.this)
                                    .setTitle("Add Theme to the Hotel")
                                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).setView(v).create();
                            add_theme_dialog.show();
                            add_theme_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(theme_name.getText().toString().isEmpty()){
                                        theme_name.setError("Enter Food Name");
                                    }else if(theme_price.getText().toString().isEmpty()){
                                        theme_price.setError("Enter Food Price");
                                    }else if(bitmap==null||image_uri==null){
                                        Toast.makeText(Admin_home.this,"Select Image for Theme",Toast.LENGTH_LONG).show();
                                    }else if(Integer.parseInt(theme_price.getText().toString())<1000){
                                        theme_price.setError("Theme Price too less");
                                    }else{
                                        Firebase_Operations.add_themes_to_the_hotel(Admin_home.this,FirebaseAuth.getInstance().getCurrentUser().getUid(),theme_name.getText().toString(),Integer.parseInt(theme_price.getText().toString()),image_uri);
                                        theme_name.setText("");
                                        theme_price.setText("");
                                        image.setImageResource(R.drawable.upload);
                                    }
                                }
                            });
                        }
                        else if(item.getItemId()==R.id.add_other_services){
                            View v= LayoutInflater.from(Admin_home.this).inflate(R.layout.add_themes_layout,null);
                            TextInputLayout food_name_textinputlayout =v.findViewById(R.id.theme_name_textinputlayout);
                            food_name_textinputlayout.setHint("Service Name");
                            TextInputLayout food_price_textinputlayout =v.findViewById(R.id.theme_price_textinputlayout);
                            food_price_textinputlayout.setHint("Service Price");
                            final TextInputEditText service_name =  v.findViewById(R.id.theme_name_txt);
                            final TextInputEditText service_price = v.findViewById(R.id.theme_price_txt);
                            image=v.findViewById(R.id.theme_image);
                            image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                    photoPickerIntent.setType("image/*");
                                    startActivityForResult(photoPickerIntent, 1000);
                                }
                            });
                            AlertDialog add_food_dialog=new AlertDialog.Builder(Admin_home.this)
                                    .setTitle("Add Service to the Hotel")
                                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).setView(v).create();
                            add_food_dialog.show();
                            add_food_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(service_name.getText().toString().isEmpty()){
                                        service_name.setError("Enter Service Name");
                                    }else if(service_price.getText().toString().isEmpty()){
                                        service_price.setError("Enter Service Price");
                                    }else if(bitmap==null||image_uri==null){
                                        Toast.makeText(Admin_home.this,"Select Image for Service",Toast.LENGTH_LONG).show();
                                    }else if(Integer.parseInt(service_price.getText().toString())<1000){
                                        service_price.setError("Service Price too less");
                                    }else{
                                        Firebase_Operations.add_other_services_to_the_hotel(FirebaseAuth.getInstance().getCurrentUser().getUid(),Admin_home.this,service_name.getText().toString(),image_uri,Integer.parseInt(service_price.getText().toString()));
                                        service_name.setText("");
                                        service_price.setText("");
                                        image.setImageResource(R.drawable.upload);
                                    }
                                }
                            });
                        }
                        else if(item.getItemId()==R.id.add_hall){
                            View v= LayoutInflater.from(Admin_home.this).inflate(R.layout.add_themes_layout,null);
                            TextInputLayout food_name_textinputlayout =v.findViewById(R.id.theme_name_textinputlayout);
                            food_name_textinputlayout.setHint("Hall Name");
                            TextInputLayout food_price_textinputlayout =v.findViewById(R.id.theme_price_textinputlayout);
                            food_price_textinputlayout.setHint("Hall Rent Price");
                            final TextInputEditText hall_name = v.findViewById(R.id.theme_name_txt);
                            final TextInputEditText hall_price =v.findViewById(R.id.theme_price_txt);
                            image=v.findViewById(R.id.theme_image);
                            image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                    photoPickerIntent.setType("image/*");
                                    startActivityForResult(photoPickerIntent, 1000);
                                }
                            });
                            AlertDialog add_food_dialog=new AlertDialog.Builder(Admin_home.this)
                                    .setTitle("Add Hall to the Hotel")
                                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).setView(v).create();
                            add_food_dialog.show();
                            add_food_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(hall_name.getText().toString().isEmpty()){
                                        hall_name.setError("Enter Hall Name");
                                    }else if(hall_price.getText().toString().isEmpty()){
                                        hall_price.setError("Enter Hall Price");
                                    }else if(Integer.parseInt(hall_price.getText().toString())<1000){
                                        hall_price.setError("Hall Price too less");
                                    }else if(bitmap==null||image_uri==null){
                                        Toast.makeText(Admin_home.this,"Select Image for Hall",Toast.LENGTH_LONG).show();
                                    }else{
                                        Firebase_Operations.add_halls_to_the_hotel(Admin_home.this,hall_name.getText().toString(),Integer.parseInt(hall_price.getText().toString()),FirebaseAuth.getInstance().getCurrentUser().getUid(),image_uri);
                                        hall_name.setText("");
                                        hall_price.setText("");
                                        image.setImageResource(R.drawable.upload);
                                    }
                                }
                            });
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        bookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Admin_home.this,serviceProviderBookingsList.class));
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                prefs.edit().remove("user_info").apply();
                prefs.edit().remove("user_role").apply();
                prefs.edit().remove("selected_food").apply();
                prefs.edit().remove("selected_services").apply();
                prefs.edit().remove("selected_halls").apply();
                prefs.edit().remove("event_time").apply();
                prefs.edit().remove("selected_theme").apply();
                prefs.edit().remove("event_date").apply();
                prefs.edit().remove("no_of_guests").apply();
                prefs.edit().remove("hotel_name").apply();
                prefs.edit().remove("selected_party_type").apply();
                startActivity(new Intent(Admin_home.this,Login.class));
                finish();
            }
        });
        fab_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View update_profile_view = LayoutInflater.from(Admin_home.this).inflate(R.layout.update_profile_layout,null);
                final TextInputEditText name=update_profile_view.findViewById(R.id.name_txt);
                final TextInputEditText email=update_profile_view.findViewById(R.id.email_txt);
                final TextInputEditText phone=update_profile_view.findViewById(R.id.phone_txt);
                final MaterialSpinner city=update_profile_view.findViewById(R.id.choose_city);
                final TextInputEditText address=update_profile_view.findViewById(R.id.address_txt);
                final TextInputEditText price=update_profile_view.findViewById(R.id.price_txt);
                final TextInputEditText capacity=update_profile_view.findViewById(R.id.capacity_txt);
                price.setVisibility(View.VISIBLE);
                capacity.setVisibility(View.VISIBLE);
                name.setText(sp.getName());
                email.setText(sp.getEmail());
                phone.setText(sp.getPhone());
                address.setText(sp.getAddress());
                price.setText(String.valueOf(sp.getPrice()));
                capacity.setText(String.valueOf(sp.getCapacity()));
                city.setSelection(1);
                android.app.AlertDialog update_profile_dialog =new android.app.AlertDialog.Builder(Admin_home.this)
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
                        }else if(TextUtils.isEmpty(price.getText().toString())||Integer.parseInt(price.getText().toString())==0){
                            price.setError("Enter Price");
                        }else if(TextUtils.isEmpty(capacity.getText().toString())||Integer.parseInt(capacity.getText().toString())==0){
                            price.setError("Enter Capacity");
                        }else{
                            HashMap<String,Object> map=new HashMap<>();
                            map.put("name",name.getText().toString());
                            map.put("address",address.getText().toString());
                            map.put("email",email.getText().toString());
                            map.put("city",city.getSelectedItem().toString());
                            map.put("phone",phone.getText().toString());
                            map.put("capacity",Integer.parseInt(capacity.getText().toString()));
                            map.put("price",Integer.parseInt(price.getText().toString()));
                            service_provider serviceProvider =new service_provider(sp.getCategory(),name.getText().toString(),city.getSelectedItem().toString(),email.getText().toString(),address.getText().toString(),sp.getImage_url(),phone.getText().toString(),Integer.parseInt(capacity.getText().toString()),Integer.parseInt(price.getText().toString()),sp.isApproved());
                            prefs.edit().putString("user_info",new Gson().toJson(serviceProvider)).apply();
                            prefs.edit().putString("user_role",sp.getCategory()).apply();
                            Firebase_Operations.update_service_provider_profile(Admin_home.this,map,FirebaseAuth.getInstance().getCurrentUser().getUid());
                        }
                    }
                });
            }
        });

    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                image_uri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(image_uri);
                bitmap = BitmapFactory.decodeStream(imageStream);
                image.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}
