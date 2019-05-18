package org.quietlip.voicescapstone.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.quietlip.voicescapstone.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView profile_pic;
    private ImageView soundwave;
    private ImageView soundwaveRight;
    private ImageView mic;
    private RecyclerView recyclerView;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_recycler);
//        ImageView home = findViewById(R.id.home_tab);
//        home.setImageResource(R.drawable.ic_home_black_24dp);
//        ImageView profile = findViewById(R.id.profile_tab);
//        profile.setImageResource(R.drawable.ic_person_black_24dp);
//        ImageView friends = findViewById(R.id.friends_tab);
//        friends.setImageResource(R.drawable.ic_people_black_24dp);
//        ImageView settings = findViewById(R.id.settings_tab);
//        settings.setImageResource(R.drawable.ic_settings_black_24dp);
        profile_pic = findViewById(R.id.profile_image);
        soundwave = findViewById(R.id.soundwave_left);
        soundwaveRight = findViewById(R.id.soundwave_right);
        mic = findViewById(R.id.mic);
        recyclerView = findViewById(R.id.recycler_view);
        navigation = findViewById(R.id.bottom_nav);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_tab:
                        Intent a = new Intent(ProfileActivity.this,ProfileActivity.class);
                        startActivity(a);
                        break;
                    case R.id.profile_tab:
                        break;
                    case R.id.friends_tab:
                        Intent b = new Intent(ProfileActivity.this,RegisterActivity.class);
                        startActivity(b);

                        break;
                }
                return true;
            }
        });
    }
}
