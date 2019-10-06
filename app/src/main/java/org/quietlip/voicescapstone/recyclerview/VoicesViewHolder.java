package org.quietlip.voicescapstone.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.utilis.CurrentUserManager;
import org.quietlip.voicescapstone.views.BaseActivity;
import org.quietlip.voicescapstone.views.CommentActivity;
import org.quietlip.voicescapstone.views.FeedActivity;
import org.quietlip.voicescapstone.views.ProfileActivity;
import org.quietlip.voicescapstone.views.RecordActivity;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class VoicesViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "PROUD";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ImageView play;
    private TextView title;
    private CircleImageView profilePic;
    private TextView username;
    private ImageView commentMic;
    private SeekBar durationSb;
    private TextView timeStamp;
    private ImageView color;

    private MediaPlayer mediaPlayer;
    private boolean mPlay = true;
    private Handler handler;
    private Runnable runnable;
    private CardView content;

    private String userid;
    private String audioId;
    private String pathId;
    private StorageReference stRef;

    public VoicesViewHolder(@NonNull View itemView) {
        super(itemView);
        play = itemView.findViewById(R.id.profile_play);
        title = itemView.findViewById(R.id.profile_title);
        profilePic = itemView.findViewById(R.id.profile_image);
        username = itemView.findViewById(R.id.profile_username);
        timeStamp = itemView.findViewById(R.id.time_stamp);
        handler = new Handler();
        durationSb = itemView.findViewById(R.id.profile_seekbar);
        commentMic = itemView.findViewById(R.id.profile_mic);
        color = itemView.findViewById(R.id.color);
        content = itemView.findViewById(R.id.content);
        duratonSeek();
    }

    public void onBind(final AudioModel audio) {
        title.setText(audio.getTitle());
        audioId = audio.getAudioId();
        pathId = audio.getPathId();

        long time = Long.parseLong(audioId);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        timeStamp.setText(formatter.format(calendar.getTime()));

        final UserModel user1 = CurrentUserManager.getInstance().getCurrentUser();
        if (user1 != null) {
            String currentUserName = user1.getUserName();
            userid = user1.getUserId();
            getColor();
            username.setText(currentUserName);
            Picasso.get().load(user1.getImageUrl()).fit().into(profilePic);


        }
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stRef = FirebaseStorage.getInstance().getReference(user1.getUserId()).child("audio").child(audioId);
                Log.d(TAG, "onClick: " + audioId);
                stRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (mPlay) {

                            play.setImageResource(R.drawable.ic_stopp);
                            startPlaying(itemView.getContext(), uri);
                            changeSeekBar();

                        } else {
                            play.setImageResource(R.drawable.play_button);
                            stopPlaying();

                        }
                        mPlay = !mPlay;
                    }
                });
            }
        });

        commentMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCommentActivity();

            }
        });

    }

    private void startPlaying(Context context, Uri audio) {
        mediaPlayer = new MediaPlayer();
        Log.d(VoicesViewHolder.class.getName(), "startPlaying: " + audio);
        try {
            mediaPlayer.setDataSource(context, audio);
            // mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    durationSb.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    changeSeekBar();
                }
            });

            mediaPlayer.prepareAsync();
            Log.d("VIEW HOLDER", String.valueOf(mediaPlayer.isPlaying()));
        } catch (IOException e) {
            Log.e("VIEW HOLDER", "prepare() failed");
        }
    }

    private void stopPlaying() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void goToCommentActivity() {
        Intent commentActivityIntent = new Intent(itemView.getContext(), CommentActivity.class);
        commentActivityIntent.putExtra("userid", userid);
        commentActivityIntent.putExtra("audioid", audioId);
        commentActivityIntent.putExtra("pathid", pathId);
        itemView.getContext().startActivity(commentActivityIntent);

    }

    private void changeSeekBar() {
        if (mediaPlayer != null) {
            durationSb.setProgress(mediaPlayer.getCurrentPosition());
            if (mediaPlayer.isPlaying()) {
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
        durationSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.seekTo(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void getColor() {
        switch (userid) {
            case "Y0VTyMlb5rfjSnA9zic3QMIJ43y1":
                color.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.purple));
                /*Picasso.get().load(R.drawable.purple).fit().into(color);*/


                break;
            case "mPm3onnfwCXlAoNgyj5i5tcHeND3":
                color.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.pink));
//                Picasso.get().load(R.drawable.pink).fit().into(color);
                break;

            case "yUr700mkPmUAPvrdp1Z4BuVT9GO2":
                color.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.green));
//                Picasso.get().load(R.drawable.green).fit().into(color);
                break;
        }
    }

}





