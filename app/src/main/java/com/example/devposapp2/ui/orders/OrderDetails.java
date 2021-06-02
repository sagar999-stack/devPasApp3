package com.example.devposapp2.ui.orders;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devposapp2.Connection;
import com.example.devposapp2.LoginActivity;
import com.example.devposapp2.MainActivity;
import com.example.devposapp2.Print;
import com.example.devposapp2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class OrderDetails extends AppCompatActivity {
    ListView  listView;
    List<OrdersViewModel> orders;
    String printerIp;
    int port=9100;
    ArrayList<DishDetailsModel> orderedItemsList = new ArrayList<DishDetailsModel>();
    TextView firstName,address_,date_,orderTime_,mobileNumber_,deliveryTime_,subTotal_,discount_,serviceCharge_,deliveryCharge_,grandTotal_,paymentMethod_, number_0f_item,offerText_;
    Button printButton;
Connection connection = new Connection();
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.listItem);
        Intent intent = getIntent();

        SharedPreferences connectionFields = OrderDetails.this.getSharedPreferences("connectionFields",OrderDetails.MODE_PRIVATE);
        printerIp= connectionFields.getString("ipAddress","data not found");
        String  portStr= connectionFields.getString("port","data not found");
        if(portStr=="data not found"){

        }else{
            port = Integer.parseInt(portStr);
        }

        orderedItemsList =(ArrayList<DishDetailsModel>) getIntent().getSerializableExtra("arrayList") ;
        DishListAddapter arrayAdapter = new DishListAddapter(this,R.layout.dish_items, orderedItemsList);
//        ArrayList<DishDetailsModel> orderedItemsList = new ArrayList<>();


        listView.setAdapter(arrayAdapter);

        String name = intent.getStringExtra("firstName");
        String address = intent.getStringExtra("address");
        String date = intent.getStringExtra("orderDate");
        String orderTime = intent.getStringExtra("orderTime");
        String mobileNumber = intent.getStringExtra("mobileNumber");
        String deliveryTime = intent.getStringExtra("deliveryTime");
        String numberOfDish = intent.getStringExtra("numberOfDish");
        String subTotal = intent.getStringExtra("subTotal");
        String discount = intent.getStringExtra("discount");
        String serviceCharge = intent.getStringExtra("serviceCharge");
        String deliveryCharge = intent.getStringExtra("deliveryCharge");
        String grandTotal = intent.getStringExtra("grandTotall");
        String paymentMethod = intent.getStringExtra("paymentMethod");
        String orderDate = intent.getStringExtra("orderDate");
        String positionStr = intent.getStringExtra("position");

        String offerText = intent.getStringExtra("offerText");
        String discountText = intent.getStringExtra("discountText");
        String resName = intent.getStringExtra("resName");
        String jsonArray = intent.getStringExtra("jsonArray");


        JSONArray
        orderedItems = null;
        try {
            orderedItems = new JSONArray(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String _id = intent.getStringExtra("_id");
        firstName = findViewById(R.id.full_name);
        address_ = findViewById(R.id.address);
        date_ = findViewById(R.id.editTextDate);
        orderTime_ = findViewById(R.id.in_time);
        mobileNumber_ = findViewById(R.id.customer_number);
        deliveryTime_ = findViewById(R.id.out_time);
        subTotal_ = findViewById(R.id.subTotal) ;
        discount_ = findViewById(R.id.discount) ;
        serviceCharge_ = findViewById(R.id.serviceCh) ;
        deliveryCharge_ = findViewById(R.id.deliCh) ;
        grandTotal_ = findViewById(R.id.grand_total) ;
        paymentMethod_ = findViewById(R.id.paymentMethod) ;
        number_0f_item = findViewById(R.id.numItem) ;
        printButton = findViewById(R.id.printButton);
        offerText_ = findViewById(R.id.offerText);


        firstName.setText(name);
        address_.setText(address);
        date_.setText(date);
        orderTime_.setText(orderTime);
        mobileNumber_.setText(mobileNumber);
        deliveryTime_.setText(deliveryTime);
        subTotal_.setText("Sub Total: £"+subTotal);
        discount_.setText("Discount : £"+discount);
        serviceCharge_.setText("Service ch : £"+serviceCharge);
        deliveryCharge_.setText("Delivery ch : £"+deliveryCharge);
        grandTotal_.setText("Grand Total : £"+grandTotal);
        paymentMethod_.setText(paymentMethod);
        number_0f_item.setText("No ITEM: "+numberOfDish);
        if(offerText!="")
        {
            offerText_.setText(offerText);
        }
        if(discountText!="")
        {
            offerText_.setText(discountText);
        }

        mobileNumber_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_DIAL);
                intent1.setData(Uri.parse("tel:"+mobileNumber));
                startActivity(intent1);
            }
        });

        JSONArray finalOrderedItems = orderedItems;
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connection.checkInternetConnection(OrderDetails.this)) {
                    if (connection.conTest(printerIp, port)) {
                        Print print = new Print(OrderDetails.this, resName, orderDate, orderTime, deliveryTime, finalOrderedItems, subTotal, discount, grandTotal, offerText, printerIp, port, _id, discountText);
                        print.PrintOut();
                    } else {
                        Toast.makeText(getApplicationContext(), "printer not connected.", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"No Internet. PLease check your internet connection.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
    @Override
    protected void onStop() {
        super.onStop();
        ArrayList<DishDetailsModel> orderedItemsList = new ArrayList<DishDetailsModel>();
        orderedItemsList.clear();
    }

}