package com.example.devposapp2.ui.orders;

import com.example.devposapp2.Connection;
import com.example.devposapp2.Socketmanager;
import com.example.devposapp2.SpaceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class PrintOrder {
    public Socketmanager mSockManager = new Socketmanager();
    List<OrdersViewModel> orders = new ArrayList<>();
    SpaceManager spaceManager = new SpaceManager();
    Connection connection = new Connection();
    public void print(int position){
        String gbp = "£";

        if(orders != null && orders.size() !=0){
            if (connection.PrintfData((orders.get(position).getResName()+"\n").getBytes(),2,1)) {
                connection.PrintfData(("17 East street\n").getBytes(),1,1);
                connection.PrintfData(("Horsham, West Sussex, RH12 1HH\n").getBytes(),1,1);
                connection.PrintfData((orders.get(position).getOrderDate()+"\n").getBytes(),0,1);
                connection.PrintfData(( "\n").getBytes(),1,1);
                connection.PrintfData(("COLLECTION\n").getBytes(),1,1);
                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);

                String inText = "IN:"+orders.get(position).getOrderTime();
                String outText = "OUT:"+orders.get(position).getDeliveryTime();
                String totalWidthInOut = "_______________________________________________";
                String spaceInOut = spaceManager.getSpace(inText,outText,totalWidthInOut);
                connection.PrintfData((inText+spaceInOut+outText+"\n").getBytes(),1,0);
                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
                JSONArray orderedItems = orders.get(position).getOrderedItems();
                for (int i = 0; i < orderedItems.length(); i++) {

                    try {
                        JSONObject order_item = orderedItems.getJSONObject(i);
                        String dishName = order_item.getString("dish_name");
                        double totalPriceDishInt = order_item.getInt("total_price");
                        String totalPriceDish = String.valueOf(totalPriceDishInt);
                        String totalWidth = "______________________________________________________________";
                        String space = spaceManager.getSpace(dishName,totalPriceDish,totalWidth);
                        connection.PrintfData((dishName+space+gbp+totalPriceDish+"\n").getBytes(Charset.forName("IBM00858")),0,0);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                String spaceSubTotal = spaceManager.getSpace("Sub Total"," "+orders.get(position).getSubTotal(),totalWidthInOut);
                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
                connection.PrintfData(("Sub Total"+spaceSubTotal+gbp+orders.get(position).getSubTotal()+"\n").getBytes(Charset.forName("IBM00858")),1,0);
                String spaceDiscount = spaceManager.getSpace("Discount"," "+orders.get(position).getDiscount(),totalWidthInOut);
                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
                connection.PrintfData(("Discount"+spaceDiscount+gbp+orders.get(position).getDiscount()+"\n").getBytes(Charset.forName("IBM00858")),1,0);
                String spaceGrandTotal = spaceManager.getSpace("Grand Total"," "+orders.get(position).getGrandTotal(),totalWidthInOut);
                connection.PrintfData(("-----------------------------------------------\n").getBytes(),1,0);
                connection.PrintfData(("Grand Total"+spaceGrandTotal+gbp+orders.get(position).getGrandTotal()+"\n").getBytes(Charset.forName("IBM00858")),1,0);



                connection.PrintfData(("").getBytes(),2,3);
            }
        }


    }

}
