package org.quietlip.voicescapstone.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.quietlip.voicescapstone.R;
import org.quietlip.voicescapstone.models.AudioModel;
import org.quietlip.voicescapstone.models.UserModel;
import org.quietlip.voicescapstone.utilis.CurrentUserManager;
import org.quietlip.voicescapstone.views.CommentActivity;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VoicesViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "PROUD";

    private ImageView play;
    private TextView title;
    private CircleImageView profilePic;
    private TextView username;
    private ImageView commentMic;
    private SeekBar durationSb;
    private TextView timeStamp;
    private ImageView color;
    private CardView content;

    private MediaPlayer mediaPlayer;
    private boolean mPlay = true;
    private Handler handler;
    private Runnable runnable;

    private String userid;
    private String audioId;
    private String pathId;
    private StorageReference stRef;
    private FirebaseFirestore firestore;
    private UserModel user1;

    private boolean multiSelect = false;
    private ArrayList<AudioModel> selectedItems = new ArrayList<AudioModel>();
    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            menu.add("Delete");
//                mode.setTitle(selectedItems.size() + " items selected");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Log.d(TAG, "onActionItemClicked: ");
            if (item.getTitle().equals("Delete")){
                deletion(selectedItems);
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };

    public VoicesViewHolder(@NonNull View itemView) {
        super(itemView);
        play = itemView.findViewById(R.id.profile_play);
        title = itemView.findViewById(R.id.profile_title);
        profilePic = itemView.findViewById(R.id.profile_image);
        username = itemView.findViewById(R.id.profile_username);
        timeStamp = itemView.findViewById(R.id.time_stamp);
        handler = new Handler();
        durationSb = itemView.findViewById(R.id.profile_seekbar);
        commentMic = itemView.findViewById(R.id.profile_mic);
        color = itemView.findViewById(R.id.color);
        content = itemView.findViewById(R.id.content);
        duratonSeek();

    }

    public void onBind(final AudioModel audio) {
        title.setText(audio.getTitle());
        audioId = audio.getAudioId();
        pathId = audio.getPathId();

        long time = Long.parseLong(audioId);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        timeStamp.setText(formatter.format(calendar.getTime()));

        user1 = CurrentUserManager.getCurrentUser();
        if (user1 != null) {
            String currentUserName = user1.getUserName();
            userid = user1.getUserId();
            getColor();
            username.setText(currentUserName);
            Picasso.get().load(user1.getImageUrl()).fit().into(profilePic);


        }


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stRef = FirebaseStorage.getInstance().getReference(user1.getUserId()).child(
                        "audio").child(audioId);
                Log.d(TAG, "onClick: " + audioId);
                stRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (mPlay) {

                            play.setImageResource(R.drawable.ic_stopp);
                            startPlaying(itemView.getContext(), uri);
                            changeSeekBar();

                        } else {
                            play.setImageResource(R.drawable.play_button);
                            stopPlaying();

                        }
                        mPlay = !mPlay;
                    }
                });
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((AppCompatActivity) v.getContext()).startActionMode(actionModeCallbacks);
                selectItem(audio);
                return true;
            }
        });


        commentMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCommentActivity();

            }
        });

    }

    private void startPlaying(Context context, Uri audio) {
        mediaPlayer = new MediaPlayer();
        Log.d(VoicesViewHolder.class.getName(), "startPlaying: " + audio);
        try {
            mediaPlayer.setDataSource(context, audio);
            // mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    durationSb.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    changeSeekBar();
                }
            });

            mediaPlayer.prepareAsync();
            Log.d("VIEW HOLDER", String.valueOf(mediaPlayer.isPlaying()));
        } catch (IOException e) {
            Log.e("VIEW HOLDER", "prepare() failed");
        }
    }

    private void stopPlaying() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void goToCommentActivity() {
        Intent commentActivityIntent = new Intent(itemView.getContext(), CommentActivity.class);
        commentActivityIntent.putExtra("userid", userid);
        commentActivityIntent.putExtra("audioid", audioId);
        commentActivityIntent.putExtra("pathid", pathId);
        itemView.getContext().startActivity(commentActivityIntent);

    }

    private void changeSeekBar() {
        if (mediaPlayer != null) {
            durationSb.setProgress(mediaPlayer.getCurrentPosition());
            if (mediaPlayer.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        changeSeekBar();
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        }
    }

    private void duratonSeek() {
        durationSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.seekTo(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void getColor() {
        switch (userid) {
            case "Y0VTyMlb5rfjSnA9zic3QMIJ43y1":
                color.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.purple));
                /*Picasso.get().load(R.drawable.purple).fit().into(color);*/


                break;
            case "mPm3onnfwCXlAoNgyj5i5tcHeND3":
                color.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.pink));
//                Picasso.get().load(R.drawable.pink).fit().into(color);
                break;

            case "yUr700mkPmUAPvrdp1Z4BuVT9GO2":
                color.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.green));
//                Picasso.get().load(R.drawable.green).fit().into(color);
                break;
        }
    }

    void selectItem(AudioModel audio) {
        if (multiSelect) {
            if (selectedItems.contains(audio)) {
                selectedItems.remove(audio);
                title.setText("Removed");
                content.setCardElevation(10);
            } else {
                selectedItems.add(audio);
                title.setText("Added");
                content.setCardElevation(0);
            }
        }
    }

    public void deletion(final List<AudioModel> audio) {
        if (user1 != null && audio.size() == 1) {
            stRef = FirebaseStorage.getInstance().getReference(user1.getUserId()).child("audio").child(audio.get(0).getAudioId());
            stRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: note deleted" + audio.get(0).getAudioId());
                }
            });
            firestore = FirebaseFirestore.getInstance();
            firestore.collection("users").document(user1.getUserId())
                    .collection("audio").document(audio.get(0).getAudioId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: fireStore Deletion");
                }
            });
        }
    }
}





