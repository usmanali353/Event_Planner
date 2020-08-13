package com.example.event_planner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.example.event_planner.Model.Halls;
import com.example.event_planner.Model.service_provider;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class hotel_details_page extends AppCompatActivity {
    TextView location,email,phone,hotel_name;
    service_provider snapshot;
    Button book_hotel_btn;
    ImageView header,profile_photo;
    String selectedPartyType,selectedEventTime;
    ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
    // generate random color
    FloatingActionButton fab;
    int color = generator.getRandomColor(),y,m,day;
    SharedPreferences prefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024,1024);
        setContentView(R.layout.activity_hotel_details_page);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        prefs= PreferenceManager.getDefaultSharedPreferences(this);
        getSupportActionBar().setTitle("");
         book_hotel_btn=findViewById(R.id.book_hotel_btn);
        location=findViewById(R.id.location);
        email=findViewById(R.id.email);
        fab=findViewById(R.id.fab);
        phone=findViewById(R.id.phonenumber);
        hotel_name=findViewById(R.id.hotel_name);
        header=findViewById(R.id.header_cover_image);
        profile_photo=findViewById(R.id.service_provider_photo);
        snapshot=new Gson().fromJson(getIntent().getStringExtra("hotel_info"),service_provider.class);
        if(snapshot!=null){
            location.setText(String.valueOf(snapshot.getAddress()));
            email.setText(String.valueOf(snapshot.getEmail()));
            phone.setText(String.valueOf(snapshot.getPhone()));
            hotel_name.setText(String.valueOf(snapshot.getName()));
            Glide.with(hotel_details_page.this).load(String.valueOf(snapshot.getImage_url())).into(header);
            TextDrawable drawable = TextDrawable.builder().buildRect(snapshot.getName().substring(0,1), color);
            profile_photo.setImageDrawable(drawable);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(hotel_details_page.this,hotel_offering_page.class).putExtra("hotel_id",getIntent().getStringExtra("hotel_id")).putExtra("image_url",snapshot.getImage_url()));
                }
            });
        }
        book_hotel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               bookHotel();
            }
        });
    }
    private void bookHotel(){
        prefs.edit().putString("hotel_name",snapshot.getName()).apply();
        AlertDialog.Builder partyTypeDialog =new AlertDialog.Builder(hotel_details_page.this);
        partyTypeDialog.setTitle("Select Type of Party");
        partyTypeDialog.setSingleChoiceItems(getResources().getStringArray(R.array.party_type), -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedPartyType=  Arrays.asList(getResources().getStringArray(R.array.party_type)).get(which);
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prefs.edit().putString("selected_party_type",selectedPartyType).apply();
                View number_picker_view= getLayoutInflater().inflate(R.layout.choose_number_of_guests_layout,null);
                final NumberPicker numberPicker=number_picker_view.findViewById(R.id.select_number_of_people);
                numberPicker.setMaxValue(snapshot.getCapacity());
                AlertDialog no_of_guests_dialog =new AlertDialog.Builder(hotel_details_page.this)
                        .setTitle("Select Number of Guests")
                        .setView(number_picker_view)
                        .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                prefs.edit().putInt("no_of_guests",numberPicker.getValue()).apply();
                                DatePickerDialog datePickerDialog=new DatePickerDialog(hotel_details_page.this, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        day=dayOfMonth;
                                        m=month;
                                        y=year;
                                        prefs.edit().putString("event_date",String.valueOf(dayOfMonth)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year)).apply();
                                        selectEventTime();
                                    }
                                },y,m,day);
                                datePickerDialog.setTitle("Select Event Date");
                                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-3000);
                                datePickerDialog.show();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                no_of_guests_dialog.show();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
    private void selectEventTime(){
        final String[] time={"8am-10am","10am-12pm","12pm-2pm","2pm-7pm","7pm-10pm"};
        AlertDialog.Builder timeDialog=new AlertDialog.Builder(hotel_details_page.this);
        timeDialog.setTitle("Select Event Time");
        timeDialog.setSingleChoiceItems(time, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedEventTime=time[which];
            }
        }).setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prefs.edit().putString("event_time",selectedEventTime).apply();
                startActivity(new Intent(hotel_details_page.this,Halls_list_page.class).putExtra("hotel_id",getIntent().getStringExtra("hotel_id")));
            }
        }).show();

    }
}
