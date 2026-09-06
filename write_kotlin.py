from pathlib import Path

base = Path('C:/Users/stand/Documents/hermes_project/sound-healing-app')

# ============ AudioEngine.kt ============
ae = '''package com.example.soundhealing.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.sin
import kotlin.math.PI

class AudioEngine {
    companion object {
        const val TAG = "AudioEngine"
    }

    private var audioTrack: AudioTrack? = null
    private val playing = AtomicBoolean(false)
    private var sampleRate = 44100
    private var volume = 0.5f
    private var frequency = 440.0

    fun startTone(frequencyHz: Double) {
        Log.d(TAG, "startTone: $frequencyHz Hz")
        stop()
        frequency = frequencyHz

        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = minBufferSize * 2

        audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
            AudioTrack.MODE_STREAM
        )

        if (audioTrack?.state != AudioTrack.STATE_INITIALIZED) {
            Log.e(TAG, "AudioTrack init failed")
            audioTrack?.release()
            audioTrack = null
            return
        }

        audioTrack?.play()
        playing.set(true)
        Log.d(TAG, "AudioTrack playing started")

        Thread {
            val buffer = ShortArray(1024)
            var phase = 0.0
            while (playing.get()) {
                for (i in buffer.indices) {
                    phase += 2 * PI * frequency / sampleRate
                    if (phase > 2 * PI) phase -= 2 * PI
                    buffer[i] = (sin(phase) * volume * Short.MAX_VALUE).toInt().toShort()
                }
                audioTrack?.write(buffer, 0, buffer.size)
            }
            Log.d(TAG, "Tone generation stopped")
        }.start()
    }

    fun stop() {
        Log.d(TAG, "stop()")
        playing.set(false)
        try {
            audioTrack?.stop()
        } catch (e: Exception) {
            Log.w(TAG, "stop() exception: ${e.message}")
        }
        audioTrack?.release()
        audioTrack = null
        Log.d(TAG, "AudioTrack released")
    }

    fun isPlaying(): Boolean = playing.get()

    fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0f, 1f)
        Log.d(TAG, "setVolume: $volume")
    }
}
'''

# ============ SoundHealingViewModel.kt ============
vm = '''package com.example.soundhealing.viewmodel

import android.app.Application
import android.widget.Toast
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundhealing.audio.AudioEngine
import com.example.soundhealing.domain.SoundType
import com.example.soundhealing.service.AudioPlaybackService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SoundHealingViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "ViewModel"
    }

    data class UiState(
        val playing: SoundType? = null,
        val volume: Float = 0.5f,
        val timerSeconds: Int = 0,
        val timerRunning: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var audioEngine: AudioEngine? = null

    fun playSound(soundType: SoundType) {
        Log.d(TAG, "playSound type=$soundType")
        AudioPlaybackService.start(getApplication(), soundType)
        _uiState.value = _uiState.value.copy(playing = soundType)
    }

    fun stopSound(soundType: SoundType) {
        Log.d(TAG, "stopSound type=$soundType")
        AudioPlaybackService.stop(getApplication())
        if (_uiState.value.playing == soundType) {
            _uiState.value = _uiState.value.copy(playing = null)
        }
    }

    fun stopAll() {
        Log.d(TAG, "stopAll")
        AudioPlaybackService.stop(getApplication())
        _uiState.value = _uiState.value.copy(playing = null, timerRunning = false)
    }

    fun setVolume(volume: Float) {
        Log.d(TAG, "setVolume $volume")
        AudioPlaybackService.updateVolume(getApplication(), volume)
        _uiState.value = _uiState.value.copy(volume = volume)
    }

    fun startTimer(seconds: Int) {
        stopAll()
        _uiState.value = _uiState.value.copy(timerSeconds = seconds, timerRunning = true)
        timerJob = viewModelScope.launch {
            for (i in seconds downTo 1) {
                kotlinx.coroutines.delay(1000L)
                _uiState.value = _uiState.value.copy(timerSeconds = i)
            }
            stopAll()
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(timerRunning = false, timerSeconds = 0)
    }

    fun testTone(frequency: Double) {
        Log.d(TAG, "testTone: $frequency Hz")
        Toast.makeText(getApplication(), "testTone $frequency Hz", Toast.LENGTH_SHORT).show()
        audioEngine = AudioEngine()
        audioEngine?.startTone(frequency)
    }

    fun stopTestTone() {
        Log.d(TAG, "stopTestTone")
        Toast.makeText(getApplication(), "stopTestTone", Toast.LENGTH_SHORT).show()
        audioEngine?.stop()
        audioEngine = null
    }
}
'''

