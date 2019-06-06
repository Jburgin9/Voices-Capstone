package org.quietlip.voicescapstone.views;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
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
import com.squareup.picasso.Picasso;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.recyclerview.VoicesAdapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedActivity extends BaseActivity {


    private CircleImageView profilePic;
    private TextView userName;
    private TextView title;
    private static String audioFile;

    private ImageButton play;
    private ImageView commentMic;
    private MediaPlayer player;

    private VoicesAdapter voicesAdapter;
    private RecyclerView recyclerView;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String allUsersUID = FirebaseAuth.getInstance().getUid();

    List audioList = new ArrayList<>();
    UserModel userInfo;

    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        navigation = findViewById(R.id.bottom_nav);
        setBottomNav(navigation);

        userName = findViewById(R.id.feed_username);
        title = findViewById(R.id.feed_title);
        play = findViewById(R.id.feed_play);
        profilePic = findViewById(R.id.feed_image);
        commentMic = findViewById(R.id.feed_mic);

        recyclerView = findViewById(R.id.feed_recycler);


        audioFile = getExternalCacheDir().getAbsolutePath();
        retrieveUserInfo();
//        getListfromdb();

    }
    private void retrieveUserInfo() {
        db.collection("users").document("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            userInfo = new UserModel(document.get("userName").toString(),document.get("userId").toString(), document.get("imageUrl").toString(),
                                    document.get("aboutMe").toString());


                            userName.setText(userInfo.getUserName());

                            Picasso.get().load(userInfo.getImageUrl()).fit().into(profilePic);


                        } else {
                            Log.d("help", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
//    private void getFeed (){
//        private void getListfromdb() {
//            db.collection("users").document(currentUserUID).collection("audio")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    audioList.add(new AudioModel(document.get("uri").toString(), document.get("title").toString(),currentUserUID));
//
//                                }
//                                voicesAdapter = new VoicesAdapter(audioList);
//                                recyclerView.setAdapter(voicesAdapter);
//
//                            } else {
//                                Log.d("help", "Error getting documents: ", task.getException());
//                            }
//                        }
//                    });
//
//        }

}

