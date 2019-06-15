package org.quietlip.voicescapstone.views;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.firestore.model.value.StringValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.recyclerview.CommentAdapter;
import org.quietlip.voicescapstone.recyclerview.FeedAdapter;
import org.quietlip.voicescapstone.recyclerview.VoicesAdapter;
import org.quietlip.voicescapstone.utilis.CurrentUserManager;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.security.AccessController.getContext;

public class CommentActivity extends BaseActivity {

    String users = "users";
    String commentList1 = "commentlist";
    List<AudioModel> commentList;
    private CommentActivity commentActivity;
    private static String audioFile;
    private final String audioFolderName = "audio";
    private ProgressDialog progressDialog;

    private BottomNavigationView navigationView;
    private MediaRecorder mediaRecorder;
    private MediaPlayer player;
    private CircleImageView record;
    private ImageView play;
    private Button post;
    private EditText titleInput;
    private SeekBar recordSeekbar;

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;

    private ImageView parentImage;
    private TextView parentUsername;
    private ImageButton parentPlay;
    private SeekBar parentSeekBar;
    private TextView parentTitle;
    private boolean pPlay = true;

    private boolean mPlay = true;
    private boolean mRecord = true;

    String pathId;
    String userId;
    String currentAudioId;
    String audioId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://voicescapstone.appspot.com");
    private StorageReference mStorageRef = storage.getReference();
    private String currentUserUID = FirebaseAuth.getInstance().getUid();
    private final String TAG = "CURRENTTIME";
    private final String TAG2 = "TIME";
    private StorageReference stRef;
    private Handler handler;
    private Runnable runnable;


    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context;
        commentActivity = this;
        setContentView(R.layout.activity_comment);
        navigationView = findViewById(R.id.bottom_nav);
        setBottomNav(navigationView);


        userId = getIntent().getStringExtra("userid");
        currentAudioId = getIntent().getStringExtra("audioid");
        pathId = getIntent().getStringExtra("pathid");

        handler = new Handler();
        record = findViewById(R.id.record_button_comment);
        play = findViewById(R.id.play_button_comment);
        post = findViewById(R.id.post_button_comment);
        titleInput = findViewById(R.id.title_input_comment);

        recyclerView = findViewById(R.id.comment_recycler);
        progressDialog = new ProgressDialog(this);

        parentImage = findViewById(R.id.parent_comment_image);
        parentUsername = findViewById(R.id.parent_comment_username);
        parentPlay = findViewById(R.id.parent_comment_play);
        parentSeekBar = findViewById(R.id.parent_comment_seekbar);
        parentTitle = findViewById(R.id.parent_comment_title);

        audioFile = getExternalCacheDir().getAbsolutePath();
        audioFile += System.currentTimeMillis() + "_recorded_audio.3pg";
        Log.d(TAG, audioFile);

