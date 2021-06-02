package com.example.devposapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class PinCodeActivity extends AppCompatActivity {
VollyRequest vollyRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_code);
        EditText editTextEmail;
        Button buttonSubmit;
        editTextEmail = (EditText)findViewById(R.id.verificationCode) ;
        buttonSubmit = (Button)findViewById(R.id.submitButtonVerification);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vollyRequest = new VollyRequest(PinCodeActivity.this);
                String VerificationToken = editTextEmail.getText().toString();
                JSONObject js = new JSONObject();
                try {
                    js.put("verification_token", VerificationToken);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String url = "https://devoretapi.co.uk/api/v1/systemUserVerifyToken";
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                        Request.Method.POST, url, js,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                int count = 0;
                                try {
                                    String status = response.getString("status");


                                    if(response.getString("status").matches("true")){
                                        String firstName = response.getJSONObject("data").getString("first_name");
                                        String lastName = response.getJSONObject("data").getString("last_name");
                                        String email = response.getJSONObject("data").getString("email");
                                        String mobileNum = response.getJSONObject("data").getString("mobile_no");
                                        String resId = response.getJSONObject("data").getString("restaurant_id");
                                        String userRole = response.getJSONObject("data").getString("user_role");
                                        String token = response.getString("token");
                                        SharedPreferences loginInfo = PinCodeActivity.this.getSharedPreferences("loginInfo", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = loginInfo.edit();
                                        ResInfo resInfo = new ResInfo();
                                        resInfo.getResInfo(resId,PinCodeActivity.this);
                                        editor.putString("status", status);
                                        editor.putString("firstName", firstName);
                                        editor.putString("lastName", lastName);
                                        editor.putString("email", email);
                                        editor.putString("mobileNum", mobileNum);
                                        editor.putString("resId", resId);
                                        editor.putString("userRole", userRole);
                                        editor.putString("token", token);
                                        editor.apply();
                                        Toast.makeText(getApplicationContext(),"Successfully logged in.",Toast.LENGTH_LONG).show();
                                        sendToMain(null);
                                        LoginStatus loginStatus = new LoginStatus();
                                        loginStatus.setLogIn();
                                    }
                                    else if(response.getString("status").matches("error")){
                                        Toast.makeText(getApplicationContext(),response.getString("status")+". "+response.getString("message"),Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),response.getString("status")+". "+response.getString("message"),Toast.LENGTH_LONG).show();
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

                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();

                    }
                });
                vollyRequest.addObjectRequest(jsonObjReq);
            }
        });

    }
    public void sendToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }
}