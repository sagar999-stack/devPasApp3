package com.example.devposapp2;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {
    SharedPreferences rememberMePref;
    SharedPreferences.Editor editor;
    Context context;
    private static final  String IS_REMEMBERME = "IsRememberMe";
    private static final  String KEY_EMAIL = "email";
    private static final  String KEY_PASSWORD = "Password";
    public SessionManagement(Context _context){
     context = _context;
        rememberMePref = context.getSharedPreferences("rememberMePref", Context.MODE_PRIVATE);
        editor = rememberMePref.edit();


    }

    public void rememberMeSession(Boolean rememberMe, String email, String password){
        editor.putBoolean(IS_REMEMBERME, rememberMe);
        editor.putString(KEY_EMAIL,email);
        editor.putString(KEY_PASSWORD,password);
        editor.commit();
    }

    public Boolean getIsChecked(){
     return rememberMePref.getBoolean(IS_REMEMBERME,false);
    }
    public String getEmail(){
        return rememberMePref.getString(KEY_EMAIL," ");
    }
    public String getPassword(){
        return rememberMePref.getString(KEY_PASSWORD," ");
    }
}
