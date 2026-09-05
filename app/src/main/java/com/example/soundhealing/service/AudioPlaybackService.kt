package com.example.soundhealing.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.soundhealing.Constants
import com.example.soundhealing.audio.AudioEngine
import com.example.soundhealing.domain.SoundType

class AudioPlaybackService : Service() {
    companion object {
        private val engine = AudioEngine()
        private val handler = Handler(Looper.getMainLooper())

        fun start(context: Context, type: SoundType) {
            val (kind, id) = serialize(type)
            val i = Intent(context, AudioPlaybackService::class.java).apply {
                action = Constants.ACTION_PLAY
                putExtra(Constants.EXTRA_KIND, kind)
                putExtra(Constants.EXTRA_ID, id)
            }
            ContextCompat.startForegroundService(context, i)
        }

        fun startWithDelay(context: Context, type: SoundType, delayMs: Long) {
        Handler(Looper.getMainLooper()).postDelayed({ start(context, type) }, delayMs)
    }

    fun stop(context: Context) {
            val i = Intent(context, AudioPlaybackService::class.java).apply {
                action = Constants.ACTION_STOP
            }
            context.startService(i)
        }

        fun updateVolume(context: Context, volume: Float) {
            val i = Intent(context, AudioPlaybackService::class.java).apply {
                action = Constants.ACTION_UPDATE_VOLUME
                putExtra(Constants.EXTRA_VOLUME, volume)
            }
            context.startService(i)
        }

        private fun serialize(type: SoundType): Pair<String, String> = when (type) {
            is SoundType.Solfeggio -> "SOLFEGGIO" to type.frequency.id.toString()
            is SoundType.Nature -> "NATURE" to type.sound.name
            is SoundType.Brainwave -> "BRAINWAVE" to type.type.name
        }

        fun displayName(type: SoundType): String = Constants.getDisplayName(type)
    }

    private var currentType: SoundType? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(Constants.TAG, "onCreate")
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(Constants.TAG, "onStartCommand action=${intent?.action}")
        when (intent?.action) {
            Constants.ACTION_PLAY -> {
                val kind = intent.getStringExtra(Constants.EXTRA_KIND)
                val id = intent.getStringExtra(Constants.EXTRA_ID)
                val type = deserialize(kind, id)
                if (type != null) {
                    currentType = type
                    startForeground(Constants.NOTIFICATION_ID, buildNotification(type))
                    engine.startSimple(type)
                }
            }
            Constants.ACTION_UPDATE_VOLUME -> {
                engine.setVolume(intent.getFloatExtra(Constants.EXTRA_VOLUME, 0.5f))
            }
            Constants.ACTION_STOP -> {
                Log.d(Constants.TAG, "stop")
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun buildNotification(type: SoundType): Notification {
        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, AudioPlaybackService::class.java).apply {
                action = Constants.ACTION_STOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, Constants.CHANNEL_ID)
            .setSmallIcon(com.example.soundhealing.R.mipmap.ic_launcher)
            .setContentTitle("サウンドヒーリング")
            .setContentText(Constants.getDisplayName(type))
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(stopIntent)
            .addAction(com.example.soundhealing.R.drawable.ic_stop, "停止", stopIntent)
            .build()
    }

    private fun createChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                "サウンドヒーリング再生",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun deserialize(kind: String?, id: String?): SoundType? {
        if (kind == null || id == null) return null
        return when (kind) {
            "SOLFEGGIO" -> com.example.soundhealing.domain.SolfeggioFrequency.ALL
                .firstOrNull { it.id == id.toIntOrNull() }
                ?.let { SoundType.Solfeggio(it) }
            "NATURE" -> com.example.soundhealing.domain.NatureSound.ALL
                .firstOrNull { it.name == id }
                ?.let { SoundType.Nature(it) }
            "BRAINWAVE" -> com.example.soundhealing.domain.BrainwaveType.values()
                .firstOrNull { it.name == id }
                ?.let { SoundType.Brainwave(it) }
            else -> null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d(Constants.TAG, "onDestroy")
        handler.removeCallbacksAndMessages(null)
        engine.stop()
        super.onDestroy()
    }
}
