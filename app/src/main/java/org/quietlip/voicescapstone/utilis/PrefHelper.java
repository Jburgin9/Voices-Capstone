package org.quietlip.voicescapstone.utilis;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefHelper {
    private static PrefHelper instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public PrefHelper(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("savedUser", Context.MODE_PRIVATE);
    }

    public void storeUser(String uId){
        editor = sharedPreferences.edit();
        editor.putString("savedUserId", uId);
        editor.apply();
    }

    public boolean isUserSignedIn(String uId){
        return sharedPreferences.contains(uId);
    }

    public String retrieveUser(String key){
        return sharedPreferences.getString(key, "No user found");
    }

    public void userLogout(){
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

//    public PrefHelper getInstance(){
//        if(instance == null){
//
//        }
//    }
}
