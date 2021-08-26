package com.example.devposapp2;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyPeriodicWork extends Worker {

    private static final String TAG = "time ";
Context context;
    private static MyPeriodicWork INSTANCE = null;
    private static Object mutex = new Object();
    private MyPeriodicWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {

        super(context, workerParams);
        this.context=context;
    }
    public static MyPeriodicWork getInstance(Context context,WorkerParameters workerParams) {
        synchronized (mutex) {
            if (INSTANCE == null) {
                INSTANCE = new MyPeriodicWork(context,workerParams);
            }
        }
        return(INSTANCE);
    }
    @NonNull
    @Override
    public Result doWork() {
//        Intent intent = new Intent(getApplicationContext(), UpdateService.class);
//        context.startService(intent);
        Log.d(TAG, "doWork: Work is done.");
        return Result.success();
    }
}
