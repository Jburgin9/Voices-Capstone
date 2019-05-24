package org.quietlip.voicescapstone.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
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
    private ImageView logoIV;

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
        logoIV = findViewById(R.id.logo_reg_iv);
    }

    private void registerUser(){
        String username = registerUsername.getText().toString();
        String password = registerPassword.getText().toString();

        if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            helper.makeFirelog(this, "", "loading...");
            registerAuth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                helper.dismissFirelog();
                                helper.makeSnackie(coord, "hello");
                                startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
                            } else {
                                helper.dismissFirelog();
                                helper.makeSnackie(coord, task.getException().toString());
                            }

                        }
                    });
        } else if(TextUtils.isEmpty(username)){
            helper.makeSnackie(coord, "please enter a valid username");
        } else if(TextUtils.isEmpty(password)){
            helper.makeSnackie(coord, "please enter a valid password");
        }
    }
}
