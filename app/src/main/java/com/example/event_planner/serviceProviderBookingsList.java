package com.example.event_planner;

import android.os.Bundle;

import com.example.event_planner.Firebase_Operations.Firebase_Operations;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

public class serviceProviderBookingsList extends AppCompatActivity {
RecyclerView orders_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_bookings_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        orders_list=findViewById(R.id.serviceProviderBookingList);
        orders_list.setLayoutManager(new LinearLayoutManager(this));
        Firebase_Operations.get_booking_of_serviceProvider(this, FirebaseAuth.getInstance().getCurrentUser().getUid(),orders_list);
    }

}
