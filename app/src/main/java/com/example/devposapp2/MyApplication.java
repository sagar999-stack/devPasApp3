package com.example.devposapp2;

import android.app.Application;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class MyApplication extends Application {
    private static final String ONESIGNAL_APP_ID = "bf591344-0bdb-475b-97ed-f01dfe90f30d";

    private static MyApplication mInstance;

    public MyApplication() {
        mInstance = this;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        OneSignal.setNotificationOpenedHandler(new NotificationHandler(this));
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

                // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, ResultActivity.class);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.
                create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this, "1").setSmallIcon(R.drawable.email_icon);
        builder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());


    }
    class NotificationHandler implements OneSignal.OSNotificationOpenedHandler{
Context context;
        public NotificationHandler(MyApplication myApplication) {
            context=myApplication;
        }

        @Override
        public void notificationOpened(OSNotificationOpenedResult result) {
            Log.d("OSNotification", "result.notification.toJSONObject(): " + result.getNotification().toJSONObject());
            JSONObject data = result.getNotification().getAdditionalData();
            if (data != null) {
                String customKey = data.optString("customkey", null);
                if (customKey != null) {
//                    Log.i("", "customkey set with value: " + customKey);
                }
            }
            OSNotificationAction.ActionType actionType = result.getAction().getType();
            if (actionType == OSNotificationAction.ActionType.ActionTaken) {
//                Log.i("OneSignalExample", "Button pressed with id: " + result.getAction().getActionId());

            }
            Toast.makeText(getApplicationContext(),"clicked#####",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, ResultActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }


    }
    public static  synchronized MyApplication getInstance(){
        return mInstance;
    }
}
