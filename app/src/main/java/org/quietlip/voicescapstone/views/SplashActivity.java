package org.quietlip.voicescapstone.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.utilis.CurrentUserManager;

import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends AppCompatActivity {
    private ImageView voicesHeader;
    private GifImageView splash_image;
    private FirebaseAuth loginAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        voicesHeader = findViewById(R.id.voices_header);
        splash_image = findViewById(R.id.gifImageView);
        loginAuth = FirebaseAuth.getInstance();
        sharedPreferences = this.getSharedPreferences("savedUser", MODE_PRIVATE);
        int secondsDelayed = 1;
        FirebaseUser currentUser = loginAuth.getCurrentUser();
        if (sharedPreferences.contains("userId")) {
            String retrievedUser = sharedPreferences.getString("userId", "error");
            Log.d("Splash", retrievedUser);
            CurrentUserManager.getInstance().setUser(retrievedUser);
            startActivity(new Intent(SplashActivity.this,
                    ProfileActivity.class));
            finish();
        } else {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(SplashActivity.this,
                            LoginActivity.class));
                    finish();
                }
            }, secondsDelayed * 2500);
        }
    }
}

