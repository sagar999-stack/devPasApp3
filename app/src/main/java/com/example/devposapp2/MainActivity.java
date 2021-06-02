package com.example.devposapp2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.devposapp2.ui.orders.OrdersAdapter;
import com.example.devposapp2.ui.orders.OrdersFragment;
import com.example.devposapp2.ui.orders.OrdersViewModel;
import com.example.devposapp2.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {




    private RequestQueue mQueue;
    List<OrdersViewModel> orders = new ArrayList<>();

    public Socketmanager mSockManager;
    int port=9100;
    SpaceManager spaceManager = new SpaceManager();
    String printerIp;
    String resId;
    TextToSpeech t1;
    Thread thread1;
    Thread thread2;
    SpinnerVigi spinnerVigi = new SpinnerVigi();
    Connection connection = new Connection();
    Boolean audio,threadFlag=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.



        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_orders, R.id.navigation_reservations, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        t1=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());


// Instantiate the RequestQueue with the cache and network.
        mQueue = new RequestQueue(cache, network);



// Start the queue
        mQueue.start();
        SharedPreferences connectionFields = MainActivity.this.getSharedPreferences("connectionFields",MainActivity.MODE_PRIVATE);

        printerIp= connectionFields.getString("ipAddress","data not found");
        String  portStr= connectionFields.getString("port","data not found");
        NavInflater navInflater = navController.getNavInflater();
        NavGraph graph = navInflater.inflate(R.navigation.mobile_navigation);
        if(portStr=="data not found"){


            graph.setStartDestination(R.id.navigation_settings);
        }else{
            port = Integer.parseInt(portStr);
        }
