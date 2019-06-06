package org.quietlip.voicescapstone.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.recyclerview.CommentAdapter;
import org.quietlip.voicescapstone.recyclerview.VoicesAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class
CommentActivity extends BaseActivity {
    String users = "users";
    String userlist = "userlist";

    private BottomNavigationView navigationView;
    private MediaRecorder mediaRecorder;
    private MediaPlayer player;
    private CircleImageView record;
    private ImageView play;
    private Button post;
    private EditText titleInput;
    private RecyclerView recyclerView;

    private boolean mPlay = true;
    private boolean mRecord = true;

    List commentList = new ArrayList();
    private CommentActivity commentActivity;
    private static String audioFile;
    private final String audioFolderName = "audio";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CommentAdapter commentAdapter;

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://voicescapstone.appspot.com");
    private StorageReference mStorageRef = storage.getReference();

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
    String format = simpleDateFormat.format(new Date());

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String currentUserUID = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        navigationView = findViewById(R.id.bottom_nav);
        setBottomNav(navigationView);

        commentActivity = this;

        record = findViewById(R.id.record_button_comment);
        play = findViewById(R.id.play_button_comment);
        post = findViewById(R.id.post_button_comment);
        titleInput = findViewById(R.id.title_input_comment);
        recyclerView = findViewById(R.id.comment_recycler);

        audioFile = getExternalCacheDir().getAbsolutePath();
        audioFile += System.currentTimeMillis() + "_recorded_audio.3pg";
        askPermission();
        setRecordAudioOnClick();
        setPlayAudioBackOnClick();
        setPostAudioOnClick();
    }

    private void setPlayAudioBackOnClick() {
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlay) {
                    play.setImageResource(R.drawable.stop);
                    startPlaying();
                } else {
                    play.setImageResource(R.drawable.play_button);
                    stopPlaying();
                }
                mPlay = !mPlay;
            }
        });
    }

    private void setPostAudioOnClick() {
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAudio();
            }
        });
    }


    private void askPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(commentActivity, new String[]{Manifest.permission.RECORD_AUDIO}, 2);
        }
    }

    private void setRecordAudioOnClick() {
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecord) {
                    record.setImageResource(R.drawable.recordon3);
                    startRecording();
                } else {
                    record.setImageResource(R.drawable.recordoff2);
                    stopRecording();
                }
                mRecord = !mRecord;
            }
        });
    }

    private void getListFromDatabase(){
        db.collection("users").document(currentUserUID).collection("audio").document("comments").collection("commentlist").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                commentList.add(new AudioModel(document.get("uri").toString(), document.get("title").toString(),currentUserUID));

                            }
                            commentAdapter = new CommentAdapter(commentList);
                            recyclerView.setAdapter(commentAdapter);

                        } else {
                            Log.d("help", "Error getting documents: ", task.getException());
                        }
            }
        });
    }

    private void uploadAudio() {
        StorageReference filePath = mStorageRef.child(currentUserUID).child(audioFolderName).child(String.valueOf(System.currentTimeMillis()));
        final Uri uri = Uri.fromFile(new File(audioFile));
        filePath.putFile(uri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                AudioModel audioModel = new AudioModel(uri.toString(), titleInput.getText().toString(),currentUserUID);
                db.collection(users).document(currentUserUID).collection("audio").document("comments").collection("commentlist")
                        .add(audioModel)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("test", "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("test", "Error adding document", e);
                            }
                        });
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted)
            finish();
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(audioFile);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    player.start();
                }
            });
            player.prepareAsync();

        } catch (IOException e) {
            Log.e("play", "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            //ToDo give user feedback that recorder is not ready
            String a = "";
        }


    }

    private void stopRecording() {

        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

        } catch (RuntimeException stopException) {
            Log.e("stoprecord", "failed");
        }
    }
}
