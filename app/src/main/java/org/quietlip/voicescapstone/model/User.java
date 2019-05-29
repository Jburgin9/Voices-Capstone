package org.quietlip.voicescapstone.model;

public class User {
    private String username;
    private String imageUrl;
    private String userID;

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public User(String username, String userID) {
        this.username = username;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }
}