//            if(printerIp=="data not found"){
//                Toast.makeText(this,"get port",Toast.LENGTH_LONG).show();
//            }else{
//                port = Integer.parseInt(portStr);
//            }

        SharedPreferences loginInfo = this.getSharedPreferences("loginInfo", this.MODE_PRIVATE);
        resId = loginInfo.getString("resId", "data not found");
        if(resId =="data not found"){
            sendToLogin(null);
        }else{




Intent intent = new Intent(MainActivity.this, UpdateService.class);
            startService(intent);


//
//            thread1 = new Thread(){
//                @Override
//                public  void  run(){
//                    if(threadFlag){
//                        while (!interrupted()){
//                        try {
//
//
//                            SharedPreferences connectionFields = MainActivity.this.getSharedPreferences("connectionFields",MainActivity.MODE_PRIVATE);
//                            printerIp= connectionFields.getString("ipAddress","data not found");
//                            String  portStr= connectionFields.getString("port","data not found");
//                            SharedPreferences loginInfo = MainActivity.this.getSharedPreferences("loginInfo", MainActivity.MODE_PRIVATE);
//                            resId = loginInfo.getString("resId", "data not found");
//                            if(resId =="data not found"){
//
//
//                            }else{
//                                if(portStr=="data not found"){
//                                }else{
//                                    port = Integer.parseInt(portStr);
//                                }
//
//                                if (connection.conTest(printerIp,port)) {
//
//                                    jsonParseAutoPrint();
//                                }
//                                else{
//
//                                    getNotificationWhenNotConnected();
//                                }
//                            }
//
//                            Thread.sleep(15000);
//
//
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }}
//
//                }
//            };
//
//
//            thread1.start();
        }














    }
    public void sendToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);

        startActivity(intent);
    }
    public void jsonParseAutoPrint() {

        SharedPreferences AudioNotification = MainActivity.this.getSharedPreferences("AudioNotification", MainActivity.this.MODE_PRIVATE);
        audio = AudioNotification.getBoolean("audio", false);
        String url = "https://devoretapi.co.uk/epos/getLastOrders/"+resId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,null,
                new com.android.volley.Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int count = 0;
                        int numOfOrders = response.length();
                        String numOfOrdersStr = String.valueOf(numOfOrders);

                        while (count<response.length()){


                            try {
                                JSONObject obj = response.getJSONObject(count);

                                JSONObject customerInfo = obj.getJSONObject("customer_info");
                                String printingStatus = obj.getString("printing_status");
                                int printingStatusInt = Integer.parseInt(printingStatus);
                                String _id = obj.getString("_id");
                                String firstName = customerInfo.getString("first_name");

                                String phoneNumber = customerInfo.getString("mobile_no");
                                String address1 = customerInfo.getString("address1");
                                String address2 = customerInfo.getString("address2");
                                String postCode = customerInfo.getString("postcode");
                                String city = customerInfo.getString("city");
                                String customerAddress = address1+","+address2+","+postCode+","+city;
                                String orderDate = obj.getString("order_date");
                                String orderTime = obj.getString("order_time");
                                String deliveryTime = obj.getString("delivery_time");
                                JSONArray orderedItems = obj.getJSONArray("order_items");
                                String subTotal = obj.getString("sub_total");
                                String discount = obj.getString("discount_amount");
                                String serviceCharge = obj.getString("service_charge");
                                String deliveryCharge = obj.getString("delivery_charge");
                                String grandTotal = obj.getString("grand_total");
                                String order_policy = obj.getString("order_policy");
                                String paymentMethod = obj.getString("payment_method");
                                String resName = obj.getString("restaurant_name");
                                String offerText = obj.getString("offer_text");
                                String discountText = obj.getString("discount_text");
                                boolean printed = false;

                                Print print = new Print(MainActivity.this, resName, orderDate, orderTime, deliveryTime, orderedItems, subTotal, discount, grandTotal,offerText, printerIp,port,_id,discountText);

                                if (print.PrintOut()) {

                                    updateStatus(_id);

                                    if (audio) {

                                        t1.speak("You have "+numOfOrdersStr+" new order. ", TextToSpeech.QUEUE_FLUSH, null);


                                    }

                                } else {
                                    if (!audio) {

                                    } else {
                                        if (audio) {
                                            t1.speak("printer disconnected.", TextToSpeech.QUEUE_FLUSH, null);
                                        }
                                    }

                                }

//                                if (connection.conTest(printerIp,port)) {
//
//
//                                    String gbp = "£";
//
//
//                                    if (connection.PrintfData((resName+"\n").getBytes(),2,1)) {
//                                        connection.PrintfData(("17 East street\n").getBytes(),1,1);
//                                        connection.PrintfData(("Horsham, West Sussex, RH12 1HH\n").getBytes(),1,1);
//                                        connection.PrintfData((orderDate+"\n").getBytes(),0,1);
//                                        connection.PrintfData(( "\n").getBytes(),1,1);
//                                        connection.PrintfData(("COLLECTION\n").getBytes(),1,1);
//                                        connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
//
//                                        String inText = "IN:"+orderTime;
//                                        String outText = "OUT:"+deliveryTime;
//                                        String totalWidthInOut = "_______________________________________________";
//                                        String spaceInOut = spaceManager.getSpace(inText,outText,totalWidthInOut);
//                                        connection.PrintfData((inText+spaceInOut+outText+"\n").getBytes(),1,0);
//                                        connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
//
//                                        for (int i = 0; i < orderedItems.length(); i++) {
//
//                                            try {
//                                                JSONObject order_item = orderedItems.getJSONObject(i);
//                                                String dishName = order_item.getString("dish_name");
//                                                double totalPriceDishInt = order_item.getInt("total_price");
//                                                String totalPriceDish = String.valueOf(totalPriceDishInt);
//                                                String totalWidth = "______________________________________________________________";
//                                                String space = spaceManager.getSpace(dishName,totalPriceDish,totalWidth);
//                                                connection.PrintfData((dishName+space+gbp+totalPriceDish+"\n").getBytes(Charset.forName("IBM00858")),0,0);
//
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//
//                                        String spaceSubTotal = spaceManager.getSpace("Sub Total"," "+subTotal,totalWidthInOut);
//                                        connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
//                                        connection.PrintfData(("Sub Total"+spaceSubTotal+gbp+subTotal+"\n").getBytes(Charset.forName("IBM00858")),1,0);
//                                        String spaceDiscount = spaceManager.getSpace("Discount"," "+discount,totalWidthInOut);
//                                        connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
//                                        connection.PrintfData(("Discount"+spaceDiscount+gbp+discount+"\n").getBytes(Charset.forName("IBM00858")),1,0);
//                                        String spaceGrandTotal = spaceManager.getSpace("Grand Total"," "+grandTotal,totalWidthInOut);
//                                        connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
//                                        connection.PrintfData(("Grand Total"+spaceGrandTotal+gbp+grandTotal+"\n").getBytes(Charset.forName("IBM00858")),1,0);
//
//
//
//                                        connection.PrintfData(("").getBytes(),2,3);
//                                        updateStatus(_id);
//                                    }
//                                    t1.speak("Receipt printed out", TextToSpeech.QUEUE_FLUSH, null);
//
//
////                    updateList();
//
//                                }
//                                else {
//
//
//
//
//                                }

//









                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            count++;

                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);

    }
    public void getNotificationWhenNotConnected() {

        SharedPreferences AudioNotification = MainActivity.this.getSharedPreferences("AudioNotification", MainActivity.this.MODE_PRIVATE);
        audio = AudioNotification.getBoolean("audio", false);
        String url = "https://devoretapi.co.uk/epos/getLastOrders/"+resId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,null,
                new com.android.volley.Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int count = 0;
                        int numOfOrders = response.length();
                        if(numOfOrders>0){
                            String numOfOrdersStr = String.valueOf(numOfOrders);

                            if (audio) {


                                t1.speak("You have "+numOfOrdersStr+" new order. Please check your printer connection. ", TextToSpeech.QUEUE_FLUSH, null);

                            }
                        }

                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);

    }

    private void updateStatus(String id) {

        JSONObject jsonObject = new JSONObject();
        JSONArray x = new JSONArray();

        try{
            jsonObject.put("ids", x.put(id));
        }catch (Exception e){}
        String url="https://devoretapi.co.uk/api/v1/printer/update_order_status";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("JSON", String.valueOf(response));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("JSON", String.valueOf(error));
            }
        });

        mQueue.add(jsonObjectRequest);
    }


}