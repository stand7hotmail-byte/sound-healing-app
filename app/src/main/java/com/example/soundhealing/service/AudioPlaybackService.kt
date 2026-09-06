package com.example.soundhealing.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.soundhealing.Constants
import com.example.soundhealing.audio.AudioEngine
import com.example.soundhealing.domain.SolfeggioFrequency

class AudioPlaybackService : Service() {
    companion object {
        private var engine: AudioEngine? = null

        fun start(context: Context, freq: Double) {
            val i = Intent(context, AudioPlaybackService::class.java).apply {
                action = Constants.ACTION_PLAY
                putExtra(Constants.EXTRA_VOLUME, 0.5f)
            }
            ContextCompat.startForegroundService(context, i)
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
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Constants.ACTION_PLAY -> {
                val freq = 440.0
                startForeground(Constants.NOTIFICATION_ID, buildNotification())
                engine?.stop()
                engine = AudioEngine()
                engine?.startTone(freq)
                Log.d(Constants.TAG, "playing freq=$freq")
            }
            Constants.ACTION_UPDATE_VOLUME -> {
                val volume = intent.getFloatExtra(Constants.EXTRA_VOLUME, 0.5f)
                engine?.setVolume(volume)
            }
            Constants.ACTION_STOP -> {
                Log.d(Constants.TAG, "stop")
                engine?.stop()
                engine = null
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun buildNotification(): Notification {
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
            .setContentText("440Hz純音再生中")
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(stopIntent)
            .build()
    }
}
