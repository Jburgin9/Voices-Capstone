package org.quietlip.voicescapstone.recyclerview;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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


    private MediaPlayer mediaPlayer;
    private boolean mPlay = true;


    AudioModel audioModel;

    public VoicesViewHolder(@NonNull View itemView) {
        super(itemView);
        play = itemView.findViewById(R.id.profile_play);
        title = itemView.findViewById(R.id.profile_title);
        profilePic = itemView.findViewById(R.id.profile_image);
        username = itemView.findViewById(R.id.profile_username);

    }

    public void onBind(final AudioModel audio) {
        title.setText(audio.getTitle());
        audioModel = audio;
        UserModel user = audio.getUser();
        String username1 = user .getUserName();
        username.setText(username1);
        Picasso.get().load(audio.getUser().getImageUrl()).fit().into(profilePic);


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlay) {
                    play.setImageResource(R.drawable.stop2);
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





