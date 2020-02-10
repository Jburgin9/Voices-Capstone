package org.quietlip.voicescapstone.views;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.recyclerview.FeedAdapter;
import org.quietlip.voicescapstone.utilis.FeedSwipeDelete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                                final UserModel user = new UserModel(document.get("aboutMe").toString(), document.get("imageUrl").toString(),
                                        document.get("userId").toString(), document.get("userName").toString());

                                db.collection("users").document(id).collection("audio")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        feedAudioList.add(new AudioModel(document.get("uri").toString(), document.get("title").toString(), user, document.get("audioId").toString(), document.getId()));
                                                    }
                                                    Collections.sort(feedAudioList);
                                                    for (int i = 0; i < feedAudioList.size(); i++) {
                                                        AudioModel audioModel = feedAudioList.get(i);
                                                        Log.e("Testing", feedAudioList.get(i).getAudioId());

                                                    }
                                                    feedAdapter = new FeedAdapter(feedAudioList);
                                                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new FeedSwipeDelete(feedAdapter, getApplicationContext()));
                                                    recyclerView.setAdapter(feedAdapter);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                    itemTouchHelper.attachToRecyclerView(recyclerView);
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




