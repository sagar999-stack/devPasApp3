package com.example.devposapp2;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.onesignal.OSInAppMessageAction;
import com.onesignal.OneSignal;
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
import com.example.devposapp2.ui.orders.OrdersAdapter;
import com.example.devposapp2.ui.orders.OrdersFragment;
import com.example.devposapp2.ui.orders.OrdersViewModel;
import com.example.devposapp2.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements MessageDialog.ExampleDialogListener {


    private static final String TAG = "MainActivity";

    private RequestQueue mQueue;
    List<OrdersViewModel> orders = new ArrayList<>();


    int port=9100;

    String printerIp;
    String resId;
    TextToSpeech t1;
    Thread thread1;
    Thread thread2;

    Connection connection = Connection.getInstance();
    Boolean audio,threadFlag=true;
    private static final String ONESIGNAL_APP_ID = "bf591344-0bdb-475b-97ed-f01dfe90f30d";
Zillion zillion;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zillion = Zillion.getInstance();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // Enable verbose OneSignal logging to debug issues if needed.
//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
//
//        // OneSignal Initialization
//        OneSignal.initWithContext(this);
//        OneSignal.setAppId(ONESIGNAL_APP_ID);

        // Create an Intent for the activity you want to start




        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);

            }
            boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());

        }
String deviceId = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
//        Toast.makeText(getApplicationContext(),deviceId,Toast.LENGTH_LONG).show();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_orders, R.id.navigation_reservations, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        t1=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());


// Instantiate the RequestQueue with the cache and network.-




// Start the queue

        SharedPreferences connectionFields = MainActivity.this.getSharedPreferences("connectionFields",MainActivity.MODE_PRIVATE);

        printerIp= connectionFields.getString("ipAddress","data not found");
        String  portStr= connectionFields.getString("port","data not found");
        NavInflater navInflater = navController.getNavInflater();
        NavGraph graph = navInflater.inflate(R.navigation.mobile_navigation);
        if(portStr=="data not found" || printerIp=="data not found"){

            sendToSettings(null);

        }else{
            port = Integer.parseInt(portStr);
        }
//            if(printerIp=="data not found"){
//                Toast.makeText(this,"get port",Toast.LENGTH_LONG).show();
//            }else{
//                port = Integer.parseInt(portStr);
//            }

        SharedPreferences loginInfo = this.getSharedPreferences("loginInfo", this.MODE_PRIVATE);
        resId = loginInfo.getString("resId", "data not found");
        if(resId =="data not found"){
            sendToLogin(null);
        }else{

//            Constraints constraints = new Constraints.Builder()
//                    .setRequiresBatteryNotLow(true)
//                    .setRequiredNetworkType(NetworkType.CONNECTED)
//                    .setRequiresCharging(true)
//                    .setRequiresStorageNotLow(true)
//                    .build();
//
//            PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
//                    MyPeriodicWork.class, 15, TimeUnit.MINUTES)
//                    .setConstraints(constraints)
//                    .build();
//
//            WorkManager.getInstance(this).enqueue(periodicWorkRequest);


            Intent intent = new Intent(getApplicationContext(), UpdateService.class);
            MainActivity.this.startService(intent);


//
//            thread1 = new Thread(){
//                @Override
//                public  void  run(){
//                    if(threadFlag){
//                        while (!interrupted()){
//                        try {
//
//
//                            SharedPreferences connectionFields = MainActivity.this.getSharedPreferences("connectionFields",MainActivity.MODE_PRIVATE);
//                            printerIp= connectionFields.getString("ipAddress","data not found");
//                            String  portStr= connectionFields.getString("port","data not found");
//                            SharedPreferences loginInfo = MainActivity.this.getSharedPreferences("loginInfo", MainActivity.MODE_PRIVATE);
//                            resId = loginInfo.getString("resId", "data not found");
//                            if(resId =="data not found"){
//
//
//                            }else{
//                                if(portStr=="data not found"){
//                                }else{
//                                    port = Integer.parseInt(portStr);
//                                }
//
//                                if (connection.conTest(printerIp,port)) {
//
//                                    jsonParseAutoPrint();
//                                }
//                                else{
//
//                                    getNotificationWhenNotConnected();
//                                }
//                            }
//
//                            Thread.sleep(15000);
//
//
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }}
//
//                }
//            };
//
//
//            thread1.start();
        }


    }

    private void sendToSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);
    }

    public void sendToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);

        startActivity(intent);
    }


    @Override
    public void applyTexts(String username) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onStop();
    }
}