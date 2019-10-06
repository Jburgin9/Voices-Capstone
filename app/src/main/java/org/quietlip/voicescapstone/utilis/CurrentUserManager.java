package org.quietlip.voicescapstone.utilis;

import android.os.Debug;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.recyclerview.VoicesAdapter;

public class CurrentUserManager {
    private static final String TAG = "UserMgr";

    static UserModel currentUser;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final CurrentUserManager ourInstance = new CurrentUserManager();

    public static CurrentUserManager getInstance() {
        return ourInstance;
    }

    private CurrentUserManager() {}

    public static void createUser(UserModel user){
        Log.d(TAG, "createUser: called");
        db.collection("users").document(user.getUserId())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    public static void setUser(String id) {
        db.collection("users").document(id)
                .get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot snapshot = task.getResult();
                        UserModel user = new UserModel(snapshot.get("userName").toString(),snapshot.get("userId").toString(),snapshot.get("imageUrl").toString(),snapshot.get("aboutMe").toString());
                        currentUser = user;
                    }
                });
    }

    public static UserModel getCurrentUser() {
        return currentUser;
    }

}
