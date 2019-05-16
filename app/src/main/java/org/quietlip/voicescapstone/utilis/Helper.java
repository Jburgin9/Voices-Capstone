package org.quietlip.voicescapstone.utilis;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

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

    public void makeSnackie(CoordinatorLayout coord, String ... varags){
        //add action
    }

    public void makeFirelog(Context context, String ... setup){
        firelog = new ProgressDialog(context);
        firelog.setTitle(setup[0]);
        firelog.setMessage(setup[1]);
        firelog.setCanceledOnTouchOutside(true);
        firelog.show();
    }

    public void dismissFirelog(){
        firelog.dismiss();
    }
}
