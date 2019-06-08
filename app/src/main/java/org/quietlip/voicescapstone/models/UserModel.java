package org.quietlip.voicescapstone.models;

import android.net.Uri;

import java.util.List;

public class UserModel {
    private String userId;
    private String userName;
    private String imageUrl;
    private String aboutMe;



   public UserModel(String userName , String userId, String imageUrl, String aboutMe) {
        this.userName = userName;
        this.userId = userId;
       this.imageUrl = imageUrl;
       this.aboutMe = aboutMe;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
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

}
