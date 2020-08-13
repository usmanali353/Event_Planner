package com.example.event_planner.Firebase_Operations;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.event_planner.Adapters.food_list_adapter;
import com.example.event_planner.Adapters.halls_list_adapter;
import com.example.event_planner.Adapters.hotels_list_adapter;
import com.example.event_planner.Adapters.orders_list_adapter;
import com.example.event_planner.Adapters.request_list_adapter;
import com.example.event_planner.Adapters.serviceProviderBookingListAdapter;
import com.example.event_planner.Adapters.services_list_adapter;
import com.example.event_planner.Adapters.themes_list_adapter;
import com.example.event_planner.Admin_home;
import com.example.event_planner.Home;
import com.example.event_planner.Login;
import com.example.event_planner.Model.Food;
import com.example.event_planner.Model.Halls;
import com.example.event_planner.Model.Orders;
import com.example.event_planner.Model.Services;
import com.example.event_planner.Model.Themes;
import com.example.event_planner.Model.approvalRequest;
import com.example.event_planner.Model.service_provider;
import com.example.event_planner.Register_User;
import com.example.event_planner.request_list_page;
import com.example.event_planner.sendmail;
import com.example.event_planner.theme_list_page;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Firebase_Operations {
    public static void get_hotel_by_city(String city, final RecyclerView hotels_list, final Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ProgressDialog dialog = new ProgressDialog(context);
        final List<String> hotel_ids = new ArrayList<>();
        dialog.setMessage("Getting Hotels in Your City");
        dialog.show();
        final ArrayList<service_provider> serviceProviders = new ArrayList<>();
        db.collection("service_provider").whereEqualTo("city", city).whereEqualTo("approved",true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }

                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                    serviceProviders.add(queryDocumentSnapshots.getDocuments().get(i).toObject(service_provider.class));
                    hotel_ids.add(queryDocumentSnapshots.getDocuments().get(i).getId());
                }
                hotels_list.setAdapter(new hotels_list_adapter(serviceProviders, context, hotel_ids));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void add_themes_to_the_hotel(final Context context, final String user_id, final String theme_name, final int theme_price, final Uri theme_image) {
        final StorageReference storageReference, theme_image_ref;
        storageReference = FirebaseStorage.getInstance().getReference();
        theme_image_ref = storageReference.child("images/" + UUID.randomUUID().toString());
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Please Wait...");
        pd.show();
        FirebaseFirestore.getInstance().collection("service_provider").document(user_id).collection("themes").whereEqualTo("theme_name", theme_name).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                    if (pd.isShowing()) {
                        pd.cancel();
                    }
                    Toast.makeText(context, "Theme Already Exists", Toast.LENGTH_LONG).show();
                } else {
                    theme_image_ref.putFile(theme_image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            theme_image_ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Themes themes = new Themes(theme_name, theme_price, uri.toString(), true);
                                    db.collection("service_provider").document(user_id).collection("themes").document().set(themes).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            pd.cancel();
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Theme Added to the Hotel", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.cancel();
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.cancel();
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.cancel();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (pd.isShowing()) {
                    pd.cancel();
                }
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void send_notification(String topic, String title, String body, final Context context) {
        //String TOPIC = "/topics/userABC";
        String FCM_API = "https://fcm.googleapis.com/fcm/send";
        final String serverKey = "key=" + "Your Firebase server key";
        final String contentType = "application/json";
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", title);
            notifcationBody.put("message", body);

            notification.put("to", topic);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("resp", "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show();
                        Log.i("volley_error", "onErrorResponse: Didn't work");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.getCache().clear();

        requestQueue.add(jsonObjectRequest);
    }

    public static void get_themes_of_the_hotel(final String user_id, final RecyclerView themes_list, final Context context, final String page_name, final Fragment fragment) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Getting Hotel Themes...");
        final ArrayList<String> themeId = new ArrayList<>();
        if (!pd.isShowing())
            pd.show();
        final ArrayList<Themes> themes = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("service_provider").document(user_id).collection("themes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (pd.isShowing()) {
                    pd.cancel();
                }
                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                    for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                        themes.add(queryDocumentSnapshots.getDocuments().get(i).toObject(Themes.class));
                        themeId.add(queryDocumentSnapshots.getDocuments().get(i).getId());
                    }
                    themes_list.setAdapter(new themes_list_adapter(themes, context, user_id, page_name, themeId,fragment));
                } else {
                    Toast.makeText(context, "Themes for Hotel not Found", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (pd.isShowing()) {
                    pd.cancel();
                }
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void add_food_to_the_hotel(final String user_id, final Context context, final String food_name, final Uri food_image, final int food_price) {
        final StorageReference storageReference, food_image_ref;
        storageReference = FirebaseStorage.getInstance().getReference();
        food_image_ref = storageReference.child("images/" + UUID.randomUUID().toString());
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Adding Food to the Hotel...");
        if (!pd.isShowing()) {
            pd.show();
        }
        FirebaseFirestore.getInstance().collection("service_provider").document(user_id).collection("Food").whereEqualTo("food_name", food_name).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                    pd.cancel();
                    Toast.makeText(context, "This Food Item Already Exists", Toast.LENGTH_LONG).show();
                } else {
                    food_image_ref.putFile(food_image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            food_image_ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (uri != null) {
                                        FirebaseFirestore.getInstance().collection("service_provider").document(user_id).collection("Food").document().set(new Food(food_name, uri.toString(), food_price, true)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (pd.isShowing()) {
                                                    pd.cancel();
                                                }
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(context, "Food is Added for the Hotel", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if (pd.isShowing()) {
                                                    pd.cancel();
                                                }
                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (pd.isShowing()) {
                                pd.cancel();
                            }
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void get_food_of_hotel(final String user_id, final Context context, final RecyclerView food_list, final Button add_food_btn, final String page, final Fragment fragment) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Getting Food of the Restaurant...");
        pd.show();
        final ArrayList<String> foodId = new ArrayList<>();
        final ArrayList<Food> foods = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("service_provider").document(user_id).collection("Food").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                pd.cancel();
                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                    foods.add(queryDocumentSnapshots.getDocuments().get(i).toObject(Food.class));
                    foodId.add(queryDocumentSnapshots.getDocuments().get(i).getId());
                }
                food_list.setAdapter(new food_list_adapter(foods, context, add_food_btn, user_id, page, foodId,fragment));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void check_if_user_already_exist(final Context context, final String mobile) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Checking if User already Exist...");
        dialog.show();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(user.getUid().equals("O7ukEb7DcJdLS6Sr4MqQhpwfoYG3")){
            prefs.edit().putString("user_role", "Admin").apply();
           context.startActivity(new Intent(context,request_list_page.class));
            ((AppCompatActivity)context).finish();
        }else{
            FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (!documentSnapshot.exists()) {
                        FirebaseFirestore.getInstance().collection("service_provider").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    if (dialog.isShowing()) {
                                        dialog.cancel();
                                    }
                                    service_provider u = documentSnapshot.toObject(com.example.event_planner.Model.service_provider.class);
                                    if (!u.isApproved()){
                                        Toast.makeText(context,"Your Request for approval is currently pending wait for request to be approved",Toast.LENGTH_LONG).show();
                                    }else {
                                        prefs.edit().putString("user_info", new Gson().toJson(u)).apply();
                                        prefs.edit().putString("user_role", "Service Provider").apply();
                                        context.startActivity(new Intent(context, Admin_home.class));
                                        ((AppCompatActivity) context).finish();
                                    }
                                } else {
                                    if (dialog.isShowing()) {
                                        dialog.cancel();
                                    }
                                    context.startActivity(new Intent(context, Register_User.class).putExtra("mobile", mobile));
                                    ((AppCompatActivity) context).finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (dialog.isShowing()) {
                                    dialog.cancel();
                                }
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        if (dialog.isShowing()) {
                            dialog.cancel();
                        }
                        com.example.event_planner.Model.user u = documentSnapshot.toObject(com.example.event_planner.Model.user.class);
                        prefs.edit().putString("user_info", new Gson().toJson(u)).apply();
                        prefs.edit().putString("user_role", "Customer").apply();
                        context.startActivity(new Intent(context, Home.class));
                        ((AppCompatActivity) context).finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (dialog.isShowing()) {
                        dialog.cancel();
                    }
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public static void add_other_services_to_the_hotel(final String user_id, final Context context, final String service_name, final Uri service_image, final int service_price) {
        final StorageReference storageReference, services_image_ref;
        storageReference = FirebaseStorage.getInstance().getReference();
        services_image_ref = storageReference.child("images/" + UUID.randomUUID().toString());
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Adding Service to the Hotel...");
        if (!pd.isShowing()) {
            pd.show();
        }
        FirebaseFirestore.getInstance().collection("service_provider").document(user_id).collection("Other_Services").whereEqualTo("service_name", service_name).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                    pd.dismiss();
                    Toast.makeText(context, "This Service already Exists", Toast.LENGTH_LONG).show();
                } else {
                    services_image_ref.putFile(service_image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            services_image_ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (uri != null) {
                                        FirebaseFirestore.getInstance().collection("service_provider").document(user_id).collection("Other_Services").document().set(new Services(service_name, service_price, uri.toString(), true)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (pd.isShowing()) {
                                                    pd.cancel();
                                                }
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(context, "Service is Added for the Hotel", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if (pd.isShowing()) {
                                                    pd.cancel();
                                                }
                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (pd.isShowing()) {
                                pd.cancel();
                            }
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    public static void get_services_of_hotel(final String user_id, final Context context, final RecyclerView services_list, final Button add_services_btn, final String page, final Fragment fragment) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Getting Services of the Restaurant...");
        pd.show();
        final ArrayList<Services> services = new ArrayList<>();
        final ArrayList<String> serviceId = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("service_provider").document(user_id).collection("Other_Services").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                pd.cancel();
                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                    services.add(queryDocumentSnapshots.getDocuments().get(i).toObject(Services.class));
                    serviceId.add(queryDocumentSnapshots.getDocuments().get(i).getId());
                }
                services_list.setAdapter(new services_list_adapter(services, context, add_services_btn, user_id, page, serviceId,fragment));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void book_hotel(final Context context, final Orders orders) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Booking Hotel...");
        if (!pd.isShowing())
            pd.show();
        FirebaseFirestore.getInstance().collection("Orders").document().set(orders).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (pd.isShowing())
                    pd.cancel();
                if (task.isSuccessful()) {
                    // new sendmail(context,orders.getUser_email(),"Your Request for Booking is Recieved","We have recieved your booking request you will get confirmation email when the order will be confirmed.").execute();
                    prefs.edit().remove("selected_food").apply();
                    prefs.edit().remove("selected_services").apply();
                    prefs.edit().remove("event_time").apply();
                    prefs.edit().remove("selected_halls").apply();
                    prefs.edit().remove("selected_theme").apply();
                    prefs.edit().remove("event_date").apply();
                    prefs.edit().remove("no_of_guests").apply();
                    prefs.edit().remove("hotel_name").apply();
                    prefs.edit().remove("selected_party_type").apply();
                    new sendmail(context,orders.getUser_email(),"Request for booking is sent","Request for booking is sent you wiil get conformation email soon").execute();
                    Toast.makeText(context, "Hotel is Booked", Toast.LENGTH_LONG).show();
                    context.startActivity(new Intent(context, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    ((AppCompatActivity) context).finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void get_booking_of_user(final Context context, String user_id, final RecyclerView orders_list) {
        final ProgressDialog pd = new ProgressDialog(context);
        final ArrayList<String> order_ids = new ArrayList<>();
        pd.setMessage("Getting Your Bookings...");
        if (!pd.isShowing()) {
            pd.show();
        }
        FirebaseFirestore.getInstance().collection("Orders").whereEqualTo("customer_id", user_id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            ArrayList<Orders> orders = new ArrayList<>();

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (pd.isShowing()) {
                    pd.cancel();
                }
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                    orders.add(queryDocumentSnapshots.getDocuments().get(i).toObject(Orders.class));
                    order_ids.add(queryDocumentSnapshots.getDocuments().get(i).getId());
                }

                orders_list.setAdapter(new orders_list_adapter(orders, context, order_ids));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void cancel_booking(final Context context, String order_id) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Canceling Your Booking...");
        if (!pd.isShowing()) {
            pd.show();
        }
        FirebaseFirestore.getInstance().collection("Orders").document(order_id).update("status", "Canceled").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.cancel();
                Toast.makeText(context, "Status is Updated", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void get_booking_of_serviceProvider(final Context context, String user_id, final RecyclerView orders_list) {
        final ProgressDialog pd = new ProgressDialog(context);
        final ArrayList<String> order_ids = new ArrayList<>();
        pd.setMessage("Getting Your Bookings...");
        if (!pd.isShowing()) {
            pd.show();
        }
        FirebaseFirestore.getInstance().collection("Orders").whereEqualTo("hotel_id", user_id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            ArrayList<Orders> orders = new ArrayList<>();

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (pd.isShowing()) {
                    pd.cancel();
                }
                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                    orders.add(queryDocumentSnapshots.getDocuments().get(i).toObject(Orders.class));
                    order_ids.add(queryDocumentSnapshots.getDocuments().get(i).getId());
                }

                orders_list.setAdapter(new serviceProviderBookingListAdapter(orders, context, order_ids));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void confirm_booking(final Context context, String order_id) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Conforming Your Booking...");
        if (!pd.isShowing()) {
            pd.show();
        }
        FirebaseFirestore.getInstance().collection("Orders").document(order_id).update("status", "Conformed").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.cancel();
                Toast.makeText(context, "Status is Updated", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void update_theme(final Context context, HashMap<String, Object> map, String hotel_id, String themeId, final Fragment fragment) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Updating Theme Data...");
        pd.show();
        FirebaseFirestore.getInstance().collection("service_provider").document(hotel_id).collection("themes").document(themeId).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.cancel();
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Theme Updated", Toast.LENGTH_LONG).show();
                    ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void update_food(final Context context, HashMap<String, Object> map, final String hotel_id, String foodId, final Fragment fragment) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Updating Food Data...");
        pd.show();
        FirebaseFirestore.getInstance().collection("service_provider").document(hotel_id).collection("Food").document(foodId).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.cancel();
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Food Updated", Toast.LENGTH_LONG).show();
                    ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void update_other_services(final Context context, HashMap<String, Object> map, String hotel_id, String serviceId, final Fragment fragment) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Updating Service Data...");
        pd.show();
        FirebaseFirestore.getInstance().collection("service_provider").document(hotel_id).collection("Other_Services").document(serviceId).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.cancel();
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Service Updated", Toast.LENGTH_LONG).show();
                    ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void update_customer_profile(final Context context, HashMap<String, Object> map, String userId) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Updating Your Profile...");
        pd.show();
        FirebaseFirestore.getInstance().collection("users").document(userId).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.cancel();
                if (task.isSuccessful()) {
                    Toast.makeText(context, "User Info Updated", Toast.LENGTH_LONG).show();
                    context.startActivity(new Intent(context, Home.class));
                    ((AppCompatActivity) context).finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void update_service_provider_profile(final Context context, HashMap<String, Object> map, String userId) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Updating Your Profile...");
        pd.show();
        FirebaseFirestore.getInstance().collection("service_provider").document(userId).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.cancel();
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Service Provider Info Updated", Toast.LENGTH_LONG).show();
                    context.startActivity(new Intent(context, Admin_home.class));
                    ((AppCompatActivity) context).finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void add_halls_to_the_hotel(final Context context, final String hallName, final int rentPrice, final String hotelId, final Uri hall_image) {
        final StorageReference storageReference, hall_image_ref;
        storageReference = FirebaseStorage.getInstance().getReference();
        hall_image_ref = storageReference.child("images/" + UUID.randomUUID().toString());
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Adding Hall to the Hotel");
        pd.show();
        FirebaseFirestore.getInstance().collection("service_provider").document(hotelId).collection("halls").whereEqualTo("name", hallName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                    pd.dismiss();
                    Toast.makeText(context, "Hall Already exist", Toast.LENGTH_LONG).show();
                } else {
                    hall_image_ref.putFile(hall_image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            hall_image_ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (uri != null) {
                                        Halls halls = new Halls(hallName, uri.toString(), rentPrice, true);
                                        FirebaseFirestore.getInstance().collection("service_provider").document(hotelId).collection("halls").document().set(halls).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                pd.dismiss();
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(context, "Hall Added to the Hotel", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (pd.isShowing()) {
                                pd.cancel();
                            }
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void get_Halls_of_hotel(final Context context, final String hotelId, final Button add_hall_btn, final RecyclerView hallsList, final String page, final Fragment fragment) {
        final ArrayList<String> halls_ids = new ArrayList<>();
        final ArrayList<Halls> hallsArrayList = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("service_provider").document(hotelId).collection("halls").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                    for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                        halls_ids.add(queryDocumentSnapshots.getDocuments().get(i).getId());
                        hallsArrayList.add(queryDocumentSnapshots.getDocuments().get(i).toObject(Halls.class));
                    }
                    hallsList.setAdapter(new halls_list_adapter(hotelId, page, hallsArrayList, halls_ids, context, add_hall_btn,fragment));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void update_Halls(final Context context, HashMap<String, Object> map, String hotel_id, String hallId, final Fragment fragment) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Updating Hall Data...");
        pd.show();
        FirebaseFirestore.getInstance().collection("service_provider").document(hotel_id).collection("halls").document(hallId).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.cancel();
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Hall Updated", Toast.LENGTH_LONG).show();
                    ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.cancel();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void check_if_hotel_already_booked(final Context context, String eventTime, String eventDate, final String hotelId, final ArrayList<Halls> selectedHallsList) {
        final ArrayList<Orders> ordersArrayList=new ArrayList<>();
        final ArrayList<String> hallNames=new ArrayList<>();
        final ArrayList<String> selectedHallNames=new ArrayList<>();
        final ArrayList<Halls> hallsArrayList=new ArrayList<>();
        final SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Please Wait...");
        if (!pd.isShowing()) {
            pd.show();
        }
        FirebaseFirestore.getInstance().collection("Orders").whereEqualTo("date",eventDate).whereEqualTo("hotel_id",hotelId).whereEqualTo("event_time", eventTime).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
              pd.dismiss();
               if(queryDocumentSnapshots.getDocuments().size()>0){
                   for (int i=0;i<selectedHallsList.size();i++){
                       selectedHallNames.add(selectedHallsList.get(i).getName());
                   }
                    for (int i=0;i<queryDocumentSnapshots.getDocuments().size();i++){
                        ordersArrayList.add(queryDocumentSnapshots.getDocuments().get(i).toObject(Orders.class));
                    }
                   for (int i=0;i<ordersArrayList.size();i++){
                      hallsArrayList.addAll(ordersArrayList.get(i).getHalls());
                   }
                   for (int i=0;i<hallsArrayList.size();i++){
                       hallNames.add(hallsArrayList.get(i).getName());
                   }
                   for (int i=0;i<selectedHallNames.size();i++){
                     if(hallNames.contains(selectedHallNames.get(i))){
                         AlertDialog.Builder alreadyBooked=new AlertDialog.Builder(context);
                         alreadyBooked.setTitle("Notice");
                         alreadyBooked.setCancelable(false);
                         alreadyBooked.setMessage("One or more Halls you choose are already Booked at selected Time");
                         alreadyBooked.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 context.startActivity(new Intent(context, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                 ((AppCompatActivity)context).finish();
                             }
                         }).show();
                         break;
                       }
                       Log.e("itContains", String.valueOf(hallNames.contains(selectedHallNames.get(i))));
                   }
               }else{
                   prefs.edit().putString("selected_halls", new Gson().toJson(selectedHallsList)).apply();
                   context.startActivity(new Intent(context, theme_list_page.class).putExtra("hotel_id",hotelId));
               }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void submitApprovalRequest(final Context context, String hotel_id, String name, String email, String phone, String address, String requestDate){
        final ProgressDialog pd=new ProgressDialog(context);
        pd.setMessage("Submitting Request for approval");
        pd.show();
        approvalRequest request=new approvalRequest(requestDate,name,address,phone,email,hotel_id);
        FirebaseFirestore.getInstance().collection("approvalRequests").document().set(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(context,"Your Request for approval is submitted",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, Login.class);
                    context.startActivity(intent);
                    ((AppCompatActivity)context).finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void get_request_list(final Context context, final RecyclerView request_list){
        final ArrayList<approvalRequest> approvalRequests=new ArrayList<>();
              final ArrayList<String> requestId=new ArrayList<>();
        final ProgressDialog pd=new ProgressDialog(context);
        pd.setMessage("Fetching Approval Requests");
        pd.show();
        FirebaseFirestore.getInstance().collection("approvalRequests").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                pd.dismiss();
                if(queryDocumentSnapshots.getDocuments().size()>0){
                    for(int i=0;i<queryDocumentSnapshots.getDocuments().size();i++){
                        approvalRequests.add(queryDocumentSnapshots.getDocuments().get(i).toObject(approvalRequest.class));
                        requestId.add(queryDocumentSnapshots.getDocuments().get(i).getId());
                    }
                    request_list.setAdapter(new request_list_adapter(context,approvalRequests,requestId));
                }else{
                    Toast.makeText(context,"No Requests Found",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void approveRequest(final Context context, HashMap<String,Object> map, String hotelId, final Button btn, final String requestId){
        final ProgressDialog pd=new ProgressDialog(context);
        pd.setMessage("Approving the Request");
        pd.show();
        FirebaseFirestore.getInstance().collection("service_provider").document(hotelId).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseFirestore.getInstance().collection("approvalRequests").document(requestId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pd.dismiss();
                            Toast.makeText(context,"Request was approved",Toast.LENGTH_LONG).show();
                            btn.setVisibility(View.GONE);
                            context.startActivity(new Intent(context, request_list_page.class));
                            ((AppCompatActivity)context).finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

}
