package org.quietlip.voicescapstone.models;

import android.net.Uri;

public class UserModel {
    private String username;
    private Uri imageUrl;
    private String userID;

    public String getUsername() {
        return username;
    }

    public Uri getImageUrl() {
        return imageUrl;
    }

    public UserModel(String username, String userID) {
        this.username = username;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }
}
