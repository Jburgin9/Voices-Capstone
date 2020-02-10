package org.quietlip.voicescapstone.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.utilis.CurrentUserManager;
import org.quietlip.voicescapstone.utilis.Helper;

import de.hdodenhof.circleimageview.CircleImageView;
/*
 *Think of using a viewpager for this activity where you would to separate info
 */

public class RegisterActivity extends AppCompatActivity {
    public static final String DOC_ONE = "Profile Info";
    public static final String DOC_PHOTO = "Photos";
    private static final String TAG = "PROUD";
    public static final int IMAGE_REQUEST = 0;

    private ImageView logoIV;
    private EditText registerEmail, registerPassword, registerUsername, regAboutMe;
    private Button createAccountBtn;
    private CoordinatorLayout coord;
    private TextView uploadImageTv;

    private Helper helper;
    private Uri imageUri;
    private UserModel user;

    String email;
    String password;
    String username;
    String aboutMe;

    private FirebaseFirestore firestore;
    private CircleImageView profileImage;
    private StorageReference storage;
    private FirebaseAuth registerAuth;
    private CurrentUserManager instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComps();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChoser();
            }
        });
        uploadImageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChoser();
            }
        });

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.makeFirelog(RegisterActivity.this, "", "loading...");
                registerUser();
            }
        });
    }

    private void initComps() {
        logoIV = findViewById(R.id.logo_reg_iv);
        uploadImageTv = findViewById(R.id.upload_image_tv);
        profileImage = findViewById(R.id.profile_image);
        registerEmail = findViewById(R.id.register_email_et);
        registerPassword = findViewById(R.id.register_password_et);
        registerUsername = findViewById(R.id.register_username_et);
        regAboutMe = findViewById(R.id.about_me_input);
        createAccountBtn = findViewById(R.id.create_account_btn);
        registerAuth = FirebaseAuth.getInstance();
        helper = Helper.getInstance();
        firestore = FirebaseFirestore.getInstance();
        coord = findViewById(R.id.coordinator_register);
        instance = CurrentUserManager.getInstance();
    }

    private void registerUser() {
        email = registerEmail.getText().toString();
        password = registerPassword.getText().toString();
        username = registerUsername.getText().toString();
        aboutMe = regAboutMe.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)) {
            registerAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                downLoadUri();
                                //TODO: BELOW SEND TO PROFILE AFTER CREATION
                                String uid = registerAuth.getCurrentUser().getUid();
                                if (imageUri != null) {
                                    UserModel user = new UserModel(aboutMe, imageUri.toString(),
                                            uid, username);
                                    instance.createUser(user);
                                    sharedPref(uid);
                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        instance.setUser(uid);
                                        Log.d(TAG, "onComplete: setUser called");
                                    }
                                }, 5000);

                                startActivity(new Intent(RegisterActivity.this,
                                        ProfileActivity.class));
                                helper.dismissFirelog();
                            } else {
                                helper.dismissFirelog();
                                Log.d(TAG, "onComplete: " + task.getException().toString());
                                helper.makeSnackie(coord, task.getException().toString());
                            }
                        }
                    });
        }
    }

    private void imageChoser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            //TODO: Create a sharedPref to put the image uri in?
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profileImage);
        }
    }

    private void downLoadUri() {
        storage = FirebaseStorage.getInstance().getReference(registerAuth.getCurrentUser().getUid()).child(DOC_PHOTO);
        if (imageUri != null) {
            storage.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                }
            });
        } else {
            Log.d(TAG, "downLoadUri: ");
        }

//        Task uploadTask =
//                storage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask
//                .TaskSnapshot>() {
//            @Override
//            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
//                storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        fireUIPath = uri.toString();
//                        Log.d(TAG, "onSuccess: " + uri.toString());
//                        user = new UserModel(username, fireUser.getUid(), fireUIPath, aboutMe);
//                        firestore.collection("users").document(fireUser.getUid()).set(user)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(RegisterActivity.this,
//                                        ProfileActivity.class));
//                            }
//                        });
//                    }
//                });
//            }
//        });
    }


    public void sharedPref(String userId) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("savedUser", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.apply();
    }
}