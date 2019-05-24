package org.quietlip.voicescapstone.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;

public class VoicesViewHolder extends RecyclerView.ViewHolder {
    private AppCompatImageButton play;
    private TextView title;


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
