package org.quietlip.voicescapstone.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.utilis.CurrentUserManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import io.opencensus.tags.Tag;

public class RecordActivity extends BaseActivity {


    String users = "users";
    String userlist = "userlist";

    private BottomNavigationView navigation;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private SeekBar durationSb;
    private Handler handler;
    private Runnable runnable;

    private CircleImageView record;
    private ImageView play;
    private Button post;
    private EditText titleInput;

    private boolean mPlay = true;
    private boolean mrecord = true;

    private RecordActivity recordActivity;

    private String dateFormatted;
    private static String audioFile;
    private final String audioFolderName = "audio";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;


    FirebaseStorage storage = FirebaseStorage.getInstance("gs://voicescapstone.appspot.com");
    private StorageReference mStorageRef = storage.getReference();

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
    String format = simpleDateFormat.format(new Date());

    String newAudioModelId;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;

    private String currentUserUID = FirebaseAuth.getInstance().getUid();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        setContentView(R.layout.activity_record);

        navigation = findViewById(R.id.bottom_nav);
        setBottomNav(navigation);

        recordActivity = this;

//        Date date = new Date(System.currentTimeMillis());
//        String pattern = "EEE, d MMM yyyy HH:mm:ss Z";
//        DateFormat formatter = new SimpleDateFormat(pattern);
//        formatter.setTimeZone(TimeZone.getTimeZone("EST"));
//        dateFormatted = formatter.format(date);

        mediaRecorder = new MediaRecorder();
        durationSb = findViewById(R.id.progress_bar);
        handler = new Handler();
        record = findViewById(R.id.record_button1);
        play = findViewById(R.id.play_button);
        post = findViewById(R.id.post_button);
        titleInput = findViewById(R.id.title_input);

        audioFile = getExternalCacheDir().getAbsolutePath();
        audioFile += System.currentTimeMillis() + "_recorded_audio.3gp";
        askPermission();
        setRecordAudioOnClick();
        setPlayAudioBackOnClick();
        setPostAudioOnClick();


        durationSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void setPlayAudioBackOnClick() {
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                stopRecording();

                if (mPlay) {
                    play.setImageResource(R.drawable.stop);
                    startPlaying();
                    changeSeekBar();
                } else {
                    play.setImageResource(R.drawable.play_button);
                    stopPlaying();
                }
                mPlay = !mPlay;
            }

            //                fetchAudioUrlFromFirebase();
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
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(recordActivity, new String[]{Manifest.permission.RECORD_AUDIO},
                    2);
        }
        //else do something
    }

    private void setRecordAudioOnClick() {
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mrecord) {
                    record.setImageResource(R.drawable.recordon3);
                    startRecording();
                } else {
                    record.setImageResource(R.drawable.recordoff2);

                    stopRecording();
                }
                mrecord = !mrecord;
            }
        });
    }


    private void goToProfile() {
        Intent profileIntent = new Intent(RecordActivity.this, ProfileActivity.class);
        startActivity(profileIntent);
    }


    private void uploadAudio() {
        progressDialog.setMessage("Uploading Audio...");
        progressDialog.show();
        newAudioModelId = String.valueOf(System.currentTimeMillis());
        StorageReference filepath = mStorageRef.child(currentUserUID).child(audioFolderName).child(newAudioModelId);
        final Uri uri = Uri.fromFile(new File(audioFile));
        filepath.putFile(uri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();


            }
        });
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                AudioModel audioModel = new AudioModel(uri.toString(), titleInput.getText().toString(), CurrentUserManager.getInstance().getCurrentUser(),newAudioModelId,"");
                db.collection(users).document(currentUserUID).collection("audio").document(newAudioModelId)
                        .set(audioModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                goToProfile();
                                Log.d("test", "DocumentSnapshot added with ID: " + newAudioModelId);
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

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
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
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(audioFile);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    durationSb.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    changeSeekBar();
                }
            });
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            Log.e("play", "prepare() failed");
        }
    }

    private void stopPlaying() {
        mediaPlayer.release();
        mediaPlayer = null;
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

    private void changeSeekBar(){
        if(mediaPlayer != null) {
            durationSb.setProgress(mediaPlayer.getCurrentPosition());

            if (mediaPlayer.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        changeSeekBar();
                    }
                };
                handler.postDelayed(runnable, 500);
            }
        }
    }

}


