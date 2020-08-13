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

public class Halls_list_page extends AppCompatActivity {
RecyclerView halls_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halls_list_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        halls_list=findViewById(R.id.halls_list);
        halls_list.setLayoutManager(new LinearLayoutManager(this));
        Button add_halls_btn=findViewById(R.id.select_halls_btn);
        Firebase_Operations.get_Halls_of_hotel(this,getIntent().getStringExtra("hotel_id"),add_halls_btn,halls_list,"Hall Ordering",null);
    }
}
