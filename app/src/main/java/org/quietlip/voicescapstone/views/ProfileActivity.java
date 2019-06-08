package org.quietlip.voicescapstone.views;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.value.StringValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.recyclerview.VoicesAdapter;
import org.quietlip.voicescapstone.utilis.CurrentUserManager;
import org.quietlip.voicescapstone.utilis.GlideApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.quietlip.voicescapstone.views.RegisterActivity.DOC_PHOTO;


public class ProfileActivity extends BaseActivity {

    private ImageButton play;
    private ImageView mic;
    private TextView title;

    private MediaPlayer player;

    private CircleImageView profile_pic;
    private TextView aboutME;
    private TextView userName;

    private VoicesAdapter voicesAdapter;
    private RecyclerView recyclerView;

    private BottomNavigationView navigation;

    private static String audioFile;
    private static final String DOC_PHOTO = "Photos";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentUserUID = FirebaseAuth.getInstance().getUid();
    StorageReference storage = FirebaseStorage.getInstance().getReference(currentUserUID).child(DOC_PHOTO);

    List audioList = new ArrayList<>();
    UserModel userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_recycler);
        navigation = findViewById(R.id.bottom_nav);
        setBottomNav(navigation);


        profile_pic = findViewById(R.id.profile_image);
        aboutME = findViewById(R.id.about_me);
        userName = findViewById(R.id.user_name);


        recyclerView = findViewById(R.id.recycler_view);
        title = findViewById(R.id.profile_title);
        play = findViewById(R.id.profile_play);
        audioFile = getExternalCacheDir().getAbsolutePath();

        retrieveUserInfo();
        getListfromdb();
//        navigationItemSelected();
//        recordActivityIntent();


        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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

    private void goToCommentActivity() {
        Intent commentActivityIntent = new Intent(ProfileActivity.this, CommentActivity.class);
        startActivity(commentActivityIntent);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    private void getListfromdb() {


        db.collection("users").document(currentUserUID).collection("audio")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                audioList.add(new AudioModel(document.get("uri").toString(), document.get("title").toString(), CurrentUserManager.getInstance().getCurrentUser()));

                            }


                        } else {
                            Log.d("help", "Error getting documents: ", task.getException());
                        }
                        voicesAdapter = new VoicesAdapter(audioList);
                        recyclerView.setAdapter(voicesAdapter);
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