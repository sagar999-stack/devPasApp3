package com.example.devposapp2;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderManager {
    Context context;

    public OrderManager(Context context) {
        this.context = context;
    }

    public void acceptOrder(String orderId, String status, String msg){
        VollyRequest vollyRequest = new VollyRequest(context);

        JSONObject js = new JSONObject();
        try {
            js.put("_id", orderId);
            js.put("order_status", status);
            js.put("message", msg);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "https://devoretapi.co.uk/api/v1/admin/updateOrderStatus";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.PUT, url, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int count = 0;
                        try {
                            String status = response.getString("status");


                            if(response.getString("status").matches("true")){



                            }
//                                    if(response.length()>1){
//                                            if(response.getJSONObject("msg").getInt("statusCode")==422){
//                                            Toast.makeText(getApplicationContext(),"Blank Submission",Toast.LENGTH_LONG).show();
//
//                                        }
//                                    }


                            else if(response.getString("status").matches("false")){
                                Toast.makeText(context,response.getString("msg"),Toast.LENGTH_LONG).show();

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
        vollyRequest.addObjectRequest(jsonObjReq);
    }
}
