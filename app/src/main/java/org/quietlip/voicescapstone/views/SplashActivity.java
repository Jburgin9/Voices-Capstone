package org.quietlip.voicescapstone.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.quietlip.voicescapstone.R;

import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends AppCompatActivity {
    private ImageView voicesHeader;
    private GifImageView splash_image;
    private FirebaseUser fireUser;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        voicesHeader = findViewById(R.id.voices_header);
        splash_image = findViewById(R.id.gifImageView);
        auth = FirebaseAuth.getInstance();
        fireUser = auth.getCurrentUser();
        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(fireUser != null){
                    startActivity(new Intent(SplashActivity.this, ProfileActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this,
                            LoginActivity.class));
                    finish();
                }
            }
        }, secondsDelayed * 4000);
    }

}
