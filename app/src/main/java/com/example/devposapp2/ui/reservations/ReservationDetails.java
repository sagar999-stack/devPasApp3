package com.example.devposapp2.ui.reservations;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devposapp2.Connection;
import com.example.devposapp2.MainActivity;
import com.example.devposapp2.Print;
import com.example.devposapp2.R;
import com.example.devposapp2.ui.orders.DishDetailsModel;
import com.example.devposapp2.ui.orders.DishListAddapter;
import com.example.devposapp2.ui.orders.OrderDetails;
import com.example.devposapp2.ui.orders.OrdersViewModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ReservationDetails extends AppCompatActivity {

    String printerIp;
    int port=9100;
    ArrayList<DishDetailsModel> orderedItemsList = new ArrayList<DishDetailsModel>();
    TextView status_,bookingDate_,customerName_,email_,phone_,bookingTime_,numberOfGuest_,createdDate_;
    Button printButton;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        String status = intent.getStringExtra("status");
        String bookingDate = intent.getStringExtra("bookingDate");
        String customerName = intent.getStringExtra("customerName");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String numOfGuest = intent.getStringExtra("numOfGuest");
        String createdDate = intent.getStringExtra("createdDate");

        status_ = findViewById(R.id.status_info);
        bookingDate_ = findViewById(R.id.booking_date);
        customerName_ = findViewById(R.id.customer_name);
        email_ = findViewById(R.id.email_reservation);
        phone_ = findViewById(R.id.phone_reservation);
        bookingTime_ = findViewById(R.id.booking_time);
        numberOfGuest_ = findViewById(R.id.num_of_guest);
        createdDate_ = findViewById(R.id.create_date);




        status_.setText(status);
        bookingDate_.setText(bookingDate);
        customerName_.setText(customerName);
        email_.setText(email);
        phone_.setText(phone);
        bookingTime_.setText(bookingDate);
        numberOfGuest_.setText(numOfGuest);
        createdDate_.setText(createdDate);


    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}