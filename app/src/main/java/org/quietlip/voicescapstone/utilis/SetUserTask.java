package org.quietlip.voicescapstone.utilis;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;

import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.views.ProfileActivity;
import org.quietlip.voicescapstone.views.SplashActivity;

import java.lang.ref.WeakReference;

public class SetUserTask extends AsyncTask<String, Void, Void> {
    private WeakReference<Context> context;

    public SetUserTask(Context context){
        this.context = new WeakReference<>(context);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Helper.getInstance().makeFirelog(context.get(), "Setting user", "Please wait");
    }

    @Override
    protected Void doInBackground(String... strings) {
        CurrentUserManager.getInstance().setUser(strings[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Helper.getInstance().dismissFirelog();
    }
}
