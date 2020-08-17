package org.quietlip.voicescapstone.recyclerview;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.utilis.DeletionDialog;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> implements DeletionDialog.OnCompleteListener {
    private List<AudioModel> commentList;
    private String audioId;
    private View view;
    private DeletionDialog dialog;
    private int selectedPosition;

    public CommentAdapter(List<AudioModel> commentList, String audioId) {
        this.commentList = commentList;
        this.audioId = audioId;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item_view, viewGroup, false);
        dialog = new DeletionDialog(view.getContext());
        dialog.setCompleteListener(this);
        return new CommentViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {
        commentViewHolder.onBind(commentList.get(i));
        commentViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectedPosition = commentViewHolder.getAdapterPosition();
                dialog.show();
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return commentList.size();
    }

    private void deleteItem(int position) {
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
                .collection("audio").document(audioId)
                .collection("commentlist").document(recentlyDeletedItem.getAudioId())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
        commentList.remove(position);
        notifyDataSetChanged();
//        notifyItemRemoved(position);

    }

    @Override
    public void onComplete() {
        deleteItem(selectedPosition);
        dialog.dismiss();
    }
}
