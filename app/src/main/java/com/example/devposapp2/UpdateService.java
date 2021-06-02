package com.example.devposapp2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.Locale;

import static java.lang.Thread.interrupted;

public class UpdateService extends Service implements TextToSpeech.OnInitListener {

    String printerIp,resId;
    private boolean isInit;
    Integer port ;
    Connection connection = new Connection();
    private TextToSpeech tts;
    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(getApplicationContext(), this);

    }

    public UpdateService() {

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

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {

                isInit = true;
                Runnable runnableObj = () -> {
                    while (!interrupted()) {
                        try {


                            SharedPreferences connectionFields1 = getApplicationContext().getSharedPreferences("connectionFields", getApplicationContext().MODE_PRIVATE);
                            printerIp = connectionFields1.getString("ipAddress", "data not found");
                            String portStr1 = connectionFields1.getString("port", "data not found");
                            SharedPreferences loginInfo1 = getApplicationContext().getSharedPreferences("loginInfo", getApplicationContext().MODE_PRIVATE);
                            resId = loginInfo1.getString("resId", "data not found");
                            if (resId == "data not found") {

                            } else {
                                if (portStr1 == "data not found") {}
                                else {
                                    port = Integer.parseInt(portStr1);
                                    if (connection.conTest(printerIp, port)) {
                                        AutoPrint autoPrint = new AutoPrint(getApplicationContext());
                                        autoPrint.jsonParseAutoPrint(resId,printerIp,port);
                                    } else {
                                        AutoPrint autoPrint = new AutoPrint(getApplicationContext());
                                        getNotificationWhenNotConnected(resId);


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
            }
        }


    }


    public void getNotificationWhenNotConnected(String resId) {

        VollyRequest vollyRequest = new VollyRequest(getApplicationContext());
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
                                tts.speak("You have " + numOfOrdersStr + " new order. Please check your printer connection. ", TextToSpeech.QUEUE_FLUSH, null);

                            }


                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        vollyRequest.addArrayRequest(request);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}