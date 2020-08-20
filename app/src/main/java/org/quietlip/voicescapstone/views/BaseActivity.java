package org.quietlip.voicescapstone.views;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.utilis.PrefHelper;

public abstract class BaseActivity extends AppCompatActivity {
    private PrefHelper prefHelper;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        prefHelper = new PrefHelper(this);

    }

    public void setBottomNav(BottomNavigationView bottomNav) {
        navigation = bottomNav;
        navigationItemSelected();
    }


    private void navigationItemSelected() {
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.feed_tab:
                        Intent feedIntent = new Intent(BaseActivity.this, FeedActivity.class);
                        startActivity(feedIntent);
                        break;
                    case R.id.record_tab:
                        Intent recordIntent = new Intent(BaseActivity.this, RecordActivity.class);
                        startActivity(recordIntent);
                        break;

                    case R.id.account_tab:
                        Intent settingsIntent = new Intent(BaseActivity.this, ProfileActivity.class);
                        startActivity(settingsIntent);
                        break;

                    case R.id.logout_tab:
                        Intent logoutIntent = new Intent(BaseActivity.this, LoginActivity.class);
                        FirebaseAuth.getInstance().signOut();
                        prefHelper.userLogout();
                        startActivity(logoutIntent);
                        break;
                }
                return true;
            }
        });
    }
}
