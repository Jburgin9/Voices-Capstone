package org.quietlip.voicescapstone.recyclerview;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.views.CommentActivity;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "PROUD";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView play;
    private TextView title;
    private CircleImageView profilePic;
    private TextView username;
    private ImageView commentMic;
    private MediaPlayer mediaPlayer;
    private TextView timeStamp;
    private ImageView color;

    private boolean mPlay = true;
    AudioModel audioModel;
    private String userid;
    private String audioId;

    private StorageReference stRef;

    private SeekBar durationSb;
    private Handler handler;
    private Runnable runnable;

    public FeedViewHolder(@NonNull View itemView) {
        super(itemView);
        play = itemView.findViewById(R.id.profile_play);
        title = itemView.findViewById(R.id.profile_title);
        profilePic = itemView.findViewById(R.id.profile_image);
        username = itemView.findViewById(R.id.profile_username);
        commentMic = itemView.findViewById(R.id.profile_mic);
        durationSb = itemView.findViewById(R.id.profile_seekbar);
        handler = new Handler();
        timeStamp = itemView.findViewById(R.id.time_stamp);
        color = itemView.findViewById(R.id.color);


        durationSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
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

    public void onBind(final AudioModel audio) {
        title.setText(audio.getTitle());
        audioModel = audio;
        audioId = audio.getAudioId();

        long time = Long.parseLong(audioId);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        timeStamp.setText(formatter.format(calendar.getTime()));

        final UserModel user = audio.getUser();
        userid = user.getUserId();
        getColor();
        String username1 = user.getUserName();
        username.setText(username1);
        Picasso.get().load(audio.getUser().getImageUrl()).fit().into(profilePic);

        play.setOnClickListener(new View.OnClickListener() {
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
                            startPlaying(itemView.getContext(), uri);
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
        commentMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCommentActivity();
            }
        });
    }

    private void startPlaying(Context context, Uri audio) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(context, audio);
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

//    profilePic.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//        }
//    });
