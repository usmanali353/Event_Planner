package com.example.event_planner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class selection extends AppCompatActivity {
    SharedPreferences prefs;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(1024,1024);
        setContentView(R.layout.activity_selection);
        prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("email",null)!=null&&prefs.getString("password",null)!=null&&FirebaseAuth.getInstance().getCurrentUser()!=null){
             if(prefs.getString("role",null).equalsIgnoreCase("Service Provider")){
                 startActivity(new Intent(selection.this,Admin_home.class));
                 finish();
             }else{
                 startActivity(new Intent(selection.this,Home.class));
                 finish();
             }
        }
        findViewById(R.id.main_login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(selection.this,Login.class));
            }
        });
        findViewById(R.id.main_join_now_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(selection.this,Register_User.class));
            }
        });

    }
}
