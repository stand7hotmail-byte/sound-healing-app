package com.example.soundhealing

import android.content.pm.ServiceInfo

object Constants {
    const val TAG = "AudioPlayback"
    const val CHANNEL_ID = "sound_healing_playback"
    const val NOTIFICATION_ID = 1001
    val SERVICE_TYPE = ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK

    const val ACTION_PLAY = "com.example.soundhealing.action.PLAY"
    const val ACTION_STOP = "com.example.soundhealing.action.STOP"
    const val ACTION_UPDATE_VOLUME = "com.example.soundhealing.action.UPDATE_VOLUME"

    const val EXTRA_KIND = "extra_kind"
    const val EXTRA_ID = "extra_id"
    const val EXTRA_VOLUME = "extra_volume"

    fun getDisplayName(type: com.example.soundhealing.domain.SoundType): String = when (type) {
        is com.example.soundhealing.domain.SoundType.Solfeggio -> type.frequency.name
        is com.example.soundhealing.domain.SoundType.Nature -> type.sound.name
        is com.example.soundhealing.domain.SoundType.Brainwave -> type.type.label
    }
}
