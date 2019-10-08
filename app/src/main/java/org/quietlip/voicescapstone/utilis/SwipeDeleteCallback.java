package org.quietlip.voicescapstone.utilis;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.recyclerview.CommentAdapter;
import org.quietlip.voicescapstone.recyclerview.FeedAdapter;
import org.quietlip.voicescapstone.recyclerview.VoicesAdapter;

public class SwipeDeleteCallback extends ItemTouchHelper.SimpleCallback {
   private VoicesAdapter voicesAdapter;
   private FeedAdapter feedAdapter;
   private CommentAdapter commentAdapter;
   private Drawable icon;
   private final ColorDrawable background;

    public SwipeDeleteCallback(VoicesAdapter adapter, Context context){
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.voicesAdapter = adapter;
        icon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        background = new ColorDrawable(Color.RED);
    }

    public SwipeDeleteCallback(FeedAdapter adapter, Context context){
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.feedAdapter = adapter;
        icon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        background = new ColorDrawable(Color.RED);
    }

    public SwipeDeleteCallback(CommentAdapter adapter, Context context){
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.commentAdapter = adapter;
        icon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        background = new ColorDrawable(Color.RED);
    }


    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int position = viewHolder.getAdapterPosition();
        voicesAdapter.deleteItem(position);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
                actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) {
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = iconLeft+ icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());
        } else if (dX < 0) {
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else {
            background.setBounds(0, 0, 0, 0);
        }
        background.draw(c);
        icon.draw(c);
    }
}
