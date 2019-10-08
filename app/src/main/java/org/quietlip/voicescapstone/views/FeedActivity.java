package org.quietlip.voicescapstone.views;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import org.quietlip.voicescapstone.recyclerview.FeedAdapter;
import org.quietlip.voicescapstone.recyclerview.VoicesAdapter;
import org.quietlip.voicescapstone.utilis.CurrentUserManager;
import org.quietlip.voicescapstone.utilis.SwipeDeleteCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedActivity extends BaseActivity {

    private FeedAdapter feedAdapter;
    private RecyclerView recyclerView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<AudioModel> feedAudioList = new ArrayList<>();

    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        navigation = findViewById(R.id.bottom_nav);
        setBottomNav(navigation);

        recyclerView = findViewById(R.id.feed_recycler);
        retrieveFeed();
    }

    private void retrieveFeed() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final String id = (String) document.get("userId");
                                final UserModel user = new UserModel(document.get("userName").toString(), document.get("userId").toString(),
                                        document.get("imageUrl").toString(), document.get("aboutMe").toString());

                                db.collection("users").document(id).collection("audio")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {

                                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                                        HashMap<String, String> usermap = (HashMap<String, String>) document.get("user");
//                                                        feedAudioList = new ArrayList<>();
//                                                        List<DocumentSnapshot> myList = task.getResult().getDocuments();
//                                                        for (int i = 0; i < myList.size(); i++) {
                                                        feedAudioList.add(new AudioModel(document.get("uri").toString(), document.get("title").toString(), user, document.get("audioId").toString(), document.getId()));


                                                    }
                                                    Collections.sort(feedAudioList);
                                                    for (int i = 0; i < feedAudioList.size(); i++) {
                                                        AudioModel audioModel = feedAudioList.get(i);
                                                        Log.e("Testing", feedAudioList.get(i).getAudioId());

                                                    }
                                                    feedAdapter = new FeedAdapter(feedAudioList);
                                                    recyclerView.setAdapter(feedAdapter);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                } else {
                                                    Log.d("help", "Error getting documents: ", task.getException());

                                                }
                                            }
                                        });
                            }

                        } else {
                            Log.d("help", "Error getting documents: ", task.getException());
                        }

                    }
                });
    }
}




