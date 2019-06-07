package org.quietlip.voicescapstone.models;

public class AudioModel {
    private String uri;
    private String title;



    private UserModel user;


    public AudioModel(String uri, String audioTitle, UserModel user) {
        this.uri = uri;
        this.title = audioTitle;
        this.user = user;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }


}