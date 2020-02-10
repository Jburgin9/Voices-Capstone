package org.quietlip.voicescapstone.models;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.List;


public class UserModel{
    private String aboutMe;
    private String imageUrl;
    private String userId;
    private String userName;

   public UserModel(String aboutMe, String imageUrl, String userId, String userName) {
        this.aboutMe = aboutMe;
        this.imageUrl = imageUrl;
       this.userId = userId;
       this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        if(userId != null) {
            return userId;
        } else {
            return "null userId";
        }
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    @NonNull
    @Override
    public String toString() {
        return "About me: " + aboutMe +
                "Image Url: " + imageUrl +
                "User Id: " + userId +
                "Username: " + userName;
    }

}
