package org.quietlip.voicescapstone.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    List<AudioModel> commentList;

    public CommentAdapter(List<AudioModel> commentList) {
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
}
