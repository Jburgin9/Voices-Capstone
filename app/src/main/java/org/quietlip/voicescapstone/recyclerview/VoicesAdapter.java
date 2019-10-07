package org.quietlip.voicescapstone.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioDiff;
import org.quietlip.voicescapstone.models.AudioModel;

import java.util.ArrayList;
import java.util.List;


public class VoicesAdapter extends RecyclerView.Adapter<VoicesViewHolder> {

    List<AudioModel> audioList;

    public VoicesAdapter(List<AudioModel> audioList) {
        this.audioList = audioList;
    }

    @NonNull
    @Override
    public VoicesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_item_view, viewGroup, false);
        return new VoicesViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull VoicesViewHolder voicesViewHolder, int i) {
        voicesViewHolder.onBind(audioList.get(i));
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public void updateList(List<AudioModel> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new AudioDiff(audioList, newList));
        diffResult.dispatchUpdatesTo(this);
    }

}