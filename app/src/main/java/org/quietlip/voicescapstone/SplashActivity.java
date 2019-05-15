package org.quietlip.voicescapstone;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends AppCompatActivity {
    private ImageView voicesHeader;
    private Button logInButton;
    private Button signUpButton;
    private GifImageView splash_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        voicesHeader = findViewById(R.id.voices_header);
        logInButton = findViewById(R.id.log_in_button);
        signUpButton = findViewById(R.id.sign_up_button);
        splash_image = findViewById(R.id.gifImageView);
        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                logInButton.setVisibility(View.VISIBLE);
                signUpButton.setVisibility(View.VISIBLE);
                //   startActivity(new Intent(SplashActivity.this,
                //         MainActivity.class));
                // finish();
            }
        }, secondsDelayed * 4000);

    }
}
