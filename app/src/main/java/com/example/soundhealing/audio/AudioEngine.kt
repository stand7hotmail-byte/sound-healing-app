package com.example.soundhealing.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import com.example.soundhealing.domain.RandomSession
import com.example.soundhealing.domain.SoundType
import com.example.soundhealing.domain.BrainwaveType
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
    
    // For RandomSession (multi-frequency)
    fun start(session: RandomSession) {
        Log.d(TAG, "start session:${session.frequency.name} fade=${session.fadeInSeconds}s dur=${session.durationSeconds}s")
        stop()
        frequency = session.frequency.frequency.toDouble()
        
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
            Log.e(TAG, "AudioTrack initialization failed!")
            audioTrack?.release()
            audioTrack = null
            return
        }
        
        audioTrack?.play()
        playing.set(true)
        
        Thread {
            val buffer = ShortArray(1024)
            var phase = 0.0
            val fadeInSamples = (session.fadeInSeconds * sampleRate).toInt()
            val totalSamples = (session.durationSeconds * sampleRate).toInt()
            var sampleCount = 0
            
            while (playing.get() && sampleCount < totalSamples) {
                val fadeInNorm = if (sampleCount < fadeInSamples) {
                    sampleCount.toFloat() / fadeInSamples
                } else {
                    1.0f
                }
                val fadeOutNorm = if (session.durationSeconds > 0 && 
                    sampleCount > totalSamples - fadeInSamples) {
                    (totalSamples - sampleCount).toFloat() / fadeInSamples
                } else {
                    1.0f
                }
                val envelope = fadeInNorm * fadeOutNorm
                
                for (i in buffer.indices) {
                    phase += 2 * PI * frequency / sampleRate
                    if (phase > 2 * PI) phase -= 2 * PI
                    buffer[i] = (sin(phase) * envelope * volume * Short.MAX_VALUE).toInt().toShort()
                }
                audioTrack?.write(buffer, 0, buffer.size)
                sampleCount += buffer.size
            }
            if (playing.get()) stop()
        }.start()
    }
    
    // Simple version for test
    fun startSimple(frequencyHz: Double = 440.0) {
        Log.d(TAG, "startSimple: frequency=$frequencyHz")
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
            Log.e(TAG, "AudioTrack initialization failed!")
            audioTrack?.release()
            audioTrack = null
            return
        }
        
        audioTrack?.play()
        playing.set(true)
        
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
        }.start()
    }
    
    fun stop() {
        Log.d(TAG, "stop()")
        playing.set(false)
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }
    
    fun isPlaying(): Boolean = playing.get()
    
    fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0f, 1f)
    }
}