        askPermission();
        retrieveParentAudio();
        setRecordAudioOnClick();
        setPlayAudioBackOnClick();
        setPostAudioOnClick();
        retrieveComments();
        duratonSeek();

//        Date date = new Date(System.currentTimeMillis());
//        String pattern = "EEE, d MMM yyyy HH:mm:ss Z";
//        DateFormat formatter = new SimpleDateFormat(pattern);
//        formatter.setTimeZone(TimeZone.getTimeZone("EST"));
//        String dateFormatted = formatter.format(date);
//        Log.d(TAG2, dateFormatted);


    }

    private void retrieveParentAudio() {
        Log.d(TAG, "retrieveParentAudio: " + currentAudioId);
        if(userId != null && currentAudioId != null ){
            db.collection("users").document(userId).collection("audio").document(currentAudioId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                HashMap<String, String> usermap = (HashMap<String, String>) document.get("user");

                                final UserModel user = new UserModel(usermap.get("userName"), usermap.get("userId"), usermap.get("imageUrl"), usermap.get("aboutMe"));
                                final AudioModel audio = (new AudioModel(document.get("uri").toString(), document.get("title").toString(), user, document.get("audioId").toString(), document.getId()));


                                parentUsername.setText(user.getUserName());
                                parentTitle.setText((audio.getTitle()));
                                Picasso.get().load(user.getImageUrl()).fit().into(parentImage);
                                audioId = audio.getAudioId();

                                parentPlay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        stRef = FirebaseStorage.getInstance().getReference(user.getUserId()).child("audio").child(audioId);
                                        Log.d(TAG, "onClick: " + audioId);
                                        stRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                if (mPlay) {

//                    Picasso.get().load(R.drawable.stop).fit().into(play);
                                                    play.setImageResource(R.drawable.ic_stopp);
                                                    startPlayingParent(uri);
                                                    changeSeekBar();
                                                    //startPlaying(itemView.getContext(), Uri.parse(audio.getUri()));
                                                } else {
                                                    play.setImageResource(R.drawable.play_button);
                                                    stopPlaying();

                                                }
                                                mPlay = !mPlay;
                                            }
                                        });
                                    }
                                });
                            }

                        }
                    });
        }
        }


    private void setPlayAudioBackOnClick() {
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlay) {
                    play.setImageResource(R.drawable.ic_stopp);
                    startPlayingRecord();
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
                titleInput.onEditorAction(EditorInfo.IME_ACTION_DONE);
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

    private void retrieveComments() {
        if (userId != null && currentAudioId != null) {
            db.collection("users").document(userId).collection("audio").document(currentAudioId).collection("commentlist").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                commentList = new ArrayList<>();
                                List<DocumentSnapshot> myList = task.getResult().getDocuments();
                                for (int i = 0; i < myList.size(); i++) {

                                    HashMap<String, String> usermap = (HashMap<String, String>) myList.get(i).get("user");
                                    final UserModel user = new UserModel(usermap.get("userName"), usermap.get("userId"),
                                            usermap.get("imageUrl"), usermap.get("aboutMe"));
                                    myList.get(i).get("uri").toString();
                                    commentList.add(new AudioModel(myList.get(i).get("uri").toString(), myList.get(i).get("title").toString(), user, myList.get(i).get("audioId").toString(), myList.get(i).getId()));
                                }

//                                for (DocumentSnapshot document : task.getResult()) {
//                                    Log.d(CommentActivity.class.getName(), "onComplete: " + document);
//                                    HashMap<String, String> usermap = (HashMap<String, String>) document.get("user");
//                                    final UserModel user = new UserModel(usermap.get("userName"), usermap.get("userId"),
//                                            usermap.get("imageUrl"), usermap.get("aboutMe"));
//                                    commentList.add(new AudioModel(document.get("uri").toString(), document.get("title").toString(), user, document.getId()));
//                                }
//                                Collections.sort(commentList,new MySort());
                                for (int i = 0; i < commentList.size(); i++) {
                                    Log.e("Testing", commentList.get(i).getAudioId());
                                }
                                commentAdapter = new CommentAdapter(commentList);
                                recyclerView.setAdapter(commentAdapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                progressDialog.dismiss();
                            } else {
                                Log.d("help", "Error getting documents: ", task.getException());
                            }
                        }

                    });
        }

    }

    private void uploadAudio() {
        progressDialog.setMessage("Uploading Audio...");
        progressDialog.show();
        audioId = String.valueOf(System.currentTimeMillis());
        StorageReference filePath = mStorageRef.child(currentUserUID).child(audioFolderName).child(String.valueOf(audioId));
        final Uri uri = Uri.fromFile(new File(audioFile));
        filePath.putFile(uri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();

            }
        });
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                AudioModel audioModel = new AudioModel(uri.toString(), titleInput.getText().toString(), CurrentUserManager.getCurrentUser(), audioId, "");
                db.collection(users).document(userId).collection("audio").document(currentAudioId).collection("commentlist")
                        .document(audioId)

                        .set(audioModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                retrieveComments();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("test", "Error adding document", e);
                            }
                        });
                progressDialog.dismiss();
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

    private void startPlayingParent(Uri uri) {
        player = new MediaPlayer();
        try {
            player.setDataSource(uri.toString());
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    parentSeekBar.setMax(player.getDuration());
                    player.start();
                    changeSeekBar();
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
    private void startPlayingRecord() {
        player = new MediaPlayer();
        try {
            player.setDataSource(audioFile);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    player.start();
                    changeSeekBar();
                }
            });
            player.prepareAsync();
        } catch (IOException e) {
            Log.e("play", "prepare() failed");
        }
    }

    private void changeSeekBar() {
        if (player != null) {
            parentSeekBar.setProgress(player.getCurrentPosition());
            if (player.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        changeSeekBar();
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        }
    }

    private void duratonSeek(){
        parentSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int  progress, boolean fromUser) {
                player.seekTo(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


//        private void duratonSeekRecord(){
//            parentSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int  progress, boolean fromUser) {
//                    player.seekTo(progress);
//
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//
//                }
//            });
//    }
}}