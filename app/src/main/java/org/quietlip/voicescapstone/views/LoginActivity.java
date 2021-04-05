package org.quietlip.voicescapstone.views;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.utilis.AuthHelper;
import org.quietlip.voicescapstone.utilis.CurrentUserManager;
import org.quietlip.voicescapstone.utilis.Helper;
import org.quietlip.voicescapstone.utilis.PrefHelper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AuthHelper.LoginCallback {
    private EditText usernameInputET, passwordInputET;
    private Button loginBtn, signUpBTN;
    private FirebaseAuth loginAuth;
    private Helper helper;
    private CoordinatorLayout coord;
    private ConnectivityManager conMgr;
    private NetworkInfo activeNetworkInfo;
    private AuthHelper authHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        signUpBTN.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = conMgr.getActiveNetworkInfo();
        authHelper = new AuthHelper(this);
    }

    //initViews
    private void initViews() {
        usernameInputET = findViewById(R.id.username_et);
        passwordInputET = findViewById(R.id.password_et);
        loginBtn = findViewById(R.id.login_btn);
        signUpBTN = findViewById(R.id.sign_up_btn);
        loginAuth = FirebaseAuth.getInstance();
        helper = Helper.getInstance();
        coord = findViewById(R.id.coordinator_helper);
    }

    //onClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_btn:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.login_btn:
                AuthHelper authHelper = new AuthHelper(this);
                helper.makeFirelog(this, "", "Loading . . .");
                login();
                break;
        }
    }

    //login
    protected void login() {
        String username = usernameInputET.getText().toString().trim();
        String password = passwordInputET.getText().toString().trim();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            if (authHelper.canUserLogin(username, password) == 1) {
                startActivity(new Intent(LoginActivity.this,
                        ProfileActivity.class));
                helper.dismissFirelog();
            } else {
                helper.makeSnackie(coord, "Failure");
                helper.dismissFirelog();
            }
        }
    }

    @Override
    public void canUserLogin(boolean canUserLogin) {
        String username = usernameInputET.getText().toString().trim();
        String password = passwordInputET.getText().toString().trim();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            authHelper.canUserLogin(username, password);
//            new Runnable(new Hand).run();
            if (canUserLogin) {
                startActivity(new Intent(LoginActivity.this,
                        ProfileActivity.class));
                helper.dismissFirelog();
            } else {
                helper.makeSnackie(coord, "Failure");
                helper.dismissFirelog();
            }
        }
    }
}
