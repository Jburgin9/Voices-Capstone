package org.quietlip.voicescapstone.recyclerview;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.quietlip.voicescapstone.utilis.AudioViewModel;
import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.utilis.CurrentUserManager;
import org.quietlip.voicescapstone.views.CommentActivity;
import org.quietlip.voicescapstone.views.LoginActivity;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class VoicesViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "PROUD";

    private ImageView playIv;
    private TextView title;
    private CircleImageView profilePic;
    private TextView username;
    private ImageView commentMic;
    private TextView timeStamp;

    private MediaPlayer mediaPlayer;

    private String audioId;
    private String pathId;
    private StorageReference stRef;
    private UserModel user1;
    private AudioViewModel viewModel;
    private CurrentUserManager instance;

    public VoicesViewHolder(@NonNull View itemView) {
        super(itemView);
        playIv = itemView.findViewById(R.id.profile_play_btn);
        title = itemView.findViewById(R.id.profile_title);
        profilePic = itemView.findViewById(R.id.profile_image);
        username = itemView.findViewById(R.id.profile_username);
        timeStamp = itemView.findViewById(R.id.time_stamp);
        commentMic = itemView.findViewById(R.id.profile_comments_btn);
        mediaPlayer = new MediaPlayer();
        instance = CurrentUserManager.getInstance();
    }

    public void onBind(final AudioModel audio) {
        title.setText(audio.getTitle());
        audioId = audio.getAudioId();
        pathId = audio.getPathId();
        viewModel = new AudioViewModel(audio);
        timeStamp.setText(viewModel.getTimeStamp());
        user1 = instance.getCurrentUser();
        if (user1 != null) {
            username.setText(user1.getUserName());
            Picasso.get().load(user1.getImageUrl()).fit().into(profilePic);
        }
        playIv.setOnClickListener(new View.OnClickListener() {
            //Create a loading spinner in place of the play button to indicate that audio is loading
            @Override
            public void onClick(View v) {
                if (user1 != null) {
                    stRef = FirebaseStorage.getInstance().getReference(user1.getUserId())
                            .child("audio").child(audioId);
                    Log.d(TAG, "onClick: " + audioId);
                    stRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (!mediaPlayer.isPlaying()) {
                                playIv.setImageResource(R.drawable.ic_stopp);
                                startPlaying(uri);
                            } else {
                                playIv.setImageResource(R.drawable.play_button);
                                mediaPlayer.stop();
                            }
                        }
                    });
                }
            }
        });

        commentMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCommentActivity();

            }
        });
    }

    private void startPlaying(Uri audio) {
        mediaPlayer = new MediaPlayer();
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setDataSource(itemView.getContext(), audio);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                playIv.setImageResource(R.drawable.play_button);
                                mediaPlayer.reset();
                            }
                        });
                    }
                });
                mediaPlayer.prepareAsync();
                Log.d("VIEW HOLDER", String.valueOf(mediaPlayer.isPlaying()));
            } catch (IOException e) {
                Log.e("VIEW HOLDER", "prepare() failed");
            }
        } else {
            Log.d(TAG, "startPlaying: null");
        }
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void goToCommentActivity() {
        Intent commentActivityIntent = new Intent(itemView.getContext(), CommentActivity.class);
        Log.d(TAG, "goToCommentActivity: Jose says goToCommentActivity " + user1);
        if(null != user1) {
            commentActivityIntent.putExtra("userid", user1.getUserId());
            commentActivityIntent.putExtra("audioid", audioId);
            commentActivityIntent.putExtra("pathid", pathId);
            itemView.getContext().startActivity(commentActivityIntent);
        } else {
            Intent intent = new Intent(itemView.getContext(), LoginActivity.class);
            FirebaseAuth.getInstance().signOut();
            itemView.getContext().startActivity(intent);
        }
    }
}





