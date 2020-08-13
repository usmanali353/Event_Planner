package com.example.event_planner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.event_planner.Firebase_Operations.Firebase_Operations;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class request_list_page extends AppCompatActivity {
 RecyclerView request_list;
 SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        prefs= PreferenceManager.getDefaultSharedPreferences(this);
        setSupportActionBar(toolbar);
        request_list=findViewById(R.id.request_list);
        request_list.setLayoutManager(new LinearLayoutManager(this));
        Firebase_Operations.get_request_list(request_list_page.this,request_list);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.request_list_page_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.sign_out){
            prefs.edit().remove("user_role").apply();
            startActivity(new Intent(request_list_page.this,Login.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
