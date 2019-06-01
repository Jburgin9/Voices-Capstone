package org.quietlip.voicescapstone.models;

import java.util.List;

public class UserModel {
    private String userId;
    private String userName;
    private String profilePicture;
    private String aboutMe;
    private List<AudioModel> audioList;
    private List<UserModel> friendsList;


    public UserModel(String userId, String userName, String profilePicture, String aboutMe, List<AudioModel> audioList, List<UserModel> friendsList) {
        this.userId = userId;
        this.userName = userName;
        this.profilePicture = profilePicture;
        this.aboutMe = aboutMe;
        this.audioList = audioList;
        this.friendsList = friendsList;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
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
