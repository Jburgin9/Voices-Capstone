package org.quietlip.voicescapstone.recyclerview;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class VoicesViewHolder extends RecyclerView.ViewHolder {

    private static String audioFile;

    private AppCompatImageButton play;

    private TextView title;

    private MediaPlayer mediaPlayer;

    List<AudioModel> audioList;

    public VoicesViewHolder(@NonNull View itemView) {
        super(itemView);
        play = itemView.findViewById(R.id.play_button_item_view);
        title = itemView.findViewById(R.id.title_item_view);

    }

    public void onBind(AudioModel audio) {
        title.setText(audio.getTitle());
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
            });
        }

}
//                int position = getAdapterPosition();
//                mediaPlayer = MediaPlayer.create(v.getContext(), audioList[position]);
//                mediaPlayer.start();
//                mediaPlayer.setDataSource(audioFile);
//                mediaPlayer.prepare();
//                startPlaying();
//
//            }
//
//            private void startPlaying() {
//                mediaPlayer = new MediaPlayer();
//                try {
//                    mediaPlayer.setDataSource(audioFile);
//                    mediaPlayer.prepare();
//                    mediaPlayer.start();
//                } catch (IOException e) {
////                    Log.e(LOG_TAG, "prepare() failed");
//                }
//            }
