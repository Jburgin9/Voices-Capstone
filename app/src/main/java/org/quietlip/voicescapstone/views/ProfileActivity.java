package org.quietlip.voicescapstone.views;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.recyclerview.VoicesAdapter;
import org.quietlip.voicescapstone.utilis.CurrentUserManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseActivity {


    private CircleImageView profile_pic;
    private TextView aboutME;
    private TextView userName;
    private ImageView play;
    private TextView title;
    private ImageView mic;

    private MediaPlayer player;
    private boolean mPlay = true;

    private VoicesAdapter voicesAdapter;
    private RecyclerView recyclerView;
    private BottomNavigationView navigation;

    private static String audioFile;
    private static final String DOC_PHOTO = "Photos";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentUserUID = FirebaseAuth.getInstance().getUid();
    StorageReference storage = FirebaseStorage.getInstance().getReference(currentUserUID).child(DOC_PHOTO);

    private List<AudioModel> audioList;
    private UserModel userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_recycler);
        navigation = findViewById(R.id.bottom_nav);
        setBottomNav(navigation);

        profile_pic = findViewById(R.id.profile_image);
        aboutME = findViewById(R.id.about_me);
        userName = findViewById(R.id.user_name);
        audioList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        title = findViewById(R.id.profile_title);
        play = findViewById(R.id.profile_play);
        audioFile = getExternalCacheDir().getAbsolutePath();

        retrieveUserInfo();
        getListfromdb();
    }

    private void retrieveUserInfo() {
        db.collection("users").document(currentUserUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            userInfo = new UserModel(document.get("userName").toString(), document.get("userId").toString(), document.get("imageUrl").toString(),
                                    document.get("aboutMe").toString());
                            aboutME.setText(userInfo.getAboutMe());
                            userName.setText(userInfo.getUserName());
                            Picasso.get().load(userInfo.getImageUrl()).fit().into(profile_pic);
                        } else {
                            Log.d("help", "Error getting documents: ", task.getException());
                        }
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
                                audioList.add(new AudioModel(document.get("uri").toString(), document.get("title").toString(),CurrentUserManager.getInstance().getCurrentUser(),document.get("audioId").toString(), document.getId()));
                            }
                        } else {
                            Log.d("help", "Error getting documents: ", task.getException());
                            Collections.sort(audioList);
                            for (int i = 0; i < audioList.size(); i++) {
                                AudioModel audioModel = audioList.get(i);
                                Log.e("Testing", audioList.get(i).getAudioId());
                            }
                        }
                        voicesAdapter = new VoicesAdapter(audioList);
                        recyclerView.setAdapter(voicesAdapter);
                        voicesAdapter.updateList(audioList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                });
    }

    public void startPlay() {
        player = new MediaPlayer();
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlay) {
                    if (play.isSelected()) {
                        play.setImageResource(R.drawable.stop);
                        startPlay();
                    } else {
                        play.setImageResource(R.drawable.play_button);
                        stopPlaying();
                    }
                    mPlay = !mPlay;
                }

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
        });
    }
}
