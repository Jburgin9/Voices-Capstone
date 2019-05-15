package org.quietlip.voicescapstone.utilis;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

public class Helper {
    private static Helper instance;

    private Helper(){}

    public static Helper getInstance() {
        if (instance == null) {
            instance = new Helper();
        }
        return instance;

    }

    public void makeSnackie(CoordinatorLayout coord, String displayMessage){
        Snackbar snackie = Snackbar.make(coord, displayMessage, Snackbar.LENGTH_SHORT);
        snackie.show();
    }

    public void makeSnackie(CoordinatorLayout coord, String ... varags){


    }
}
