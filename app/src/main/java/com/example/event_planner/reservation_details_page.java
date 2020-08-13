package com.example.event_planner;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.event_planner.Firebase_Operations.Firebase_Operations;
import com.example.event_planner.Model.Food;
import com.example.event_planner.Model.Halls;
import com.example.event_planner.Model.Orders;
import com.example.event_planner.Model.Services;
import com.example.event_planner.Model.Themes;
import com.example.event_planner.Model.user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class reservation_details_page extends AppCompatActivity {
 SharedPreferences prefs;
 TextView eventDate,startTime,theme,otherServices,food,no_of_guests,themePrice,foodPrice,servicesPrice,partyType,reservedHalls,reservedHallsRent;
 Button confirm_button;
 user userInfo;
 int theme_price,food_price,services_price,hallRent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_details_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        confirm_button=findViewById(R.id.confirm_button);
        no_of_guests=findViewById(R.id.no_of_guests);
        reservedHalls=findViewById(R.id.reservedHalls);
        partyType=findViewById(R.id.partyType);
        reservedHallsRent=findViewById(R.id.reservedHallsRent);
        prefs= PreferenceManager.getDefaultSharedPreferences(this);
        userInfo=new Gson().fromJson(prefs.getString("user_info",""), user.class);
        theme_price=new Gson().fromJson(prefs.getString("selected_theme",""), Themes.class).getTheme_price();
        eventDate=findViewById(R.id.start_date);
        themePrice=findViewById(R.id.theme_price);
        foodPrice=findViewById(R.id.food_price);
        servicesPrice=findViewById(R.id.services_price);
        themePrice.setText("Rs "+theme_price);
        eventDate.setText(prefs.getString("event_date",""));
        startTime=findViewById(R.id.event_time);
        startTime.setText(prefs.getString("event_time",""));
        no_of_guests.setText(String.valueOf(prefs.getInt("no_of_guests",0)));
        food=findViewById(R.id.food);
        theme=findViewById(R.id.theme);
        theme.setText(new Gson().fromJson(prefs.getString("selected_theme",""), Themes.class).getTheme_name());
        otherServices=findViewById(R.id.other_services);
        final ArrayList<Food> foods=new Gson().fromJson(prefs.getString("selected_food",""),new TypeToken<ArrayList<Food>>(){}.getType());
        final ArrayList<Services> services=new Gson().fromJson(prefs.getString("selected_services",""),new TypeToken<ArrayList<Services>>(){}.getType());
        final ArrayList<Halls> halls=new Gson().fromJson(prefs.getString("selected_halls",""),new TypeToken<ArrayList<Halls>>(){}.getType());
        StringBuilder builder3 = new StringBuilder();
        for (Halls hall: halls){
            builder3.append(hall.getName()+"\n");
        }
        reservedHalls.setText(builder3.toString());
        for(int i=0;i<halls.size();i++){
            hallRent=hallRent+halls.get(i).getPrice();
        }
        reservedHallsRent.setText(String.valueOf(hallRent));
        for(int i=0;i<foods.size();i++){
            food_price=food_price+foods.get(i).getPrice();
        }
        for(int i=0;i<services.size();i++){
            services_price=services_price+services.get(i).getService_price();
        }
        servicesPrice.setText("Rs "+services_price);
        foodPrice.setText("Rs "+food_price*prefs.getInt("no_of_guests",0));
       partyType.setText(prefs.getString("selected_party_type",""));
        StringBuilder builder = new StringBuilder();
        for (Food details : foods) {
            builder.append(details.getFood_name() + "\n");
        }
        food.setText(builder.toString());
        StringBuilder builder2 = new StringBuilder();
        for (Services details : services) {
            builder2.append(details.getService_name() + "\n");
        }
        otherServices.setText(builder2.toString());

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Orders orders=new Orders(prefs.getInt("no_of_guests",0),food_price*prefs.getInt("no_of_guests",0),services_price,theme_price,prefs.getString("event_date",""),prefs.getString("event_time",""),new Gson().fromJson(prefs.getString("selected_theme",""), Themes.class).getTheme_name(),"Unconfirmed",foods,services, FirebaseAuth.getInstance().getUid(), getIntent().getStringExtra("hotel_id"),prefs.getString("hotel_name",""),userInfo.getEmail(),userInfo.getPhone(),halls,hallRent,prefs.getString("selected_party_type",""));
                Firebase_Operations.book_hotel(reservation_details_page.this,orders);
            }
        });
    }
}
