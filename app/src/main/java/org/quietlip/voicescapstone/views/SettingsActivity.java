package org.quietlip.voicescapstone.views;

import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.quietlip.voicescapstone.R;

public class SettingsActivity extends BaseActivity {

    private BottomNavigationView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        navigation = findViewById(R.id.bottom_nav);
        setBottomNav(navigation);

    }
}
