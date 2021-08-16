package com.example.devposapp2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        EditText editTextEmail;
        Button buttonSubmit;
        editTextEmail = (EditText)findViewById(R.id.verificationCode) ;
        buttonSubmit = (Button)findViewById(R.id.submitButtonVerification);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            public void onClick(View v) {

                RequestQueue queue = MySingleton.getInstance(ResetPassword.this).getRequestQueue();
               String email = editTextEmail.getText().toString();
                JSONObject js = new JSONObject();
                try {
                    js.put("email", email);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String url = "https://devoretapi.co.uk/api/v1/systemUserSendToken";
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
                                        Toast.makeText(getApplicationContext(),token,Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(ResetPassword.this, PinCodeActivity.class);
                                        startActivity(intent);
                                        LoginStatus loginStatus = new LoginStatus();
                                        loginStatus.setLogIn();
                                    }
//                                    if(response.length()>1){
//                                            if(response.getJSONObject("msg").getInt("statusCode")==422){
//                                            Toast.makeText(getApplicationContext(),"Blank Submission",Toast.LENGTH_LONG).show();
//
//                                        }
//                                    }


                                    else if(response.getString("status").matches("false")){
                                        Toast.makeText(getApplicationContext(),response.getString("msg"),Toast.LENGTH_LONG).show();

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
                MySingleton.getInstance(ResetPassword.this).addToRequestQueue(jsonObjReq);

            }
        });


    }
}