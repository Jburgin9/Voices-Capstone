package org.quietlip.voicescapstone.utilis;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.IntentCompat;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.views.CommentActivity;

public class DeletionDialog extends Dialog implements View.OnClickListener {
    public interface OnCompleteListener {
        void onComplete();
    }

    private Button positiveBtn, negativeBtn;
    private OnCompleteListener completeListener;

    public DeletionDialog(@NonNull Context context) {
        super(context);
    }

    public void setCompleteListener(OnCompleteListener listener){
        completeListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.deletion_dialog);

        positiveBtn = findViewById(R.id.dialog_positive_btn);
        negativeBtn = findViewById(R.id.dialog_negative_btn);

        positiveBtn.setOnClickListener(this);
        negativeBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.dialog_positive_btn:
                completeListener.onComplete();
                break;
            case R.id.dialog_negative_btn:
                cancel();
                break;
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        completeListener = null;
    }
}
