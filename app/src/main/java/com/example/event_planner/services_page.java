package com.example.event_planner;

import android.os.Bundle;

import com.example.event_planner.Firebase_Operations.Firebase_Operations;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;

public class services_page extends AppCompatActivity {
RecyclerView services_list;
Button add_services_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        services_list=findViewById(R.id.services_list);
        services_list.setLayoutManager(new LinearLayoutManager(this));
        add_services_btn=findViewById(R.id.select_services);
        Firebase_Operations.get_services_of_hotel(getIntent().getStringExtra("hotel_id"),services_page.this,services_list,add_services_btn,"Other Services Ordering",null);
    }

}
