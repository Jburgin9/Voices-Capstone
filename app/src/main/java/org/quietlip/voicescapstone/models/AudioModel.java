package org.quietlip.voicescapstone.models;

import java.util.Comparator;

public class AudioModel implements Comparator<AudioModel>, Comparable<AudioModel> {
    private String uri;
    private String title;
    private String audioId;
    private String pathId;


    private UserModel user;


    public AudioModel(String uri, String audioTitle, UserModel user, String audioId, String pathId) {
        this.uri = uri;
        this.title = audioTitle;
        this.user = user;
        this.audioId = audioId;
        this.pathId = pathId;
    }

    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
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

    @Override
    public int compare(AudioModel o1, AudioModel o2) {
        return o2.audioId.compareTo(o1.audioId);
    }

    @Override
    public int compareTo(AudioModel o) {
        return o.audioId.compareTo(this.audioId);
    }
}