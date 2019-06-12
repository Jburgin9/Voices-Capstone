package org.quietlip.voicescapstone.recyclerview;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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

import de.hdodenhof.circleimageview.CircleImageView;
public class FeedViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "PROUD";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AppCompatImageButton play;
    private TextView title;
    private CircleImageView profilePic;
    private TextView username;
    private ImageButton commentMic;
    private MediaPlayer mediaPlayer;
    private boolean mPlay = true;
    AudioModel audioModel;
    private String userid;
    private String audioId;
    private StorageReference stRef;
    public FeedViewHolder(@NonNull View itemView) {
        super(itemView);
        play = itemView.findViewById(R.id.profile_play);
        title = itemView.findViewById(R.id.profile_title);
        profilePic = itemView.findViewById(R.id.profile_image);
        username = itemView.findViewById(R.id.profile_username);
        commentMic = itemView.findViewById(R.id.profile_mic);
    }
    public void onBind(final AudioModel audio) {
        title.setText(audio.getTitle());
        audioModel = audio;
        audioId = audio.getAudioId();

        final UserModel user = audio.getUser();
        userid = user.getUserId();
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
                            play.setImageResource(R.drawable.stop);
                            startPlaying(itemView.getContext(), uri);
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
            mediaPlayer.prepare();
            mediaPlayer.start();
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
        itemView.getContext().startActivity(commentActivityIntent);

    }
}//    profilePic.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//        }
//    });
