package com.example.soundhealing.audio

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
