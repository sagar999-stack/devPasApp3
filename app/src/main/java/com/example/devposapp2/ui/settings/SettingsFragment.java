package com.example.devposapp2.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.devposapp2.Connection;
import com.example.devposapp2.LoginActivity;
import com.example.devposapp2.MainActivity;
import com.example.devposapp2.R;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.text.SpannableString;
import java.awt.font.TextAttribute;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.collection.ArraySet;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.devposapp2.Socketmanager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class SettingsFragment extends Fragment {

    private SettingsViewModel notificationsViewModel;
    private WebView mWebView;
    private Button buttonCash=null;
    private Button buttonCut=null;
    private EditText mTextIp=null;
    private EditText mTextPort=null;
    private EditText mprintfData=null;
    private EditText mprintfLog=null;
    private Socketmanager mSockManager;
    private String mydata = null;
Connection connection = new Connection();
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
         buttonCon = root.findViewById(R.id.conTest);
         buttonDisCon = root.findViewById(R.id.disConTest);
         buttonLogOut = root.findViewById(R.id.buttonLogOut);
//        buttonPf=root.findViewById(R.id.printf);
//        buttonCash=root.findViewById(R.id.buttonCash);
//        buttonCut=root.findViewById(R.id.buttonCut);
        mTextIp=root.findViewById(R.id.printerIp);
        mTextPort=root.findViewById(R.id.printerPort);
//        mprintfData=root.findViewById(R.id.printfData);

        SwitchCompat aSwitch = (SwitchCompat) root.findViewById(R.id.audioSwitch);
        ButtonListener buttonListener=new ButtonListener();
        buttonCon.setOnClickListener(buttonListener);
        buttonDisCon.setOnClickListener(buttonListener);
        buttonLogOut.setOnClickListener(buttonListener);
//        buttonPf.setOnClickListener(buttonListener);
//        buttonCash.setOnClickListener(buttonListener);
//        buttonCut.setOnClickListener(buttonListener);
        mSockManager=new Socketmanager(getContext());

//        click = root. findViewById(R.id.buttonData);
//        data = root.findViewById(R.id.fetcheddata);
        SharedPreferences AudioNotification = getContext().getSharedPreferences("AudioNotification", getContext().MODE_PRIVATE);

        aSwitch.setChecked(AudioNotification.getBoolean("audio",false));
        SharedPreferences printerConditionPref = getContext().getSharedPreferences("printerCondition" , getContext().MODE_PRIVATE);
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

aSwitch.setOnClickListener(new OnClickListener() {
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


        return root;
    }


    class ButtonListener implements OnClickListener{

        @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.conTest:

                     port = Integer.parseInt( mTextPort.getText().toString() );
                    if(connection.checkInternetConnection(getContext())) {
                        if (connection.conTest(mTextIp.getText().toString(), port)) {

                            Toast.makeText(getContext(), "Connected", Toast.LENGTH_LONG).show();
                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("connectionFields", getContext().MODE_PRIVATE);
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
                            SharedPreferences printerConditionPref = getContext().getSharedPreferences("printerCondition", getContext().MODE_PRIVATE);
                            SharedPreferences.Editor editor1= printerConditionPref.edit();
                            editor1.putBoolean("printerCondition",true);
                            editor1.apply();
                        } else {
                            Toast.makeText(getContext(), "Not connected. Please check your printer connection.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(getContext(), "No Internet. Please check your Internet connection.", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.disConTest:

                    if (mSockManager.close()) {
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("connectionFields",getContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("ipAddress","");

                        editor.apply();
                        Toast.makeText(getContext(),"Disconnected",Toast.LENGTH_LONG).show();
                        buttonCon.setEnabled(true);
                        buttonDisCon.setEnabled(false);
                        buttonDisCon.setText("Disconnected");
                        buttonCon.setText("Connect");
                        buttonDisCon.setAlpha((float) 0.6);
                        buttonCon.setAlpha((float) 1);
                        SharedPreferences printerConditionPref = getContext().getSharedPreferences("printerCondition", getContext().MODE_PRIVATE);
                        SharedPreferences.Editor editor1= printerConditionPref.edit();
                        editor1.putBoolean("printerCondition",false);
                     editor1.apply();

                    }else {
                        Toast.makeText(getContext(),"Connected.",Toast.LENGTH_LONG).show();
                    }

                    break;
                case R.id.buttonLogOut:
                    SharedPreferences loginInfo = getContext().getSharedPreferences("loginInfo", getContext().MODE_PRIVATE);
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
                        Toast.makeText(getContext(),"Successfully logged out.",Toast.LENGTH_LONG).show();

                        sendToLogin(null);
                    }else {
                        Toast.makeText(getContext(),"Still logged in.",Toast.LENGTH_LONG).show();
                    }

                    break;
                case R.id.audioSwitch:



                    break;
         }

        }
    }
    public void sendToLogin(View view) {
        Intent intent = new Intent(getContext(), LoginActivity.class);

        startActivity(intent);
    }






}