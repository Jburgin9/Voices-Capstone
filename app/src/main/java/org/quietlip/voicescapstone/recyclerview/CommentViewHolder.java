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

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;

import java.io.IOException;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    private AppCompatImageButton play;
    private TextView title;
    private MediaPlayer mediaPlayer;
    private boolean mPlay = true;

    public CommentViewHolder (@NonNull View itemView) {
        super(itemView);
        play = itemView.findViewById(R.id.play_button_comment);
        title = itemView.findViewById(R.id.title_input_comment);
    }

    public void onBind(final AudioModel audio) {
        title.setText(audio.getTitle());
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
