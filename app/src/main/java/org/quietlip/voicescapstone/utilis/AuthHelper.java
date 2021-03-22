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
    private boolean isLoginSuccessful = false;

    //is it safe to pass activities like this? If not what is the best way to pass it in for
    //firebase method?
    public AuthHelper(Activity activity){
        auth = FirebaseAuth.getInstance();
        this.activity = activity;
    }

    public boolean canUserLogin(String username, String password){
        auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                                    auth.signInWithEmailAndPassword(username, password)
                                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                try {
                                                    prefHelper.setUser(FirebaseAuth.getInstance().getUid());
                                                    isLoginSuccessful = true;
                                                    //CurrentUserManager.getInstance().setUser(FirebaseAuth.getInstance().getUid());
                                                } catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }
                                            } else {

                                            }
                                        }
                                    });
                        } else if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {

                        }
                    }
                });
        return isLoginSuccessful;
    }
}
