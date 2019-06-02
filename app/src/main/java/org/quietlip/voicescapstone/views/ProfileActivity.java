package org.quietlip.voicescapstone.views;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.recyclerview.VoicesAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton play;

    private CircleImageView profile_pic;

    private ImageView mic;
    private FloatingActionButton recordButton;

    private MediaPlayer player;

    private VoicesAdapter voicesAdapter;
    private RecyclerView recyclerView;

    private BottomNavigationView navigation;
    private TextView title, usernameTv;
    private static String audioFile;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentUserUID = FirebaseAuth.getInstance().getUid();
    List audioList = new ArrayList<>();

    private FirebaseUser fireUser;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private StorageReference storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_recycler);
        profile_pic = findViewById(R.id.profile_image);

        mic = findViewById(R.id.mic);

        recordButton = findViewById(R.id.mic);
        recyclerView = findViewById(R.id.recycler_view);
        title = findViewById(R.id.title_item_view);
        usernameTv = findViewById(R.id.user_name);
        navigation = findViewById(R.id.bottom_nav);
        play = findViewById(R.id.play_button_item_view);
        audioFile = getExternalCacheDir().getAbsolutePath();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference(currentUserUID).child("Photos");

        getListfromdb();
        navigationItemSelected();
        recordActivityIntent();

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    public void recordActivityIntent() {

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recordintent = new Intent(ProfileActivity.this, RecordActivity.class);
                startActivity(recordintent);
            }
        });
    }


    private void navigationItemSelected() {
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_tab:
                        Intent homeIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
                        startActivity(homeIntent);
                        break;
                    case R.id.profile_tab:
                        Intent profileIntent = new Intent(ProfileActivity.this, RecordActivity.class);
                        startActivity(profileIntent);
                        break;
                    case R.id.friends_tab:
                        Intent friendsIntent = new Intent(ProfileActivity.this, RegisterActivity.class);
                        startActivity(friendsIntent);
                        break;
                    case R.id.settings_tab:
                        Intent settingsIntent = new Intent(ProfileActivity.this, RegisterActivity.class);
                        startActivity(settingsIntent);
                        break;
                }
                return true;
            }
        });
    }

    private void getListfromdb() {
        db.collection(currentUserUID).document("uploads").collection("audiolist")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                audioList.add(new AudioModel(document.get("uri").toString(), document.get("title").toString()));

                            }
                            voicesAdapter = new VoicesAdapter(audioList);
                            recyclerView.setAdapter(voicesAdapter);

                        } else {
                            Log.d("help", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void startPlay(View view) {
        player = new MediaPlayer();
        try {
            player.setDataSource(audioFile);
            player.prepare();
            player.start();
        } catch (IOException e) {
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(currentUserUID != null){
            firestore.collection("Users").document(currentUserUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                   Picasso.get().load((String) task.getResult().get("imageUrl")).into(profile_pic);
                   usernameTv.setText(task.getResult().get("username").toString());
                }
            });
        }
    }
}