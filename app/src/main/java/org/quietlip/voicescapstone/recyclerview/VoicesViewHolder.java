package org.quietlip.voicescapstone.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;

public class VoicesViewHolder extends RecyclerView.ViewHolder {
    private Button play;
    private TextView title;



    public VoicesViewHolder(@NonNull View itemView) {
        super(itemView);
     play = itemView.findViewById(R.id.play_button_item_view);
    title = itemView.findViewById(R.id.title);

}
    public void onBind(AudioModel audio) {
        title.setText(audio.getAudioTitle());
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
