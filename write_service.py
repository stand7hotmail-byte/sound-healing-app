import base64
from pathlib import Path

base = Path('C:/Users/stand/Documents/hermes_project/sound-healing-app')

# Update AudioPlaybackService with startWithDelay
svc = '''package com.example.soundhealing.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.soundhealing.R
import com.example.soundhealing.audio.AudioEngine
import com.example.soundhealing.domain.BrainwaveType
import com.example.soundhealing.domain.NatureSound
import com.example.soundhealing.domain.RandomSession
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
        const val EXTRA_SESSION = "extra_session"
        const val EXTRA_FADE_IN = "extra_fade_in"
        const val EXTRA_FADE_OUT = "extra_fade_out"
        const val EXTRA_DURATION = "extra_duration"
        const val EXTRA_START_DELAY = "extra_start_delay"

        fun start(context: Context, type: SoundType) {
            val (kind, id) = serialize(type)
            val i = Intent(context, AudioPlaybackService::class.java).apply {
                action = ACTION_PLAY
                putExtra(EXTRA_KIND, kind)
                putExtra(EXTRA_ID, id)
            }
            ContextCompat.startForegroundService(context, i)
        }

        fun startWithDelay(context: Context, session: RandomSession, delayMs: Long) {
            val i = Intent(context, AudioPlaybackService::class.java).apply {
                action = ACTION_PLAY
                putExtra(EXTRA_SESSION, session)
            }
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                ContextCompat.startForegroundService(context, i)
            }, delayMs)
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
    private val handler = Handler(Looper.getMainLooper())
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
                val session = intent.getParcelableExtra<RandomSession>(EXTRA_SESSION)
                if (session != null) {
                    Log.d(TAG, "play session: ${session.frequency.name}")
                    currentType = session.soundType
                    startForeground(NOTIFICATION_ID, buildNotification(session))
                    engine.start(session)
                } else {
                    val kind = intent.getStringExtra(EXTRA_KIND)
                    val id = intent.getStringExtra(EXTRA_ID)
                    val type = deserialize(kind, id)
                    if (type != null) {
                        currentType = type
                        startForeground(NOTIFICATION_ID, buildNotification(type))
                        engine.startSimple(type)
                    }
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

    private fun buildNotification(session: RandomSession): Notification {
        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, AudioPlaybackService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("サウンドヒーリング")
            .setContentText("${session.frequency.name} (${session.fadeInSeconds}s fadeIn / ${session.fadeOutSeconds}s fadeOut)")
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(stopIntent)
            .addAction(R.drawable.ic_stop, "停止", stopIntent)
            .build()
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
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(stopIntent)
            .addAction(R.drawable.ic_stop, "停止", stopIntent)
            .build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "サウンドヒーリング再生", NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        handler.removeCallbacksAndMessages(null)
        engine.stop()
        super.onDestroy()
    }
}
'''

encoded = base64.b64encode(svc.encode('utf-8')).decode()
b64_path = base / 'tmp_svc.b64'
b64_path.write_text(encoded)

content = base64.b64decode(encoded).decode()
p = base / 'app/src/main/java/com/example/soundhealing/service/AudioPlaybackService.kt'
p.write_bytes(content.encode())
b = p.read_bytes()
print(f'AudioPlaybackService.kt: eq={b.count(b"=")} bytes={len(b)}')
b64_path.unlink()
