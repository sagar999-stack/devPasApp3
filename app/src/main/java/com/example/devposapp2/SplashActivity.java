package com.example.devposapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
    private Intent myintent;
    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        myintent = new Intent(SplashActivity.this,LoginActivity.class);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(myintent);
            }
        },3000);


        }

//    private void goActivity() {
//    startActivity(myintent);
//    }
//
//
//    private void takeTime() {
//        int i;
//        for( i = 0; i<3 ;i++){
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

}