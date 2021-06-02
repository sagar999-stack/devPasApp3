package com.example.devposapp2;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class AutoPrint {
    Context context;
    Boolean audio;
    public AutoPrint(Context context) {
        this.context = context;
    }
    TextToSpeech t1;



    public void jsonParseAutoPrint(String resId, String printerIp, Integer port) {
        t1 = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        SharedPreferences AudioNotification = context.getSharedPreferences("AudioNotification", context.MODE_PRIVATE);
        audio = AudioNotification.getBoolean("audio", false);

        VollyRequest vollyRequest = new VollyRequest(context);
        String url = "https://devoretapi.co.uk/epos/getLastOrders/" + resId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new com.android.volley.Response.Listener <JSONArray> () {
                    @Override
                    public void onResponse(JSONArray response) {
                        int count = 0;
                        int numOfOrders = response.length();
                        String numOfOrdersStr = String.valueOf(numOfOrders);

                        while (count < response.length()) {

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
                                String customerAddress = address1 + "," + address2 + "," + postCode + "," + city;
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

                                Print print = new Print(context, resName, orderDate, orderTime, deliveryTime, orderedItems, subTotal, discount, grandTotal, offerText, printerIp, port, _id, discountText);

                                if (print.PrintOut()) {

                                    updateStatus(_id);

                                    if (audio) {

                                        String msg = "You have " + numOfOrdersStr + " new order. ";
                                        AudioManager audioManager = new AudioManager(context);
                                        audioManager.speak(msg);
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

        vollyRequest.addArrayRequest(request);

    }

    public void getNotificationWhenNotConnected(String resId) {

        VollyRequest vollyRequest = new VollyRequest(context);
        String url = "https://devoretapi.co.uk/epos/getLastOrders/" + resId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new com.android.volley.Response.Listener < JSONArray > () {
                    @Override
                    public void onResponse(JSONArray response) {
                        int count = 0;
                        int numOfOrders = response.length();
                        if (numOfOrders > 0) {
                            t1 = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        t1.setLanguage(Locale.UK);
                                    }
                                }
                            });
                            String numOfOrdersStr = String.valueOf(numOfOrders);
                            SharedPreferences AudioNotification = context.getSharedPreferences("AudioNotification", context.MODE_PRIVATE);
                            audio = AudioNotification.getBoolean("audio", false);
                            if (audio) {


                                String msg = "You have " + numOfOrdersStr + " new order. Please check your printer connection. ";
                                AudioManager audioManager = new AudioManager(context);
                                audioManager.speak(msg);
                            }

                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        vollyRequest.addArrayRequest(request);

    }

    private void updateStatus(String id) {
        VollyRequest vollyRequest = new VollyRequest(context);
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

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("JSON", String.valueOf(error));
            }
        });


        vollyRequest.addObjectRequest(jsonObjectRequest);
    }
}
