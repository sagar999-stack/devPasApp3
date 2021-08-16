package com.example.devposapp2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static java.lang.Thread.interrupted;

public class AutoPrint {
    Context context;
    Boolean audio;
    public int resLen;
    Boolean lastLoop = false;
    private Boolean isUpdated = true;
    private static AutoPrint INSTANCE = null;
    private static Object mutex = new Object();
    UpdateService updateService = new UpdateService();
    Connection connection = new Connection();
    private AutoPrint(Context context) {
        this.context = context;
    }

    public static AutoPrint getInstance(Context context) {
        synchronized (mutex) {
            if (INSTANCE == null) {
                INSTANCE = new AutoPrint(context);
            }
        }
        return(INSTANCE);
    }
//    public void setContext(Context context){
//        this.context =context;
//    }
    TextToSpeech t1;
    SharedPreferences updateStatus;

SharedPreferences.Editor editor;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    public void jsonParseAutoPrint(String resId, String printerIp, Integer port) {

        //UpdateService updateService = new UpdateService();
        //updateService.threadFlag=false;
         updateStatus = this.context.getSharedPreferences("updateStatus",Context.MODE_PRIVATE);
        SharedPreferences AudioNotification = context.getSharedPreferences("AudioNotification", Context.MODE_PRIVATE);
        audio = AudioNotification.getBoolean("audio", false);



//        SharedPreferences getUpdateStatus = context.getSharedPreferences("updateStatus", Context.MODE_PRIVATE);
//        isUpdated = getUpdateStatus.getBoolean("isUpdated", true);
//        if (isUpdated) {


        t1 = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });


        String url = "https://devoretapi.co.uk/epos/getLastOrders/" + resId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new com.android.volley.Response.Listener <JSONArray> () {
                    @Override
                    public void onResponse(JSONArray response) {
                        int count = 0;
                        int numOfOrders = response.length();
                        String numOfOrdersStr = String.valueOf(numOfOrders);

                        while (count < response.length()) {
                            updatedFlag(false);
//                            editor = updateStatus.edit();
//                            editor.putBoolean("isUpdated", false);
//                            editor.apply();

                            try {
                                JSONObject obj = response.getJSONObject(count);

                                JSONObject customerInfo = obj.getJSONObject("customer_info");
                                String printingStatus = obj.getString("printing_status");
                                int printingStatusInt = Integer.parseInt(printingStatus);
                                String _id = obj.getString("_id");
                                String order_id = obj.getString("order_id");
                                String firstName = customerInfo.getString("first_name");

                                String phoneNumber = customerInfo.getString("mobile_no");
                                String address1 = customerInfo.getString("address1");
//                                String address2 = customerInfo.getString("address2");
                                String postCode = customerInfo.getString("postcode");
                                String city = customerInfo.getString("city");
//                                String customerAddress = address1 + "," + address2 + "," + postCode + "," + city;
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

                                RequestQueue queue = MySingleton.getInstance(context).getRequestQueue();


if(printingStatus.matches("0")){}

                                    Print print = new Print(context, resName, orderDate, orderTime, deliveryTime, orderedItems, subTotal, discount, grandTotal, offerText, printerIp, port, _id, discountText, order_id);
                                    if (print.PrintOut()) {
                                        if(count+1<response.length()){
                                            updatedFlag(false);
                                        }
                                        if(count+1==response.length())
                                        {
                                            lastLoop=true;

                                        }
                                        if(connection.checkInternetConnection(context)) {

                                            updateStatus(_id);
                                        }
                                        else
                                        {
                                            t1.speak("Network problem", TextToSpeech.QUEUE_FLUSH, null);
                                        }
//                                        else
//                                        {
//                                            Thread t1 = new Thread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    if (!connection.checkInternetConnection(context)) {
//                                                        try {
//                                                            Thread.sleep(1000);
//                                                        } catch (Exception e) {
//                                                            //  Block of code to handle errors
//                                                        }
//                                                    }
//                                                    else{
//                                                        updateStatus(_id);
//                                                    }
//                                                }
//                                            });
//                                            t1.start();
//
//                                        }
                                        if (audio) {

                                            String msg = "You have " + numOfOrdersStr + " new order. ";
                                            AudioManager audioManager = new AudioManager(context);
                                            audioManager.speak(msg);
                                        }

                                    } else {
                                        if (!audio) {

                                        } else {
                                            if (audio) {
                                                // t1.speak("printer disconnected.", TextToSpeech.QUEUE_FLUSH, null);
                                            }
                                        }

                                    }


                                } catch(JSONException e){
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

        MySingleton.getInstance(context).addToRequestQueue(request);
//                }
    }



    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    public void updateStatus(String id) {
        RequestQueue queue = MySingleton.getInstance(context).getRequestQueue();
        JSONObject jsonObject = new JSONObject();
        JSONArray x = new JSONArray();

        try {
            jsonObject.put("ids", x.put(id));
        } catch (Exception e) {}
        String url = "https://devoretapi.co.uk/api/v1/printer/update_order_status";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                new Response.Listener < JSONObject > () {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("JSON", String.valueOf(response));
//                        editor = updateStatus.edit();
//                        editor.putBoolean("isUpdated", true);
//                        editor.apply();
                        if(lastLoop){
                            //UpdateService updateService = new UpdateService();
                            //updateService.threadFlag=true;
                            t1.speak("", TextToSpeech.QUEUE_FLUSH, null);
                            updatedFlag(true);

                        }
                        else{

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("JSON", String.valueOf(error));
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
    public void updatedFlag(Boolean flag)
    {
        isUpdated =flag;
    }
    public Boolean getIsUpdated()
    {
        return isUpdated;
    }

}
