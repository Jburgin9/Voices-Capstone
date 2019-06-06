package org.quietlip.voicescapstone.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.utilis.Helper;

import java.io.File;
import java.io.PrintStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    public static final String DOC_ONE = "Profile Info";
    public static final String DOC_PHOTO = "Photos";
    private static final String TAG = "PROUD";
    public static final int IMAGE_REQUEST = 0;

    private ImageView logoIV;
    private EditText registerEmail, registerPassword, registerUsername, regAboutMe;
    private Button createAccountBtn;
    private CoordinatorLayout coord;


    private Helper helper;

    private Uri imageUri;
    private UserModel user;
    private TextView uploadImageTv;

     String email;
     String password;
     String username;
     String aboutMe;

    private FirebaseFirestore firestore;
    private CircleImageView profileImage;
    private FirebaseUser fireUser;
    private StorageReference storage;
    private FirebaseAuth registerAuth;

    private String currentUserUID = FirebaseAuth.getInstance().getUid();
    private final String photosFolderName = "Photos";
    String users = "users";
    private Task uploadTask;
    String fireUIPath;

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

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChoser();
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


        fireUser = registerAuth.getCurrentUser();

    }

    private void registerUser() {
        Log.d(TAG, "registerUser: ");
         email = registerEmail.getText().toString();
         password = registerPassword.getText().toString();
         username = registerUsername.getText().toString();
         aboutMe = regAboutMe.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)) {
            helper.makeFirelog(this, "", "loading...");
            registerAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: register ");

                                helper.dismissFirelog();
                                downLoadUri();
                            } else {
                                helper.dismissFirelog();
                                Log.d(TAG, "onComplete: " + task.getException().toString());
                                helper.makeSnackie(coord, task.getException().toString());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: register " + e.getMessage());
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
            imageUri = data.getData();

            Picasso.get().load(imageUri).into(profileImage);
        }
    }


//    private void uploadFile() {
//        if (imageUri != null) {
//
//
//            storage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Log.d(TAG, "onSuccess: " + imageUri);
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    //snackie sad
//                }
//            });
//        } else {
//
//        }
//
//    }


    private void downLoadUri() {
       storage = FirebaseStorage.getInstance().getReference(fireUser.getUid()).child(DOC_PHOTO);
        uploadTask = storage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        fireUIPath = uri.toString();


                        Log.d(TAG, "onSuccess: " + uri.toString());
                        user = new UserModel(username, fireUser.getUid(),fireUIPath,aboutMe);

                        firestore.collection("users").document(fireUser.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));


                            }
                        });
                    }
                });
            }
        });


    }
}