package org.quietlip.voicescapstone.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.model.User;
import org.quietlip.voicescapstone.utilis.Helper;

public class RegisterActivity extends AppCompatActivity {
    public static final String DOC_ONE = "Profile Info";
    private static final String TAG = "PROUD";
    private EditText registerEmail, registerPassword, registerUsername;
    private Button createAccountBtn;
    private FirebaseAuth registerAuth;
    private Helper helper;
    private CoordinatorLayout coord;
    private ImageView logoIV;
    private FirebaseFirestore firestore;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        initComps();

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    //init views & necessary components
    private void initComps(){
        registerPassword = findViewById(R.id.register_password_et);
        registerEmail = findViewById(R.id.register_email_et);
        createAccountBtn = findViewById(R.id.create_account_btn);
        registerUsername = findViewById(R.id.register_username_et);
        registerAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        helper = Helper.getInstance();
        coord = findViewById(R.id.coordinator_register);
        logoIV = findViewById(R.id.logo_reg_iv);
        user = registerAuth.getCurrentUser();
    }

    private void registerUser(){
        Log.d(TAG, "registerUser: ");
        final String email = registerEmail.getText().toString();
        final String password = registerPassword.getText().toString();
        final String username = registerUsername.getText().toString();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)) {
            helper.makeFirelog(this, "", "loading...");
            registerAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: ");
                                helper.dismissFirelog();
                                helper.makeSnackie(coord, "hello");
                                startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
                            } else {

                                helper.dismissFirelog();
                                helper.makeSnackie(coord, task.getException().toString());
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                }
            });
            User userOne = new User(username, user.getUid());
            firestore.collection(userOne.getUserID()).document(DOC_ONE).set(userOne).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                }
            });
        } else if(TextUtils.isEmpty(email)){
            helper.makeSnackie(coord, "please enter a valid username");
        } else if(TextUtils.isEmpty(password)){
            helper.makeSnackie(coord, "please enter a valid password");
        }
    }
}
