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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import org.quietlip.voicescapstone.utilis.Helper;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    public static final String DOC_ONE = "Profile Info";
    public static final String DOC_PHOTO= "Photos";
    private static final String TAG = "PROUD";
    public static final int IMAGE_REQUEST = 0;
    private EditText registerEmail, registerPassword, registerUsername;
    private Button createAccountBtn;
    private FirebaseAuth registerAuth;
    private Helper helper;
    private CoordinatorLayout coord;
    private ImageView logoIV;
    private FirebaseFirestore firestore;
    private CircleImageView profileImage;
    private FirebaseUser fireUser;
    private StorageReference storage;
    private Uri imageUri;
    private UserModel user;

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

    //init views & necessary components
    private void initComps(){
        registerPassword = findViewById(R.id.register_password_et);
        registerEmail = findViewById(R.id.register_email_et);
        createAccountBtn = findViewById(R.id.create_account_btn);
        registerUsername = findViewById(R.id.register_username_et);
        registerAuth = FirebaseAuth.getInstance();
        helper = Helper.getInstance();
        firestore = FirebaseFirestore.getInstance();
        coord = findViewById(R.id.coordinator_register);
        logoIV = findViewById(R.id.logo_reg_iv);
        profileImage = findViewById(R.id.profile_image);
        fireUser = registerAuth.getCurrentUser();
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
                                storage = FirebaseStorage.getInstance().getReference(fireUser.getUid()).child(DOC_PHOTO);
                                helper.dismissFirelog();
                                uploadFile();
                                user = new UserModel(username, registerAuth.getCurrentUser().getUid());
                                firestore.collection(fireUser.getUid()).document(DOC_ONE).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
                                    }
                                });
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
           }
    }

    private void imageChoser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK &&
        data != null && data.getData() != null){
            imageUri = data.getData();

            Picasso.get().load(imageUri).into(profileImage);
        }
    }


    private void uploadFile(){
        if(imageUri != null){
            storage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //snackie happy

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //snackie sad
                }
            });
        }else {
          //pick something cuzz
        }
    }
}
