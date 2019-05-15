package org.quietlip.voicescapstone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText usernameInputET, passwordInputET;
    private Button loginBtn;
    private ImageView logoIV;
    private TextView signUpTV;

    private final List<AuthUI.IdpConfig> loginProviders = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.PhoneBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initViews();


        signUpTV.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
    }

    //initViews
    private void initViews(){
        usernameInputET = findViewById(R.id.username_et);
        passwordInputET = findViewById(R.id.password_et);
        loginBtn = findViewById(R.id.log_in_button);
        logoIV = findViewById(R.id.logo_iv);
        signUpTV = findViewById(R.id.sign_up_tv);
    }

    @Override
    public void onClick(View v) {
    switch (v.getId()){
        case R.id.sign_up_tv:

            break;
        case R.id.log_in_button:

            break;
    }
    }
}
