import base64
from pathlib import Path

base = Path('C:/Users/stand/Documents/hermes_project/sound-healing-app')

# AudioEngine.kt with fade support
audio_engine = '''package com.example.soundhealing.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.example.soundhealing.domain.BrainwaveType
import com.example.soundhealing.domain.SoundType
import com.example.soundhealing.domain.RandomSession
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
    private var volume = 0.6f
    private var frequency = 440.0
    
    fun start(session: RandomSession) {
        android.util.Log.d(TAG, "start session: ${session.frequency.name} fade=${session.fadeInSeconds}s dur=${session.durationSeconds}s")
        stop()
        frequency = session.frequency.frequency.toDouble()
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize * 2,
            AudioTrack.MODE_STREAM
        )
        audioTrack?.play()
        playing.set(true)
        
        Thread {
            val buffer = ShortArray(1024)
            var phase = 0.0
            val fadeInSamples = (session.fadeInSeconds * sampleRate).toInt()
            val totalSamples = (session.durationSeconds * sampleRate).toInt()
            var sampleCount = 0
            
            while (playing.get() && sampleCount < totalSamples) {
                val fadeInNorm = (sampleCount.toDouble() / fadeInSamples).coerceIn(0.0, 1.0)
                for (i in buffer.indices) {
                    phase += 2 * PI * frequency / sampleRate
                    if (phase > 2 * PI) phase -= 2 * PI
                    buffer[i] = (sin(phase) * Short.MAX_VALUE * volume * fadeInNorm).toInt().toShort()
                }
                audioTrack?.write(buffer, 0, buffer.size)
                sampleCount += buffer.size
            }
            
            if (playing.get()) {
                val fadeOutSamples = (session.fadeOutSeconds * sampleRate).toInt()
                var fadeCount = 0
                while (playing.get() && fadeCount < fadeOutSamples) {
                    val fadeOutNorm = 1.0 - (fadeCount.toDouble() / fadeOutSamples)
                    for (i in buffer.indices) {
                        phase += 2 * PI * frequency / sampleRate
                        if (phase > 2 * PI) phase -= 2 * PI
                        buffer[i] = (sin(phase) * Short.MAX_VALUE * volume * fadeOutNorm).toInt().toShort()
                    }
                    audioTrack?.write(buffer, 0, buffer.size)
                    fadeCount += buffer.size
                }
            }
            stop()
        }.start()
    }
    
    fun startSimple(soundType: SoundType) {
        android.util.Log.d(TAG, "startSimple soundType=$soundType")
        stop()
        frequency = getFrequency(soundType)
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize * 2,
            AudioTrack.MODE_STREAM
        )
        audioTrack?.play()
        playing.set(true)
        generateTone()
    }
    
    private fun getFrequency(soundType: SoundType): Double {
        return when (soundType) {
            is SoundType.Solfeggio -> soundType.frequency.frequency.toDouble()
            is SoundType.Nature -> 200.0
            is SoundType.Brainwave -> {
                val t = soundType.type
                when (t) {
                    BrainwaveType.DELTA -> 2.0
                    BrainwaveType.THETA -> 6.0
                    BrainwaveType.ALPHA -> 10.0
                    BrainwaveType.BETA -> 20.0
                }
            }
        }
    }
    
    private fun generateTone() {
        Thread {
            val buffer = ShortArray(1024)
            var phase = 0.0
            while (playing.get()) {
                for (i in buffer.indices) {
                    phase += 2 * PI * frequency / sampleRate
                    if (phase > 2 * PI) phase -= 2 * PI
                    buffer[i] = (sin(phase) * 0.3 * Short.MAX_VALUE * volume).toInt().toShort()
                }
                audioTrack?.write(buffer, 0, buffer.size)
            }
        }.start()
    }
    
    fun stop() {
        android.util.Log.d(TAG, "stop")
        playing.set(false)
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }
    
    fun setVolume(v: Float) {
        volume = v
    }
}
'''

# Base64 encode
encoded = base64.b64encode(audio_engine.encode('utf-8')).decode('ascii')
# Write base64 to temp file
b64_path = base / 'tmp_audio.b64'
b64_path.write_text(encoded)
print(f'Encoded length: {len(encoded)}')
print(f'Original eq count: {audio_engine.count("=")}')
