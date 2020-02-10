package org.quietlip.voicescapstone.utilis

import com.google.firebase.storage.StorageReference
import org.quietlip.voicescapstone.models.AudioModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class AudioViewModel(private val audioModel: AudioModel){
    init {
        val storeRef : StorageReference
    }

    //TODO: convert to java code in audiomodel file
    fun getUserName(): String{
        return audioModel.user.userName
    }

    fun getUserProfileImageUrl(): String{
        return audioModel.user.imageUrl
    }

    fun getAudioTitle(): String{
        return audioModel.title
    }

    fun getAudioUrl(): String{
        return audioModel.uri
    }

    fun getTimeStamp(): String{
        val formatter : DateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm")
        val calendar : Calendar  = Calendar.getInstance()
        calendar.setTimeInMillis(audioModel.audioId.toLong())
        return formatter.format(calendar.getTime())
    }

//    fun getTimeCreated(): String{
//
//    }
}
