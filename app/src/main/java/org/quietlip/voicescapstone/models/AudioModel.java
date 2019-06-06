package org.quietlip.voicescapstone.models;

public class AudioModel {
    private String uri;
    private String title;
    private String userId;


    public AudioModel(String uri, String audioTitle, String userId) {
        this.uri = uri;
        this.title = audioTitle;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}