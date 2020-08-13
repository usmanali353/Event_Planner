package com.example.event_planner;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.event_planner.Firebase_Operations.Firebase_Operations;

public class theme_list_page extends AppCompatActivity {
RecyclerView themes_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_list_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        themes_list=findViewById(R.id.themes_list);
        themes_list.setLayoutManager(new LinearLayoutManager(this));
        Firebase_Operations.get_themes_of_the_hotel(getIntent().getStringExtra("hotel_id"),themes_list,this,"Theme Ordering",null);
    }

}
