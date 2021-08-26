package com.example.devposapp2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.Locale;

import static java.lang.Thread.interrupted;

public class UpdateService extends Service implements TextToSpeech.OnInitListener {
    int c=-10;
    String printerIp,resId;
    private boolean isInit;
    Integer port ;
    Connection connection = Connection.getInstance();
   public Boolean threadFlag = true;
    private TextToSpeech tts;
    int statusLocal;
    private PendingIntent alarmIntent;
    private AlarmManager alarmMgr;
    private int countAlarm=0;
    private int countAlarmNoInternet=0;
private Boolean connectionAlertFlag;


    @Override
    public void onCreate() {
        super.onCreate();
        alarmManagerForAwakeDevice();
        if (statusLocal == TextToSpeech.SUCCESS) {
            tts = new TextToSpeech(getApplicationContext(), this);
            int result =  tts.setLanguage(Locale.UK);



                isInit = true;
                Runnable runnableObj = () -> {
                    while (!interrupted()) {

                        c = c + 10;
                        Log.d("time", String.valueOf(c));
                        try {


                            SharedPreferences connectionFields1 = getApplicationContext().getSharedPreferences("connectionFields", getApplicationContext().MODE_PRIVATE);
                            printerIp = connectionFields1.getString("ipAddress", "data not found");
                            String portStr1 = connectionFields1.getString("port", "data not found");
                            SharedPreferences loginInfo1 = getApplicationContext().getSharedPreferences("loginInfo", getApplicationContext().MODE_PRIVATE);
                            resId = loginInfo1.getString("resId", "data not found");
                            AutoPrint autoPrint = AutoPrint.getInstance(getApplicationContext());
                            if (resId == "data not found") {

                            } else {
                                if (portStr1 == "data not found") {
                                    tts.speak("Please connect your printer in settings.", TextToSpeech.QUEUE_FLUSH, null);
                                } else {
//
//                                        if(autoPrint.getIsUpdated() )
//                                        {
//                                            threadFlag=true;
//
//                                        }
//                                        if(!autoPrint.getIsUpdated())
//                                        {
//                                            threadFlag=false;
//
//                                        }
                                    port = Integer.parseInt(portStr1);
                                    if (connection.checkInternetConnection(getApplicationContext())) {
                                        countAlarmNoInternet=0;
                                        if (connection.conTest(printerIp, port)) {
                                           countAlarm =0;
                                            if (autoPrint.getIsUpdated()) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {

                                                    autoPrint.jsonParseAutoPrint(resId, printerIp, port);
                                                }
                                            }
                                        } else {

                                            if(countAlarm<5){
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                                                    getNotificationWhenNotConnected(resId);
                                                }
                                            }
                                            countAlarm++;
                                        }
                                    } else {
                                        if(countAlarmNoInternet<5){

                                            SharedPreferences AudioNotification = getApplicationContext().getSharedPreferences("AudioNotification", getApplicationContext().MODE_PRIVATE);
                                            Boolean audio = AudioNotification.getBoolean("audio", false);
                                            if (audio)
                                            {
                                                tts.speak("No Internet. Please check you internet connection.", TextToSpeech.QUEUE_FLUSH, null);
                                            }
                                        }
                                 countAlarmNoInternet++;
                                    }
                                }


                            }


                            Thread.sleep(10000);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    }
                };
                Thread threadObj = new Thread(runnableObj);

                threadObj.start();


//

        }

    }

    private void alarmManagerForAwakeDevice() {
        alarmMgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent2, 0);

// Set the alarm to start at 8:30 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 35);

// setRepeating() lets you specify a precise custom interval--in this case,
// 20 minutes.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 10, alarmIntent);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        PowerManager powerManager2 = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock2 = powerManager2.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock2.acquire();
    }

    public UpdateService() {

    }

    @Override
    public void onInit(int status) {
        statusLocal=status;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    public void getNotificationWhenNotConnected(String resId) {


        RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        String url = "https://devoretapi.co.uk/epos/getLastOrders/" + resId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new com.android.volley.Response.Listener <JSONArray> () {
                    @Override
                    public void onResponse(JSONArray response) {
                        int count = 0;
                        int numOfOrders = response.length();
                        if (numOfOrders > 0) {

                            String numOfOrdersStr = String.valueOf(numOfOrders);

                            SharedPreferences AudioNotification = getApplicationContext().getSharedPreferences("AudioNotification", getApplicationContext().MODE_PRIVATE);



                            Boolean audio = AudioNotification.getBoolean("audio", false);
                            if (audio) {

                                if(numOfOrders==1){
                                    tts.speak("You have a new order. Please check your printer connection. ", TextToSpeech.QUEUE_FLUSH, null);

                                }else{
                                    tts.speak("You have " + numOfOrdersStr + " new orders. Please check your printer connection. ", TextToSpeech.QUEUE_FLUSH, null);

                                }

                            }


                        }
                        else {
                            SharedPreferences AudioNotification = getApplicationContext().getSharedPreferences("AudioNotification", getApplicationContext().MODE_PRIVATE);



                            Boolean audio = AudioNotification.getBoolean("audio", false);
                            if (audio) {
                                tts.speak("Printer disconnected. Please check your printer connection. ", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}