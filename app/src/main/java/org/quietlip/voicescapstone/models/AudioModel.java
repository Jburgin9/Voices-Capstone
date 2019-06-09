package org.quietlip.voicescapstone.models;

public class AudioModel {
    private String uri;
    private String title;
    private String audioId;


    private UserModel user;


    public AudioModel(String uri, String audioTitle, UserModel user, String audioId) {
        this.uri = uri;
        this.title = audioTitle;
        this.user = user;
        this.audioId = audioId;
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

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }
}