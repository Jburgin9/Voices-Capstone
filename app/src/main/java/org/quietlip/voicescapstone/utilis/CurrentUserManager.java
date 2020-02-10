package org.quietlip.voicescapstone.utilis;

import androidx.annotation.NonNull;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.quietlip.voicescapstone.models.UserModel;

/*
*Already extends application but still loses track of UserModel state
 */

public class CurrentUserManager extends Application {
    private UserModel currentUser;
    private FirebaseFirestore db;

    private CurrentUserManager() {
        db = FirebaseFirestore.getInstance();
    }

    public void createUser(UserModel user) {
        db.collection("users").document(user.getUserId())
                .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    public void setUser(String id) {
        db.collection("users").document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot != null) {
                                currentUser = new UserModel(snapshot.get("aboutMe").toString(),snapshot.get("imageUrl").toString(),
                                        snapshot.get("userId").toString(),snapshot.get("userName").toString());
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("UserMgr", e.getMessage());
            }
        });
    }

    public UserModel getCurrentUser() {
        Log.d("User", "get: " + currentUser);
        return currentUser;
    }

    private static class SingletonHelper{
        private static final CurrentUserManager INSTANCE = new CurrentUserManager();
    }

    public static CurrentUserManager getInstance(){
        return SingletonHelper.INSTANCE;
    }

}
