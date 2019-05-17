package org.quietlip.voicescapstone.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;

import java.util.List;

public class VoicesAdapter extends RecyclerView.Adapter<VoicesViewholder> {
    List<AudioModel> audioList;

    public VoicesAdapter(List<AudioModel> audioList) {
        this.audioList = audioList;
    }

    @NonNull
    @Override
    public VoicesViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_recycler,viewGroup,false);
        return new VoicesViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoicesViewholder voicesViewholder, int i) {
voicesViewholder.onBind(audioList.get(i));
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }
}
