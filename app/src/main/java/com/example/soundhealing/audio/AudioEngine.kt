package com.example.soundhealing.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.example.soundhealing.domain.BrainwaveType
import com.example.soundhealing.domain.NatureSound
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.random

class AudioEngine {
    companion object {
        private const val TAG = "AudioEngine"
        private const val SAMPLE_RATE = 44100
        private const val BUFFER_SIZE = 8192
        private const val MASTER_VOLUME = 0.3f
    }

    private val audioTracks = mutableListOf<AudioTrack>()
    private var masterVolume: Float = MASTER_VOLUME

    init {
        AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        ).let { minBufferSize ->
            // Verify minimum buffer size is acceptable
        }
    }

    fun playSolfeggio(frequency: Double): AudioTrack {
        val audioTrack = createAudioTrack()
        val thread = Thread {
            val buffer = ShortArray(BUFFER_SIZE)
            var sampleIndex = 0
            while (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
                for (i in buffer.indices) {
                    val t = (sampleIndex + i).toDouble() / SAMPLE_RATE
                    val value = sin(2.0 * PI * frequency * t) * masterVolume
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

    fun playNatureSound(type: NatureSound): AudioTrack {
        val audioTrack = createAudioTrack()
        val thread = Thread {
            val buffer = ShortArray(BUFFER_SIZE)
            var sampleIndex = 0
            var random = java.util.Random(sampleIndex.toLong())

            while (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
                for (i in buffer.indices) {
                    val t = (sampleIndex + i).toDouble() / SAMPLE_RATE
                    var value = when (type.id) {
                        1 -> generateRain(t, random)      // Rain - filtered noise
                        2 -> generateOcean(t, random)     // Ocean - modulated noise
                        3 -> generateForest(t, random)    // Forest - mixed tones
                        4 -> generateWind(t, random)      // Wind - low freq noise
                        5 -> generateStream(t, random)    // Stream - bubbly noise
                        else -> 0.0
                    }
                    value *= masterVolume
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
        // Binaural beats: different frequency per ear
        val baseFreq = 200.0  // Base carrier frequency
        val beatFreq = type.frequencyRangeHz.first + (type.frequencyRangeHz.second - type.frequencyRangeHz.first) * 0.5
        val leftFreq = baseFreq
        val rightFreq = baseFreq + beatFreq

        val thread = Thread {
            // Stereo: buffer holds interleaved L,R samples, so need BUFFER_SIZE * 2
            val buffer = ShortArray(BUFFER_SIZE * 2)
            var sampleIndex = 0
            while (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
                for (i in 0 until BUFFER_SIZE) {
                    val t = (sampleIndex + i).toDouble() / SAMPLE_RATE
                    // Left channel: base frequency
                    val left = sin(2.0 * PI * leftFreq * t) * 0.5
                    // Right channel: base + beat frequency
                    val right = sin(2.0 * PI * rightFreq * t) * 0.5
                    // Interleaved stereo: L,R,L,R,...
                    buffer[i * 2] = (left * masterVolume * Short.MAX_VALUE).toInt().toShort()
                    buffer[i * 2 + 1] = (right * masterVolume * Short.MAX_VALUE).toInt().toShort()
                }
                audioTrack.write(buffer, 0, BUFFER_SIZE * 2)
                sampleIndex += BUFFER_SIZE
            }
        }
        thread.isDaemon = true
        thread.start()
        audioTracks.add(audioTrack)
        return audioTrack
    }

    fun stopAll() {
        audioTracks.forEach { track ->
            try {
                if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    track.stop()
                }
                track.release()
            } catch (e: Exception) {
                // Track already released
            }
        }
        audioTracks.clear()
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
    }

    private fun createAudioTrack(): AudioTrack {
        val bufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
            AudioTrack.MODE_STREAM
        )
        audioTrack.play()
        return audioTrack
    }

    fun release() {
        stopAll()
    }

    // Nature sound generators
    private fun generateRain(t: Double, random: java.util.Random): Double {
        // White noise with low-pass filter envelope
        val noise = (random.nextDouble() - 0.5) * 2.0
        val envelope = sin(2.0 * PI * 0.3 * t) * 0.5 + 0.5
        return noise * envelope * 0.15
    }

    private fun generateOcean(t: Double, random: java.util.Random): Double {
        // Modulated noise with slow wave envelope
        val noise = (random.nextDouble() - 0.5) * 2.0
        val wave = sin(2.0 * PI * 0.1 * t) * 0.5 + 0.5
        val envelope = sin(2.0 * PI * 0.05 * t) * 0.5 + 0.5
        return noise * wave * envelope * 0.2
    }

    private fun generateForest(t: Double, random: java.util.Random): Double {
        // Mixed tones simulating rustling leaves
        val tone1 = sin(2.0 * PI * 800.0 * t) * 0.05
        val tone2 = sin(2.0 * PI * 1200.0 * t) * 0.03
        val noise = (random.nextDouble() - 0.5) * 2.0 * 0.1
        val envelope = sin(2.0 * PI * 0.5 * t) * 0.5 + 0.5
        return (tone1 + tone2 + noise) * envelope
    }

    private fun generateWind(t: Double, random: java.util.Random): Double {
        // Filtered noise with slow variation
        val noise = (random.nextDouble() - 0.5) * 2.0
        val lowFreq = sin(2.0 * PI * 0.2 * t) * 0.5 + 0.5
        val midFreq = sin(2.0 * PI * 0.05 * t) * 0.5 + 0.5
        return noise * lowFreq * midFreq * 0.12
    }

    private fun generateStream(t: Double, random: java.util.Random): Double {
        // Bubbly noise with higher frequency content
        val noise = (random.nextDouble() - 0.5) * 2.0
        val bubble = sin(2.0 * PI * 2000.0 * t) * (random.nextDouble() * 0.1)
        val flow = sin(2.0 * PI * 0.3 * t) * 0.5 + 0.5
        return (noise * 0.08 + bubble) * flow
    }
}
