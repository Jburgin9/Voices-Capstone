package org.quietlip.voicescapstone.views;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.quietlip.voicescapstone.R;

import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends AppCompatActivity {
    private ImageView voicesHeader;
    private GifImageView splash_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        voicesHeader = findViewById(R.id.voices_header);
        splash_image = findViewById(R.id.gifImageView);
        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                   startActivity(new Intent(SplashActivity.this,
                         LoginActivity.class));
                 finish();
            }
        }, secondsDelayed * 4000);
    }

}
