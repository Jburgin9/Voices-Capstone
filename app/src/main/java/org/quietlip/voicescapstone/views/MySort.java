package org.quietlip.voicescapstone.views;

import org.quietlip.voicescapstone.models.AudioModel;

import java.util.Comparator;


class MySort implements Comparator<AudioModel> {

    AudioModel audioModel;

    @Override
    public int compare(AudioModel o1, AudioModel o2) {
        return o1.getAudioId().compareTo(o2.getAudioId());
    }



}
