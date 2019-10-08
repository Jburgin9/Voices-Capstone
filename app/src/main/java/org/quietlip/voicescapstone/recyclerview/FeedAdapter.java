package org.quietlip.voicescapstone.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;

import java.util.List;

public class FeedAdapter  extends RecyclerView.Adapter<FeedViewHolder>{
    List<AudioModel> audioList;

    public FeedAdapter(List<AudioModel> audioList) {
        this.audioList = audioList;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_item_view, viewGroup, false);
        return new FeedViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder feedViewHolder, int i) {
        feedViewHolder.onBind(audioList.get(i));
    }

    @Override
    public int getItemCount() {
        return audioList.size();


    }
}

