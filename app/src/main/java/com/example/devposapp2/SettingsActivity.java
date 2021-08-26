package com.example.devposapp2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.example.devposapp2.ui.settings.SettingsFragment;
import com.example.devposapp2.ui.settings.SettingsViewModel;

public class SettingsActivity extends AppCompatActivity {
    private SettingsViewModel notificationsViewModel;
    private WebView mWebView;
    private Button buttonCash=null;
    private Button buttonCut=null;
    private EditText mTextIp=null;
    private EditText mTextPort=null;
    private EditText mprintfData=null;
    private EditText mprintfLog=null;
    private Socketmanager mSockManager = Socketmanager.getInstance();
    private String mydata = null;
    Connection connection = Connection.getInstance();
    private Button click=null;
    private TextView data=null;
    private RequestQueue mQueue;
    private Switch aSwitch;
    private Button buttonCon;
    private Button buttonDisCon;
    private Button buttonLogOut;
    private boolean printerCondition ;
    int port ;
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        buttonCon = findViewById(R.id.conTest);
        buttonDisCon = findViewById(R.id.disConTest);
        buttonLogOut = findViewById(R.id.buttonLogOut);
//        buttonPf=root.findViewById(R.id.printf);
//        buttonCash=root.findViewById(R.id.buttonCash);
//        buttonCut=root.findViewById(R.id.buttonCut);
        mTextIp=findViewById(R.id.printerIp);
        mTextPort=findViewById(R.id.printerPort);
//        mprintfData=root.findViewById(R.id.printfData);

        SwitchCompat aSwitch = (SwitchCompat) findViewById(R.id.audioSwitch);
        SettingsActivity.ButtonListener buttonListener=new SettingsActivity.ButtonListener();
        buttonCon.setOnClickListener(buttonListener);
        buttonDisCon.setOnClickListener(buttonListener);
        buttonLogOut.setOnClickListener(buttonListener);
//        buttonPf.setOnClickListener(buttonListener);
//        buttonCash.setOnClickListener(buttonListener);
//        buttonCut.setOnClickListener(buttonListener);


//        click = root. findViewById(R.id.buttonData);
//        data = root.findViewById(R.id.fetcheddata);
        SharedPreferences AudioNotification = SettingsActivity.this.getSharedPreferences("AudioNotification", SettingsActivity.this.MODE_PRIVATE);

        aSwitch.setChecked(AudioNotification.getBoolean("audio",false));
        SharedPreferences printerConditionPref = SettingsActivity.this.getSharedPreferences("printerCondition" , SettingsActivity.this.MODE_PRIVATE);
        printerCondition= printerConditionPref.getBoolean("printerCondition",false);
        if(printerCondition){
            buttonCon.setEnabled(false);
            buttonDisCon.setEnabled(true);
            buttonCon.setText("Connected");
            buttonDisCon.setText("Disconnect");
            buttonCon.setAlpha((float) 0.6);
            buttonDisCon.setAlpha((float) 1);
        }
        else{
            buttonCon.setEnabled(true);
            buttonDisCon.setEnabled(false);
            buttonDisCon.setText("Disconnected");
            buttonCon.setText("Connect");
            buttonDisCon.setAlpha((float) 0.6);
            buttonCon.setAlpha((float) 1);
        }

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aSwitch.isChecked()){

                    SharedPreferences.Editor editor = AudioNotification.edit();
                    editor.putBoolean("audio", true );
                    editor.apply();
                    aSwitch.setChecked(true);
                    aSwitch.setTextOff("ON");

                }
                else{
                    SharedPreferences.Editor editor = AudioNotification.edit();
                    editor.putBoolean("audio", false );
                    editor.apply();
                    aSwitch.setChecked(false);
                    aSwitch.setTextOff("OFF");
                }
            }
        });


    
    }


    class ButtonListener implements View.OnClickListener {

        @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.conTest:

                    port = Integer.parseInt( mTextPort.getText().toString() );
                    if(connection.checkInternetConnection(SettingsActivity.this)) {
                        if (connection.conTest(mTextIp.getText().toString(), port)) {

                            Toast.makeText(SettingsActivity.this, "Connected", Toast.LENGTH_LONG).show();
                            SharedPreferences sharedPreferences = SettingsActivity.this.getSharedPreferences("connectionFields", SettingsActivity.this.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("ipAddress", mTextIp.getText().toString());
                            editor.putString("port", mTextPort.getText().toString());
                            editor.apply();
                            buttonCon.setEnabled(false);
                            buttonDisCon.setEnabled(true);
                            buttonCon.setText("Connected");
                            buttonDisCon.setText("Disconnect");
                            buttonCon.setAlpha((float) 0.6);
                            buttonDisCon.setAlpha((float) 1);
                            SharedPreferences printerConditionPref = SettingsActivity.this.getSharedPreferences("printerCondition", SettingsActivity.this.MODE_PRIVATE);
                            SharedPreferences.Editor editor1= printerConditionPref.edit();
                            editor1.putBoolean("printerCondition",true);
                            editor1.apply();
                            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);

                            startActivity(intent);
                        } else {
                            Toast.makeText(SettingsActivity.this, "Not connected. Please check your printer connection.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(SettingsActivity.this, "No Internet. Please check your Internet connection.", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.disConTest:

                    if (mSockManager.close()) {
                        SharedPreferences sharedPreferences = SettingsActivity.this.getSharedPreferences("connectionFields",SettingsActivity.this.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("ipAddress","");

                        editor.apply();
                        Toast.makeText(SettingsActivity.this,"Disconnected",Toast.LENGTH_LONG).show();
                        buttonCon.setEnabled(true);
                        buttonDisCon.setEnabled(false);
                        buttonDisCon.setText("Disconnected");
                        buttonCon.setText("Connect");
                        buttonDisCon.setAlpha((float) 0.6);
                        buttonCon.setAlpha((float) 1);
                        SharedPreferences printerConditionPref = SettingsActivity.this.getSharedPreferences("printerCondition", SettingsActivity.this.MODE_PRIVATE);
                        SharedPreferences.Editor editor1= printerConditionPref.edit();
                        editor1.putBoolean("printerCondition",false);
                        editor1.apply();

                    }else {
                        Toast.makeText(SettingsActivity.this,"Connected.",Toast.LENGTH_LONG).show();

                    }

                    break;
                case R.id.buttonLogOut:
                    SharedPreferences loginInfo = SettingsActivity.this.getSharedPreferences("loginInfo", SettingsActivity.this.MODE_PRIVATE);
                    String resId = loginInfo.getString("resId", "data not found");


                    if (resId!=null) {
                        SharedPreferences.Editor editor = loginInfo.edit();
                        editor.putString("status",null);
                        editor.putString("firstName",null);
                        editor.putString("lastName",null);
                        editor.putString("email",null);
                        editor.putString("mobileNum",null);
                        editor.putString("resId",null);
                        editor.putString("userRole",null);
                        editor.putString("token",null);

                        editor.apply();
                        Toast.makeText(SettingsActivity.this,"Successfully logged out.",Toast.LENGTH_LONG).show();

                        sendToLogin(null);
                    }else {
                        Toast.makeText(SettingsActivity.this,"Still logged in.",Toast.LENGTH_LONG).show();
                    }

                    break;
                case R.id.audioSwitch:



                    break;
            }

        }
    }
    public void sendToLogin(View view) {
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);

        startActivity(intent);
    }






}