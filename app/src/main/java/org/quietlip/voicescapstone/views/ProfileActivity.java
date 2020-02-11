package org.quietlip.voicescapstone.views;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.recyclerview.VoicesAdapter;
import org.quietlip.voicescapstone.utilis.CurrentUserManager;
import org.quietlip.voicescapstone.utilis.Helper;
import org.quietlip.voicescapstone.utilis.SetUserTask;
import org.quietlip.voicescapstone.utilis.VoicesSwipeDelete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends BaseActivity {
    private static final String DOC_PHOTO = "Photos";

    private CircleImageView profile_pic;
    private TextView aboutME;
    private TextView userName;
    private ImageView play;
    private TextView title;
    private ImageView mic;
    private Toolbar toolbar;

    private VoicesAdapter voicesAdapter;
    private RecyclerView recyclerView;
    private BottomNavigationView navigation;

    private static String audioFile;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentUserUID = FirebaseAuth.getInstance().getUid();
//    StorageReference storage = FirebaseStorage.getInstance().getReference(currentUserUID).child(DOC_PHOTO);
    private SetUserTask userTask;

    private List<AudioModel> audioList;
    private CurrentUserManager instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        instance = CurrentUserManager.getInstance();
        userTask = new SetUserTask(this);

            userTask.execute(currentUserUID);


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
        getListfromdb();
        retrieveUserInfo();
    }

    private void retrieveUserInfo() {
        UserModel user = CurrentUserManager.getInstance().getCurrentUser();
        if (user != null) {
            Log.d("Profile", user.toString());
            aboutME.setText(user.getAboutMe());
            userName.setText(user.getUserName());
            Picasso.get().load(user.getImageUrl()).into(profile_pic);
        }
    }


    private void getListfromdb() {
        db.collection("users").document(currentUserUID).collection("audio")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                audioList.add(new AudioModel(document.get("uri").toString(),
                                        document.get("title").toString(), instance.getCurrentUser(),
                                        document.get("audioId").toString(), document.getId()));
                            }
                        } else {
                            Collections.sort(audioList);
                        }
                        voicesAdapter = new VoicesAdapter(audioList);
                        recyclerView.setAdapter(voicesAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        ItemTouchHelper itemTouchHelper =
                                new ItemTouchHelper(new VoicesSwipeDelete(voicesAdapter,
                                        getApplicationContext()));
                        itemTouchHelper.attachToRecyclerView(recyclerView);
                    }
                });
    }

    public void setUser(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        instance.setUser(auth.getUid());
    }


}
