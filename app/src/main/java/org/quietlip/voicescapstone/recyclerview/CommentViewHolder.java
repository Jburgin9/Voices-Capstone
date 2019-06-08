package org.quietlip.voicescapstone.recyclerview;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.models.UserModel;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    private AppCompatImageButton play;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView title;
    private MediaPlayer mediaPlayer;
    private boolean mPlay = true;
    private CircleImageView profile_pic;
    private TextView username;
    AudioModel audioModel;


    public CommentViewHolder (@NonNull View itemView) {
        super(itemView);
        play = itemView.findViewById(R.id.play_button_comment);
        title = itemView.findViewById(R.id.title_input_comment);
        profile_pic = itemView.findViewById(R.id.profile_image);
        username = itemView.findViewById(R.id.user_name);
    }

    public void onBind(final AudioModel audio) {
        title.setText(audio.getTitle());
        audioModel = audio;
        UserModel user = audio.getUser();
        String username1 = user.getUserName();
        username.setText(username1);
        Picasso.get().load(audio.getUser().getImageUrl()).fit().into(profile_pic);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlay) {
                    play.setImageResource(R.drawable.stop);
                    startPlaying(itemView.getContext(), Uri.parse(audio.getUri()));
                } else {
                    play.setImageResource(R.drawable.play_button);
                    stopPlaying();
                }
                mPlay = !mPlay;

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

}
