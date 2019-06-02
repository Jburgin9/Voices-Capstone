package org.quietlip.voicescapstone.models;

public class UserModel {
    private String username;
    private String imageUrl;
    private String userID;
    private String aboutMe;

    public UserModel(String username, String userID, String imageUrl) {
        this.username = username;
        this.userID = userID;
        this.imageUrl = imageUrl;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserID() {
        return userID;
    }
}
