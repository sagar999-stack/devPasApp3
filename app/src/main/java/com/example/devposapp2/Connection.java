package com.example.devposapp2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connection extends Socketmanager {
Socketmanager mSockManager = new Socketmanager() ;



    public boolean conTest(String printerIp, int port) {
        mSockManager.mPort=port;
        mSockManager.mstrIp=printerIp;
        mSockManager.threadconnect();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mSockManager.getIstate()) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean PrintfData(byte[]data, int size, int align ) {
        mSockManager.threadconnectwrite(data,size,align);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mSockManager.getIstate()) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }

}
