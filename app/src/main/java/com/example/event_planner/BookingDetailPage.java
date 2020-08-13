package com.example.event_planner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.event_planner.Firebase_Operations.Firebase_Operations;
import com.example.event_planner.Model.Food;
import com.example.event_planner.Model.Halls;
import com.example.event_planner.Model.Orders;
import com.example.event_planner.Model.Services;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BookingDetailPage extends AppCompatActivity {
    TextView eventDate,startTime,theme,otherServices,food,no_of_guests,themePrice,foodPrice,servicesPrice,partyType,halls,hallRent;
    Button cancel_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Orders orders=new Gson().fromJson(getIntent().getStringExtra("order_data"),Orders.class);
        eventDate=findViewById(R.id.start_date);
        cancel_btn=findViewById(R.id.cancel_button);
        if(orders.getStatus().equals("Canceled")){
            cancel_btn.setVisibility(View.GONE);
        }else{
            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder cancel_booking_dialog=new AlertDialog.Builder(BookingDetailPage.this);
                    cancel_booking_dialog.setTitle("Cancel Booking");
                    cancel_booking_dialog.setMessage("are you sure you want to cancel your booking");
                    cancel_booking_dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Firebase_Operations.cancel_booking(BookingDetailPage.this,getIntent().getStringExtra("order_id"));
                            cancel_btn.setVisibility(View.GONE);
                        }
                    });
                    cancel_booking_dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    cancel_booking_dialog.show();
                }
            });
        }
        startTime=findViewById(R.id.event_time);
        partyType=findViewById(R.id.partyType);
        halls=findViewById(R.id.reservedHalls);
        hallRent=findViewById(R.id.reservedHallsRent);
        theme=findViewById(R.id.theme);
        themePrice=findViewById(R.id.theme_price);
        otherServices=findViewById(R.id.other_services);
        servicesPrice=findViewById(R.id.services_price);
        food=findViewById(R.id.food);
        foodPrice=findViewById(R.id.food_price);
        no_of_guests=findViewById(R.id.no_of_guests);
        //Setting Values
        eventDate.setText(orders.getDate());
        partyType.setText(orders.getPartyType());

        startTime.setText(orders.getEvent_time());
        theme.setText(orders.getTheme());
        themePrice.setText("Rs "+orders.getTheme_price());
        StringBuilder builder = new StringBuilder();
        for (Food details : orders.getFoods()) {
            builder.append(details.getFood_name() + "\n");
        }
        food.setText(builder.toString());
        StringBuilder builder2 = new StringBuilder();
        for (Services details : orders.getServices()) {
            builder2.append(details.getService_name() + "\n");
        }
        otherServices.setText(builder2.toString());
        StringBuilder builder3 = new StringBuilder();
        for (Halls details : orders.getHalls()) {
            builder3.append(details.getName() + "\n");
        }
        halls.setText(builder3.toString());
        otherServices.setText(builder2.toString());
        servicesPrice.setText("Rs "+orders.getServices_price());
        foodPrice.setText("Rs "+orders.getFoods_price());
        no_of_guests.setText(String.valueOf(orders.getNumber_of_guests()));
        hallRent.setText("Rs "+orders.getHallsRent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            startActivity(new Intent(this,BookingListPage.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
