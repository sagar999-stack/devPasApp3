package com.example.devposapp2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class ResInfo {


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    public void getResInfo(String resId, Context context) {

        RequestQueue queue = MySingleton.getInstance(context).getRequestQueue();
        SharedPreferences resInfo = context.getSharedPreferences("resInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = resInfo.edit();
        String url = "https://devoretapi.co.uk/api/v1/printer/getRestInfo/"+ resId;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int count = 0;
                        try {
                            String restaurantName = response.getString("restaurant_name");
                            String postcode = response.getString("postcode");
                            String streetAddress = response.getString("street_address");
                            String reservationTel = response.getString("reservation_tel");
                            String businessTel = response.getString("business_tel");
                            String email = response.getString("email");
                            String domain = response.getString("domain");
                            String localArea = response.getString("local_area");
                            String cityOrTown = response.getString("city_or_town");



                            editor.putString("restaurantName", restaurantName);
                            editor.putString("postcode", postcode);
                            editor.putString("streetAddress", streetAddress);
                            editor.putString("reservationTel", reservationTel);
                            editor.putString("businessTel", businessTel);
                            editor.putString("email", email);
                            editor.putString("domain", domain);
                            editor.putString("localArea", localArea);
                            editor.putString("cityOrTown", cityOrTown);

                            editor.apply();





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

                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();

            }
        });
        MySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
    }
}
