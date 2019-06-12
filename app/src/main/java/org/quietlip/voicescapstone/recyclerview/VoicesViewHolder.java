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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
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
import org.quietlip.voicescapstone.views.CommentActivity;
import org.quietlip.voicescapstone.views.ProfileActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class VoicesViewHolder extends RecyclerView.ViewHolder {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private AppCompatImageButton play;
    private TextView title;
    private CircleImageView profilePic;
    private TextView username;
    private ImageButton commentMic;
    private SeekBar durationSb;

    private MediaPlayer mediaPlayer;
    private boolean mPlay = true;
    private Handler handler;
    private Runnable runnable;

    private String userid;
    private String audioId;
    private String pathId;

    public VoicesViewHolder(@NonNull View itemView) {
        super(itemView);
        play = itemView.findViewById(R.id.profile_play);
        title = itemView.findViewById(R.id.profile_title);
        profilePic = itemView.findViewById(R.id.profile_image);
        username = itemView.findViewById(R.id.profile_username);
        handler = new Handler();
        durationSb = itemView.findViewById(R.id.profile_seekbar);
        commentMic = itemView.findViewById(R.id.profile_mic);
    }

    public void onBind(final AudioModel audio) {
        duratonSeek();
        title.setText(audio.getTitle());
        audioId = audio.getAudioId();
        pathId = audio.getPathId();

        UserModel user1 = CurrentUserManager.getInstance().getCurrentUser();
        if(user1 != null) {
            String currentUserName = user1.getUserName();
            userid = user1.getUserId();
            username.setText(currentUserName);
            Log.e("currentUser", currentUserName);
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
                if (mPlay) {
                    changeSeekBar();
//                    Picasso.get().load(R.drawable.stop).fit().into(play);
                    play.setImageResource(R.drawable.ic_stopp);
                    startPlaying(itemView.getContext(), Uri.parse(audio.getUri()));
                } else {
                    play.setImageResource(R.drawable.play_button);
                    stopPlaying();
                }
                mPlay = !mPlay;

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
        commentActivityIntent.putExtra("audioid",audioId);
        commentActivityIntent.putExtra("pathid",pathId);
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
        private void duratonSeek(){
        durationSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int  progress, boolean fromUser) {
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
}





