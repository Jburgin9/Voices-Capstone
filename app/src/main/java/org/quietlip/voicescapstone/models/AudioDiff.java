package org.quietlip.voicescapstone.models;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class AudioDiff extends DiffUtil.Callback {
    private List<AudioModel> oldList;
    private List<AudioModel> newList;

    public AudioDiff(List<AudioModel> oldList, List<AudioModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int i, int i1) {
        return oldList.get(i).getAudioId().equals(newList.get(i1).getAudioId());
    }

    @Override
    public boolean areContentsTheSame(int i, int i1) {
        return oldList.get(i) == newList.get(i1);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
