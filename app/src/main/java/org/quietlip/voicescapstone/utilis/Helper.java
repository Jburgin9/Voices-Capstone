package org.quietlip.voicescapstone.utilis;

import android.app.ProgressDialog;
import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;

public class Helper {
    private static Helper instance;
    private Snackbar snackie;
    private ProgressDialog firelog;

    private Helper(){}

    public static Helper getInstance() {
        if (instance == null) {
            instance = new Helper();
        }
        return instance;

    }

    public void makeSnackie(CoordinatorLayout coord, String displayMessage){
        snackie = Snackbar.make(coord, displayMessage, Snackbar.LENGTH_SHORT);
        snackie.show();
    }

    public void makeFirelog(Context context, String title, String message){
        firelog = new ProgressDialog(context);
        firelog.setTitle(title);
        firelog.setMessage(message);
        firelog.setCanceledOnTouchOutside(true);
        firelog.show();
    }

    public void dismissFirelog(){
        firelog.dismiss();
    }
}
