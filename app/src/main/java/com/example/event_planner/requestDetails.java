package com.example.event_planner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.event_planner.Firebase_Operations.Firebase_Operations;
import com.example.event_planner.Model.approvalRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class requestDetails extends AppCompatActivity {
 TextView hotelName,hotelEmail,hotelPhone,hotelAddress,requestId,requestDate;
 Button requestActionButton;
 String choice;
  String[] choices={"Approve","Reject"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        hotelName=findViewById(R.id.name);
        hotelAddress=findViewById(R.id.address);
        hotelEmail=findViewById(R.id.email);
        hotelPhone=findViewById(R.id.phone);
        requestId=findViewById(R.id.requestId);
        requestDate=findViewById(R.id.requestDate);
        requestActionButton=findViewById(R.id.confirm_button);
        final approvalRequest request= new Gson().fromJson(getIntent().getStringExtra("requestData"),approvalRequest.class);
        if(request!=null){
            hotelName.setText(request.getServiceProviderName());
            hotelAddress.setText(request.getAddress());
            hotelPhone.setText(request.getPhone());
            hotelEmail.setText(request.getEmail());
            requestId.setText(getIntent().getStringExtra("requestId"));
            requestDate.setText(request.getRequestDate());
        }
        requestActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder requestActionDialog=new AlertDialog.Builder(requestDetails.this);
                requestActionDialog.setTitle("Approve/disapprove Request");
                requestActionDialog.setSingleChoiceItems(choices, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choice=choices[which];
                    }
                });
                requestActionDialog.setPositiveButton("set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       if(choice.equalsIgnoreCase("Approve")){
                           HashMap<String, Object> map = new HashMap<>();
                           map.put("approved",true);
                           Firebase_Operations.approveRequest(requestDetails.this,map,request.getHotelId(),requestActionButton,getIntent().getStringExtra("requestId"));
                       }else {
                           final ProgressDialog pd=new ProgressDialog(requestDetails.this);
                           pd.setMessage("Rejecting the Request");
                           pd.show();
                           FirebaseFirestore.getInstance().collection("approvalRequests").document(getIntent().getStringExtra("requestId")).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                       FirebaseFirestore.getInstance().collection("service_provider").document(request.getHotelId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if(task.isSuccessful()){
                                                   pd.dismiss();
                                                   Toast.makeText(requestDetails.this,"Request was Rejected",Toast.LENGTH_LONG).show();
                                                   requestActionButton.setVisibility(View.GONE);
                                                   startActivity(new Intent(requestDetails.this,request_list_page.class));
                                                   finish();
                                               }
                                           }
                                       }).addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {
                                               Toast.makeText(requestDetails.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                           }
                                       });
                                   }
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   pd.dismiss();
                                   Toast.makeText(requestDetails.this,e.getMessage(),Toast.LENGTH_LONG).show();
                               }
                           });
                       }
                    }
                });
                requestActionDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }).show();

            }
        });
    }

}
