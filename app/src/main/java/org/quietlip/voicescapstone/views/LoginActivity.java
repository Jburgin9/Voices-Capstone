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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.utilis.Helper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText usernameInputET, passwordInputET;
    private Button loginBtn;
    private ImageView logoIV;
    private TextView signUpTV;
    private FirebaseAuth loginAuth;
    private Helper helper;
    private CoordinatorLayout coord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();


        signUpTV.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
    }

    //initViews
    private void initViews() {
        usernameInputET = findViewById(R.id.username_et);
        passwordInputET = findViewById(R.id.password_et);
        loginBtn = findViewById(R.id.login_btn);
        logoIV = findViewById(R.id.logo_iv);
        signUpTV = findViewById(R.id.sign_up_tv);
        loginAuth = FirebaseAuth.getInstance();
        helper = Helper.getInstance();
        coord = findViewById(R.id.coordinator_login);
    }

    //onClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_tv:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.login_btn:
                login();
                break;
        }
    }

    //onStart
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = loginAuth.getCurrentUser();
        if (currentUser == null) {
            //snackbar user not signed in
        }
    }

    //login
    protected void login() {
        String username = usernameInputET.getText().toString();
        String password = passwordInputET.getText().toString();

        if (!TextUtils.isEmpty(username)  && !TextUtils.isEmpty(password)) {
            helper.makeFirelog(this, "title", "loading . . .");
            loginAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                helper.dismissFirelog();
                                helper.makeSnackie(coord, "success");
                            } else {
                                helper.dismissFirelog();
                                helper.makeSnackie(coord, "failure");
                            }
                        }
                    });
        } else if (TextUtils.isEmpty(username)){
            helper.makeSnackie(coord, "please enter username");
        } else if (TextUtils.isEmpty(password)) {
            helper.makeSnackie(coord, "please enter password");
        }
    }
}
