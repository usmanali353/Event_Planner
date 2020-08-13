package com.example.event_planner;

import android.content.DialogInterface;
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

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class serviceProviderBookingDetailsPage extends AppCompatActivity {
    TextView eventDate,startTime,theme,otherServices,food,no_of_guests,themePrice,foodPrice,servicesPrice,reservedHalls,reservedHallsRent,partyType;
    Button confirm_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_booking_details_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Orders orders=new Gson().fromJson(getIntent().getStringExtra("order_data"),Orders.class);
        eventDate=findViewById(R.id.start_date);
        partyType=findViewById(R.id.partyType);
        confirm_btn=findViewById(R.id.confirm_button);
        reservedHalls=findViewById(R.id.reservedHalls);
        reservedHallsRent=findViewById(R.id.reservedHallsRent);
        if(orders.getStatus().equals("Canceled")||orders.getStatus().equals("Conformed")){
            confirm_btn.setVisibility(View.GONE);
        }else{
            confirm_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder cancel_booking_dialog=new AlertDialog.Builder(serviceProviderBookingDetailsPage.this);
                    cancel_booking_dialog.setTitle("Cancel/Conform Booking");
                    cancel_booking_dialog.setMessage("Select from below if u want to cancel or confirm Booking");
                    cancel_booking_dialog.setPositiveButton("Conform Booking", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Firebase_Operations.confirm_booking(serviceProviderBookingDetailsPage.this,getIntent().getStringExtra("order_id"));
                            new sendmail(serviceProviderBookingDetailsPage.this,orders.getUser_email(),"Booking for your choosen hotel is conformed","Hotel you choose is booked we hope you will enjoy your Valueable ocassion").execute();
                            confirm_btn.setVisibility(View.GONE);
                        }
                    });
                    cancel_booking_dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    cancel_booking_dialog.setNeutralButton("Cancel Booking", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Firebase_Operations.cancel_booking(serviceProviderBookingDetailsPage.this,getIntent().getStringExtra("order_id"));
                            new sendmail(serviceProviderBookingDetailsPage.this,orders.getUser_email(),"Booking for your chosen hotel is Canceled","Booking for your chosen hotel is Canceled on your request we hope you will come back for another event").execute();
                            confirm_btn.setVisibility(View.GONE);
                        }
                    });
                    cancel_booking_dialog.show();
                }
            });
        }
        startTime=findViewById(R.id.event_time);
        theme=findViewById(R.id.theme);
        themePrice=findViewById(R.id.theme_price);
        otherServices=findViewById(R.id.other_services);
        servicesPrice=findViewById(R.id.services_price);
        food=findViewById(R.id.food);
        foodPrice=findViewById(R.id.food_price);
        no_of_guests=findViewById(R.id.no_of_guests);
        //Setting Values
        eventDate.setText(orders.getDate());
        startTime.setText(orders.getEvent_time());
        theme.setText(orders.getTheme());
        partyType.setText(orders.getPartyType());
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
        StringBuilder builder3 = new StringBuilder();
        for (Halls details : orders.getHalls()) {
            builder3.append(details.getName() + "\n");
        }
        otherServices.setText(builder2.toString());
        servicesPrice.setText("Rs "+orders.getServices_price());
        foodPrice.setText("Rs "+orders.getFoods_price());
        no_of_guests.setText(String.valueOf(orders.getNumber_of_guests()));
        reservedHalls.setText(builder3.toString());
        reservedHallsRent.setText("Rs "+orders.getHallsRent());
    }

}
