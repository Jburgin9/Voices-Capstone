package org.quietlip.voicescapstone.utilis;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefHelper {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PrefHelper(Context context){
        sharedPreferences = context.getSharedPreferences("savedUser", Context.MODE_PRIVATE);
    }

    public void storeUser(String uId){
        editor = sharedPreferences.edit();
        editor.putString("savedUserId", uId);
        editor.apply();
    }

    public boolean isUserSignedIn(){
        return sharedPreferences.contains("savedUserId");
    }

    public String retrieveUser(){
        return sharedPreferences.getString("savedUserId", "No user found");
    }

    public void userLogout(){
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
