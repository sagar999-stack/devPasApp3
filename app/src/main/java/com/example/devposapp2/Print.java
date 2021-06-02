package com.example.devposapp2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

public class Print {
Connection connection = new Connection();
SpaceManager spaceManager = new SpaceManager();
  Context context; String resName; String orderDate; String orderTime; String deliveryTime; JSONArray orderedItems; String subTotal; String discount; String grandTotal; String offerText; String printerIp; int port; String _id;String discountText;

    public Print(Context mContext, String resName, String orderDate, String orderTime, String deliveryTime, JSONArray orderedItems, String subTotal, String discount, String grandTotal, String offerText, String printerIp, int port, String _id,String discountText) {
        this.context= mContext;
        this.resName = resName;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.deliveryTime = deliveryTime;
        this.orderedItems = orderedItems;
        this.subTotal = subTotal;
        this.discount = discount;
        this.grandTotal = grandTotal;
        this.offerText = offerText;
        this.printerIp = printerIp;
        this.port = port;
        this._id = _id;
        this.discountText=discountText;
    }

    public synchronized boolean PrintOut() {



        if (connection.conTest(printerIp,port)) {

            SharedPreferences resInfo = context.getSharedPreferences("resInfo", context.MODE_PRIVATE);
          String  resName = resInfo.getString("restaurantName", "data not found");
            String  streetAddress = resInfo.getString("streetAddress", "data not found");
            String  localArea = resInfo.getString("localArea", "data not found");
            String  cityOrTown = resInfo.getString("cityOrTown", "data not found");
            String  postcode = resInfo.getString("postcode", "data not found");
            String gbp = "£";
            Double discountDbl= Double.valueOf(discount);
            String discountStr = String.format("%.2f",discountDbl );

            if (connection.PrintfData((resName+"\n").getBytes(),2,1)) {
                connection.PrintfData((streetAddress+"\n").getBytes(),1,1);
                connection.PrintfData((cityOrTown+", "+postcode+"\n").getBytes(),1,1);
                connection.PrintfData((orderDate+"\n").getBytes(),0,1);
                connection.PrintfData(( "\n").getBytes(),0,1);
                connection.PrintfData(("COLLECTION\n").getBytes(),1,1);
                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);

                String inText = "IN: "+orderTime;
                String outText = "OUT: "+deliveryTime;
                String totalWidthInOut = "_______________________________________________";
                String spaceInOut = spaceManager.getSpace(inText,outText,totalWidthInOut);
                connection.PrintfData((inText+spaceInOut+outText+"\n").getBytes(),1,0);
                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);

                for (int i = 0; i < orderedItems.length(); i++) {

                    try {
                        JSONObject order_item = orderedItems.getJSONObject(i);
                        String dishName = order_item.getString("dish_name");
                        String quantity = order_item.getString("quantity");
                        String quantityAndDishName = quantity+" X "+dishName;
                        String totalPriceDish = order_item.getString("total_price");
                        Double totalPriceDishInt = Double.valueOf(totalPriceDish);
                        @SuppressLint("DefaultLocale") String totalPriceDishF = String.format("%.2f",totalPriceDishInt );
                        String totalWidth = "______________________________________________________________";
                        String space = spaceManager.getSpace(quantityAndDishName,totalPriceDishF,totalWidth);
                        connection.PrintfData((quantityAndDishName+space+gbp+totalPriceDishF+"\n").getBytes(Charset.forName("IBM00858")),0,0);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(offerText.length()>1)
                {
                    connection.PrintfData(("*** "+offerText+" ***\n").getBytes(),1,1);
                }
                if(discountText.length()>1)
                {
                    connection.PrintfData(("*** "+discountText+" ***\n").getBytes(),1,1);
                }

                String spaceSubTotal = spaceManager.getSpace("Sub Total"," "+subTotal,totalWidthInOut);
                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
                connection.PrintfData(("Sub Total"+spaceSubTotal+gbp+subTotal+"\n").getBytes(Charset.forName("IBM00858")),1,0);
                String spaceDiscount = spaceManager.getSpace("Discount"," "+discountStr,totalWidthInOut);
                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
                connection.PrintfData(("Discount"+spaceDiscount+gbp+discountStr+"\n").getBytes(Charset.forName("IBM00858")),1,0);
                String spaceGrandTotal = spaceManager.getSpace("Grand Total"," "+grandTotal,totalWidthInOut);
                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
                connection.PrintfData(("Grand Total"+spaceGrandTotal+gbp+grandTotal+"\n").getBytes(Charset.forName("IBM00858")),1,0);
                connection.PrintfData(("\n").getBytes(),2,3);

            }
//            t1.speak("Receipt printed out", TextToSpeech.QUEUE_FLUSH, null);


//                    updateList();
            return  true;

        }
        else {

return  false;


        }





        }

    }


