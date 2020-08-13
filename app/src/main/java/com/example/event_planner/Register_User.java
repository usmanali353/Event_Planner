package com.example.event_planner;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.example.event_planner.Firebase_Operations.Firebase_Operations;
import com.example.event_planner.Model.service_provider;
import com.example.event_planner.Model.user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import fr.ganfra.materialspinner.MaterialSpinner;


public class Register_User extends AppCompatActivity {
     TextInputEditText name;
     MaterialSpinner city;
     TextInputEditText email;
     TextInputEditText address;
     TextInputEditText capacity;
     TextInputEditText price;
     TextInputLayout capacity_textinputlayout,price_textinputlayout;
     MaterialSpinner category;
     ImageView hotel_image;
     Uri image_uri=null;
     Bitmap bitmap=null;
     StorageReference storageReference,hotel_image_ref;
     SharedPreferences prefs;
     Button add_info;
     FirebaseUser user;
     Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_now);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        name = findViewById(R.id.name_txt);
        add_info=findViewById(R.id.add_profile_info);
        capacity_textinputlayout=findViewById(R.id.capacity_textinputlayout);
        price_textinputlayout=findViewById(R.id.pricing_textinputlayout);
        city = findViewById(R.id.choose_city);
        email = findViewById(R.id.email_txt);
        address = findViewById(R.id.address_txt);
        category = findViewById(R.id.choose_category);
        hotel_image=findViewById(R.id.hotel_image);
        capacity=findViewById(R.id.capacity_txt);
        price=findViewById(R.id.pricing_txt);
        user= FirebaseAuth.getInstance().getCurrentUser();
        prefs= PreferenceManager.getDefaultSharedPreferences(this);
        storageReference= FirebaseStorage.getInstance().getReference();
        hotel_image_ref= storageReference.child("images/"+ UUID.randomUUID().toString());
        if(category.getSelectedItem()==null){
            if(hotel_image.getVisibility()==View.VISIBLE){
                hotel_image.setVisibility(View.GONE);
            }
            if(capacity_textinputlayout.getVisibility()==View.VISIBLE){
                capacity_textinputlayout.setVisibility(View.GONE);
            }
            if(price_textinputlayout.getVisibility()==View.VISIBLE){
                price_textinputlayout.setVisibility(View.GONE);
            }
        }
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(category.getSelectedItem()!=null&&position==1){
                    hotel_image.setVisibility(View.VISIBLE);
                    capacity_textinputlayout.setVisibility(View.VISIBLE);
                    price_textinputlayout.setVisibility(View.VISIBLE);

                }else if(category.getSelectedItem()!=null&&position==0){
                    hotel_image.setVisibility(View.GONE);
                    capacity_textinputlayout.setVisibility(View.GONE);
                    price_textinputlayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        hotel_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1000);
            }
        });
        add_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty()) {
                    name.setError("Enter Name");
                } else if (city.getSelectedItem().toString().equalsIgnoreCase("Choose City")) {
                    city.setError("Choose City");
                } else if (category.getSelectedItem().toString().equalsIgnoreCase("Choose Category")) {
                    category.setError("Choose Category");
                } else if (email.getText().toString().isEmpty()) {
                    email.setError("Enter Email");
                } else if (!Utils.isEmailValid(email.getText().toString())) {
                    email.setError("Invalid Email");
                } else if (address.getText().toString().isEmpty()) {
                    address.setError("Enter Address");
                } else {
                    final ProgressDialog dialog=new ProgressDialog(Register_User.this);
                    dialog.setMessage("Please Wait...");
                    dialog.show();
                    final com.example.event_planner.Model.user userinfo = new user(name.getText().toString(), city.getSelectedItem().toString(), email.getText().toString(), address.getText().toString(),getIntent().getStringExtra("mobile"), category.getSelectedItem().toString());
                    if (category.getSelectedItem().toString().equalsIgnoreCase("Customer")) {
                        FirebaseFirestore.getInstance().collection("users").document(user.getUid()).set(userinfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if(dialog.isShowing()){
                                        dialog.cancel();
                                    }
                                    prefs.edit().putString("user_info",new Gson().toJson(userinfo)).apply();
                                    prefs.edit().putString("user_role","Customer").apply();
                                    Intent intent = new Intent(Register_User.this, Home.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if(dialog.isShowing()){
                                    dialog.cancel();
                                }
                                Toast.makeText(Register_User.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else if (category.getSelectedItem().toString().equalsIgnoreCase("Service Provider")) {
                        if(bitmap==null||image_uri==null){
                            Toast.makeText(Register_User.this,"Upload Image for Service Provider",Toast.LENGTH_LONG).show();
                        }else if(TextUtils.isEmpty(capacity.getText().toString())){
                             capacity.setError("Enter Capacity of hotel");
                        }else if(TextUtils.isEmpty(price.getText().toString())){
                             price.setError("Enter Price");
                        }else {
                            hotel_image_ref.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    hotel_image_ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final service_provider hotel = new service_provider(category.getSelectedItem().toString(), name.getText().toString(), city.getSelectedItem().toString(), email.getText().toString(), address.getText().toString(), uri.toString(),getIntent().getStringExtra("mobile"),Integer.parseInt(capacity.getText().toString()),Integer.parseInt(price.getText().toString()),false);
                                            FirebaseFirestore.getInstance().collection("service_provider").document(user.getUid()).set(hotel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        if (dialog.isShowing()) {
                                                            dialog.cancel();
                                                        }
                                                        Date c = Calendar.getInstance().getTime();
                                                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                                        String formattedDate = df.format(c);
                                                        Firebase_Operations.submitApprovalRequest(Register_User.this,user.getUid(),hotel.getName(),hotel.getEmail(),hotel.getPhone(),hotel.getAddress(),formattedDate);
//                                                        prefs.edit().putString("user_info", new Gson().toJson(hotel)).apply();
//                                                        prefs.edit().putString("user_role","Service Provider").apply();
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    if (dialog.isShowing()) {
                                                        dialog.cancel();
                                                    }
                                                    Toast.makeText(Register_User.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if (dialog.isShowing()) {
                                                dialog.cancel();
                                            }
                                            Toast.makeText(Register_User.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (dialog.isShowing()) {
                                        dialog.cancel();
                                    }
                                    Toast.makeText(Register_User.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }

            }
        });

    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                image_uri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(image_uri);
                bitmap = BitmapFactory.decodeStream(imageStream);
                hotel_image.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

}
