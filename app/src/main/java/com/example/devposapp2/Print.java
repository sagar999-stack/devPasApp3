package com.example.devposapp2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

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
    private static Print INSTANCE = null;
    private static Object mutex = new Object();
Connection connection = Connection.getInstance();
SpaceManager spaceManager = SpaceManager.getInstance();
  Context context; String resName; String orderDate; String orderTime; String deliveryTime; JSONArray orderedItems; String subTotal; String discount; String grandTotal; String offerText; String printerIp; int port; String resId;String discountText ;String order_id
;

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public JSONArray getOrderedItems() {
        return orderedItems;
    }

    public void setOrderedItems(JSONArray orderedItems) {
        this.orderedItems = orderedItems;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getOfferText() {
        return offerText;
    }

    public void setOfferText(String offerText) {
        this.offerText = offerText;
    }

    public String getPrinterIp() {
        return printerIp;
    }

    public void setPrinterIp(String printerIp) {
        this.printerIp = printerIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public void setDiscountText(String discountText) {
        this.discountText = discountText;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    private Print(Context context) {
        this.context = context;
    }
//
//    private Print(Context mContext, String resName, String orderDate, String orderTime, String deliveryTime, JSONArray orderedItems, String subTotal, String discount, String grandTotal, String offerText, String printerIp, int port, String _id,String discountText, String order_id) {
////        this.context= mContext;
////        this.resName = resName;
////        this.orderDate = orderDate;
////        this.orderTime = orderTime;
////        this.deliveryTime = deliveryTime;
////        this.orderedItems = orderedItems;
////        this.subTotal = subTotal;
////        this.discount = discount;
////        this.grandTotal = grandTotal;
////        this.offerText = offerText;
////        this.printerIp = printerIp;
////        this.port = port;
////        this.resId = _id;
////        this.discountText=discountText;
////        this.order_id=order_id;
//    }


    public static Print getInstance(Context context) {
        synchronized (mutex) {
            if (INSTANCE == null) {
                INSTANCE = new Print(context);
            }
        }
        return(INSTANCE);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    public synchronized boolean PrintOut() {


        SharedPreferences resInfo = context.getSharedPreferences("resInfo", context.MODE_PRIVATE);
        String  resName = resInfo.getString("restaurantName", "data not found");
        String  streetAddress = resInfo.getString("streetAddress", "data not found");
        String  localArea = resInfo.getString("localArea", "data not found");
        String  cityOrTown = resInfo.getString("cityOrTown", "data not found");
        String  postcode = resInfo.getString("postcode", "data not found");
        String gbp = "£";
        Double discountDbl= Double.valueOf(discount);
        String discountStr = String.format("%.2f",discountDbl );
        String ESC = "\u001B";
        String GS = "\u001D";
        String InitializePrinter = ESC + "@";
        String BoldOn = ESC + "E" + "\u0001";
        String BoldOff = ESC + "E" + "\0";
        String DoubleOn = GS + "!" + "\u0011";  // 2x sized text (double-high + double-wide)
        String DoubleOff = GS + "!" + "\0";
        String textAlignLeft = ESC + "a" + "0";
        String textAlignCenter = ESC + "a" + "1";
        String textAlignRight = ESC + "a" + "2";
        String doubleHeight = GS + "!" + "\u0001";
        String doubleWidth = GS + "!" + "\u0010";
        String inText = "IN: "+orderTime;
        String outText = "OUT: "+deliveryTime;
        String boldedDoubleWidthSpace = "###############################";
        String dishes="";
        String dish = "";
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

                dishes = dishes +  quantityAndDishName+space+gbp+totalPriceDishF+"\n";

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        if (connection.conTest(printerIp,port)) {
//
//
//
//            if (connection.PrintfData((resName+"\n").getBytes(),2,1)) {
//                connection.PrintfData((streetAddress+"\n").getBytes(),1,1);
//                connection.PrintfData((cityOrTown+", "+postcode+"\n").getBytes(),1,1);
//                connection.PrintfData((orderDate+"\n").getBytes(),0,1);
//                connection.PrintfData(( "\n").getBytes(),0,1);
//                connection.PrintfData(("COLLECTION\n").getBytes(),1,1);
//                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
//
//                String inText = "IN: "+orderTime;
//                String outText = "OUT: "+deliveryTime;
//                String totalWidthInOut = "_______________________________________________";
//                String spaceInOut = spaceManager.getSpace(inText,outText,totalWidthInOut);
//                connection.PrintfData((inText+spaceInOut+outText+"\n").getBytes(),1,0);
//                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
//
//                for (int i = 0; i < orderedItems.length(); i++) {
//
//                    try {
//                        JSONObject order_item = orderedItems.getJSONObject(i);
//                        String dishName = order_item.getString("dish_name");
//                        String quantity = order_item.getString("quantity");
//                        String quantityAndDishName = quantity+" X "+dishName;
//                        String totalPriceDish = order_item.getString("total_price");
//                        Double totalPriceDishInt = Double.valueOf(totalPriceDish);
//                        @SuppressLint("DefaultLocale") String totalPriceDishF = String.format("%.2f",totalPriceDishInt );
//                        String totalWidth = "______________________________________________________________";
//                        String space = spaceManager.getSpace(quantityAndDishName,totalPriceDishF,totalWidth);
//                        connection.PrintfData((quantityAndDishName+space+gbp+totalPriceDishF+"\n").getBytes(Charset.forName("IBM00858")),0,0);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                if(offerText.length()>1)
//                {
//                    connection.PrintfData(("*** "+offerText+" ***\n").getBytes(),1,1);
//                }
//                if(discountText.length()>1)
//                {
//                    connection.PrintfData(("*** "+discountText+" ***\n").getBytes(),1,1);
//                }
//
//                String spaceSubTotal = spaceManager.getSpace("Sub Total"," "+subTotal,totalWidthInOut);
//                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
//                connection.PrintfData(("Sub Total"+spaceSubTotal+gbp+subTotal+"\n").getBytes(Charset.forName("IBM00858")),1,0);
//                String spaceDiscount = spaceManager.getSpace("Discount"," "+discountStr,totalWidthInOut);
//                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
//                connection.PrintfData(("Discount"+spaceDiscount+gbp+discountStr+"\n").getBytes(Charset.forName("IBM00858")),1,0);
//                String spaceGrandTotal = spaceManager.getSpace("Grand Total"," "+grandTotal,totalWidthInOut);
//                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
//                connection.PrintfData(("Grand Total"+spaceGrandTotal+gbp+grandTotal+"\n").getBytes(Charset.forName("IBM00858")),1,0);
//                connection.PrintfData((".\n").getBytes(),2,3);
//
//
//            }
////            t1.speak("Receipt printed out", TextToSpeech.QUEUE_FLUSH, null);
//
//
////                    updateList();
////            updatePrintingStatus(order_id,resId);
//            return  true;
//
//        }

        if (connection.conTest(printerIp,port)) {

            if( discountText.length()>1)
            {
                String totalWidthInOut = "_______________________________________________";
                connection.PrintfData((textAlignCenter+DoubleOn+resName+DoubleOff+"\n"+" \n"+
                        textAlignCenter+streetAddress+"\n"+
                        textAlignCenter+cityOrTown+", "+postcode+"\n"+
                        textAlignCenter+orderDate+"\n\n"+
                        textAlignCenter+BoldOn+doubleWidth+"COLLECTION"+BoldOff+DoubleOff+"\n"+
                        BoldOn+ "---------------------------------------------------------------"+BoldOff+"\n"+
                        BoldOn+doubleWidth+inText+ spaceManager.getSpace(inText,outText,boldedDoubleWidthSpace)+outText+BoldOff+DoubleOff+"\n"+
                        BoldOn+ "---------------------------------------------------------------"+BoldOff+"\n"+
                        dishes+"\n"+
//                        getOffertext()+"\n"+
                        getDiscountText()+"\n"+
                        BoldOn+ "---------------------------------------------------------------"+BoldOff+"\n"+
                        BoldOn+doubleWidth+"Sub Total"+ spaceManager.getSpace("Sub Total"," "+subTotal,boldedDoubleWidthSpace)+gbp+subTotal+BoldOff+DoubleOff+"\n"+
                        BoldOn+ "---------------------------------------------------------------"+BoldOff+"\n"+
                        BoldOn+doubleWidth+"Discount"+ spaceManager.getSpace("Discount"," "+discountStr,boldedDoubleWidthSpace)+gbp+discountStr+BoldOff+DoubleOff+"\n"+
                        BoldOn+ "---------------------------------------------------------------"+BoldOff+"\n"+
                        BoldOn+doubleWidth+"Grand Total"+ spaceManager.getSpace("Grand Total"," "+grandTotal,boldedDoubleWidthSpace)+gbp+grandTotal+BoldOff+DoubleOff+"\n\n\n"
                ).getBytes(Charset.forName("IBM00858")),0,0) ;
                connection.PrintfData((" \n").getBytes(),2,3);

            }
            if(offerText.length()>1)
            {
                String totalWidthInOut = "_______________________________________________";
                connection.PrintfData((textAlignCenter+DoubleOn+resName+DoubleOff+"\n"+" \n"+
                        textAlignCenter+streetAddress+"\n"+
                        textAlignCenter+cityOrTown+", "+postcode+"\n"+
                        textAlignCenter+orderDate+"\n\n"+
                        textAlignCenter+BoldOn+doubleWidth+"COLLECTION"+BoldOff+DoubleOff+"\n"+
                        BoldOn+ "---------------------------------------------------------------"+BoldOff+"\n"+
                        BoldOn+doubleWidth+inText+ spaceManager.getSpace(inText,outText,boldedDoubleWidthSpace)+outText+BoldOff+DoubleOff+"\n"+
                        BoldOn+ "---------------------------------------------------------------"+BoldOff+"\n"+
                        dishes+"\n"+
                        getOffertext()+"\n"+
//                        getDiscountText()+"\n"+
                        BoldOn+ "---------------------------------------------------------------"+BoldOff+"\n"+
                        BoldOn+doubleWidth+"Sub Total"+ spaceManager.getSpace("Sub Total"," "+subTotal,boldedDoubleWidthSpace)+gbp+subTotal+BoldOff+DoubleOff+"\n"+
                        BoldOn+ "---------------------------------------------------------------"+BoldOff+"\n"+
                        BoldOn+doubleWidth+"Discount"+ spaceManager.getSpace("Discount"," "+discountStr,boldedDoubleWidthSpace)+gbp+discountStr+BoldOff+DoubleOff+"\n"+
                        BoldOn+ "---------------------------------------------------------------"+BoldOff+"\n"+
                        BoldOn+doubleWidth+"Grand Total"+ spaceManager.getSpace("Grand Total"," "+grandTotal,boldedDoubleWidthSpace)+gbp+grandTotal+BoldOff+DoubleOff+"\n\n\n"
                ).getBytes(Charset.forName("IBM00858")),0,0) ;
                connection.PrintfData((" \n").getBytes(),2,3);

            }

            else{
                String totalWidthInOut = "_______________________________________________";
                connection.PrintfData((textAlignCenter+DoubleOn+resName+DoubleOff+"\n"+" \n"+
                        textAlignCenter+streetAddress+"\n"+
                        textAlignCenter+cityOrTown+", "+postcode+"\n"+
                        textAlignCenter+orderDate+"\n\n"+
                        textAlignCenter+BoldOn+doubleWidth+"COLLECTION"+BoldOff+DoubleOff+"\n"+
                        BoldOn+ "---------------------------------------------------------------"+BoldOff+"\n"+
                        BoldOn+doubleWidth+inText+ spaceManager.getSpace(inText,outText,boldedDoubleWidthSpace)+outText+BoldOff+DoubleOff+"\n"+
                        BoldOn+ "---------------------------------------------------------------"+BoldOff+"\n"+
                        dishes+"\n"+
//                        getOffertext()+"\n"+
//                        getDiscountText()+"\n"+
                        BoldOn+ "---------------------------------------------------------------"+BoldOff+"\n"+
                        BoldOn+doubleWidth+"Sub Total"+ spaceManager.getSpace("Sub Total"," "+subTotal,boldedDoubleWidthSpace)+gbp+subTotal+BoldOff+DoubleOff+"\n"+
                        BoldOn+ "---------------------------------------------------------------"+BoldOff+"\n"+
                        BoldOn+doubleWidth+"Discount"+ spaceManager.getSpace("Discount"," "+discountStr,boldedDoubleWidthSpace)+gbp+discountStr+BoldOff+DoubleOff+"\n"+
                        BoldOn+ "---------------------------------------------------------------"+BoldOff+"\n"+
                        BoldOn+doubleWidth+"Grand Total"+ spaceManager.getSpace("Grand Total"," "+grandTotal,boldedDoubleWidthSpace)+gbp+grandTotal+BoldOff+DoubleOff+"\n\n\n"
                ).getBytes(Charset.forName("IBM00858")),0,0) ;
                connection.PrintfData((" \n").getBytes(),2,3);
            }




//                String spaceInOut = spaceManager.getSpace(inText,outText,totalWidthInOut);
//                connection.PrintfData((inText+spaceInOut+outText+"\n").getBytes(),1,0);
//                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);




//                String spaceSubTotal = spaceManager.getSpace("Sub Total"," "+subTotal,totalWidthInOut);
//                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
//                connection.PrintfData(("Sub Total"+spaceSubTotal+gbp+subTotal+"\n").getBytes(Charset.forName("IBM00858")),1,0);
//                String spaceDiscount = spaceManager.getSpace("Discount"," "+discountStr,totalWidthInOut);
//                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
//                connection.PrintfData(("Discount"+spaceDiscount+gbp+discountStr+"\n").getBytes(Charset.forName("IBM00858")),1,0);
//                String spaceGrandTotal = spaceManager.getSpace("Grand Total"," "+grandTotal,totalWidthInOut);
//                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
//                connection.PrintfData(("Grand Total"+spaceGrandTotal+gbp+grandTotal+"\n").getBytes(Charset.forName("IBM00858")),1,0);
//                connection.PrintfData((".\n").getBytes(),2,3);



//            t1.speak("Receipt printed out", TextToSpeech.QUEUE_FLUSH, null);


//                    updateList();
//            updatePrintingStatus(order_id,resId);
            return  true;

        }
        else {

return  false;


        }





        }

    private String getDiscountText() {
        if(discountText.length()>1)
        {

            return "*** "+discountText+" ***";
        }
        else{
            return null;
        }
    }

    private String getOffertext() {
        if(offerText.length()>1)
        {

            return "*** "+offerText+" ***";
        }
        else{
            return null;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    private void updatePrintingStatus(String order_id, String resId) {
        RequestQueue queue = MySingleton.getInstance(context).getRequestQueue();

        JSONObject js = new JSONObject();
        try {
            js.put("restaurant_id", resId);
            js.put("order_id", order_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "https://devoretapi.co.uk/epos/updateOrderSyncingStatus";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, url, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int count = 0;
                        try {
//                                    String status = response.getString("status");


                            if(response.getString("status").matches("true")){
                                String token = response.getString("token");
                                Toast.makeText(context,"Printed successfully ",Toast.LENGTH_LONG).show();

                            }
//                                    if(response.length()>1){
//                                            if(response.getJSONObject("msg").getInt("statusCode")==422){
//                                            Toast.makeText(getApplicationContext(),"Blank Submission",Toast.LENGTH_LONG).show();
//
//                                        }
//                                    }


                            else if(response.getString("status").matches("false")){
//                                Toast.makeText(getApplicationContext(),response.getString("msg"),Toast.LENGTH_LONG).show();

                            }


//                                            if (loginInfo.contains("status")) {
//
//                                                Toast.makeText(getApplicationContext(),status,Toast.LENGTH_LONG).show();
////                                                sendToMain(null);
//                                            }
//                                            if(status=="error"){
//                                                Toast.makeText(getApplicationContext(),response.getString("msg"),Toast.LENGTH_LONG).show();
//                                            }
//                                            else{
//
//                                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {



            }
        });

        MySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
    }

}


