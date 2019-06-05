package org.quietlip.voicescapstone.models;

import android.net.Uri;

import java.util.List;

public class UserModel {
    private String userId;
    private String userName;
    private String imageUrl;
    private String aboutMe;
    private List<AudioModel> audioList;
    private List<UserModel> friendsList;

   public UserModel(String userName , String userId, String imageUrl, String aboutMe) {
        this.userName = userName;
        this.userId = userId;
       this.imageUrl = imageUrl;
       this.aboutMe = aboutMe;
    }

    public UserModel(String userId, String userName, String imageUrl , String aboutMe, List<AudioModel> audioList, List<UserModel> friendsList) {
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.aboutMe = aboutMe;
        this.audioList = audioList;
        this.friendsList = friendsList;
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

    public List<UserModel> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<UserModel> friendsList) {
        this.friendsList = friendsList;
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

    public List<AudioModel> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<AudioModel> audioList) {
        this.audioList = audioList;
    }
}
