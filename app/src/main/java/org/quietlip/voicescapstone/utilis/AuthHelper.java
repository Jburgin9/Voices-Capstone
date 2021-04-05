package org.quietlip.voicescapstone.utilis;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthHelper {
    private FirebaseAuth auth;
    private Activity activity;
    private PrefHelper prefHelper;
    private LoginCallback loginCallback;
    //change to -1 & 1 to try and make switch happen on first button press
    private int isLoginSuccessful = 0;

    //is it safe to pass activities like this? If not what is the best way to pass it in for
    //firebase method?
    public AuthHelper(Activity activity){
        auth = FirebaseAuth.getInstance();
        prefHelper = new PrefHelper(activity.getApplicationContext());
        this.activity = activity;
    }

    public void canUserLogin(String username, String password){
        auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                isLoginSuccessful = 1;
                                                try {
                                                    prefHelper.setUser(FirebaseAuth.getInstance().getUid());
                                                    //boolean only changes after 2nd button press
                                                    loginCallback.canUserLogin(true);
                                                    //CurrentUserManager.getInstance().setUser(FirebaseAuth.getInstance().getUid());
                                                } catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                isLoginSuccessful = -1;
                                                loginCallback.canUserLogin(false);
                                            }
                                        }
                                    });
//        return isLoginSuccessful;
    }

    public interface LoginCallback {
         void canUserLogin(boolean canUserLogin);
    }
}

