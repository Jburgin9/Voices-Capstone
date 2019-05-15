package org.quietlip.voicescapstone.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.utilis.Helper;

public class RegisterActivity extends AppCompatActivity {
    private EditText registerUsername, registerPassword;
    private Button createAccountBtn;
    private FirebaseAuth registerAuth;
    private Helper helper;
    private CoordinatorLayout coord;

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
        registerUsername = findViewById(R.id.register_username_et);
        createAccountBtn = findViewById(R.id.create_account_btn);
        registerAuth = FirebaseAuth.getInstance();
        helper = Helper.getInstance();
        coord = findViewById(R.id.coordinator_register);
    }

    private void registerUser(){
        String username= "";
        String password = "";

        if(!TextUtils.isEmpty(registerUsername.getText())){
            username = registerUsername.getText().toString();
        }

        if(!TextUtils.isEmpty(registerPassword.getText())){
            password = registerPassword.getText().toString();
        }

        registerAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //snackie: welcome
                            helper.makeSnackie(coord, "hello");
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        helper.makeSnackie(coord, e.getMessage());
                    }
                });
    }
}
