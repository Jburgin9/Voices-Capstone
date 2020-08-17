package org.quietlip.voicescapstone.views;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.picasso.Picasso;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.recyclerview.CommentAdapter;
import org.quietlip.voicescapstone.utilis.AudioViewModel;
import org.quietlip.voicescapstone.utilis.CurrentUserManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.subjects.PublishSubject;

public class CommentActivity extends BaseActivity {
    BiFunction<Integer, Integer, Integer> sum = (x, y) -> x + y;

    String users = "users";
    List<AudioModel> commentList;
    private CommentActivity commentActivity;
    private static String audioFile;
    private final String audioFolderName = "audio";
    private ProgressDialog progressDialog;

    private BottomNavigationView navigationView;
    private MediaRecorder mediaRecorder;
    private MediaPlayer commentPlayer;
    private CircleImageView record;
    private ImageView commentRecordingPlay;
    private Button post;
    private EditText titleInput;
    private SeekBar commentSeekBar;

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;

    private ImageView parentImage;
    private TextView parentUsername;
    private ImageView parentPlay;
    private SeekBar parentSeekBar;
    private TextView parentTitle;
    private TextView parentTime;
    private MediaPlayer parentPlayer;
    private ImageView color;

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
    private StorageReference stRef;
    private Handler handler;
    private Runnable runnable;
    private AudioViewModel viewModel;
    private AudioModel audioResult;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commentActivity = this;
        setContentView(R.layout.activity_comment);
        navigationView = findViewById(R.id.bottom_nav);
        setBottomNav(navigationView);

        userId = getIntent().getStringExtra("userid");
        currentAudioId = getIntent().getStringExtra("audioid");
        pathId = getIntent().getStringExtra("pathid");

        commentSeekBar = findViewById(R.id.comment_record_seek);
        handler = new Handler();
        record = findViewById(R.id.record_button_comment);
        commentRecordingPlay = findViewById(R.id.play_button_comment);
        post = findViewById(R.id.post_button_comment);
        titleInput = findViewById(R.id.title_input_comment);

        recyclerView = findViewById(R.id.comment_recycler);
        progressDialog = new ProgressDialog(this);

        parentImage = findViewById(R.id.parent_comment_image);
        parentUsername = findViewById(R.id.parent_comment_username);
        parentPlay = findViewById(R.id.parent_comment_play);
        parentSeekBar = findViewById(R.id.parent_comment_seekbar);
        parentTitle = findViewById(R.id.parent_comment_title);
        parentTime = findViewById(R.id.parent_time_stamp);
        color = findViewById(R.id.color);

        audioFile = getExternalCacheDir().getAbsolutePath();
        audioFile += System.currentTimeMillis() + "_recorded_audio.3pg";

