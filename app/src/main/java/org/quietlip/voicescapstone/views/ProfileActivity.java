package org.quietlip.voicescapstone.views;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

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
import com.google.rpc.Help;
import com.squareup.picasso.Picasso;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.recyclerview.VoicesAdapter;
import org.quietlip.voicescapstone.utilis.CurrentUserManager;
import org.quietlip.voicescapstone.utilis.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class ProfileActivity extends BaseActivity {
    private CircleImageView profile_pic;
    private TextView aboutME;
    private TextView userName;
    private ImageView play;
    private TextView title;
    private ImageView mic;

    private VoicesAdapter voicesAdapter;
    private RecyclerView recyclerView;
    private BottomNavigationView navigation;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentUserUID = FirebaseAuth.getInstance().getUid();

    private List<AudioModel> audioList;
    private CurrentUserManager instance;
    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        helper = Helper.getInstance();
        instance = CurrentUserManager.getInstance();
        navigation = findViewById(R.id.bottom_nav);
        setBottomNav(navigation);
        profile_pic = findViewById(R.id.profile_image);
        aboutME = findViewById(R.id.about_me);
        userName = findViewById(R.id.user_name);
        audioList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        title = findViewById(R.id.profile_title);
        play = findViewById(R.id.profile_play);
        helper.makeFirelog(this, "Preparing", "Wait");
        Completable.timer(2000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(this::retrieveUserInfo);
        getListfromdb();
    }

    private void retrieveUserInfo() {
        UserModel user = CurrentUserManager.getInstance().getCurrentUser();
        if (user != null) {
            Log.d("Profile", user.toString());
            aboutME.setText(user.getAboutMe());
            userName.setText(user.getUserName());
            Picasso.get().load(user.getImageUrl()).into(profile_pic);
        }
        helper.dismissFirelog();
    }


    private void getListfromdb() {
        db.collection("users").document(currentUserUID).collection("audio")
                .get()
                .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                audioList.add(new AudioModel(document.get("uri").toString(),
                                        document.get("title").toString(), instance.getCurrentUser(),
                                        document.get("audioId").toString(), document.getId()));
                            }
                            Collections.sort(audioList);
                        }
                        voicesAdapter = new VoicesAdapter(audioList);
                        recyclerView.setAdapter(voicesAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                });
    }
}
