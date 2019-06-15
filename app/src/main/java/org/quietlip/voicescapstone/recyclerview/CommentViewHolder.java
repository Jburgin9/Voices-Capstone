package org.quietlip.voicescapstone.recyclerview;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "PROUD";
    private ImageView play;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView title;
    private MediaPlayer mediaPlayer;
    private boolean mPlay = true;
    private CircleImageView comment_pic;
    private TextView username;
    private TextView timeStamp;
    private ImageView color;


    AudioModel audioModel;
    private StorageReference stRef;
    private SeekBar durationSb;
    private String audioId;
    private Handler handler;
    private Runnable runnable;


    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        play = itemView.findViewById(R.id.comment_play);
        title = itemView.findViewById(R.id.comment_title);
        comment_pic = itemView.findViewById(R.id.comment_image);
        username = itemView.findViewById(R.id.comment_username);
        durationSb = itemView.findViewById(R.id.comment_seekbar);
        timeStamp = itemView.findViewById(R.id.comment_time_stamp);
        color = itemView.findViewById(R.id.color);
        handler = new Handler();
    }

    public void onBind(final AudioModel audio) {
        title.setText(audio.getTitle());
        audioId = audio.getAudioId();
        audioModel = audio;

        long time = Long.parseLong(audioId);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        timeStamp.setText(formatter.format(calendar.getTime()));

        final UserModel user = audio.getUser();
        getColor();
        String username1 = user.getUserName();
        username.setText(username1);
        Picasso.get().load(audio.getUser().getImageUrl()).fit().into(comment_pic);


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
                if (runnable != null) {
                    handler.postDelayed(runnable, 1000);
                }
            }
        }
    }
    private void getColor() {
        switch (audioModel.getUser().getUserId()) {
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

