package com.example.soundhealing.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.example.soundhealing.domain.BrainwaveType
import com.example.soundhealing.domain.NatureSound
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class AudioEngine {
    companion object {
        private const val SAMPLE_RATE = 44100
        private const val BUFFER_SIZE = 4096
        private const val MASTER_VOLUME = 0.3f
    }

    private val audioTracks = mutableListOf<AudioTrack>()
    private var masterVolume: Float = MASTER_VOLUME

    init {
        val minBufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        require(minBufferSize > 0) { "AudioTrack min buffer size invalid: $minBufferSize" }
    }

    fun playSolfeggio(frequency: Double): AudioTrack {
        val audioTrack = createAudioTrack()
        val thread = Thread {
            val buffer = ShortArray(BUFFER_SIZE)
            var sampleIndex = 0
            while (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
                for (i in buffer.indices) {
                    val t = (sampleIndex + i).toDouble() / SAMPLE_RATE
                    val value = sin(2.0 * PI * frequency * t) * MASTER_VOLUME
                    buffer[i] = (value * Short.MAX_VALUE).toInt().toShort()
                }
                audioTrack.write(buffer, 0, buffer.size)
                sampleIndex += buffer.size
            }
        }
        thread.isDaemon = true
        thread.start()
        audioTracks.add(audioTrack)
        return audioTrack
    }

    fun playNatureSound(sound: NatureSound): AudioTrack {
        val audioTrack = createAudioTrack()
        val thread = Thread {
            val buffer = ShortArray(BUFFER_SIZE)
            var sampleIndex = 0
            val random = Random(sound.id.toLong())
            while (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
                for (i in buffer.indices) {
                    val t = (sampleIndex + i).toDouble() / SAMPLE_RATE
                    val value = (random.nextDouble() * 2.0 - 1.0) * MASTER_VOLUME * 0.5
                    buffer[i] = (value * Short.MAX_VALUE).toInt().toShort()
                }
                audioTrack.write(buffer, 0, buffer.size)
                sampleIndex += buffer.size
            }
        }
        thread.isDaemon = true
        thread.start()
        audioTracks.add(audioTrack)
        return audioTrack
    }

    fun playBrainwave(type: BrainwaveType): AudioTrack {
        val audioTrack = createAudioTrack()
        val carrierFreq = 200.0
        val thread = Thread {
            val buffer = ShortArray(BUFFER_SIZE)
            var sampleIndex = 0
            while (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
                for (i in buffer.indices) {
                    val t = (sampleIndex + i).toDouble() / SAMPLE_RATE
                    val modFreq = (type.frequencyRangeHz.first + type.frequencyRangeHz.second) / 2.0
                    val modValue = sin(2.0 * PI * modFreq * t) * 0.5 + 0.5
                    val value = sin(2.0 * PI * carrierFreq * t) * modValue * MASTER_VOLUME
                    buffer[i] = (value * Short.MAX_VALUE).toInt().toShort()
                }
                audioTrack.write(buffer, 0, buffer.size)
                sampleIndex += buffer.size
            }
        }
        thread.isDaemon = true
        thread.start()
        audioTracks.add(audioTrack)
        return audioTrack
    }

    fun stopAll() {
        for (track in audioTracks) {
            if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                track.stop()
            }
            track.release()
        }
        audioTracks.clear()
    }

    fun setVolume(volume: Float) {
        masterVolume = volume
    }

    private fun createAudioTrack(): AudioTrack {
        val bufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        return AudioTrack(
            AudioManager.STREAM_MUSIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
            AudioTrack.MODE_STREAM
        ).apply {
            play()
        }
    }
}
