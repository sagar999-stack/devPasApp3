package com.example.devposapp2;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {
    private static SessionManagement INSTANCE = null;
    private static Object mutex = new Object();
    Context context;

    private SessionManagement(Context context) {
        this.context = context;

        rememberMePref = context.getSharedPreferences("rememberMePref", Context.MODE_PRIVATE);
        editor = rememberMePref.edit();
    }

    public static SessionManagement getInstance(Context context) {
        synchronized (mutex) {
            if (INSTANCE == null) {
                INSTANCE = new SessionManagement(context);
            }
        }
        return (INSTANCE);
    }


    // rememberMe block start

    SharedPreferences rememberMePref;
    SharedPreferences.Editor editor;
    private static final String IS_REMEMBERME = "IsRememberMe";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "Password";

    public void rememberMeSession(Boolean rememberMe, String email, String password) {
        editor.putBoolean(IS_REMEMBERME, rememberMe);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }

    public Boolean getIsChecked() {
        return rememberMePref.getBoolean(IS_REMEMBERME, false);
    }

    public String getEmail() {
        return rememberMePref.getString(KEY_EMAIL, " ");
    }

    public String getPassword() {
        return rememberMePref.getString(KEY_PASSWORD, " ");
    }

    // rememberMe block end


    // printer connection block start

    SharedPreferences printerConnectionPref;

    private static final String KEY_IP_ADDRESS = "IsRememberMe";
    private static final String KEY_PORT = "email";
    public void printerConnectionSession( String ip_address, String port) {
        editor.putString(KEY_IP_ADDRESS, ip_address);
        editor.putString(KEY_PORT, port);
        editor.commit();
    }



    public String getIPAddress() {
        return printerConnectionPref.getString(KEY_IP_ADDRESS, " ");
    }

    public String getPort() {
        return printerConnectionPref.getString(KEY_PORT, " ");
    }

    // rememberMe block end

}
