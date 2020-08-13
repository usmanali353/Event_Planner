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

public class BookingListPage extends AppCompatActivity {
RecyclerView orders_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        orders_list=findViewById(R.id.orders_list);
        orders_list.setLayoutManager(new LinearLayoutManager(this));
        Firebase_Operations.get_booking_of_user(this, FirebaseAuth.getInstance().getUid(),orders_list);
    }

}
