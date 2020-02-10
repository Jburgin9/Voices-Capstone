package org.quietlip.voicescapstone.recyclerview;


import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.quietlip.voicescapstone.utilis.AudioViewModel;
import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.views.CommentActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedItemViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "PROUD";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView playIv;
    private TextView title;
    private CircleImageView profilePic;
    private TextView username;
    private ImageView commentMic;
    private MediaPlayer mediaPlayer;
    private TextView timeStamp;
    private CardView cardView;

    private boolean mPlay = true;
    AudioModel audioModel;
    private String userid;
    private String audioId;
    private AudioViewModel viewModel;

    private StorageReference stRef;

    public FeedItemViewHolder(@NonNull final View itemView) {
        super(itemView);
        playIv = itemView.findViewById(R.id.feed_play_btn);
        title = itemView.findViewById(R.id.feed_title);
        profilePic = itemView.findViewById(R.id.feed_image);
        username = itemView.findViewById(R.id.feed_username);
        commentMic = itemView.findViewById(R.id.feed_comments_btn);
        timeStamp = itemView.findViewById(R.id.feed_time_stamp);
        mediaPlayer = new MediaPlayer();
        cardView = itemView.findViewById(R.id.feed_card_view);
    }

    public void onBind(final AudioModel audio) {
        viewModel = new AudioViewModel(audio);
        title.setText(audio.getTitle());
        audioModel = audio;
        audioId = audio.getAudioId();
        timeStamp.setText(viewModel.getTimeStamp());
        //TODO Viewmodel & composition model : audio state


        final UserModel user = audio.getUser();
        userid = user.getUserId();
        String userName = user.getUserName();
        username.setText(userName);
        Picasso.get().load(audio.getUser().getImageUrl()).fit().into(profilePic);

        playIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stRef = FirebaseStorage.getInstance().getReference(user.getUserId()).child("audio"
                ).child(audioId);
                Log.d(TAG, "onClick: " + audioId);
                stRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (mPlay) {
                            playIv.setImageResource(R.drawable.ic_stopp);
                            startPlaying(itemView.getContext(), uri);
                        } else {
                            playIv.setImageResource(R.drawable.play_button);
                            stopPlaying();
                            mPlay = !mPlay;
                        }
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
        try {
            mediaPlayer.setDataSource(context, audio);
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
    }

    private void stopPlaying() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void goToCommentActivity() {
        Intent commentActivityIntent = new Intent(itemView.getContext(), CommentActivity.class);
        commentActivityIntent.putExtra("userid", userid);
        commentActivityIntent.putExtra("audioid", audioId);
        itemView.getContext().startActivity(commentActivityIntent);

    }


    //TODO: put this logic into the viewmodel
//    private void getColor() {
//        List<Integer> colorList = new ArrayList<>();
//        colorList.add(R.drawable.purple);
//        colorList.add(R.drawable.pink);
//        colorList.add(R.drawable.green);
//        Random rando = new Random();
//        color.setBackground(itemView.getContext().getResources().getDrawable(colorList.get
//        (rando.nextInt(2))));
//    }
}