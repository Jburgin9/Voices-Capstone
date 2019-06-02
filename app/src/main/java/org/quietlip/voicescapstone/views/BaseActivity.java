package org.quietlip.voicescapstone.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import org.quietlip.voicescapstone.R;

public abstract class BaseActivity extends AppCompatActivity {

    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);


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
                    case R.id.friends_tab:
                        Intent friendsIntent = new Intent(BaseActivity.this, RegisterActivity.class);
                        startActivity(friendsIntent);
                        break;
                    case R.id.record_tab:
                        Intent recordIntent = new Intent(BaseActivity.this, RecordActivity.class);
                        startActivity(recordIntent);
                        break;
                    case R.id.likes_tab:
                        Intent likesIntent = new Intent(BaseActivity.this, LikesActivity.class);
                        startActivity(likesIntent);
                        break;
                    case R.id.account_tab:
                        Intent settingsIntent = new Intent(BaseActivity.this, ProfileActivity.class);
                        startActivity(settingsIntent);
                        break;
                }
                return true;
            }
        });
    }
}