# ============ MainScreen.kt ============
ms = '''package com.example.soundhealing.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import com.example.soundhealing.domain.SolfeggioFrequency
import com.example.soundhealing.domain.SoundType
import com.example.soundhealing.ui.component.SoundCard
import com.example.soundhealing.ui.component.VolumeSlider
import com.example.soundhealing.viewmodel.SoundHealingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: SoundHealingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var clickCount by remember { mutableStateOf(0) }
    var lastClick by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopAll() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "サウンドヒーリング",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (clickCount > 0) {
                Text(text = "クリック数: $clickCount, 最後: $lastClick")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        clickCount++
                        lastClick = "再生"
                        viewModel.testTone(440.0)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("テスト音再生")
                }
                Button(
                    onClick = {
                        clickCount++
                        lastClick = "停止"
                        viewModel.stopTestTone()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("テスト音停止")
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SolfeggioFrequency.ALL) { freq ->
                    SoundCard(
                        soundType = SoundType.Solfeggio(freq),
                        isSelected = false,
                        onClick = {
                            clickCount++
                            lastClick = "カード: ${freq.name}"
                            viewModel.playSound(SoundType.Solfeggio(freq))
                        }
                    )
                }
            }

            VolumeSlider(
                value = uiState.volume,
                onValueChange = { viewModel.setVolume(it) }
            )
        }
    }
}
'''

# ============ AudioPlaybackService.kt ============
svc = '''package com.example.soundhealing.service

import android.app.Notification
import android.app.NotificationChannel
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
import com.example.soundhealing.domain.SoundType

class AudioPlaybackService : Service() {
    companion object {
        private var engine: AudioEngine? = null

        fun start(context: Context, type: SoundType) {
            val i = Intent(context, AudioPlaybackService::class.java).apply {
                action = Constants.ACTION_PLAY
                putExtra(Constants.EXTRA_TYPE, type.name)
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
                val type = intent.getStringExtra(Constants.EXTRA_TYPE)?.let { SoundType.valueOf(it) }
                if (type != null) {
                    val freq = when (type) {
                        is SoundType.Solfeggio -> type.frequency.frequency
                        else -> 440.0
                    }
                    startForeground(Constants.NOTIFICATION_ID, buildNotification(type))
                    engine?.stop()
                    engine = AudioEngine()
                    engine?.startTone(freq)
                    Log.d(Constants.TAG, "playing: $type freq=$freq")
                }
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
}
'''

# Write using terminal Python to avoid write_file bug
files = [
    ('app/src/main/java/com/example/soundhealing/audio/AudioEngine.kt', ae),
    ('app/src/main/java/com/example/soundhealing/viewmodel/SoundHealingViewModel.kt', vm),
    ('app/src/main/java/com/example/soundhealing/ui/screen/MainScreen.kt', ms),
    ('app/src/main/java/com/example/soundhealing/service/AudioPlaybackService.kt', svc),
]

for rel, content in files:
    path = base / rel
    path.write_text(content, encoding='utf-8', newline='\n')
    print(f'Written: {rel}')
    # Verify = is present
    text = path.read_text()
    eq_count = text.count('=')
    print(f'  = count: {eq_count}')
