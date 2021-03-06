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


public class FeedAdapter extends RecyclerView.Adapter<FeedItemViewHolder> implements DeletionDialog.OnCompleteListener {
    private List<AudioModel> audioList;
    private View view;
    private DeletionDialog dialog;
    private int selectedPosition;

    public FeedAdapter(List<AudioModel> audioList) {
        this.audioList = audioList;
    }

    @NonNull
    @Override
    public FeedItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_item,
                    viewGroup, false);
        dialog = new DeletionDialog(view.getContext());
        dialog.setCompleteListener(this);
        return new FeedItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedItemViewHolder viewHolder, int i) {
        viewHolder.onBind(audioList.get(i));
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectedPosition = viewHolder.getAdapterPosition();
                dialog.show();
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return audioList.size();
    }

    private void deleteItem(int position) {
        AudioModel recentlyDeletedItem = audioList.get(position);

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

    @Override
    public void onComplete() {
        deleteItem(selectedPosition);
        dialog.dismiss();
    }
}