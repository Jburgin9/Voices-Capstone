package org.quietlip.voicescapstone.models;

public class AudioModel {
    private String uri;
    private String title;

    public AudioModel(String uri, String audioTitle) {
        this.uri = uri;
        this.title = audioTitle;
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
}
