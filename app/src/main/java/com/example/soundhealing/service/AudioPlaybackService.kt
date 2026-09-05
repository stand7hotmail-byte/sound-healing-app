package com.example.soundhealing.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.soundhealing.R
import com.example.soundhealing.audio.AudioEngine
import com.example.soundhealing.domain.BrainwaveType
import com.example.soundhealing.domain.NatureSound
import com.example.soundhealing.domain.SolfeggioFrequency
import com.example.soundhealing.domain.SoundType

class AudioPlaybackService : Service() {
    companion object {
        const val TAG = "AudioPlayback"
        private const val CHANNEL_ID = "sound_healing_playback"
        private const val NOTIFICATION_ID = 1001
        private const val SERVICE_TYPE = ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK

        const val ACTION_PLAY = "com.example.soundhealing.action.PLAY"
        const val ACTION_STOP = "com.example.soundhealing.action.STOP"
        const val ACTION_UPDATE_VOLUME = "com.example.soundhealing.action.UPDATE_VOLUME"

        const val EXTRA_KIND = "extra_kind"
        const val EXTRA_ID = "extra_id"
        const val EXTRA_VOLUME = "extra_volume"

        fun start(context: Context, type: SoundType) {
            val (kind, id) = serialize(type)
            val i = Intent(context, AudioPlaybackService::class.java).apply {
                action = ACTION_PLAY
                putExtra(EXTRA_KIND, kind)
                putExtra(EXTRA_ID, id)
            }
            ContextCompat.startForegroundService(context, i)
        }

        fun stop(context: Context) {
            val i = Intent(context, AudioPlaybackService::class.java).apply { action = ACTION_STOP }
            context.startService(i)
        }

        fun updateVolume(context: Context, volume: Float) {
            val i = Intent(context, AudioPlaybackService::class.java).apply {
                action = ACTION_UPDATE_VOLUME
                putExtra(EXTRA_VOLUME, volume)
            }
            context.startService(i)
        }

        private fun serialize(type: SoundType): Pair<String, String> = when (type) {
            is SoundType.Solfeggio -> "SOLFEGGIO" to type.frequency.id.toString()
            is SoundType.Nature -> "NATURE" to type.sound.name
            is SoundType.Brainwave -> "BRAINWAVE" to type.type.name
        }

        private fun deserialize(kind: String?, id: String?): SoundType? {
            if (kind == null || id == null) return null
            return when (kind) {
                "SOLFEGGIO" -> SolfeggioFrequency.ALL.firstOrNull { it.id == id.toIntOrNull() }
                    ?.let { SoundType.Solfeggio(it) }
                "NATURE" -> NatureSound.values().firstOrNull { it.name == id }
                    ?.let { SoundType.Nature(it) }
                "BRAINWAVE" -> BrainwaveType.values().firstOrNull { it.name == id }
                    ?.let { SoundType.Brainwave(it) }
                else -> null
            }
        }

        fun displayName(type: SoundType): String = when (type) {
            is SoundType.Solfeggio -> type.frequency.name
            is SoundType.Nature -> type.sound.name.replace("_", " ").lowercase()
            is SoundType.Brainwave -> type.type.name.lowercase()
        }
    }

    private val engine = AudioEngine()
    private var currentType: SoundType? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand action=${intent?.action}")
        when (intent?.action) {
            ACTION_PLAY -> {
                val type = deserialize(intent.getStringExtra(EXTRA_KIND), intent.getStringExtra(EXTRA_ID))
                Log.d(TAG, "play type=$type")
                if (type != null) {
                    currentType = type
                    android.util.Log.d(TAG, "calling startForeground"); startForeground(NOTIFICATION_ID, buildNotification(type))
                    android.util.Log.d(TAG, "engine started"); engine.start(type)
                }
            }
            ACTION_UPDATE_VOLUME -> {
                engine.setVolume(intent.getFloatExtra(EXTRA_VOLUME, 0.5f))
            }
            ACTION_STOP -> {
                Log.d(TAG, "stop")
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun buildNotification(type: SoundType): Notification {
        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, AudioPlaybackService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("サウンドヒーリング")
            .setContentText(displayName(type))
            .setOngoing(true)
            .setContentIntent(stopIntent)
            .addAction(R.drawable.ic_stop, "停止", stopIntent)
            .build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "サウンドヒーリング再生", NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        engine.stop()
        super.onDestroy()
    }
}
