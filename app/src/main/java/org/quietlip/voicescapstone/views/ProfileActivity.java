package org.quietlip.voicescapstone.views;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.quietlip.voicescapstone.R;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {

    private MediaPlayer player;
    private ImageView play;
    private CircleImageView profile_pic;
    private ImageView soundwave;
    private ImageView soundwaveRight;
    private ImageView mic;
    private RecyclerView recyclerView;
    private BottomNavigationView navigation;
    private TextView title;
    private static String audioFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_recycler);
        profile_pic = findViewById(R.id.profile_image);
        soundwave = findViewById(R.id.soundwave_left);
        soundwaveRight = findViewById(R.id.soundwave_right);
        mic = findViewById(R.id.mic);
        recyclerView = findViewById(R.id.recycler_view);
        title = findViewById(R.id.title_item_view);
        navigation = findViewById(R.id.bottom_nav);
        play = findViewById(R.id.play_button_item_view);
        audioFile = getExternalCacheDir().getAbsolutePath(

        );



        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recordintent = new Intent(ProfileActivity.this, RecordActivity.class);
                startActivity(recordintent);
            }
        });

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_tab:
                        Intent homeIntent = new Intent(ProfileActivity.this,ProfileActivity.class);
                        startActivity(homeIntent);
                        break;
                    case R.id.profile_tab:
                        Intent profileIntent = new Intent(ProfileActivity.this, RecordActivity.class);
                        startActivity(profileIntent);
                        break;
                    case R.id.friends_tab:
                        Intent friendsIntent = new Intent(ProfileActivity.this,RegisterActivity.class);
                        startActivity(friendsIntent);
                        break;
                    case R.id.settings_tab:
                        Intent settingsIntent = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(settingsIntent);
                        break;
                }
                return true;
            }
        });
    }

    public void recordAudio(View view) {
        Intent recordIntent = new Intent(ProfileActivity.this, RecordActivity.class);
        startActivity(recordIntent);
    }


    public void startPlay(View view) {
            player = new MediaPlayer();
            try {
                player.setDataSource(audioFile);
                player.prepare();
                player.start();
            } catch (IOException e) {
//                Log.e(LOG_TAG, "prepare() failed");
            }
        }

        private void stopPlaying() {
            player.release();
            player = null;
        }
    }

