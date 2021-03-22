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
import org.quietlip.voicescapstone.utilis.PrefHelper;
import org.quietlip.voicescapstone.utilis.SetUserTask;

import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends AppCompatActivity {
    private ImageView voicesHeader;
    private GifImageView splash_image;
    private PrefHelper prefHelper;
    private SetUserTask userTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        prefHelper = new PrefHelper(this);
        voicesHeader = findViewById(R.id.voices_header);
        splash_image = findViewById(R.id.gifImageView);
        userTask = new SetUserTask(this);

        //secondsDelayed * 2500 in else run method
//        int secondsDelayed = 1;
        if(prefHelper.isUserSignedIn()){
            CurrentUserManager.getInstance().setUser(prefHelper.retrieveUser());
            startActivity(new Intent(SplashActivity.this,
                    ProfileActivity.class));
        } else {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(SplashActivity.this,
                            LoginActivity.class));
                    finish();
                }
            }, 2500);
        }
    }
}

