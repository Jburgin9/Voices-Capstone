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

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    List<AudioModel> commentList;
    AudioModel parentAudio;

    public CommentAdapter(List<AudioModel> commentList ) {
        this.commentList = commentList;

    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item_view, viewGroup, false);
        return new CommentViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {
        commentViewHolder.onBind(commentList.get(i));

    }


    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void deleteItem(int position) {
        AudioModel recentlyDeletedItem = commentList.get(position);
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
        commentList.remove(position);
        notifyItemRemoved(position);
    }
}
