package org.quietlip.voicescapstone.views;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
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
import org.quietlip.voicescapstone.recyclerview.VoicesAdapter;
import org.quietlip.voicescapstone.utilis.CurrentUserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedActivity extends BaseActivity {

    private VoicesAdapter voicesAdapter;
    private RecyclerView recyclerView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    List feedAudioList = new ArrayList<>();

    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        navigation = findViewById(R.id.bottom_nav);
        setBottomNav(navigation);

        recyclerView = findViewById(R.id.feed_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                                                        HashMap<String, String> user = (HashMap<String, String>) document.get("user");
                                                        UserModel usermodel = new UserModel(user.get("userName"), user.get("userId"), user.get("imageUrl"), user.get("aboutMe"));
                                                        feedAudioList.add(new AudioModel(document.get("uri").toString(), document.get("title").toString(), usermodel));

                                                    }
                                                    voicesAdapter = new VoicesAdapter(feedAudioList);
                                                    recyclerView.setAdapter(voicesAdapter);


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