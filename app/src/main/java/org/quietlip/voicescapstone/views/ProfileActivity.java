package org.quietlip.voicescapstone.views;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.recyclerview.VoicesAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends BaseActivity {

    private ImageButton play;

    private CircleImageView profile_pic;

    private ImageView mic;
    private FloatingActionButton recordButton;

    private MediaPlayer player;

    private VoicesAdapter voicesAdapter;
    private RecyclerView recyclerView;

    private BottomNavigationView navigation;
    private TextView title;
    private static String audioFile;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentUserUID = FirebaseAuth.getInstance().getUid();
    List audioList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_recycler);
        navigation = findViewById(R.id.bottom_nav);
        setBottomNav(navigation);
        profile_pic = findViewById(R.id.profile_image);

        mic = findViewById(R.id.mic);

        recordButton = findViewById(R.id.mic);
        recyclerView = findViewById(R.id.recycler_view);
        title = findViewById(R.id.title_item_view);

        play = findViewById(R.id.play_button_item_view);
        audioFile = getExternalCacheDir().getAbsolutePath();

        getListfromdb();
//        navigationItemSelected();
        recordActivityIntent();

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
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


    private void getListfromdb() {
        db.collection("users").document(currentUserUID).collection("audio")
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
}