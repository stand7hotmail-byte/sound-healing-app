package com.example.soundhealing.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.example.soundhealing.domain.BrainwaveType
import com.example.soundhealing.domain.NatureSound
import com.example.soundhealing.domain.SolfeggioFrequency
import com.example.soundhealing.domain.SoundType
import java.util.concurrent.atomic.AtomicBoolean

class AudioEngine {
    private var audioTrack: AudioTrack? = null
    private val playing = AtomicBoolean(false)
    private var sampleRate = 44100
    private var volume = 0.5f

    fun start() {
        stop()
        val bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
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

    private fun generateTone() {
        Thread {
            val buffer = ShortArray(1024)
            var phase = 0.0
            while (playing.get()) {
                for (i in buffer.indices) {
                    phase += 2 * Math.PI * 440.0 / sampleRate
                    if (phase > 2 * Math.PI) phase -= 2 * Math.PI
                    buffer[i] = (Math.sin(phase) * 0.3 * Short.MAX_VALUE * volume).toInt().toShort()
                }
                audioTrack?.write(buffer, 0, buffer.size)
            }
        }.start()
    }

    fun stop() {
        playing.set(false)
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }

    fun setVolume(v: Float) {
        volume = v
    }

    fun play() {
        start()
    }
}
