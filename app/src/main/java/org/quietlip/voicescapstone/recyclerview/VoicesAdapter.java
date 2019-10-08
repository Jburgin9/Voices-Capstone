package org.quietlip.voicescapstone.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioDiff;
import org.quietlip.voicescapstone.models.AudioModel;

import java.util.ArrayList;
import java.util.List;


public class VoicesAdapter extends RecyclerView.Adapter<VoicesViewHolder> {

    private List<AudioModel> audioList;

    public VoicesAdapter(List<AudioModel> audioList) {
        this.audioList = audioList;
    }

    @NonNull
    @Override
    public VoicesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_item_view,
                        viewGroup, false);
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

    public void deleteItem(int position) {
        AudioModel recentlyDeletedItem = audioList.get(position);
        int recentlyDeletedItemPosition = position;

        StorageReference stRef =
                FirebaseStorage.getInstance().getReference(recentlyDeletedItem.getUser().getUserId())
                .child("audio").child(recentlyDeletedItem.getAudioId());
        stRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(recentlyDeletedItem.getUser().getUserId())
                .collection("audio").document(recentlyDeletedItem.getAudioId())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
        audioList.remove(position);
        notifyItemRemoved(position);
    }
}