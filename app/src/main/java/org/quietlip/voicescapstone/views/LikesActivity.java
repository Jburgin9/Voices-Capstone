package org.quietlip.voicescapstone.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import org.quietlip.voicescapstone.R;

public class LikesActivity extends BaseActivity{

    private BottomNavigationView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);

        navigation = findViewById(R.id.bottom_nav);
        setBottomNav(navigation);
    }

}