        commentDurationSeek();
        askPermission();
        retrieveParentAudio();
        recordAudio();
        playbackAudio();
        postAudio();
        retrieveComments();
        duratonSeek();
    }


    //TODO: Repo class that will make call to Firebase to retrieve info
    private void retrieveParentAudio() {
        if (userId != null && currentAudioId != null) {
            db.collection("users").document(userId).collection("audio").document(currentAudioId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                    audioResult = task.getResult().toObject(AudioModel.class);
                                    UserModel user = audioResult.getUser();
                                    viewModel = new AudioViewModel(audioResult);
                                    user.getUserId();
                                    getColor();
                                    parentUsername.setText(user.getUserName());
                                    parentTitle.setText((audioResult.getTitle()));
                                    Picasso.get().load(user.getImageUrl()).fit().into(parentImage);
                                    audioId = audioResult.getAudioId();
                                    parentTime.setText(viewModel.getTimeStamp());
                                    parentPlay.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            stRef = FirebaseStorage.getInstance().getReference(user.getUserId()).child("audio").child(audioId);
                                            stRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    if (mPlay) {
                                                        parentPlay.setImageResource(R.drawable.ic_stopp);
                                                        startPlayingParent(uri);
                                                        changeSeekBar();
                                                    } else {
                                                        parentPlay.setImageResource(R.drawable.play_button);
                                                        stopParentPlayer();
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

    private void getColor() {
        switch (userId) {
            case "Y0VTyMlb5rfjSnA9zic3QMIJ43y1":
                color.setBackground(this.getApplicationContext().getResources().getDrawable(R.drawable.purple));
                /*Picasso.get().load(R.drawable.purple).fit().into(color);*/


                break;
            case "mPm3onnfwCXlAoNgyj5i5tcHeND3":
                color.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.pink));
//                Picasso.get().load(R.drawable.pink).fit().into(color);
                break;

            case "yUr700mkPmUAPvrdp1Z4BuVT9GO2":
                color.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.green));
//                Picasso.get().load(R.drawable.green).fit().into(color);
                break;
        }
    }


    private void playbackAudio() {
        commentRecordingPlay.setOnClickListener(v -> {
                if (mPlay) {
                    commentRecordingPlay.setImageResource(R.drawable.ic_stopp);
                    startCommentPlayback();
                    commentPlayer.setOnCompletionListener(mp -> commentRecordingPlay.setImageResource(R.drawable.play_button));
                } else {
                    commentRecordingPlay.setImageResource(R.drawable.play_button);
                    stopPlaying();
                }
                mPlay = !mPlay;
        });
    }

    private void postAudio() {
        post.setOnClickListener(v -> uploadAudio());
    }

    private void askPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(commentActivity,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 2);
        }
    }

    private void recordAudio() {
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecord) {
                    record.setImageResource(R.drawable.aquamic);
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
                                    if (myList.get(i).get("user") != null) {
                                        HashMap<String, String> usermap = (HashMap<String,
                                                String>) myList.get(i).get("user");
                                        UserModel user = new UserModel(usermap.get(
                                                "aboutMe"),
                                                usermap.get("imageUrl"),
                                                usermap.get("userId"), usermap.get("userName"));
                                        myList.get(i).get("uri").toString();
                                        commentList.add(new AudioModel(myList.get(i).get("uri").toString(), myList.get(i).get("title").toString(), user, myList.get(i).get("audioId").toString(), myList.get(i).getId()));
                                    }
                                }
                                if(audioResult != null){
                                    commentAdapter = new CommentAdapter(commentList, audioResult.getAudioId());
                                }
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
        StorageReference filePath =
                mStorageRef.child(currentUserUID).child(audioFolderName).child(String.valueOf(audioId));
        final Uri uri = Uri.fromFile(new File(audioFile));
        filePath.putFile(uri).addOnFailureListener(e -> progressDialog.dismiss());
        filePath.putFile(uri).addOnSuccessListener(taskSnapshot ->  {
                AudioModel audioModel = new AudioModel(uri.toString(),
                        titleInput.getText().toString(), CurrentUserManager.getInstance().getCurrentUser(),
                        audioId, "");
                db.collection(users).document(userId).collection("audio").document(currentAudioId).collection("commentlist")
                        .document(audioId)
                        .set(audioModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                stopRecording();
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
        parentPlayer = new MediaPlayer();
        try {
            parentPlayer.setDataSource(uri.toString());
            parentPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    parentSeekBar.setMax(parentPlayer.getDuration());
                    parentPlayer.start();
                    changeSeekBar();
                }
            });
            parentPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("play", "prepare() failed");
        }
    }

    private void stopPlaying() {
        commentPlayer.release();
        commentPlayer = null;
    }

    private void stopParentPlayer() {
        parentPlayer.release();
        parentPlayer = null;
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
        }
    }

    private void stopRecording() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder.reset();
            mediaRecorder = null;
        } catch (RuntimeException stopException) {
            Log.e("stoprecord", "failed");
        }
    }

    private void startCommentPlayback() {
        commentPlayer = new MediaPlayer();
        try {
            commentPlayer.setDataSource(audioFile);
            commentPlayer.setOnPreparedListener(mp -> {
                commentSeekBar.setMax(commentPlayer.getDuration());
                commentPlayer.start();
                changeCommentSeekBar();
            });
            commentPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("play", "prepare() failed");
        }
    }

    private void changeSeekBar() {
        if (parentPlayer != null) {
            parentSeekBar.setProgress(parentPlayer.getCurrentPosition());
            if (parentPlayer.isPlaying()) {
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

    private void duratonSeek() {
        parentSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                parentPlayer.seekTo(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void changeCommentSeekBar() {
        if (commentPlayer != null) {
            commentSeekBar.setProgress(commentPlayer.getCurrentPosition());
            if (commentPlayer.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        changeCommentSeekBar();
                    }
                };
                handler.postDelayed(runnable, 500);
            }
        }
    }

    private void commentDurationSeek() {
        commentSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                commentPlayer.seekTo(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
