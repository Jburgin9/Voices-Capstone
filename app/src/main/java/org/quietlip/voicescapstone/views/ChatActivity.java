package org.quietlip.voicescapstone.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.utilis.Helper;

public class ChatActivity extends AppCompatActivity {
    private static int SIGN_IN_REQUEST_CODE = 1;
    //private FirebaseListAdapter<ChatMessage> firebaseAdapter;
    private Helper helper;
    private CoordinatorLayout coord;
    private FloatingActionButton sendFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        coord = findViewById(R.id.coordinator_chat);
        sendFab = findViewById(R.id.send_fab);

        helper = Helper.getInstance();

//        if(FirebaseAuth.getInstance().getCurrentUser() == null){
//            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
//        } else {
//            helper.makeSnackie(coord, "welcome" +
//                    FirebaseAuth.getInstance().getCurrentUser());
//        }
//        displayChatMessage();
    }

    private void displayChatMessage(){
    }

    //Activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE){
            if(requestCode == RESULT_OK){
                helper.makeSnackie(coord, "successfully signed in");
                displayChatMessage();
            } else {
                helper.makeSnackie(coord, "error attempting login");
            }
        }
    }

    //options menu created
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    //option items switch
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }
}

