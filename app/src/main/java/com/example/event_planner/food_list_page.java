package com.example.event_planner;

import android.os.Bundle;
import android.widget.Button;

import com.example.event_planner.Firebase_Operations.Firebase_Operations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class food_list_page extends AppCompatActivity {
RecyclerView food_list;
Button select_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        food_list = findViewById(R.id.food_list);
        select_btn =findViewById(R.id.select_food_btn);
        food_list.setLayoutManager(new LinearLayoutManager(this));
        Firebase_Operations.get_food_of_hotel(getIntent().getStringExtra("hotel_id"),this,food_list,select_btn,"Food Ordering",null);
    }

}
